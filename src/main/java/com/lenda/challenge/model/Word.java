package com.lenda.challenge.model;

public class Word {

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
