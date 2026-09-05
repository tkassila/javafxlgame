package com.metait.javafxlgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LPiece implements Serializable {
    private int cx;
    private int cy;
    private int orientation; // 0..7
    private final boolean isRed;

    public LPiece(int cx, int cy, int orientation, boolean isRed) {
        this.cx = cx;
        this.cy = cy;
        this.orientation = orientation;
        this.isRed = isRed;
    }

    public int getCx() { return cx; }
    public int getCy() { return cy; }
    public int getOrientation() { return orientation; }
    public boolean isRed() { return isRed; }

    public void setPosition(int cx, int cy, int orientation) {
        this.cx = cx;
        this.cy = cy;
        this.orientation = orientation;
    }

    public List<Point> getOccupiedCells() {
        return getOccupiedCells(cx, cy, orientation);
    }

    public static List<Point> getOccupiedCells(int cx, int cy, int orientation) {
        List<Point> cells = new ArrayList<>();
        List<Point> offsets = getOffsets(orientation);
        for (Point offset : offsets) {
            cells.add(new Point(cx + offset.col, cy + offset.row));
        }
        return cells;
    }

    public static List<Point> getOffsets(int orientation) {
        List<Point> offsets = new ArrayList<>();
        offsets.add(new Point(0, 0)); // Corner cell
        switch (orientation) {
            case 0: // Stem UP, foot RIGHT
                offsets.add(new Point(0, -1));
                offsets.add(new Point(0, -2));
                offsets.add(new Point(1, 0));
                break;
            case 1: // Stem UP, foot LEFT
                offsets.add(new Point(0, -1));
                offsets.add(new Point(0, -2));
                offsets.add(new Point(-1, 0));
                break;
            case 2: // Stem RIGHT, foot DOWN
                offsets.add(new Point(1, 0));
                offsets.add(new Point(2, 0));
                offsets.add(new Point(0, 1));
                break;
            case 3: // Stem RIGHT, foot UP
                offsets.add(new Point(1, 0));
                offsets.add(new Point(2, 0));
                offsets.add(new Point(0, -1));
                break;
            case 4: // Stem DOWN, foot LEFT
                offsets.add(new Point(0, 1));
                offsets.add(new Point(0, 2));
                offsets.add(new Point(-1, 0));
                break;
            case 5: // Stem DOWN, foot RIGHT
                offsets.add(new Point(0, 1));
                offsets.add(new Point(0, 2));
                offsets.add(new Point(1, 0));
                break;
            case 6: // Stem LEFT, foot UP
                offsets.add(new Point(-1, 0));
                offsets.add(new Point(-2, 0));
                offsets.add(new Point(0, -1));
                break;
            case 7: // Stem LEFT, foot DOWN
                offsets.add(new Point(-1, 0));
                offsets.add(new Point(-2, 0));
                offsets.add(new Point(0, 1));
                break;
        }
        return offsets;
    }

    public int getBitmask() {
        return getBitmask(getOccupiedCells());
    }

    public static int getBitmask(List<Point> cells) {
        int mask = 0;
        for (Point cell : cells) {
            if (cell.col >= 0 && cell.col < 4 && cell.row >= 0 && cell.row < 4) {
                mask |= (1 << (cell.row * 4 + cell.col));
            }
        }
        return mask;
    }

    public static boolean isValidPosition(int cx, int cy, int orientation) {
        for (Point p : getOccupiedCells(cx, cy, orientation)) {
            if (p.col < 0 || p.col >= 4 || p.row < 0 || p.row >= 4) {
                return false;
            }
        }
        return true;
    }

    public LPiece copy() {
        return new LPiece(cx, cy, orientation, isRed);
    }

    public static double[] getPolygonCoordinates(int cx, int cy, int orientation, double cellSize) {
        double[] relativeCoords = null;
        switch (orientation) {
            case 0: // Stem UP, foot RIGHT
                relativeCoords = new double[]{
                    0, -2,  1, -2,  1, 0,  2, 0,  2, 1,  0, 1
                };
                break;
            case 1: // Stem UP, foot LEFT
                relativeCoords = new double[]{
                    0, -2,  1, -2,  1, 1,  -1, 1,  -1, 0,  0, 0
                };
                break;
            case 2: // Stem RIGHT, foot DOWN
                relativeCoords = new double[]{
                    0, 0,  3, 0,  3, 1,  1, 1,  1, 2,  0, 2
                };
                break;
            case 3: // Stem RIGHT, foot UP
                relativeCoords = new double[]{
                    0, -1,  1, -1,  1, 0,  3, 0,  3, 1,  0, 1
                };
                break;
            case 4: // Stem DOWN, foot LEFT
                relativeCoords = new double[]{
                    -1, 0,  1, 0,  1, 3,  0, 3,  0, 1,  -1, 1
                };
                break;
            case 5: // Stem DOWN, foot RIGHT
                relativeCoords = new double[]{
                    0, 0,  2, 0,  2, 1,  1, 1,  1, 3,  0, 3
                };
                break;
            case 6: // Stem LEFT, foot UP
                relativeCoords = new double[]{
                    -2, 0,  0, 0,  0, -1,  1, -1,  1, 1,  -2, 1
                };
                break;
            case 7: // Stem LEFT, foot DOWN
                relativeCoords = new double[]{
                    -2, 0,  1, 0,  1, 2,  0, 2,  0, 1,  -2, 1
                };
                break;
        }

        if (relativeCoords == null) return new double[0];

        double[] coords = new double[relativeCoords.length];
        for (int i = 0; i < relativeCoords.length; i += 2) {
            coords[i] = (cx + relativeCoords[i]) * cellSize;
            coords[i+1] = (cy + relativeCoords[i+1]) * cellSize;
        }
        return coords;
    }
}

