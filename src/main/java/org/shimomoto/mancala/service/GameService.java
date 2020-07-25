package org.shimomoto.mancala.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.model.internal.RawBoard;
import org.shimomoto.mancala.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * This service concerns one entity, it receives or returns that entity in most cases.
 * It is intended to hide the complexity and take care of fine grained operations.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
class GameService {

	@Autowired
	GameRepository repo;

	public Iterable<Game> getAll() {
		return repo.findAll();
	}

	public Optional<Game> getGame(@Nullable final String id) {
		return Optional.ofNullable(id)
				.filter(s -> !toString().isBlank())
				.flatMap(repo::findById);
	}

	public Game newGame(@NotNull final String player1Name, @NotNull final String player2Name) {
		return Game.builder()
				.playerNames(Map.of(
						Player.ONE, player1Name,
						Player.TWO, player2Name))
				.build();
	}

	public Game createRematch(final Game previousGame) {
		final Map<Player, Integer> newScore =
				getUpdatedScore(previousGame);

		return previousGame.toBuilder()
				.board(RawBoard.builder().build())
				.gameStart(LocalDateTime.now())
				.playerNames(previousGame.getPlayerNames())
				.winsByPlayer(newScore)
				.build();
	}

	public Map<Player, Integer> getUpdatedScore(@NotNull final Game previousGame) {

		final Optional<Player> winner = previousGame.getWinner();

		if (winner.isEmpty()) {
			return previousGame.getWinsByPlayer();
		}

		return Map.of(
				winner.get(), previousGame.getWinsByPlayer().get(winner.get()) + 1,
				winner.get().opponent(), previousGame.getWinsByPlayer().get(winner.get().opponent()));
	}

	public void move(final @NotNull Game game, @NotNull final Player player, final int position) {
		game.move(player, position);
	}

	public boolean isFinished(@NotNull final Game game) {
		return game.isFinished();
	}
}
