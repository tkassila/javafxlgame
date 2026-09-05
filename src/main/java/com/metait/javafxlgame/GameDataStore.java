package com.metait.javafxlgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameDataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    public SavedGameState currentGame;
    public List<SavedGameState> unfinishedGames = new ArrayList<>();
    public List<SavedGameState> finishedGames = new ArrayList<>();
    public String player1Name = "Pelaaja 1";
    public String player2Name = "Pelaaja 2";
    public String language = "fi";
}
