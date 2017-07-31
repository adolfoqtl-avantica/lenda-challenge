package com.lenda.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.lenda.challenge.spring.EntityBase;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Entity
@Audited
public class Game extends EntityBase {

	@JsonIgnore
	private static String[] DICE_TEMPLATE = new String[] {
			"aaafrs",
			"aaeeee",
			"aafirs",
			"adennn",
			"aeeeem",
			"aeegmu",
			"aegmnn",
			"afirsy",
			"bjkqxz",
			"ccenst",
			"ceiilt",
			"ceilpt",
			"ceipst",
			"ddhnot",
			"dhhlor",
			"dhlnor",
			"dhlnor",
			"eiiitt",
			"emottt",
			"ensssu",
			"fiprsy",
			"gorrvw",
			"iprrry",
			"nootuw",
			"ooottu"
	};

	private Integer score;
	private List<String> board;
	private List<Word> words;
	private OffsetDateTime lastPlay;

	public Game() {
		this.board = Lists.newArrayList();
		this.words = Lists.newArrayList();
		this.score = 0;
		scrambleBoard();
	}

	private void scrambleBoard() {
		List<String> dice = Lists.newArrayList(Arrays.asList(DICE_TEMPLATE));
		Random r = new Random();
		StringBuilder row;
		for (int i = 0; i < 5; i++) {
			row = new StringBuilder();
			for (int j = 0; j < 5; j++) {
				row.append(dice.remove(r.nextInt(dice.size())).charAt(r.nextInt(6)));
			}
			board.add(row.toString().toUpperCase());
		}
	}
	
	public boolean checkPlayed(String word) {
		word = word.toUpperCase();
		for (Word wordOption: words) {
			if (wordOption.getWord().equals(word))
				return true;
		}
		return false;
	}

	public boolean checkValidPlay(String word) {
		word = word.toUpperCase().replaceAll("QU", "Q");
		ArrayList<Position> paths = new ArrayList<>();
		// search for beginning positions
		for (int i = 0; i < board.size(); i++) {
			int j = -1;
			while ( (j=board.get(i).indexOf(word.charAt(0),j+1)) >= 0)
				paths.add(new Position(j,i));
		}
		for (int i = 1; i < word.length() && !paths.isEmpty(); i++) {
			ArrayList<Position> newpaths = new ArrayList<>();
			for (Position p: paths) {
				newpaths.addAll(p.findPaths(word.charAt(i)));
			}
			paths = newpaths;
		}
		System.out.println("found "+paths.size()+" path(s):");
		for (Position p: paths) {
			ArrayList<Position> path = new ArrayList<>();
			for (; p != null; p = p.prev)
				path.add(0, p);
			for (Position p2: path) {
				System.out.print(p2.charAt()+"("+p2.x+","+p2.y+")" );
			}
			System.out.println();
		}
		return !paths.isEmpty();
	}
	
	public void play(Word word) {
		// note: does not do any validation!
		words.add(word);
		score += word.getScore();
		lastPlay = OffsetDateTime.now();
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Transient
	public List<String> getBoard() {
		return board;
	}

	public void setBoard(List<String> board) {
		this.board = board;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "game")
	@Fetch(FetchMode.JOIN)
	@Audited
	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

	public OffsetDateTime getLastPlay() {
		return lastPlay;
	}

	public void setLastPlay(OffsetDateTime lastPlay) {
		this.lastPlay = lastPlay;
	}

	private class Position {
		int x, y;
		Position prev;

		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		Position(int x, int y, Position prev) {
			this(x,y);
			this.prev = prev;
		}

		boolean valid() {
			return x >= 0 && x < 5 && y >= 0 && y < 5;
		}

		boolean nonoverlapping() {
			for (Position p = prev; p != null; p = p.prev) {
				if (p.x == x && p.y == y)
					return false;
			}
			return true;
		}

		char charAt() {
			return Game.this.board.get(y).charAt(x);
		}

		List<Position> findPaths(char c) {
			ArrayList<Position> paths = new ArrayList<>();
			Position[] orthogonals = new Position[] {
					new Position(x-1, y, this),
					new Position(x+1, y, this),
					new Position(x, y-1, this),
					new Position(x, y+1, this),
			};
			for (Position p: orthogonals)
				if (p.valid() && p.nonoverlapping() && p.charAt() == c)
					paths.add(p);
			return paths;
		}
	}
}
