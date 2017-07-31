package com.lenda.challenge.controller;

import com.lenda.challenge.model.Score;
import com.lenda.challenge.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class Scores {

	@Autowired
	private GameService gameService;

	@RequestMapping(value = "/scores", method = RequestMethod.GET)
	public Score topScores() {
		return new Score();
	}
}
