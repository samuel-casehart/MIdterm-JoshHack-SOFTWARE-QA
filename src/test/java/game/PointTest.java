package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PointTest {

    @Test
    void constructor_storesCoordinates() {
        Point p = new Point(1, 2, 3);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
        assertEquals(3, p.z);
    }

    @Test
    void equals_sameCoordinates_isTrue() {
        assertEquals(new Point(1, 2, 3), new Point(1, 2, 3));
    }

    @Test
    void equals_sameInstance_isTrue() {
        Point p = new Point(1, 2, 3);
        assertEquals(p, p);
    }

    @ParameterizedTest(name = "(1,2,3) != ({0},{1},{2})")
    @CsvSource({ "9,2,3", "1,9,3", "1,2,9" })
    void equals_anyCoordinateDiffers_isFalse(int x, int y, int z) {
        assertNotEquals(new Point(1, 2, 3), new Point(x, y, z));
    }

    @Test
    void equals_null_isFalse() {
        assertFalse(new Point(1, 2, 3).equals(null));
    }

    @Test
    void equals_differentType_isFalse() {
        assertNotEquals("point", new Point(1, 2, 3));
        assertFalse(new Point(1, 2, 3).equals("point"));
    }

    @Test
    void hashCode_equalPoints_haveEqualHashCodes() {
        assertEquals(new Point(4, 5, 6).hashCode(), new Point(4, 5, 6).hashCode());
    }

    @Test
    void hashCode_differentPoints_differ() {
        assertNotEquals(new Point(1, 2, 3).hashCode(), new Point(3, 2, 1).hashCode());
        assertNotEquals(new Point(0, 0, 0).hashCode(), new Point(0, 0, 1).hashCode());
    }

    @Test
    void neighbors8_returnsEightDistinctAdjacentPointsOnSameLevel() {
        Point center = new Point(5, 5, 2);

        List<Point> neighbors = center.neighbors8();

        assertEquals(8, neighbors.size());
        Set<Point> distinct = new HashSet<>(neighbors);
        assertEquals(8, distinct.size(), "neighbors must be distinct");
        assertFalse(distinct.contains(center), "center must not be its own neighbor");
        for (Point n : neighbors) {
            assertEquals(2, n.z);
            assertTrue(Math.abs(n.x - 5) <= 1 && Math.abs(n.y - 5) <= 1);
        }
    }

    @Test
    void neighbors8_includesAllEightDirections() {
        Set<Point> expected = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    expected.add(new Point(10 + dx, 20 + dy, 0));
                }
            }
        }
        assertEquals(expected, new HashSet<>(new Point(10, 20, 0).neighbors8()));
    }
}
