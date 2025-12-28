import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
/**
 * Intro2CS_2026A
 * This class represents a Graphical User Interface (GUI) for Map2D.
 * The class has save and load functions, and a GUI draw function.
 * You should implement this class, it is recommender to use the StdDraw class, as in:
 * https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
 *
 * @author boaz.benmoshe
 */
public class Ex2_GUI {

    //

    // ---------- constants ----------
    private static final double HUD_HEIGHT = 2.7;           // area above the grid
    private static final double BUTTON_WIDTH = 3.8;
    private static final double BUTTON_HEIGHT = 1.05;
    private static final double BUTTON_MARGIN = 0.55;
    private static final double DROPDOWN_ROW = 1.05;

    private static final int DEFAULT_SIZE = 30;
    private static final int OBS_COLOR = 1;
    private static final String DEFAULT_MAP_FILE = "map.txt";
    private static final String SAVE_MAP_FILE = "map_saved.txt";

    // ---------- palette ----------
    private static final Color HUD_BG = new Color(232, 239, 247);
    private static final Color GRID_BORDER = new Color(90, 90, 90, 120);
    private static final Color BUTTON_BG = new Color(208, 217, 232);
    private static final Color BUTTON_BG_ACTIVE = new Color(151, 178, 217);
    private static final Color BUTTON_BORDER = new Color(95, 115, 143);
    private static final Color BUTTON_TEXT = new Color(32, 45, 59);
    private static final Color STATUS_TEXT = new Color(64, 64, 64);

    // ---------- state ----------
    private static Map2D canvas;
    private static Panel openPanel = Panel.NONE;
    private static DrawTool tool = DrawTool.POINT;
    private static int brush = 1;
    private static boolean cyclic = false;
    private static Pixel2D anchor = null;
    private static final List<PathStroke> paintedPaths = new ArrayList<>();

    private static boolean stdReady = false;
    private static int lastW = -1, lastH = -1;

    // ---------- enums ----------
    private enum Panel {NONE, MAP, COLOR, DRAW, ACTION}

    private enum DrawTool {POINT, SEGMENT, RECT, CIRCLE, FILL, PATH}

    /**
     * @return Draw/Run the GUI on a given map. If map is null, creates a default map.
     */
    public static void drawMap(Map2D map) {
        canvas = (map != null) ? map : new Map(DEFAULT_SIZE, DEFAULT_SIZE, 0);
        run();
    }

    // ---------- entry ----------
    public static void main(String[] args) {
        Map2D loaded = loadMap(DEFAULT_MAP_FILE);
        drawMap(loaded);
    }

    // ---------- main loop ----------
    private static void run() {
        boolean prevMouse = false;
        while (true) {
            ensureStdDraw();
            render();

            boolean mouse = StdDraw.isMousePressed();
            if (mouse && !prevMouse) {
                onClick(StdDraw.mouseX(), StdDraw.mouseY());
            }
            prevMouse = mouse;

            while (StdDraw.hasNextKeyTyped()) {
                char k = StdDraw.nextKeyTyped();
                if (k == 'q' || k == 'Q') return;
                if (k == 'c' || k == 'C') cyclic = !cyclic;
            }
            StdDraw.pause(15);
        }
    }

    // ---------- rendering ----------
    private static void render() {
        StdDraw.clear(new Color(245, 245, 245));
        drawCells();
        drawPathOverlay();
        drawHud();
        StdDraw.show();
    }

