package com.metait.javafxlgame;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SavedGameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String id;
    public final LPiece redPiece;
    public final LPiece bluePiece;
    public final Point neutral1;
    public final Point neutral2;
    public final boolean isRedTurn;
    public final LGameModel.GamePhase phase;
    public final boolean winnerIsRed;
    public final String player1Name;
    public final String player2Name;
    public final String saveDate;

    public SavedGameState(String id, LPiece redPiece, LPiece bluePiece, Point neutral1, Point neutral2,
                          boolean isRedTurn, LGameModel.GamePhase phase, boolean winnerIsRed,
                          String player1Name, String player2Name) {
        this.id = id;
        this.redPiece = redPiece;
        this.bluePiece = bluePiece;
        this.neutral1 = neutral1;
        this.neutral2 = neutral2;
        this.isRedTurn = isRedTurn;
        this.phase = phase;
        this.winnerIsRed = winnerIsRed;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.saveDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
