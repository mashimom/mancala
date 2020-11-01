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
import org.shimomoto.mancala.model.transfer.UserDto;
import org.shimomoto.mancala.service.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	UserFacade facade;

	@Operation(summary = "Get player by id")
	@ApiResponses(value = {
					@ApiResponse(responseCode = "200", description = "Found the player",
									content = {@Content(mediaType = "application/json",
													schema = @Schema(implementation = UserDto.class))}),
					@ApiResponse(responseCode = "404", description = "Player not found",
									content = @Content)})
	@GetMapping("/{pid}")
	public UserDto fetch(@Parameter(description = "Player public id") @PathVariable final String pid) {
		return facade.getPlayer(pid)
						.orElseThrow(() ->
										new EntityNotFoundException(format("Unable to find player: {0}", pid)));
	}

}
