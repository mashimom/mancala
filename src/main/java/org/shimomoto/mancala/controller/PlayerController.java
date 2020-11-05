package org.shimomoto.mancala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.service.GameFacade;
import org.shimomoto.mancala.service.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	UserFacade facade;

	@Autowired
	GameFacade gameFacade;

	@Operation(summary = "Get player by id")
	@ApiResponses(value = {
					@ApiResponse(responseCode = "200", description = "Found the player",
									content = {@Content(mediaType = "application/json",
													schema = @Schema(implementation = User.class))}),
					@ApiResponse(responseCode = "404", description = "Player not found",
									content = @Content)})
	@GetMapping("/{pid}")
	public User fetch(@Parameter(description = "Player public id", required = true) @PathVariable final String pid) {
		return facade.getPlayer(pid)
						.orElseThrow(() ->
										new EntityNotFoundException(format("Unable to find player: {0}", pid)));
	}

	@Operation(summary = "Go to wait room for a new game")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiResponses(value = {
					@ApiResponse(responseCode = "202", description = "Player now waiting for a new game"),
					@ApiResponse(responseCode = "404", description = "Player not found"),
					@ApiResponse(responseCode = "409", description = "Player already waiting for a new game")})
	@PostMapping("/{pid}/wait-room")
	public void waitRoom(@Parameter(description = "Player public id", required = true) @PathVariable final String pid) {
		if (!facade.waitRoom(pid)) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Player already waiting for a new game");
		}
	}

	@Operation(summary = "Get games by player id")
	@ApiResponses(value = {
					@ApiResponse(responseCode = "200", description = "Found player's games",
									content = {@Content(mediaType = "application/json",
													schema = @Schema(implementation = Game.class))}),
					@ApiResponse(responseCode = "404", description = "Player not found",
									content = @Content)})
	@GetMapping("/{pid}/games")
	public Stream<Game> listGames(@Parameter(description = "Player public id", required = true) @PathVariable final String pid) {
		final Comparator<Game> gameSortOrder = Comparator
						.comparing(Game::isEndOfGame)
						.thenComparing(Game::getGameStart);

		return gameFacade.getAllByUser(pid)
						.sorted(gameSortOrder);
	}
}
