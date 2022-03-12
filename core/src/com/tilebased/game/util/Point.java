package com.tilebased.game.util;

import com.tilebased.game.units.Unit;

import java.util.Objects;

public class Point {
    public int x;
    public int y;
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point(Unit unit) {
        this(unit.getPositionX(), unit.getPositionY());
    }

    public boolean rightOf(Point other) {
        return rightOf(other, 1);
    }

    public boolean rightOf(Point other, int maxDist){
        return other != null && other.x - this.x  == -maxDist;
    }

    public boolean leftOf(Point other) {
        return leftOf(other, 1);
    }

    public boolean leftOf(Point other, int maxDist){
        return other != null && other.x - this.x  == maxDist;
    }

    public boolean aboveOf(Point other) {
        return aboveOf(other, 1);
    }

    public boolean aboveOf(Point other, int maxDist){
        return other != null && other.y - this.y  == -maxDist;
    }

    public boolean belowOf(Point other) {
        return belowOf(other, 1);
    }

    public boolean belowOf(Point other, int maxDist){
        return other != null && other.y - this.y  == maxDist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
