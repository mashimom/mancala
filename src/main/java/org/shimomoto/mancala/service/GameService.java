package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public Game newGame(@NotNull final String player1Name, @NotNull final String player2Name) {
		return Game.builder()
				.playerNames(MapStream.of(
						Player.ONE, player1Name,
						Player.TWO, player2Name)
						.collect())
				.build();
	}

	@NotNull
	public Game createRematch(@NotNull final Game finishedGame) {
		return Game.builder()
				.playerNames(finishedGame.getPlayerNames())
				.winsByPlayer(finishedGame.getWinsByPlayer())
				.build();
	}

	public Iterable<Game> getAll() {
		return repo.findAll();
	}

	public Optional<Game> getGame(@Nullable final String id) {
		return Optional.ofNullable(id)
				.filter(s -> !s.isBlank())
				.flatMap(repo::findById);
	}

	public void setEndOfGame(final Game game) {
		game.setEndOfGame(true);
	}

	public void increaseScore(final Game game, @Nullable final Player winner) {
		if (winner != null) {
			setWinnerScore(game, winner);
		} else {
			setDrawScore(game);
		}
	}

	private void setDrawScore(final Game game) {
		final Map<Player, Integer> oldScore = game.getWinsByPlayer();
		final Map<Player, Integer> updatedScore =
				MapStream.of(
						Player.ONE, oldScore.get(Player.ONE) + 1,
						Player.TWO, oldScore.get(Player.TWO) + 1)
						.collect();
		game.setWinsByPlayer(updatedScore);
	}

	private void setWinnerScore(final Game game, final Player winner) {
		final Map<Player, Integer> oldScore = game.getWinsByPlayer();
		final Player looser = winner.opponent();
		final Map<Player, Integer> updatedScore =
				MapStream.of(
						winner, oldScore.get(winner) + 1,
						looser, oldScore.get(looser))
						.collect();
		game.setWinsByPlayer(updatedScore);
	}

	public void save(@NotNull final Game game) {
		repo.save(game);
	}
}
