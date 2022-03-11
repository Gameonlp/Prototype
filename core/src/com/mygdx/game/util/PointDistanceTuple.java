package com.mygdx.game.util;

import java.util.Objects;

public class PointDistanceTuple implements Comparable<PointDistanceTuple> {
    public interface distanceFunc{
        int calculateDistance(Point point);
    }

    private final Point point;
    private final int distance;

    public PointDistanceTuple(Point point, int distance) {
        this.point = point;
        this.distance = distance;
    }

    public PointDistanceTuple(Point point, distanceFunc func) {
        this.point = point;
        this.distance = func.calculateDistance(point);
    }

    public Point getPoint() {
        return point;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointDistanceTuple that = (PointDistanceTuple) o;
        return Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, distance);
    }

    @Override
    public int compareTo(PointDistanceTuple other) {
        return this.distance - other.distance;
    }

    @Override
    public String toString() {
        return "PointDistanceTuple{" +
                "point=" + point +
                ", distance=" + distance +
                '}';
    }
}
