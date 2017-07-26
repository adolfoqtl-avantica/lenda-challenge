package com.lenda.challenge.service;

import com.lenda.challenge.model.Game;
import com.lenda.challenge.model.Score;
import com.lenda.challenge.model.Word;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class GameService {
	
	@Autowired
	private Logger log;
	
	protected int maxId = 0;
	protected HashMap<Integer, Game> games = new HashMap<>();
	protected HashSet<String> dictionary = new HashSet<>();

	@PostConstruct
	protected void loadDictionary() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("dictionary.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				dictionary.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			log.error("dictionary.txt not found", e);
			System.exit(1);
		} catch (IOException e) {
			log.error("I/O error reading dictionary.txt",e);
			System.exit(1);
		}
	}
	
	public Game create() {
		Game game;
		synchronized(this) {
			int id = ++maxId;
			games.put(id, game = new Game(id));
		}
		return game;
	}
	
	public Game get(int id) {
		return games.get(id);
	}
	
	public Word play(Game g, String word) throws GameException {
		word = word.toUpperCase();
		if (g.checkPlayed(word))
			throw new DuplicateWordException();
		if (!g.checkValidPlay(word))
			throw new InvalidPlayException();
		// check dictionary
		if (!dictionary.contains(word)) {
			if (!word.contains("Q"))
				throw new InvalidWordException();
			if (word.contains("QU"))
				word = word.replaceAll("QU", "Q");  // try without the U
			else
				word = word.replaceAll("Q", "QU");  // try with the U
			if (!dictionary.contains(word))
				throw new InvalidWordException();
		}
		Word w = new Word(word);
		g.play(w);
		return w;
	}	
	
	public List<Score> getTop5Scores() {
		return null;
	}

	public static class GameException extends Exception { }

	public static class DuplicateWordException extends GameException { }

	public static class InvalidWordException extends GameException { }

	private static class InvalidPlayException extends GameException { }
}
