package com.kilfat.intersection.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Event implements Comparable<Event> {
    private final Point point;
    private final EventType type;
    private final List<Edge> edges;

    @Override
    public int compareTo(Event event) {
        return Comparator.comparing(Event::getPoint).compare(this, event);
    }
}
