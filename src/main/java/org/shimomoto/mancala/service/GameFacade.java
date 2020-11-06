package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.PlayerRole;
import org.shimomoto.mancala.model.entity.Board;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.util.PublicIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

/**
 * Should be used only by externally facing controllers, REST or otherwise.
 * This facade serves the purpose of hiding the complexity of the underlying (potentially multiple) services,
 * it also is the start of transactions.
 * It takes ids and values as parameters, return entities in most cases.
 * It may be considered a "public super service" that keeps "little services" private.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
@Service
public class GameFacade {

	@Autowired
	GameService service;

	@Autowired
	BoardService boardService;

	@Autowired
	UserService userService;

	@NotNull
	public Game createGame(final @NotNull User playerOne, final @NotNull User playerTwo) {
		return service.newGame(playerOne, playerTwo);
	}

	@NotNull
	public Game createRematch(final String id) {
		final Game game = getGameById(id);

		if (!service.isEndOfGame(game)) {
			throw new UnsupportedOperationException("match has not ended");
		}

		return service.createRematch(game);
	}

	@NotNull
	public Stream<Game> getAll() {
		final Comparator<Game> gameSortOrder = Comparator
						.comparing(Game::isEndOfGame)
						.thenComparing(Game::getGameStart);

		return StreamUtils.stream(service.getAll())
						.sorted(gameSortOrder);
	}

	@NotNull
	public Game getGameById(final @Nullable String id) {
		return service.getGame(id)
						.orElseThrow(() ->
										new EntityNotFoundException(format("Unable to find game: {0}", id)));
	}

	@NotNull
	public Game move(final String id, final @NotNull PlayerRole playerRole, final Integer position) {
		final Game game = getGameById(id);
		final Board board = game.getBoard();

		if (service.isEndOfGame(game)) {
			throw new UnsupportedOperationException("The game has ended and all moves are illegal.");
		}
		if (!boardService.isLegalMove(board, playerRole, position)) {
			throw new UnsupportedOperationException(format("The requested move {0} from {1} is not legal", playerRole, position));
		}
		boardService.move(board, playerRole, position);
		//if move led to end of game
		if (boardService.isEndOfGame(board)) {
			boardService.finalizeGame(board);
			endOfGameChanges(game, boardService.findWinner(board));
		}
		service.save(game);
		return game;
	}

	private void endOfGameChanges(final @NotNull Game game, final @NotNull Optional<PlayerRole> winner) {
		service.increaseScore(game, winner.orElse(null));
		service.setEndOfGame(game);
		if (winner.isEmpty()) {
			userService.scoreDraw(service.getPlayerByRole(game, PlayerRole.ONE));
			userService.scoreDraw(service.getPlayerByRole(game, PlayerRole.TWO));
			return;
		}
		userService.scoreWin(service.getPlayerByRole(game, winner.get()));
		userService.scoreLoose(service.getOpponentOf(game, winner.get()));
		//noinspection UnnecessaryReturnStatement
		return;
	}

	public Stream<Game> getAllByUser(final String pid) {
		final User user = Optional.ofNullable(pid)
						.flatMap(PublicIdUtils::stringDecode)
						.flatMap(userService::getPlayer)
						.orElseThrow(() -> new EntityNotFoundException(format("Unable to find user for pid {0}", pid)));

		final Predicate<Game> isPlayerOn =
						(Game g) -> service.isPlayerOn(g, user);

		return getAll()
						.filter(isPlayerOn);
	}
}
