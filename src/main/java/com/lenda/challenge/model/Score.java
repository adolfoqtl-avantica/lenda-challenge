package com.lenda.challenge.model;

import java.time.OffsetDateTime;


public class Score {

	private Integer score;
	private OffsetDateTime played;
	
	public Score() {
		score = 4;
		played = OffsetDateTime.now();
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public OffsetDateTime getPlayed() {
		return played;
	}

	public void setPlayed(OffsetDateTime played) {
		this.played = played;
	}
}
