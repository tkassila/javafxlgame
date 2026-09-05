package com.metait.javafxlgame;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable {
    public final int col;
    public final int row;

    public Point(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return col == point.col && row == point.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }
}
