package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class LineTest {

    private static List<Point> expected(int... xy) {
        List<Point> pts = new ArrayList<>();
        for (int i = 0; i < xy.length; i += 2) {
            pts.add(new Point(xy[i], xy[i + 1], 0));
        }
        return pts;
    }

    @Test
    void line_toSamePoint_containsOnlyThatPoint() {
        assertEquals(expected(4, 4), new Line(4, 4, 4, 4).getPoints());
    }

    @Test
    void line_horizontalRight_includesBothEndpoints() {
        assertEquals(expected(0, 0, 1, 0, 2, 0, 3, 0), new Line(0, 0, 3, 0).getPoints());
    }

    @Test
    void line_horizontalLeft_runsInReverseOrder() {
        assertEquals(expected(3, 0, 2, 0, 1, 0, 0, 0), new Line(3, 0, 0, 0).getPoints());
    }

    @Test
    void line_verticalDown_includesBothEndpoints() {
        assertEquals(expected(0, 0, 0, 1, 0, 2), new Line(0, 0, 0, 2).getPoints());
    }

    @Test
    void line_verticalUp_runsInReverseOrder() {
        assertEquals(expected(0, 2, 0, 1, 0, 0), new Line(0, 2, 0, 0).getPoints());
    }

    @Test
    void line_perfectDiagonal_stepsBothAxes() {
        assertEquals(expected(0, 0, 1, 1, 2, 2), new Line(0, 0, 2, 2).getPoints());
    }

    @Test
    void line_negativeDiagonal_stepsBothAxesDownward() {
        assertEquals(expected(2, 2, 1, 1, 0, 0), new Line(2, 2, 0, 0).getPoints());
    }

    @Test
    void line_shallowSlope_startsAndEndsAtGivenPoints() {
        List<Point> pts = new Line(0, 0, 4, 1).getPoints();
        assertEquals(5, pts.size());
        assertEquals(new Point(0, 0, 0), pts.get(0));
        assertEquals(new Point(4, 1, 0), pts.get(pts.size() - 1));
    }

    @Test
    void line_steepSlope_startsAndEndsAtGivenPoints() {
        List<Point> pts = new Line(0, 0, 1, 4).getPoints();
        assertEquals(5, pts.size());
        assertEquals(new Point(0, 0, 0), pts.get(0));
        assertEquals(new Point(1, 4, 0), pts.get(pts.size() - 1));
    }

    @Test
    void iterator_visitsPointsInOrder() {
        List<Point> visited = new ArrayList<>();
        for (Point p : new Line(0, 0, 2, 0)) {
            visited.add(p);
        }
        assertEquals(expected(0, 0, 1, 0, 2, 0), visited);
    }
}
