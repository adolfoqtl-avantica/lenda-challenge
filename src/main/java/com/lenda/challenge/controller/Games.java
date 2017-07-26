package com.lenda.challenge.controller;

import com.lenda.challenge.model.Game;
import com.lenda.challenge.model.Word;
import com.lenda.challenge.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/game")
@RestController
public class Games {

	@Autowired
	private GameService gameService;

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Game create() {
		return gameService.create();
	}	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Game> show(@PathVariable("id") Integer id) {
		Game game = gameService.get(id);
		if (game == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(game, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Word> update(@PathVariable("id") Integer id, Word word) {
		Game game = gameService.get(id);
		if (game == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
			Word w = gameService.play(game, word.getWord());
			return new ResponseEntity<>(w, HttpStatus.OK);
		}
		catch (GameService.DuplicateWordException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT); // duplicate word
		}
		catch (GameService.InvalidWordException e) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE); // not in dictionary
		}
		catch (GameService.GameException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // invalid play or any other game error
		}
	}
}
