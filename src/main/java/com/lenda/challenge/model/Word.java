package com.lenda.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lenda.challenge.spring.EntityBase;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class Word extends EntityBase {

	private Game game;
	private String word;
	private Integer score;
	
	public Word() {
		
	}
	
	public Word(String word) {
		this.word = word;
		this.calculateScore();
	}
	
	private void calculateScore() {
		this.score = Math.min(6, Math.max(0, this.word.length() - 2));
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
