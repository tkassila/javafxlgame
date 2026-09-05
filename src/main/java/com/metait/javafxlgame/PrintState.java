package com.metait.javafxlgame;

public class PrintState {
    public static void main(String[] args) {
        LGameModel model = new LGameModel();
        if (model.loadState()) {
            System.out.println("--- Restored State ---");
            System.out.println("Phase: " + model.getPhase());
            System.out.println("Is Red Turn: " + model.isRedTurn());
            
            char[][] grid = new char[4][4];
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    grid[r][c] = '.';
                }
            }
            
            for (Point p : model.getRedPiece().getOccupiedCells()) {
                grid[p.row][p.col] = 'R';
            }
            for (Point p : model.getBluePiece().getOccupiedCells()) {
                grid[p.row][p.col] = 'B';
            }
            grid[model.getNeutral1().row][model.getNeutral1().col] = 'N';
            grid[model.getNeutral2().row][model.getNeutral2().col] = 'N';
            
            System.out.println("\nBoard Grid:");
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    System.out.print(grid[r][c] + " ");
                }
                System.out.println();
            }
            
            System.out.println("\nRed has moves: " + model.hasAnyLegalMoves(true));
            System.out.println("Blue has moves: " + model.hasAnyLegalMoves(false));
        } else {
            System.out.println("No saved state found.");
        }
    }
}
