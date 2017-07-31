package com.lenda.challenge.service;

import com.lenda.challenge.model.Game;
import com.lenda.challenge.model.Score;
import com.lenda.challenge.model.Word;
import com.lenda.challenge.repository.postgres.GameRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

@Service
public class GameService {
	
	@Autowired
	private Logger log;

	@Autowired
	private GameRepository gameRepository;

	private HashSet<String> dictionary = new HashSet<>();

	public void loadDictionary() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
			        this.getClass().getClassLoader().getResourceAsStream("data/dictionary.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				dictionary.add(line);
			}
			reader.close();
		} catch (Exception e) {
			log.error("dictionary.txt not found", e);
		}
	}

	@Transactional
	public Game create() {
		Game game = new Game();
		game = gameRepository.save(game);
		return game;
	}
	
	public Game get(Long id) {
		return gameRepository.findOne(id);
	}

	@Transactional
	public Word play(Game game, String wordTxt) throws GameException {
		wordTxt = wordTxt.toUpperCase();
		if (game.checkPlayed(wordTxt))
			throw new DuplicateWordException();
		if (!game.checkValidPlay(wordTxt))
			throw new InvalidPlayException();
		// check dictionary
		if (!dictionary.contains(wordTxt)) {
			if (!wordTxt.contains("Q"))
				throw new InvalidWordException();
			if (wordTxt.contains("QU"))
				wordTxt = wordTxt.replaceAll("QU", "Q");  // try without the U
			else
				wordTxt = wordTxt.replaceAll("Q", "QU");  // try with the U
			if (!dictionary.contains(wordTxt))
				throw new InvalidWordException();
		}
		Word word = new Word(wordTxt);
		game.play(word);

		game = gameRepository.save(game);

		return word;
	}	
	
	public List<Score> getTop5Scores() {
		return null;
	}

	public static class GameException extends Exception { }

	public static class DuplicateWordException extends GameException { }

	public static class InvalidWordException extends GameException { }

	private static class InvalidPlayException extends GameException { }
}
