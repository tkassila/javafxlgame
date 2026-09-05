package com.metait.javafxlgame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LGameModel {
    public enum GamePhase {
        L_MOVE,
        NEUTRAL_MOVE,
        GAME_OVER
    }

    private LPiece redPiece;
    private LPiece bluePiece;
    private Point neutral1;
    private Point neutral2;

    private boolean isRedTurn;
    private GamePhase phase;
    private Point selectedNeutral;
    private boolean winnerIsRed;

    private List<SavedGameState> unfinishedGames = new ArrayList<>();
    private List<SavedGameState> finishedGames = new ArrayList<>();
    private String player1Name = "Pelaaja 1";
    private String player2Name = "Pelaaja 2";
    private String language = "fi";

    public LGameModel() {
        resetGame();
    }

    public void resetGame() {
        // Red standard starting position: corner (1,0), configuration 5.
        // Occupies (1,0), (2,0), (1,1), (1,2)
        redPiece = new LPiece(1, 0, 5, true);

        // Blue standard starting position: corner (2,3), configuration 1.
        // Occupies (2,3), (2,2), (2,1), (1,3)
        bluePiece = new LPiece(2, 3, 1, false);

        // Neutral starting positions: diagonally opposite corners
        neutral1 = new Point(0, 0);
        neutral2 = new Point(3, 3);

        isRedTurn = true;
        phase = GamePhase.L_MOVE;
        selectedNeutral = null;
        winnerIsRed = false;
    }

    public LPiece getRedPiece() { return redPiece; }
    public LPiece getBluePiece() { return bluePiece; }
    public Point getNeutral1() { return neutral1; }
    public Point getNeutral2() { return neutral2; }
    public boolean isRedTurn() { return isRedTurn; }
    public GamePhase getPhase() { return phase; }
    public Point getSelectedNeutral() { return selectedNeutral; }
    public boolean isWinnerIsRed() { return winnerIsRed; }
    public void setPhase(GamePhase phase) { this.phase = phase; }
    public void setWinnerIsRed(boolean winnerIsRed) { this.winnerIsRed = winnerIsRed; }

    public int getNeutralMask() {
        int mask = 0;
        mask |= (1 << (neutral1.row * 4 + neutral1.col));
        mask |= (1 << (neutral2.row * 4 + neutral2.col));
        return mask;
    }

    public String getInvalidLMoveReason(int cx, int cy, int orientation, boolean isRed) {
        if (!LPiece.isValidPosition(cx, cy, orientation)) {
            return LanguageSupport.getTranslation("errOutOfBounds", language);
        }

        LPiece currentPiece = isRed ? redPiece : bluePiece;
        int originalMask = currentPiece.getBitmask();

        List<Point> newCells = LPiece.getOccupiedCells(cx, cy, orientation);
        int newMask = LPiece.getBitmask(newCells);

        if (newMask == originalMask) {
            return LanguageSupport.getTranslation("errNotMoved", language);
        }

        int opponentMask = (isRed ? bluePiece : redPiece).getBitmask();
        if ((newMask & opponentMask) != 0) {
            return LanguageSupport.getTranslation("errOverlapOpponent", language);
        }

        int neutralMask = getNeutralMask();
        if ((newMask & neutralMask) != 0) {
            return LanguageSupport.getTranslation("errOverlapCoin", language);
        }

        return null; // Valid
    }

    public boolean isValidLMove(int cx, int cy, int orientation, boolean isRed) {
        return getInvalidLMoveReason(cx, cy, orientation, isRed) == null;
    }

    public void confirmLMove(int cx, int cy, int orientation) {
        if (phase != GamePhase.L_MOVE) return;

        if (isValidLMove(cx, cy, orientation, isRedTurn)) {
            LPiece currentPiece = isRedTurn ? redPiece : bluePiece;
            currentPiece.setPosition(cx, cy, orientation);

            // Transition to optional neutral piece move
            phase = GamePhase.NEUTRAL_MOVE;
            selectedNeutral = null;
            saveState(); // Save state immediately!
        }
    }

    public void skipNeutralMove() {
        if (phase != GamePhase.NEUTRAL_MOVE) return;
        endTurn();
    }

    public boolean selectNeutralPiece(Point p) {
        if (phase != GamePhase.NEUTRAL_MOVE) return false;
        if (p.equals(neutral1) || p.equals(neutral2)) {
            selectedNeutral = p;
            return true;
        }
        return false;
    }

    public boolean moveSelectedNeutralTo(Point dest) {
        if (phase != GamePhase.NEUTRAL_MOVE || selectedNeutral == null) return false;

        // Destination must be within bounds (0..3)
        if (dest.col < 0 || dest.col >= 4 || dest.row < 0 || dest.row >= 4) {
            return false;
        }

        // Calculate occupancy masks
        int redMask = redPiece.getBitmask();
        int blueMask = bluePiece.getBitmask();
        int otherNeutralMask = 0;
        if (selectedNeutral.equals(neutral1)) {
            otherNeutralMask = 1 << (neutral2.row * 4 + neutral2.col);
        } else {
            otherNeutralMask = 1 << (neutral1.row * 4 + neutral1.col);
        }

        int destMask = 1 << (dest.row * 4 + dest.col);

        // Must not overlap with either L-piece or the other neutral piece
        if ((destMask & (redMask | blueMask | otherNeutralMask)) == 0) {
            if (selectedNeutral.equals(neutral1)) {
                neutral1 = dest;
            } else {
                neutral2 = dest;
            }
            selectedNeutral = null;
            endTurn();
            return true;
        }
        return false;
    }

    public boolean hasAnyLegalMoves(boolean isRed) {
        LPiece piece = isRed ? redPiece : bluePiece;
        int originalMask = piece.getBitmask();
        int otherMask = (isRed ? bluePiece.getBitmask() : redPiece.getBitmask()) | getNeutralMask();

        // 1) Etsi 3 vapaata peliruutua (vaakasuora)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col <= 1; col++) {
                if (isFree(col, row, otherMask) && isFree(col + 1, row, otherMask) && isFree(col + 2, row, otherMask)) {
                    // b) & c) Testataan löytyykö vierestä vapaata peliruutua
                    if (row > 0 && isFree(col, row - 1, otherMask) && isNewMove(col, row, col + 1, row, col + 2, row, col, row - 1, originalMask)) return true;
                    if (row < 3 && isFree(col, row + 1, otherMask) && isNewMove(col, row, col + 1, row, col + 2, row, col, row + 1, originalMask)) return true;
                    if (row > 0 && isFree(col + 2, row - 1, otherMask) && isNewMove(col, row, col + 1, row, col + 2, row, col + 2, row - 1, originalMask)) return true;
                    if (row < 3 && isFree(col + 2, row + 1, otherMask) && isNewMove(col, row, col + 1, row, col + 2, row, col + 2, row + 1, originalMask)) return true;
                }
            }
        }

        // 1) Etsi 3 vapaata peliruutua (pystysuora)
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row <= 1; row++) {
                if (isFree(col, row, otherMask) && isFree(col, row + 1, otherMask) && isFree(col, row + 2, otherMask)) {
                    // b) & c) Testataan löytyykö vierestä vapaata peliruutua
                    if (col > 0 && isFree(col - 1, row, otherMask) && isNewMove(col, row, col, row + 1, col, row + 2, col - 1, row, originalMask)) return true;
                    if (col < 3 && isFree(col + 1, row, otherMask) && isNewMove(col, row, col, row + 1, col, row + 2, col + 1, row, originalMask)) return true;
                    if (col > 0 && isFree(col - 1, row + 2, otherMask) && isNewMove(col, row, col, row + 1, col, row + 2, col - 1, row + 2, originalMask)) return true;
                    if (col < 3 && isFree(col + 1, row + 2, otherMask) && isNewMove(col, row, col, row + 1, col, row + 2, col + 1, row + 2, originalMask)) return true;
                }
            }
        }

        // d) Jos ei löydy, pelaaja on hävinnyt
        return false;
    }

    private boolean isFree(int col, int row, int otherMask) {
        if (col < 0 || col >= 4 || row < 0 || row >= 4) return false;
        int mask = 1 << (row * 4 + col);
        return (mask & otherMask) == 0;
    }

    private boolean isNewMove(int c1, int r1, int c2, int r2, int c3, int r3, int c4, int r4, int originalMask) {
        int mask = (1 << (r1 * 4 + c1)) | (1 << (r2 * 4 + c2)) | (1 << (r3 * 4 + c3)) | (1 << (r4 * 4 + c4));
        return mask != originalMask;
    }

    private void endTurn() {
        // Toggle turn
        isRedTurn = !isRedTurn;
        phase = GamePhase.L_MOVE;
        selectedNeutral = null;

        // Check if the next player has any legal moves. If not, the current player wins!
        if (!hasAnyLegalMoves(isRedTurn)) {
            phase = GamePhase.GAME_OVER;
            winnerIsRed = !isRedTurn; // The player who just finished their turn wins!

            // Automatically save finished game state
            SavedGameState finishedGame = new SavedGameState(
                "finished_" + System.currentTimeMillis(),
                new LPiece(redPiece.getCx(), redPiece.getCy(), redPiece.getOrientation(), redPiece.isRed()),
                new LPiece(bluePiece.getCx(), bluePiece.getCy(), bluePiece.getOrientation(), bluePiece.isRed()),
                new Point(neutral1.col, neutral1.row),
                new Point(neutral2.col, neutral2.row),
                isRedTurn,
                phase,
                winnerIsRed,
                player1Name,
                player2Name
            );
            finishedGames.add(finishedGame);
        }
        saveState(); // Save state immediately on disk!
    }

    public List<SavedGameState> getUnfinishedGames() { return unfinishedGames; }
    public List<SavedGameState> getFinishedGames() { return finishedGames; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public void setPlayer1Name(String name) { this.player1Name = name; }
    public void setPlayer2Name(String name) { this.player2Name = name; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public void saveCurrentToUnfinished() {
        if (phase == GamePhase.GAME_OVER) return;

        SavedGameState newState = new SavedGameState(
            String.valueOf(System.currentTimeMillis()),
            new LPiece(redPiece.getCx(), redPiece.getCy(), redPiece.getOrientation(), redPiece.isRed()),
            new LPiece(bluePiece.getCx(), bluePiece.getCy(), bluePiece.getOrientation(), bluePiece.isRed()),
            new Point(neutral1.col, neutral1.row),
            new Point(neutral2.col, neutral2.row),
            isRedTurn,
            phase,
            winnerIsRed,
            player1Name,
            player2Name
        );

        // Remove identical duplicate games from unfinished games list
        unfinishedGames.removeIf(oldState -> isDuplicateState(oldState, newState));
        unfinishedGames.add(newState);
    }

    public void loadGameState(SavedGameState state) {
        if (state == null) return;
        this.redPiece = new LPiece(state.redPiece.getCx(), state.redPiece.getCy(), state.redPiece.getOrientation(), state.redPiece.isRed());
        this.bluePiece = new LPiece(state.bluePiece.getCx(), state.bluePiece.getCy(), state.bluePiece.getOrientation(), state.bluePiece.isRed());
        this.neutral1 = new Point(state.neutral1.col, state.neutral1.row);
        this.neutral2 = new Point(state.neutral2.col, state.neutral2.row);
        this.isRedTurn = state.isRedTurn;
        this.phase = state.phase;
        this.selectedNeutral = null;
        this.winnerIsRed = state.winnerIsRed;
        this.player1Name = state.player1Name;
        this.player2Name = state.player2Name;
    }

    private boolean isDuplicateState(SavedGameState s1, SavedGameState s2) {
        return s1.redPiece.getCx() == s2.redPiece.getCx() &&
               s1.redPiece.getCy() == s2.redPiece.getCy() &&
               s1.redPiece.getOrientation() == s2.redPiece.getOrientation() &&
               s1.bluePiece.getCx() == s2.bluePiece.getCx() &&
               s1.bluePiece.getCy() == s2.bluePiece.getCy() &&
               s1.bluePiece.getOrientation() == s2.bluePiece.getOrientation() &&
               s1.neutral1.equals(s2.neutral1) &&
               s1.neutral2.equals(s2.neutral2) &&
               s1.isRedTurn == s2.isRedTurn &&
               s1.phase == s2.phase;
    }

    public void saveState() {
        File dir = new File(System.getProperty("user.home"), ".javafxlgame");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "state.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            GameDataStore store = new GameDataStore();
            store.currentGame = new SavedGameState(
                "current", redPiece, bluePiece, neutral1, neutral2, isRedTurn, phase, winnerIsRed, player1Name, player2Name
            );
            store.unfinishedGames = this.unfinishedGames;
            store.finishedGames = this.finishedGames;
            store.player1Name = this.player1Name;
            store.player2Name = this.player2Name;
            store.language = this.language;
            oos.writeObject(store);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadState() {
        File file = new File(new File(System.getProperty("user.home"), ".javafxlgame"), "state.dat");
        if (!file.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            GameDataStore store = (GameDataStore) ois.readObject();
            if (store != null) {
                if (store.currentGame != null) {
                    this.redPiece = store.currentGame.redPiece;
                    this.bluePiece = store.currentGame.bluePiece;
                    this.neutral1 = store.currentGame.neutral1;
                    this.neutral2 = store.currentGame.neutral2;
                    this.isRedTurn = store.currentGame.isRedTurn;
                    this.phase = store.currentGame.phase;
                    this.winnerIsRed = store.currentGame.winnerIsRed;
                }
                this.unfinishedGames = store.unfinishedGames != null ? store.unfinishedGames : new ArrayList<>();
                this.finishedGames = store.finishedGames != null ? store.finishedGames : new ArrayList<>();
                this.player1Name = store.player1Name != null ? store.player1Name : "Pelaaja 1";
                this.player2Name = store.player2Name != null ? store.player2Name : "Pelaaja 2";
                this.language = store.language != null ? store.language : "fi";
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
