package com.lenda.challenge.controller;

import com.lenda.challenge.model.Score;
import com.lenda.challenge.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/scores")
@RestController
public class Scores {

	@Autowired
	private GameService gameService;

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Score topScores() {
		return new Score();
	}
}
