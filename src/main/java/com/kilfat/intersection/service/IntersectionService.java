package com.kilfat.intersection.service;

import com.kilfat.intersection.model.Edge;
import com.kilfat.intersection.model.Event;
import com.kilfat.intersection.model.EventType;
import com.kilfat.intersection.model.Point;
import lombok.NonNull;

import java.util.*;

public class IntersectionService {
    private final TreeSet<Event> eventsQ = new TreeSet<>();
    private final TreeSet<Edge> edgesT = new TreeSet<>();
    private final List<Point> intersections = new ArrayList<>();

    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(new Point(1.0, 1.0), new Point(2.0, 1.0)));
        edges.add(new Edge(new Point(1.0, 1.0), new Point(1.0, 2.0)));
        edges.add(new Edge(new Point(0.0, 4.0), new Point(2.0, 4.0)));
        edges.add(new Edge(new Point(0.0, 3.0), new Point(3.0, 0.0)));
//        edges.add(new Edge(new Point(3.0, 1.0), new Point(4.0, 1.0)));
//        edges.add(new Edge(new Point(4.0, 0.0), new Point(4.0, 4.0)));
//        edges.add(new Edge(new Point(0.0, 4.0), new Point(2.0, 4.0)));
        edges.add(new Edge(new Point(1.0, 3.0), new Point(1.0, 5.0)));
//        edges.add(new Edge(new Point(2.0, 3.0), new Point(5.0, 3.0)));
//        edges.add(new Edge(new Point(4.0, 0.0), new Point(4.0, 4.0)));

        List<Point> points = new IntersectionService().findIntersections(edges);
        points.forEach(System.out::println);
        System.out.println("Finished!");
    }

    public List<Point> findIntersections(@NonNull List<Edge> edgeList) {
        initializeEvents(edgeList);
        for (Event event = eventsQ.pollFirst(); event != null; event = eventsQ.pollFirst()) {
            processPoint(event);
        }
        return intersections;
    }

    private void processPoint(Event event) {
        switch (event.getType()) {
            case BEGIN:
                caseBegin(event);
                break;
            case END:
                caseEnd(event);
                break;
            case INTERSECTION:
                caseIntersection(event);
                break;
            default:
        }
    }

    private void caseIntersection(Event event) {
        Edge edge1 = event.getEdges().get(0);
        Edge edge2 = event.getEdges().get(1);
        if (edge1.getSweepLineX() < edge2.getSweepLineX()) {
            processIntersectionCase(event, edge1, edge2);
        } else {
            processIntersectionCase(event, edge2, edge1);
        }
        intersections.add(event.getPoint());
    }

    private void processIntersectionCase(Event event, Edge edge1, Edge edge2) {
        Edge higher = edgesT.higher(edge1);
        Edge lower = edgesT.lower(edge2);
        if (higher != null) {
            addIntersection(higher, edge1, event.getPoint().getX());
            removeIntersection(higher, edge2);
        }
        if (lower != null) {
            addIntersection(lower, edge2, event.getPoint().getX());
            removeIntersection(lower, edge1);
        }
    }

    private void caseEnd(Event event) {
        for (Edge edge : event.getEdges()) {
            Edge lower = edgesT.lower(edge);
            Edge higher = edgesT.higher(edge);
            if (lower != null && higher != null) {
                addIntersection(lower, higher, event.getPoint().getX());
            }
            edgesT.remove(edge);
        }
    }

    private void caseBegin(Event event) {
        Double xBorder = event.getPoint().getX();
        recomputeValue(xBorder);
        edgesT.addAll(event.getEdges());
        for (Edge edge : event.getEdges()) {
            Edge lower = edgesT.lower(edge);
            Edge higher = edgesT.higher(edge);
            if (lower != null) {
                addIntersection(edge, lower, xBorder);
            }
            if (higher != null) {
                addIntersection(edge, higher, xBorder);
            }
            if (lower != null && higher != null) {
                removeIntersection(lower, higher);
            }
        }
    }

    private void recomputeValue(Double value) {
        edgesT.forEach(x -> x.computeSweepLineX(value));
    }

    private void removeIntersection(Edge edge1, Edge edge2) {
        eventsQ.removeIf(e -> e.getType().equals(EventType.INTERSECTION)
                && e.getEdges().contains(edge1)
                && e.getEdges().contains(edge2)
        );
    }

    private void addIntersection(Edge edge1, Edge edge2, Double xBorder) {
        Point intersection = edge1.cross(edge2, xBorder);
        if (intersection != null) {
            eventsQ.add(new Event(intersection, EventType.INTERSECTION, Arrays.asList(edge1, edge2)));
        }
    }

    /**
     * Load all points into one sorted collection
     */
    private void initializeEvents(List<Edge> edgeList) {
        Map<Point, List<Edge>> pointToEdgesBegin = new HashMap<>();
        Map<Point, List<Edge>> pointToEdgesEnd = new HashMap<>();
        for (Edge edge : edgeList) {
            pointToEdgesBegin.computeIfAbsent(edge.getFirst(), x -> new ArrayList<>()).add(edge);
        }
        pointToEdgesBegin.forEach((key, value) -> eventsQ.add(new Event(key, EventType.BEGIN, value)));
        for (Edge edge : edgeList) {
            pointToEdgesEnd.computeIfAbsent(edge.getSecond(), x -> new ArrayList<>()).add(edge);
        }
        pointToEdgesEnd.forEach((key, value) -> eventsQ.add(new Event(key, EventType.END, value)));
    }
}
