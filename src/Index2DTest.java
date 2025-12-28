import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Index2DTest {

    /**
     * Test method for get x.
     */

    @Test
    void testGetX() {
        Index2D p = new Index2D(3, 7);
        int x = p.getX();
        assertEquals(3, x);
    }

    /**
     * Test method for get x when x is negative.
     */

    @Test
    public void testGetX2() {
        Index2D p = new Index2D(-2, 5);
        assertEquals(-2, p.getX());
    }

    /**
     * Test method for get y.
     */

    @Test
    void testGetY() {
        Index2D p = new Index2D(3, 7);
        int y = p.getY();
        assertEquals(7, y);
    }

    /**
     * Test method for get y when y is negative.
     */

    @Test
    void testGetY2() {
        Index2D p = new Index2D(4, -9);
        int y = p.getY();
        assertEquals(-9, y);
    }

    /**
     * Test method for distance2D.
     */

    @Test
    void testDistance2D() {
        Pixel2D p2 = new Index2D(1, 2);
        Pixel2D p1 = new Index2D(4, 6);
        double dist = p1.distance2D(p2);
        assertEquals(5.0, dist);
    }

    /**
     * Test method for distance2D when points are the same.
     */

    @Test
    void testDistance2D2() {
        Pixel2D p1 = new Index2D(3, 7);
        Pixel2D p2 = new Index2D(3, 7);

        double dist = p1.distance2D(p2);

        assertEquals(0.0, dist);
    }

    /**
     * Test method for toString.
     */

    @Test
    void testToString() {
        Index2D p = new Index2D(3, 7);
        String str = p.toString();
        assertEquals("(3,7)", str);
    }

    /**
     * Test method for toString with negative coordinates.
     */

    @Test
    void testToString2() {
        Index2D p = new Index2D(-2, -5);
        String str = p.toString();
        assertEquals("(-2,-5)", str);
    }

    /**
     * Test method for equals.
     */

    @Test
    void testEquals() {
        Index2D p = new Index2D(3, 7);
        assertTrue(p.equals(p));
    }

    /**
     * Test method for equals with different objects.
     */

    @Test
    void testEquals2() {
        Index2D p1 = new Index2D(3, 7);
        Index2D p2 = new Index2D(3, 7);
        assertTrue(p1.equals(p2));
    }

    /**
     * Test method for equals with different coordinates.
     */

    @Test
    void testEquals3() {
        Index2D p1 = new Index2D(3, 7);
        Index2D p2 = new Index2D(4, 7);
        assertFalse(p1.equals(p2));
    }
}