package com.kilfat.intersection.model;

import lombok.*;

import java.util.Comparator;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Edge implements Comparable<Edge> {
    private final Point first;
    private final Point second;
    private Double sweepLineX;

    public Edge(Point first, Point second) {
        if (first.compareTo(second) < 0) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
        this.sweepLineX = computeSweepLineX(this.first.getX());
    }

    public Double computeSweepLineX(Double x) {
        Double x1 = this.first.getX();
        Double x2 = this.second.getX();
        Double y1 = this.first.getY();
        Double y2 = this.second.getY();
        if (x2 - x1 == 0.0) {
            // To avoid NPE
            x2 += 0.001;
        }
        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }

    @Override
    public int compareTo(Edge edge) {
        return Comparator.comparing(Edge::getFirst).thenComparing(Edge::getSecond).compare(this, edge);
    }

    public Point cross(Edge edge, Double xBorder) {
        Double x1 = first.getX();
        Double x2 = second.getX();
        Double y1 = first.getY();
        Double y2 = second.getY();

        Double x3 = edge.getFirst().getX();
        Double x4 = edge.getSecond().getX();
        Double y3 = edge.getFirst().getY();
        Double y4 = edge.getSecond().getY();


        double s1_x = x2 - x1;
        double s1_y = y2 - y1;
        double s2_x = x4 - x3;
        double s2_y = y4 - y3;

        double s, t;
        double v = -s2_x * s1_y + s1_x * s2_y;
        if (v == 0.0) {
            v = 0.001;
        }
        s = (-s1_y * (x1 - x3) + s1_x * (y1 - y3)) / v;
        t = (s2_x * (y1 - y3) - s2_y * (x1 - x3)) / v;

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            double resultX = x1 + (t * s1_x);
            if (resultX < xBorder) {
                return null; // only after border
            }
            double resultY = y1 + (t * s1_y);
            return new Point(resultX, resultY);
        }
        return null; // No collision
    }
}
