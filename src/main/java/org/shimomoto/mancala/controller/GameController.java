package org.shimomoto.mancala.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

	@GetMapping("/greeting")
	public String greeting() {
		return "Greetings, just to check backend responds.";
	}
}
