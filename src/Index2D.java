public class Index2D implements Pixel2D {
    private int x;
    private int y;

    /**
        * Constructs a new Index2D object with the given width and height.
        * @param w the width (x coordinate)
        * @param h the height (y coordinate)
        */
    public Index2D(int w, int h) {
        this.x = w;
        this.y = h;
    }

    /**
     * Copy constructor.
     * @param other the other Pixel2D to copy from.
     */
    public Index2D(Pixel2D other) {

        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * @return the x coordinate.
     */

    @Override
    public int getX() {

        return this.x;
    }

    /**
     * @return the y coordinate.
     */

    @Override
    public int getY() {

        return this.y;
    }

    /**
     * Computes the Euclidean distance between this Pixel2D and another Pixel2D.
     * @param p2 the other Pixel2D.
     * @return the Euclidean distance between this Pixel2D and p2.
     */

    @Override
    public double distance2D(Pixel2D p2) {

        int dx = this.x - p2.getX();
        int dy = this.y - p2.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * @return a string representation of this Pixel2D in the format "(x,y)".
     */

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    /**
     * Compares this Pixel2D to another object for equality.
     * @param p the object to compare to.
     * @return true if p is a Pixel2D with the same coordinates as this Pixel2D, false otherwise.
     */

    @Override
    public boolean equals(Object p) {
        if (this == p) return true;
        if (p == null) return false;
        if (!(p instanceof Pixel2D)) return false;

        Pixel2D other = (Pixel2D) p;
        return this.x == other.getX() && this.y == other.getY();
    }
}
