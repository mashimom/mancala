package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.PlayerRole;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.util.PublicIdUtils;
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

	public Game newGame(@NotNull final User playerOne, @NotNull final User playerTwo) {
		final Map<PlayerRole, User> playersByRole = Map.of(PlayerRole.ONE, playerOne,
						PlayerRole.TWO, playerTwo);
		final Game game = Game.builder()
						.playersByRole(playersByRole)
						.build();
		repo.save(game);
		return game;
	}

	@NotNull
	public Game createRematch(@NotNull final Game finishedGame) {
		final Game game = Game.builder()
						.playersByRole(Map.copyOf(finishedGame.getPlayersByRole()))
						.winsByPlayer(Map.copyOf(finishedGame.getWinsByPlayer()))
						.build();
		repo.save(game);
		return game;
	}

	public Iterable<Game> getAll() {
		return repo.findAll();
	}

	public Optional<Game> getGame(@Nullable final String id) {
		return Optional.ofNullable(id)
						.flatMap(PublicIdUtils::stringDecode)
						.flatMap(repo::findById);
	}

	public void setEndOfGame(final Game game) {
		game.setEndOfGame(true);
	}

	public void increaseScore(final Game game, @Nullable final PlayerRole winner) {
		if (winner != null) {
			setWinnerScore(game, winner);
		} else {
			setDrawScore(game);
		}
	}

	private void setDrawScore(final Game game) {
		final Map<PlayerRole, Integer> oldScore = game.getWinsByPlayer();
		final Map<PlayerRole, Integer> updatedScore =
						MapStream.of(
										PlayerRole.ONE, oldScore.get(PlayerRole.ONE) + 1,
										PlayerRole.TWO, oldScore.get(PlayerRole.TWO) + 1)
										.collect();
		game.setWinsByPlayer(updatedScore);
	}

	private void setWinnerScore(final Game game, final PlayerRole winner) {
		final Map<PlayerRole, Integer> oldScore = game.getWinsByPlayer();
		final PlayerRole looser = winner.opponent();
		final Map<PlayerRole, Integer> updatedScore =
						MapStream.of(
										winner, oldScore.get(winner) + 1,
										looser, oldScore.get(looser))
										.collect();
		game.setWinsByPlayer(updatedScore);
	}

	public void save(@NotNull final Game game) {
		repo.save(game);
	}

	public Map<PlayerRole, User> getPlayers(final Game game) {
		return Map.copyOf(game.getPlayersByRole());
	}

	public User getPlayerByRole(final @NotNull Game game, final @NotNull PlayerRole playerRole) {
		return game.getPlayersByRole().get(playerRole);
	}

	public User getOpponentOf(final @NotNull Game game, final @NotNull PlayerRole playerRole) {
		return getPlayerByRole(game, playerRole.opponent());
	}

	public boolean isEndOfGame(final @NotNull Game game) {
		return game.isEndOfGame();
	}

	public boolean isPlayerOn(final @NotNull Game game, final User player) {
		return game.getPlayersByRole().containsValue(player);
	}
}
