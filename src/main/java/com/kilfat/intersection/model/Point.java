package com.kilfat.intersection.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Point implements Comparable<Point> {
    @NonNull
    final private Double x;
    @NonNull
    final private Double y;
    final private Double z;

    public Point(@NonNull Double x, @NonNull Double y) {
        this.x = x;
        this.y = y;
        this.z = null;
    }

    @Override
    public int compareTo(Point point) {
        return Comparator.comparingDouble(Point::getX)
                .thenComparingDouble(Point::getY)
                .thenComparing(x -> {
                    if (x.getZ() != null) {
                        return x.getZ().compareTo(this.getZ());
                    }
                    return 0;
                })
                .compare(this, point);
    }
}
