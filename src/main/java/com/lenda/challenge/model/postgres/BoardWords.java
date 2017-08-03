package com.lenda.challenge.model.postgres;

import com.lenda.challenge.hibernate.JsonEntity;

import java.util.List;

public class BoardWords implements JsonEntity {

    private List<String> words;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
