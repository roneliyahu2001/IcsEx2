import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Intro2CS, 2026A, this is a very
 */
class MapTest {
    /**
     */
    private int[][] _map_3_3 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private Map2D _m0, _m1, _m3_3;
    @BeforeEach
    public void setup() {
        _m0 = new Map(3,3,0);
        _m1 = new Map(3,3,0);
        _m3_3 = new Map(_map_3_3);
    }

    @Test
    @Timeout(value = 1, unit = SECONDS)
    void init_big_map_and_fill_should_work_fast() {
        int[][] bigarr = new int[500][500];
        _m1.init(bigarr);

        assertEquals(500, _m1.getWidth());
        assertEquals(500, _m1.getHeight());

        Pixel2D p1 = new Index2D(3, 2);
        int filled = _m1.fill(p1, 1, true);

        assertTrue(filled > 0);
        assertEquals(1, _m1.getPixel(3,2));
    }

    @Test
    void testInit() {
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertEquals(_m0, _m1);
    }
    @Test
    void testEquals() {
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertEquals(_m0, _m1);

        _m1.setPixel(0,0, 9);
        assertNotEquals(_m0, _m1);
    }

    @Test
    void setPixel_should_change_single_cell() {
        Map m = new Map(3,3,0);
        m.setPixel(1,1,7);
        assertEquals(7, m.getPixel(1,1));
        assertEquals(0, m.getPixel(0,0));
    }

    @Test
    void getMap_should_return_deep_copy() {
        Map m = new Map(3,3,0);
        m.setPixel(1,1,7);

        int[][] copy = m.getMap();
        assertEquals(7, copy[1][1]);

        copy[1][1] = 99;
        assertEquals(7, m.getPixel(1,1));
    }

    @Test
    void init_from_array_should_deep_copy() {
        int[][] arr = {
                {1,2},
                {3,4},
                {5,6}
        };
        Map2D m2 = new Map(arr);

        assertEquals(3, m2.getWidth());
        assertEquals(2, m2.getHeight());
        assertEquals(4, m2.getPixel(1,1));

        arr[1][1] = 999;
        assertEquals(4, m2.getPixel(1,1));
    }

    @Test
    void isInside_should_work_for_bounds_and_null() {
        Map m = new Map(3,3,0);

        assertTrue(m.isInside(new Index2D(0,0)));
        assertTrue(m.isInside(new Index2D(2,2)));

        assertFalse(m.isInside(new Index2D(-1,0)));
        assertFalse(m.isInside(new Index2D(0,-1)));
        assertFalse(m.isInside(new Index2D(3,0)));
        assertFalse(m.isInside(new Index2D(0,3)));

        assertFalse(m.isInside(null));
    }

    @Test
    void sameDimensions_should_compare_sizes() {
        Map2D a = new Map(3,4,0);
        Map2D b = new Map(3,4,9);
        Map2D c = new Map(4,3,0);

        assertTrue(a.sameDimensions(b));
        assertFalse(a.sameDimensions(c));
        assertFalse(a.sameDimensions(null));
    }

    @Test
    void addMap2D_should_add_values_when_same_dimensions() {
        Map2D a = new Map(2,2,1);
        Map2D b = new Map(2,2,5);

        a.addMap2D(b);

        assertEquals(6, a.getPixel(0,0));
        assertEquals(6, a.getPixel(1,1));
    }

    @Test
    void addMap2D_should_do_nothing_when_different_dimensions() {
        Map2D a = new Map(2,2,1);
        Map2D b = new Map(3,2,5);

        a.addMap2D(b);

        assertEquals(1, a.getPixel(0,0));
    }

    @Test
    void mul_should_multiply_and_cast_to_int() {
        Map2D a = new Map(2,2,3);
        a.mul(2.5);

        assertEquals(7, a.getPixel(0,0));
        assertEquals(7, a.getPixel(1,1));
    }

    @Test
    void equals_should_compare_dimensions_and_values() {
        Map2D a = new Map(3,4,7);
        Map2D b = new Map(3,4,7);

        assertEquals(a, b);

        b.setPixel(0,0, 8);
        assertNotEquals(a, b);
    }

    @Test
    void fill_should_fill_connected_component_non_cyclic() {
        int[][] a = {
                {1,1,0},
                {1,0,0},
                {0,0,1}
        };
        Map m = new Map(a);

        int filled = m.fill(new Index2D(0,0), 7, false);

        assertEquals(3, filled);
        assertEquals(7, m.getPixel(0,0));
        assertEquals(7, m.getPixel(1,0));
        assertEquals(7, m.getPixel(0,1));
        assertEquals(1, m.getPixel(2,2));
    }

    @Test
    void shortestPath_basic_no_obstacles() {
        Map2D m = new Map(4,4,0);
        Pixel2D p1 = new Index2D(0,0);
        Pixel2D p2 = new Index2D(3,0);

        Pixel2D[] path = m.shortestPath(p1, p2, 9, false);
        assertNotNull(path);
        assertEquals(p1, path[0]);
        assertEquals(p2, path[path.length-1]);
        assertEquals(4, path.length);
    }

    @Test
    void shortestPath_blocked_should_return_null() {
        Map m = new Map(3,3,0);
        int obs = 1;

        m.setPixel(1,0,obs);
        m.setPixel(1,1,obs);
        m.setPixel(1,2,obs);

        Pixel2D[] path = m.shortestPath(new Index2D(0,1), new Index2D(2,1), obs, false);
        assertNull(path);
    }

    @Test
    void shortestPath_cyclic_wrap_should_be_short() {
        Map2D m = new Map(5,1,0);
        Pixel2D p1 = new Index2D(0,0);
        Pixel2D p2 = new Index2D(4,0);

        Pixel2D[] path = m.shortestPath(p1, p2, 9, true);
        assertNotNull(path);
        assertEquals(2, path.length);
    }

    @Test
    void allDistance_basic() {
        Map2D m = new Map(3,3,0);
        Map2D dist = m.allDistance(new Index2D(0,0), 9, false);

        assertNotNull(dist);
        assertEquals(0, dist.getPixel(0,0));
        assertEquals(1, dist.getPixel(1,0));
        assertEquals(2, dist.getPixel(2,0));
        assertEquals(2, dist.getPixel(1,1));
    }

    @Test
    void allDistance_obstacles_unreachable_should_be_minus1() {
        Map m = new Map(3,3,0);
        int obs = 1;

        m.setPixel(0,1,obs);
        m.setPixel(1,1,obs);
        m.setPixel(2,1,obs);

        Map2D dist = m.allDistance(new Index2D(0,0), obs, false);

        assertEquals(-1, dist.getPixel(0,2));
        assertEquals(-1, dist.getPixel(2,2));
    }
}