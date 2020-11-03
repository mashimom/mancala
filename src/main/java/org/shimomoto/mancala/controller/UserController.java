package org.shimomoto.mancala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.service.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserFacade facade;

	@Operation(summary = "Technical API to create a new player")
	@ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Player created")})
//	@ResponseStatus(value = HttpStatus.CREATED, reason = "New player created")
	@PostMapping("/")
	public User createUser(@Parameter(description = "New player's screen name") @NotNull @RequestParam final String screenName) {
		return Optional.of(screenName)
						.filter(StringUtils::isNotBlank)
						.map(facade::create)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "A screen name is needed"));
	}
}
