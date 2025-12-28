import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable{

    private int[][] _map;

    // edit this class below
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w, h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
     * The constructor Map(int[][] data) creates a new map from a given 2D array.
     * The array is deep-copied, so changes made to the original array do not affect the map.
     * The method init(int w, int h, int v) initializes the map to a size of w × h and fills all cells with the value v.
     * If either dimension is not positive, a runtime exception is thrown.
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new RuntimeException("Bad dimensions");
        }
        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                _map[x][y] = v;
            }
        }

	}

    /**
     * The method init(int[][] arr) initializes the map from a given 2D integer array.
     * It first validates that the array is not null, not empty, and that all rows have the same length (i.e., the array is rectangular).
     * If the array is invalid or ragged, a runtime exception is thrown.
     * If the array is valid, a new internal map is created and all values are copied using a deep copy,
     * so changes to the original array do not affect the map
     * @param arr a 2D int array.
     */
	@Override
	public void init(int[][] arr) {
        if (arr == null) {
           throw new RuntimeException("Bad array");
        }
        int w = arr.length;
        int h = arr[0].length;

        for (int i = 0; i < w; i++) {
            if (arr[i] == null || arr[i].length != h) {
                throw new RuntimeException("Ragged array");
            }
        }

        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                _map[x][y] = arr[x][y];
            }
        }

	}

    /**
     * The method getMap() returns a deep copy of the internal 2D map.
     * A new 2D array is created and all values from the internal map are copied into it.
     * This ensures that changes made to the returned array do not affect the original map.
     */


	@Override
	public int[][] getMap() {
        int w = getWidth();
        int h = getHeight();
        int[][] copy = new int[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                copy[x][y] = _map[x][y];
            }
        }
        return copy;
	}

    /**
     * getters
     */

	@Override
	public int getWidth() {
        return _map.length;
    }

	@Override
	public int getHeight() {
        return _map[0].length;
    }

	@Override
	public int getPixel(int x, int y) {
        return _map[x][y];
    }

	@Override
	public int getPixel(Pixel2D p) {
        return getPixel(p.getX(), p.getY());
	}

    /**
     * setters
     */

	@Override
	public void setPixel(int x, int y, int v) {
        _map[x][y] = v;
    }

	@Override
	public void setPixel(Pixel2D p, int v) {
        setPixel(p.getX(), p.getY(), v);
	}

    /**
     * The method isInside(Pixel2D p) checks whether a given pixel is inside the map boundaries.
     * It returns false if the pixel is null, or if its coordinates are outside the valid range of the map.
     * Otherwise, it returns true.
     */

    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) return false;
        int x = p.getX();
        int y = p.getY();
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * The method sameDimensions(Map2D p) checks whether another map has the same dimensions as this map.
     * It returns false if the given map is null. Otherwise, it returns true only if both maps have the same width and the same height.
     */

    @Override
    public boolean sameDimensions(Map2D p) {
        if (p == null) return false;
        return this.getWidth() == p.getWidth() && this.getHeight() == p.getHeight();
    }

    /**
     * The method addMap2D(Map2D p) adds the values of another map to this map, cell by cell.
     * If the given map is null or does not have the same dimensions as this map, the method does nothing.
     * Otherwise, each cell in this map is updated to be the sum of its current value and the corresponding value in the given map.
     */

    @Override
    public void addMap2D(Map2D p) {
        if (p == null) return;
        if (!sameDimensions(p)) return;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int v = this.getPixel(x, y) + p.getPixel(x, y);
                this.setPixel(x, y, v);
            }
        }
    }

    /**
     * The method mul(double scalar) multiplies all the values in the map by the given scalar.
     * Each cell value is multiplied by the scalar and then cast to an integer.
     * The result replaces the original value in the map.
     * @param scalar
     */

    @Override
    public void mul(double scalar) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int v = (int)(this.getPixel(x, y) * scalar);
                this.setPixel(x, y, v);
            }
        }
    }

    /**
     * The method rescale(double sx, double sy) changes the size of the map according to the given scale factors.
     * The new width is calculated as width * sx and the new height as height * sy.
     * A new map is created with the new dimensions.
     * Each cell in the new map copies its value from a corresponding cell in the original map,
     * using a simple nearest-neighbor approach (by mapping new coordinates back to old coordinates).
     * Finally, the current map is replaced with the resized map.
     */

    @Override
    public void rescale(double sx, double sy) {
        int newW = (int)(getWidth() * sx);
        int newH = (int)(getHeight() * sy);
        if (newW <= 0 || newH <= 0) return;

        int[][] old = this.getMap();
        int oldW = old.length;
        int oldH = old[0].length;

        int[][] resized = new int[newW][newH];

        for (int x = 0; x < newW; x++) {
            for (int y = 0; y < newH; y++) {
                int srcX = (int)(x / sx);
                int srcY = (int)(y / sy);

                if (srcX < 0) srcX = 0;
                if (srcY < 0) srcY = 0;
                if (srcX >= oldW) srcX = oldW - 1;
                if (srcY >= oldH) srcY = oldH - 1;

                resized[x][y] = old[srcX][srcY];
            }
        }

        this.init(resized);
    }

    /**
     * Draws a filled circle on the map.
     * The method goes over all the pixels in the map and colors every pixel whose distance from the given center is smaller than or equal to the given radius.
     * If the center point is null or located outside the map boundaries, the method does nothing.
     * center – the center point of the circle
     * rad – the radius of the circle
     * color – the color used to fill the circle
     */

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        if (center == null) return;
        if (!isInside(center)) return;

        int cx = center.getX();
        int cy = center.getY();
        double r2 = rad * rad;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                double dx = x - cx;
                double dy = y - cy;
                if (dx*dx + dy*dy <= r2) {
                    setPixel(x, y, color);
                }
            }
        }
    }

    /**
     * Draws a straight line between two points on the map.
     * The method colors all pixels that approximate the straight line connecting p1 and p2,
     * according to their relative horizontal and vertical distances.
     * If both points are the same, a single pixel is colored.
     * If one of the points is null or outside the map boundaries, the method does nothing.
     * The algorithm chooses whether to iterate over the x-axis or the y-axis based on which distance is larger,
     * and uses a linear function with rounding to determine the pixels to draw.
     * p1 – the starting point of the line
     * p2 – the ending point of the line
     * color – the color used to draw the line
     */

    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == null || p2 == null) return;
        if (!isInside(p1) || !isInside(p2)) return;

        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        if (dx == 0 && dy == 0) {
            setPixel(x1, y1, color);
            return;
        }

        if (dx >= dy) {
            if (x1 > x2) {
                int tx = x1; x1 = x2; x2 = tx;
                int ty = y1; y1 = y2; y2 = ty;
            }
            double m = (double)(y2 - y1) / (double)(x2 - x1);
            for (int x = x1; x <= x2; x++) {
                double y = y1 + m * (x - x1);
                int yy = (int)Math.round(y);
                setPixel(x, yy, color);
            }
        }
        else {
            if (y1 > y2) {
                int tx = x1; x1 = x2; x2 = tx;
                int ty = y1; y1 = y2; y2 = ty;
            }
            double m = (double)(x2 - x1) / (double)(y2 - y1);
            for (int y = y1; y <= y2; y++) {
                double x = x1 + m * (y - y1);
                int xx = (int)Math.round(x);
                setPixel(xx, y, color);
            }
        }

    }

    /**
     * Draws a filled rectangle on the map.
     * The method colors all pixels inside the axis-aligned rectangle defined by two opposite corner points p1 and p2, including the rectangle borders.
     * The order of the points does not matter – the method automatically determines the minimum and maximum x and y coordinates.
     * Pixels that fall outside the map boundaries are ignored.
     * If one of the given points is null, the method does nothing.
     * p1 – one corner of the rectangle
     * p2 – the opposite corner of the rectangle
     * color – the color used to fill the rectangle
     */

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == null || p2 == null) return;

        int x1 = Math.min(p1.getX(), p2.getX());
        int x2 = Math.max(p1.getX(), p2.getX());
        int y1 = Math.min(p1.getY(), p2.getY());
        int y2 = Math.max(p1.getY(), p2.getY());

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                Pixel2D tmp = new Index2D(x, y);
                if (isInside(tmp)) {
                    setPixel(x, y, color);
                }
            }
        }
    }

    /**
     * Compares this map to another object for equality.
     * Two maps are considered equal if and only if:
     * The other object implements the Map2D interface.
     * Both maps have the same width and height.
     * All corresponding pixels in the two maps have the same values.
     * If any of these conditions is not met, the method returns false.
     * If the object compared is the same reference as this map, the method returns true immediately.
     * @param ob the reference object with which to compare.
     * @return
     */

    @Override
    public boolean equals(Object ob) {
        if (this == ob) return true;
        if (!(ob instanceof Map2D)) return false;

        Map2D other = (Map2D) ob;
        if (!this.sameDimensions(other)) return false;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (this.getPixel(x, y) != other.getPixel(x, y)) return false;
            }
        }
        return true;
    }

	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
     *
     * Fills (flood-fills) the connected component starting from the given pixel with a new color.
     * The connected component is defined as all pixels that have the same original value as the start pixel and are connected by 4-neighborhood (up, down, left, right).
     * The algorithm uses an iterative BFS (queue) to visit all reachable pixels in the component and recolor them.
     * If cyclic is true, the map is treated as a torus (wrapping around edges). If cyclic is false, neighbors outside the bounds are ignored.
     * Returns the number of pixels that were changed. Returns 0 if the start pixel is null, outside the map, or already has the requested color.
	 */
	public int fill(Pixel2D xy, int new_v,  boolean cyclic) {
        if (xy == null) return 0;
        if (!isInside(xy)) return 0;

        int sx = xy.getX();
        int sy = xy.getY();

        int old = getPixel(sx, sy);
        if (old == new_v) return 0;

        java.util.ArrayDeque<Pixel2D> q = new java.util.ArrayDeque<>();
        q.add(new Index2D(sx, sy));
        setPixel(sx, sy, new_v);
        int count = 1;

        while (!q.isEmpty()) {
            Pixel2D p = q.removeFirst();
            int x = p.getX();
            int y = p.getY();

            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (cyclic) {
                    nx = ((nx % getWidth()) + getWidth()) % getWidth();
                    ny = ((ny % getHeight()) + getHeight()) % getHeight();
                } else {
                    if (nx < 0 || nx >= getWidth() || ny < 0 || ny >= getHeight()) continue;
                }

                if (getPixel(nx, ny) == old) {
                    setPixel(nx, ny, new_v);
                    q.add(new Index2D(nx, ny));
                    count++;
                }
            }
        }
        return count;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
     *
     * Computes the shortest valid path between p1 and p2 using Breadth-First Search (BFS).
     * A valid path is a sequence of neighboring cells (up, down, left, right) that does not pass through cells whose value equals obsColor.
     * If cyclic is true, the map is treated as a torus: moving left from x=0 wraps to x=width-1, moving right from x=width-1 wraps to x=0, and similarly for y.
     * If p1 or p2 is null, out of bounds, or located on an obstacle cell, the method returns null.
     * If p1 equals p2, the method returns an array containing a single copy of p1.
     * During BFS, the method stores a parent pointer for each visited cell in order to reconstruct the path once p2 is reached.
     * If p2 is unreachable, the method returns null. Otherwise, it returns an array of Pixel2D representing the path from p1 to p2 (inclusive).
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        if (p1 == null || p2 == null) return null;
        if (!isInside(p1) || !isInside(p2)) return null;

        if (getPixel(p1) == obsColor || getPixel(p2) == obsColor) return null;

        if (p1.equals(p2)) {
            return new Pixel2D[]{ new Index2D(p1) };
        }

        int w = getWidth(), h = getHeight();
        boolean[][] visited = new boolean[w][h];
        Pixel2D[][] parent = new Pixel2D[w][h];

        Deque<Pixel2D> q = new ArrayDeque<>();
        q.add(new Index2D(p1));
        visited[p1.getX()][p1.getY()] = true;

        while (!q.isEmpty()) {
            Pixel2D cur = q.removeFirst();
            if (cur.equals(p2)) break;

            for (Pixel2D nb : neighbors(cur, cyclic)) {
                int nx = nb.getX(), ny = nb.getY();
                if (visited[nx][ny]) continue;
                if (getPixel(nx, ny) == obsColor) continue;

                visited[nx][ny] = true;
                parent[nx][ny] = cur;
                q.addLast(nb);
            }
        }

        if (!visited[p2.getX()][p2.getY()]) return null;

        List<Pixel2D> rev = new ArrayList<>();
        Pixel2D cur = p2;
        while (cur != null) {
            rev.add(new Index2D(cur));
            if (cur.equals(p1)) break;
            cur = parent[cur.getX()][cur.getY()];
        }

        Pixel2D[] path = new Pixel2D[rev.size()];
        for (int i = 0; i < rev.size(); i++) {
            path[i] = rev.get(rev.size() - 1 - i);
        }
        return path;
	}

    /**
     * Computes a distance map from a given starting point using Breadth-First Search (BFS).
     * The returned map has the same dimensions as the original map.
     * Each cell contains the shortest path distance (number of steps) from the start point to that cell, avoiding cells with value obsColor.
     * Cells that are not reachable from the start point are marked with -1.
     * If the start point is null, out of bounds, or located on an obstacle cell, the method returns null or a map filled with -1 accordingly.
     * If cyclic is true, the map is treated as cyclic, allowing movement across opposite edges.
     * Distances are computed using BFS, ensuring minimal distances in terms of number of moves.
     * @param start the source (starting) point
     * @param obsColor the color representing obstacles
     * @param cyclic
     * @return
     */


    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        if (start == null) return null;
        if (!isInside(start)) return null;

        int w = getWidth(), h = getHeight();
        Map ans = new Map(w, h, -1);

        if (getPixel(start) == obsColor) return ans;

        boolean[][] visited = new boolean[w][h];
        Deque<Pixel2D> q = new ArrayDeque<>();

        ans.setPixel(start, 0);
        visited[start.getX()][start.getY()] = true;
        q.addLast(new Index2D(start));

        while (!q.isEmpty()) {
            Pixel2D cur = q.removeFirst();
            int curDist = ans.getPixel(cur);

            for (Pixel2D nb : neighbors(cur, cyclic)) {
                int nx = nb.getX(), ny = nb.getY();
                if (visited[nx][ny]) continue;
                if (getPixel(nx, ny) == obsColor) continue;

                visited[nx][ny] = true;
                ans.setPixel(nx, ny, curDist + 1);
                q.addLast(nb);
            }
        }
        return ans;
    }


	////////////////////// Private Methods ///////////////////////
    /**
     * Returns all valid neighboring pixels of the given pixel p.
     * A neighbor is defined as a pixel that is one step away in one of the four cardinal directions: up, down, left, or right.
     * Diagonal neighbors are not included.
     * If cyclic is false, only neighbors that are inside the map boundaries are returned.
     * If cyclic is true, the map is treated as cyclic, meaning that neighbors that go beyond one edge wrap around to the opposite edge.
     * The method always returns an array of Pixel2D objects, containing up to four neighbors.
     * @param p
     * @param cyclic
     * @return
     */
    private Pixel2D[] neighbors(Pixel2D p, boolean cyclic) {
        int x = p.getX(), y = p.getY();
        int w = getWidth(), h = getHeight();

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        List<Pixel2D> ans = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (cyclic) {
                if (nx < 0) nx = w - 1;
                if (nx >= w) nx = 0;
                if (ny < 0) ny = h - 1;
                if (ny >= h) ny = 0;
                ans.add(new Index2D(nx, ny));
            } else {
                if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                    ans.add(new Index2D(nx, ny));
                }
            }
        }
        return ans.toArray(new Pixel2D[0]);
    }
}
