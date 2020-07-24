package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
@Service
public class GameFacade {

	private GameService service;

	public Stream<Game> getAll() {
		return StreamUtils.stream(service.getAll());
	}

	public Game createGame(final @Nullable String player1, final @Nullable String player2) {
		final String player1Name = Optional.ofNullable(player1)
				.orElse("Player 1");
		final String player2Name = Optional.ofNullable(player2)
				.orElse("Player 2");

		return service.newGame(player1Name, player2Name);
	}

	public Game createRematch(final String id) {
		return service.createRematch(getGameById(id));
	}

	public Game getGameById(final @Nullable String id) {
		return service.getGame(id)
				.orElseThrow(() ->
						new EntityNotFoundException(format("Unable to find game: {0}", id)));
	}

	@NotNull
	public Game move(final String id, final @NotNull Player player, final Integer position) {
		final Game game = getGameById(id);

		if (game.isFinished()) {
			throw new UnsupportedOperationException("The game has ended and all moves are illegal.");
		}
		service.move(game, player, position);
		return game;
	}
}
