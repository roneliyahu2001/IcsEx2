import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Index2DTest {

    @Test
    void testGetX() {
        Index2D p = new Index2D(3, 7);
        int x = p.getX();
        assertEquals(3, x);
    }

    @Test
    public void testGetX2() {
        Index2D p = new Index2D(-2, 5);
        assertEquals(-2, p.getX());
    }

    @Test
    void testGetY() {
        Index2D p = new Index2D(3, 7);
        int y = p.getY();
        assertEquals(7, y);
    }

    @Test
    void testGetY2() {
        Index2D p = new Index2D(4, -9);
        int y = p.getY();
        assertEquals(-9, y);
    }

    @Test
    void testDistance2D() {
        Pixel2D p2 = new Index2D(1, 2);
        Pixel2D p1 = new Index2D(4, 6);
        double dist = p1.distance2D(p2);
        assertEquals(5.0, dist);
    }

    @Test
    void testDistance2D2() {
        Pixel2D p1 = new Index2D(3, 7);
        Pixel2D p2 = new Index2D(3, 7);

        double dist = p1.distance2D(p2);

        assertEquals(0.0, dist);
    }

    @Test
    void testToString() {
        Index2D p = new Index2D(3, 7);
        String str = p.toString();
        assertEquals("(3,7)", str);
    }

    @Test
    void testToString2() {
        Index2D p = new Index2D(-2, -5);
        String str = p.toString();
        assertEquals("(-2,-5)", str);
    }

    @Test
    void testEquals() {
        Index2D p = new Index2D(3, 7);
        assertTrue(p.equals(p));
    }

    @Test
    void testEquals2() {
        Index2D p1 = new Index2D(3, 7);
        Index2D p2 = new Index2D(3, 7);
        assertTrue(p1.equals(p2));
    }

    @Test
    void testEquals3() {
        Index2D p1 = new Index2D(3, 7);
        Index2D p2 = new Index2D(4, 7);
        assertFalse(p1.equals(p2));
    }
}