    private static void drawCells() {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                StdDraw.setPenColor(colorFor(canvas.getPixel(x, y)));
                StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
            }
        }
        StdDraw.setPenColor(GRID_BORDER);
        for (int x = 0; x <= w; x++) StdDraw.line(x, 0, x, h);
        for (int y = 0; y <= h; y++) StdDraw.line(0, y, w, y);

        if (anchor != null) {
            StdDraw.setPenColor(new Color(0, 0, 0, 120));
            StdDraw.filledCircle(anchor.getX() + 0.5, anchor.getY() + 0.5, 0.2);
        }
    }

    private static void drawHud() {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        StdDraw.setPenColor(HUD_BG);
        StdDraw.filledRectangle(w / 2.0, h + HUD_HEIGHT / 2.0, w / 2.0, HUD_HEIGHT / 2.0);
        StdDraw.setPenColor(BUTTON_BORDER);
        StdDraw.line(0, h, w, h);

        double x = BUTTON_MARGIN;
        double y = h + HUD_HEIGHT / 2.0 + 0.25;

        x = drawButton(x, y, "Map", openPanel == Panel.MAP);
        x = drawButton(x, y, "Color", openPanel == Panel.COLOR);
        x = drawButton(x, y, "Draw", openPanel == Panel.DRAW);
        drawButton(x, y, "Tools", openPanel == Panel.ACTION);

        drawStatusStrip(w, h);

        if (openPanel != Panel.NONE) {
            drawDropdown(openPanel, h);
        }
    }

    private static void drawStatusStrip(int w, int h) {
        StdDraw.setPenColor(new Color(200, 210, 225));
        StdDraw.filledRectangle(w - 6.5, h + 0.4, 6.5, 0.35);
        StdDraw.setPenColor(BUTTON_BORDER);
        StdDraw.rectangle(w - 6.5, h + 0.4, 6.5, 0.35);
        StdDraw.setPenColor(STATUS_TEXT);

        String status = "Tool: " + tool + "  |  Brush: " + brushName(brush) + "  |  Cyclic: " + (cyclic ? "On" : "Off");
        StdDraw.textLeft(w - 12.5, h + 0.4, status);
    }

    private static double drawButton(double xLeft, double centerY, String title, boolean active) {
        double cx = xLeft + BUTTON_WIDTH / 2.0;
        StdDraw.setPenColor(active ? BUTTON_BG_ACTIVE : BUTTON_BG);
        StdDraw.filledRectangle(cx, centerY, BUTTON_WIDTH / 2.0, BUTTON_HEIGHT / 2.0);
        StdDraw.setPenColor(BUTTON_BORDER);
        StdDraw.rectangle(cx, centerY, BUTTON_WIDTH / 2.0, BUTTON_HEIGHT / 2.0);
        StdDraw.setPenColor(BUTTON_TEXT);
        StdDraw.text(cx, centerY, title + " \u25be");
        return xLeft + BUTTON_WIDTH + BUTTON_MARGIN;
    }

    private static void drawDropdown(Panel panel, int mapH) {
        List<String> items = switch (panel) {
            case MAP -> Arrays.asList("Clear", "20x20", "40x40", "Load...", "Save...");
            case COLOR -> Arrays.asList("Black", "Blue", "Red", "Yellow", "Green", "Magenta");
            case DRAW -> Arrays.asList("Point", "Segment", "Rect", "Circle");
            case ACTION -> Arrays.asList("Fill", "Shortest Path", "Toggle Cyclic");
            default -> List.of();
        };

        double left = BUTTON_MARGIN + (panel.ordinal() - 1) * (BUTTON_WIDTH + BUTTON_MARGIN);
        double width = BUTTON_WIDTH;
        double top = mapH + HUD_HEIGHT - 0.1;

        for (int i = 0; i < items.size(); i++) {
            double cy = top - (i + 0.5) * DROPDOWN_ROW;
            Color rowColor = (i % 2 == 0) ? new Color(244, 248, 255) : new Color(236, 242, 252);
            StdDraw.setPenColor(rowColor);
            StdDraw.filledRectangle(left + width / 2.0, cy, width / 2.0, DROPDOWN_ROW / 2.0);
            StdDraw.setPenColor(BUTTON_BORDER);
            StdDraw.rectangle(left + width / 2.0, cy, width / 2.0, DROPDOWN_ROW / 2.0);
            StdDraw.setPenColor(BUTTON_TEXT);
            StdDraw.textLeft(left + 0.25, cy, items.get(i));
        }
    }

    private static void drawPathOverlay() {
        for (PathStroke s : paintedPaths) {
            if (s == null || s.path == null) continue;
            StdDraw.setPenColor(colorFor(s.colorValue));
            for (Pixel2D p : s.path) {
                StdDraw.filledSquare(p.getX() + 0.5, p.getY() + 0.5, 0.5);
            }
        }
    }

    // ---------- input handling ----------
    private static void onClick(double mx, double my) {
        int h = canvas.getHeight();

        if (openPanel != Panel.NONE && inDropdown(mx, my, h)) {
            chooseDropdown(mx, my, h);
            return;
        }

        if (my >= h) {
            handleHudClick(mx, my, h);
            return;
        }

        openPanel = Panel.NONE;
        Pixel2D p = toCell(mx, my);
        if (p != null) applyTool(p);
    }

    private static void handleHudClick(double mx, double my, int mapH) {
        double yTop = mapH + HUD_HEIGHT;
        double yBot = mapH;
        if (my < yBot || my > yTop) return;

        double x = BUTTON_MARGIN;
        Panel[] order = {Panel.MAP, Panel.COLOR, Panel.DRAW, Panel.ACTION};
        for (Panel p : order) {
            double left = x;
            double right = x + BUTTON_WIDTH;
            if (mx >= left && mx <= right) {
                openPanel = (openPanel == p) ? Panel.NONE : p;
                anchor = null;
                return;
            }
            x = right + BUTTON_MARGIN;
        }
    }

    private static boolean inDropdown(double mx, double my, int mapH) {
        List<String> items = switch (openPanel) {
            case MAP -> Arrays.asList("Clear", "20x20", "40x40", "Load...", "Save...");
            case COLOR -> Arrays.asList("Black", "Blue", "Red", "Yellow", "Green", "Magenta");
            case DRAW -> Arrays.asList("Point", "Segment", "Rect", "Circle");
            case ACTION -> Arrays.asList("Fill", "Shortest Path", "Toggle Cyclic");
            default -> List.of();
        };
        if (items.isEmpty()) return false;

        double left = BUTTON_MARGIN + (openPanel.ordinal() - 1) * (BUTTON_WIDTH + BUTTON_MARGIN);
        double right = left + BUTTON_WIDTH;
        double top = mapH + HUD_HEIGHT - 0.1;
        double bottom = top - items.size() * DROPDOWN_ROW;
        return mx >= left && mx <= right && my <= top && my >= bottom;
    }

    private static void chooseDropdown(double mx, double my, int mapH) {
        List<String> items = switch (openPanel) {
            case MAP -> Arrays.asList("Clear", "20x20", "40x40", "Load...", "Save...");
            case COLOR -> Arrays.asList("Black", "Blue", "Red", "Yellow", "Green", "Magenta");
            case DRAW -> Arrays.asList("Point", "Segment", "Rect", "Circle");
            case ACTION -> Arrays.asList("Fill", "Shortest Path", "Toggle Cyclic");
            default -> List.of();
        };
        double top = mapH + HUD_HEIGHT - 0.1;
        int idx = (int) ((top - my) / DROPDOWN_ROW);
        if (idx < 0 || idx >= items.size()) return;

        String choice = items.get(idx);
        applyChoice(openPanel, choice);
        openPanel = Panel.NONE;
    }

    private static void applyChoice(Panel panel, String choice) {
        switch (panel) {
            case MAP -> handleMapChoice(choice);
            case COLOR -> handleColorChoice(choice);
            case DRAW -> handleDrawChoice(choice);
            case ACTION -> handleActionChoice(choice);
            default -> {
            }
        }
    }

    private static void handleMapChoice(String choice) {
        if (choice.equalsIgnoreCase("Clear")) {
            canvas.init(canvas.getWidth(), canvas.getHeight(), 0);
            paintedPaths.clear();
            anchor = null;
            return;
        }
        if (choice.endsWith("x20")) canvas = new Map(20, 20, 0);
        if (choice.endsWith("x40")) canvas = new Map(40, 40, 0);

        if (choice.startsWith("Load")) {
            Map2D loaded = loadMap(SAVE_MAP_FILE);
            if (loaded == null) loaded = loadMap(DEFAULT_MAP_FILE);
            if (loaded != null) canvas = loaded;
        }
        if (choice.startsWith("Save")) {
            saveMap(canvas, SAVE_MAP_FILE);
        }
        ensureStdDraw();
        paintedPaths.clear();
        anchor = null;
    }

    private static void handleColorChoice(String choice) {
        if (choice.equalsIgnoreCase("Black")) brush = 1;
        else if (choice.equalsIgnoreCase("Blue")) brush = 2;
        else if (choice.equalsIgnoreCase("Red")) brush = 3;
        else if (choice.equalsIgnoreCase("Yellow")) brush = 4;
        else if (choice.equalsIgnoreCase("Green")) brush = 5;
        else if (choice.equalsIgnoreCase("Magenta")) brush = 6;
    }

    private static void handleDrawChoice(String choice) {
        if (choice.equalsIgnoreCase("Point")) tool = DrawTool.POINT;
        if (choice.equalsIgnoreCase("Segment")) tool = DrawTool.SEGMENT;
        if (choice.equalsIgnoreCase("Rect")) tool = DrawTool.RECT;
        if (choice.equalsIgnoreCase("Circle")) tool = DrawTool.CIRCLE;
        anchor = null;
    }

    private static void handleActionChoice(String choice) {
        if (choice.equalsIgnoreCase("Fill")) tool = DrawTool.FILL;
        if (choice.equalsIgnoreCase("Shortest Path")) tool = DrawTool.PATH;
        if (choice.equalsIgnoreCase("Toggle Cyclic")) cyclic = !cyclic;
        anchor = null;
    }

    private static void applyTool(Pixel2D p) {
        switch (tool) {
            case POINT -> canvas.setPixel(p, brush);
            case SEGMENT -> {
                if (anchor == null) anchor = p;
                else {
                    canvas.drawLine(anchor, p, brush);
                    anchor = null;
                }
            }
            case RECT -> {
                if (anchor == null) anchor = p;
                else {
                    canvas.drawRect(anchor, p, brush);
                    anchor = null;
                }
            }
            case CIRCLE -> {
                if (anchor == null) anchor = p;
                else {
                    double dx = p.getX() - anchor.getX();
                    double dy = p.getY() - anchor.getY();
                    double radius = Math.sqrt(dx * dx + dy * dy);
                    canvas.drawCircle(anchor, radius, brush);
                    anchor = null;
                }
            }
            case FILL -> {
                canvas.fill(p, brush, cyclic);
                anchor = null;
            }
            case PATH -> {
                if (anchor == null) anchor = p;
                else {
                    Map2D mask = buildObstacleMask(canvas);
                    Pixel2D[] path = mask.shortestPath(anchor, p, OBS_COLOR, cyclic);
                    if (path != null) {
                        paintedPaths.add(new PathStroke(path, brush));
                        for (Pixel2D step : path) {
                            if (canvas.getPixel(step.getX(), step.getY()) == 0) {
                                canvas.setPixel(step.getX(), step.getY(), brush);
                            }
                        }
                    }
                    anchor = null;
                }
            }
        }
    }

    // ---------- helpers ----------
    private static Pixel2D toCell(double mx, double my) {
        int x = (int) Math.floor(mx);
        int y = (int) Math.floor(my);
        if (x < 0 || y < 0 || x >= canvas.getWidth() || y >= canvas.getHeight()) return null;
        return new Index2D(x, y);
    }

    private static void ensureStdDraw() {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        if (!stdReady || w != lastW || h != lastH) {
            StdDraw.setCanvasSize(Math.max(700, w * 12), Math.max(600, (int) ((h + HUD_HEIGHT) * 12)));
            StdDraw.setXscale(0, w);
            StdDraw.setYscale(0, h + HUD_HEIGHT);
            StdDraw.enableDoubleBuffering();
            stdReady = true;
            lastW = w;
            lastH = h;
        }
    }

    private static Map2D buildObstacleMask(Map2D src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Map2D mask = new Map(w, h, 0);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                mask.setPixel(x, y, src.getPixel(x, y) == 0 ? 0 : 1);
            }
        }
        return mask;
    }

    private static Color colorFor(int v) {
        return switch (v) {
            case 0 -> Color.WHITE;
            case 1 -> Color.BLACK;
            case 2 -> new Color(0x2962FF);
            case 3 -> new Color(0xE53935);
            case 4 -> new Color(0xF1C40F);
            case 5 -> new Color(0x27AE60);
            case 6 -> Color.MAGENTA;
            default -> Color.getHSBColor((float) ((v * 0.37) % 1.0), 0.65f, 0.95f);
        };
    }

    private static String brushName(int value) {
        return switch (value) {
            case 1 -> "Black";
            case 2 -> "Blue";
            case 3 -> "Red";
            case 4 -> "Yellow";
            case 5 -> "Green";
            case 6 -> "Magenta";
            default -> "Color " + value;
        };
    }

    private record PathStroke(Pixel2D[] path, int colorValue) {
    }

    // ---------- file helpers ----------
    public static Map2D loadMap(String mapFileName) {
        Map2D ans = null;
        try (Scanner scanner = new Scanner(new File(mapFileName))) {
            if (!scanner.hasNextInt()) return null;
            int w = scanner.nextInt();
            if (!scanner.hasNextInt()) return null;
            int h = scanner.nextInt();

            int[][] data = new int[w][h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (!scanner.hasNextInt()) return null;
                    data[x][y] = scanner.nextInt();
                }
            }
            ans = new Map(data);
        } catch (FileNotFoundException e) {
            System.err.println("Map file not found: " + mapFileName);
        }
        return ans;
    }

    /**
     *
     * @param map
     * @param mapFileName Save a map as text:
     *                    first line: "w h"
     *                    then h lines, each line has w ints (row-major by y, then x).
     */
    public static void saveMap(Map2D map, String mapFileName) {
        if (map == null) return;
        try (PrintWriter out = new PrintWriter(mapFileName)) {
            int w = map.getWidth();
            int h = map.getHeight();
            out.println(w + " " + h);
            for (int y = 0; y < h; y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < w; x++) {
                    row.append(map.getPixel(x, y)).append(' ');
                }
                out.println(row.toString().trim());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Failed to save map to " + mapFileName);
        }
    }
}