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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Game;
import org.shimomoto.mancala.service.GameFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/game")
public class GameController {

	@Autowired
	GameFacade facade;

	@SuppressWarnings("SameReturnValue")
	@Operation(summary = "SimpleTest")
	@GetMapping("/greeting")
	@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
	public String greeting() {
		return "Greetings, just to check backend responds.";
	}

	@Operation(summary = "List games saved")
	@GetMapping(value = "/")
	public Stream<Game> list() {
		return facade.getAll();
	}

	@Operation(summary = "Get game by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the game",
					content = {@Content(mediaType = "application/json",
							schema = @Schema(implementation = Game.class))}),
			@ApiResponse(responseCode = "404", description = "Game not found",
					content = @Content)})
	@GetMapping("/{id}")
	public Game fetch(@Parameter(description = "Public id") @PathVariable final String id) {
		return facade.getGameById(id);
	}

	@Operation(summary = "Start a new game")
	@PostMapping("/")
	public Game startGame(@Nullable @Parameter(description = "Player 1 display name") @RequestParam(defaultValue = "Player 1") final String player1,
	                      @Nullable @Parameter(description = "Player 2 display name") @RequestParam(defaultValue = "Player 2") final String player2) {
		return facade.createGame(player1, player2);
	}

	@PostMapping("/{id}/rematch")
	public Game rematch(@PathVariable final String id) {
		return facade.createRematch(id);
	}

	@PostMapping("/{id}/move")
	public Game move(@Parameter(description = "Public id for game") @PathVariable final String id,
	                 @Parameter(description = "Player that will make a move") @NotNull @RequestParam final Player player,
	                 @Parameter(description = "Position that refers to movement") @RequestParam final int position) {
		try {
			return facade.move(id, player, position);
		} catch (final UnsupportedOperationException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e);
		}
	}
}
