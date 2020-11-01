package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Board;
import org.shimomoto.mancala.model.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;
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

	@NotNull
	public Game createGame(final @Nullable String player1, final @Nullable String player2) {
		final String player1Name = Optional.ofNullable(player1)
				.orElse("Player 1");
		final String player2Name = Optional.ofNullable(player2)
				.orElse("Player 2");

		final Game game = service.newGame(player1Name, player2Name);
		service.save(game);
		return game;
	}

	@NotNull
	public Game createRematch(final String id) {
		final Game game = getGameById(id);

		if (!boardService.isEndOfGame(game.getBoard())) {
			throw new UnsupportedOperationException("match has not ended");
		}

		final Game rematch = service.createRematch(game);
		service.save(rematch);
		return rematch;
	}

	@NotNull
	public Stream<Game> getAll() {
		return StreamUtils.stream(service.getAll());
	}

	@NotNull
	public Game getGameById(final @Nullable String id) {
		return service.getGame(id)
				.orElseThrow(() ->
						new EntityNotFoundException(format("Unable to find game: {0}", id)));
	}

	@NotNull
	public Game move(final String id, final @NotNull Player player, final Integer position) {
		final Game game = getGameById(id);
		final Board board = game.getBoard();

		if (boardService.isEndOfGame(board)) {
			throw new UnsupportedOperationException("The game has ended and all moves are illegal.");
		}
		if (!boardService.isLegalMove(board, player, position)) {
			throw new UnsupportedOperationException(format("The requested move {} from {} is not legal", player, position));
		}
		boardService.move(board, player, position);
		if (boardService.isEndOfGame(board)) {
			boardService.endGameMove(board);
			final Optional<Player> winner = boardService.findWinner(board);
			service.increaseScore(game, winner.orElse(null));
			service.setEndOfGame(game);
		}
		service.save(game);
		return game;
	}
}
