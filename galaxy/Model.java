package galaxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static galaxy.Place.pl;


/** The state of a Galaxies Puzzle.  Each cell, cell edge, and intersection of
 *  edges has coordinates (x, y). For cells, x and y are positive and odd.
 *  For intersections, x and y are even.  For horizontal edges, x is odd and
 *  y is even.  For vertical edges, x is even and y is odd.  On a board
 *  with w columns and h rows of cells, (0, 0) indicates the bottom left
 *  corner of the board, and (2w, 2h) indicates the upper right corner.
 *  If (x, y) are the coordinates of a cell, then (x-1, y) is its left edge,
 *  (x+1, y) its right edge, (x, y-1) its bottom edge, and (x, y+1) its
 *  top edge.  The four cells (x, y), (x+2, y), (x, y+2), and (x+2, y+2)
 *  meet at intersection (x+1, y+1).  Cells contain nonnegative integer
 *  values, or "marks". A cell containing 0 is said to be unmarked.
 *  @author Anastasia Sukhorebraya-Beck
 */
class Model {

    /** The default number of squares on a side of the board. */
    static final int DEFAULT_SIZE = 7;

    /** Initializes an empty puzzle board of size DEFAULT_SIZE x DEFAULT_SIZE,
     *  with a boundary around the periphery. */
    Model() {

        init(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /** Initializes an empty puzzle board of size COLS x ROWS, with a boundary
     *  around the periphery. */
    Model(int colS, int rowS) {

        init(colS, rowS);
    }

    /** Initializes a copy of MODEL. */
    Model(Model model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Model model) {
        if (this == model) {
            return;
        }
        this.cols = model.cols;
        this.rows = model.rows;
        this.listOfBoundaries = new ArrayList<>(model.listOfBoundaries);
        this.listOfCenters = new ArrayList<>(model.listOfCenters);
        this.listOfMarks = new ArrayList<>(model.listOfMarks);
        this.board = new ArrayList<>(model.board);
    }


    /** Sets the puzzle board size to COLS x ROWS, and clears it. */
    void init(int colS, int rowS) {
        this.cols = colS;
        this.rows = rowS;
        this.listOfMarks = new ArrayList<Integer>(xlim() * ylim());
        for (int i = 0; i < xlim() * ylim(); i += 1) {
            this.listOfMarks.add(0);
        }
        this.listOfCenters = new ArrayList<Place>();
        this.listOfBoundaries = new ArrayList<Place>();
        for (int y = 0; y < ylim(); y++) {
            this.listOfBoundaries.add(pl(0, y));
            this.listOfBoundaries.add(pl(xlim() - 1, y));
        }
        for (int x = 0; x < xlim(); x++) {
            this.listOfBoundaries.add(pl(x, 0));
            this.listOfBoundaries.add(pl(x, ylim() - 1));
        }
        this.board = new ArrayList<Place>();
        for (int y = 1; y < ylim() - 1; y += 2) {
            for (int x = 1; x < xlim() - 1; x += 2) {
                this.board.add(pl(x, y));
            }
        }
    }

    /** Making a constructor for Model type */

    /** Clears the board (removes centers, boundaries that are not on the
     *  periphery, and marked cells) without resizing. */
    void clear() {
        init(cols(), rows());
    }

    /** Returns the number of columns of cells in the board. */
    int cols() {
        return xlim() / 2;
    }

    /** Returns the number of rows of cells in the board. */
    int rows() {
        return ylim() / 2;
    }

    /** Returns the number of vertical edges and cells in a row. */
    int xlim() {
        return 2 * this.cols + 1;
    }

    /** Returns the number of horizontal edges and cells in a column. */
    int ylim() {
        return 2 * this.rows + 1;
    }

    /** Returns true iff (X, Y) is a valid cell. */
    boolean isCell(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim()
            && x % 2 == 1 && y % 2 == 1;
    }

    /** Returns true iff P is a valid cell. */
    boolean isCell(Place p) {
        return isCell(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a valid edge. */
    boolean isEdge(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim() && x % 2 != y % 2;
    }

    /** Returns true iff P is a valid edge. */
    boolean isEdge(Place p) {
        return isEdge(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a vertical edge. */
    boolean isVert(int x, int y) {
        return isEdge(x, y) && x % 2 == 0;
    }

    /** Returns true iff P is a vertical edge. */
    boolean isVert(Place p) {
        return isVert(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a horizontal edge. */
    boolean isHoriz(int x, int y) {
        return isEdge(x, y) && y % 2 == 0;
    }

    /** Returns true iff P is a horizontal edge. */
    boolean isHoriz(Place p) {
        return isHoriz(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a valid intersection. */
    boolean isIntersection(int x, int y) {
        return x % 2 == 0 && y % 2 == 0
            && x >= 0 && y >= 0 && x < xlim() && y < ylim();
    }

    /** Returns true iff P is a valid intersection. */
    boolean isIntersection(Place p) {
        return isIntersection(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a center. */
    boolean isCenter(int x, int y) {
        return isCenter(pl(x, y));
    }

    /** Returns true iff P is a center. */
    boolean isCenter(Place p) {
        if (listOfCenters.contains(p)) {
            return true;
        }
        return false;
    }

    /** Returns true iff (X, Y) is a boundary. */
    boolean isBoundary(int x, int y) {
        return isBoundary(pl(x, y));
    }

    /** Returns true iff P is a boundary. */
    boolean isBoundary(Place p) {
        if (listOfBoundaries.contains(p)) {
            return true;
        }
        return false;
    }

    /** Returns true iff the puzzle board is solved, given the centers and
     *  boundaries that are currently on the board. */
    boolean solved() {
        int total;
        total = 0;
        for (Place c : centers()) {
            HashSet<Place> r = findGalaxy(c);
            if (r == null) {
                return false;
            } else {
                total += r.size();
            }
        }
        return total == rows() * cols();
    }

    /** Finds cells reachable from CELL and adds them to REGION.  Specifically,
     *  it finds cells that are reachable using only vertical and horizontal
     *  moves starting from CELL that do not cross any boundaries and
     *  do not touch any cells that were initially in REGION. Requires
     *  that CELL is a valid cell. */
    private void accreteRegion(Place cell, HashSet<Place> region) {
        assert isCell(cell);
        if (region.contains(cell)) {
            return;
        }
        region.add(cell);
        for (int i = 0; i < 4; i += 1) {
            int dx = (i % 2) * (2 * (i / 2) - 1),
                dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
            if (!listOfBoundaries.contains(cell.move(dx, dy))) {
                accreteRegion(cell.move(2 * dx, 2 * dy), region);
            }
        }
    }

    /** Returns true iff REGION is a correctly formed galaxy. A correctly formed
     *  galaxy has the following characteristics:
     *      - is symmetric about CENTER,
     *      - contains no interior boundaries, and
     *      - contains no other centers.
     * Assumes that REGION is connected. */

    private boolean isGalaxy(Place center, HashSet<Place> region) {
        for (Place cell : region) {
            if (isCenter(cell) && cell != center) {
                return false;
            }
            List<Place> outbound = new ArrayList<Place>();

            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1),
                    dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place boundary = cell.move(dx, dy),
                    nextCell = cell.move(2 * dx, 2 * dy);
                if (!region.contains(nextCell)) {
                    outbound.add(cell.move(dx, dy));
                }
                if (listOfBoundaries.contains(boundary)
                        && !outbound.contains(boundary)) {
                    return false;
                }
            }
            if (!region.contains(opposing(center, cell))) {
                return false;
            }
        }
        return true;
    }




    /** Returns the biggest enclosed region around the center.
     * @param center this is a center */
    HashSet<Place> findEnclosed(Place center) {
        HashSet<Place> enclosedregion = new HashSet<>();
        if (isCell(center)) {
            accreteRegion(center, enclosedregion);
        } else if (isVert(center)) {
            accreteRegion(center.move(-1, 0), enclosedregion);
            accreteRegion(center.move(1, 0), enclosedregion);

        } else if (isHoriz(center)) {
            accreteRegion(center.move(0, 1), enclosedregion);
            accreteRegion(center.move(0, -1), enclosedregion);

        } else {
            accreteRegion(center.move(1, 1), enclosedregion);
            accreteRegion(center.move(-1, -1), enclosedregion);
            accreteRegion(center.move(-1, 1), enclosedregion);
            accreteRegion(center.move(1, -1), enclosedregion);

        }

        return enclosedregion;

    }

    /** Returns true if center is not placed at the center of a galaxy.
     * @param center center of the region
     * @param region the total region*/
    private boolean offcenter(Place center, HashSet<Place> region) {
        if (region == null) {
            return true;
        }
        for (Place cell : region) {
            if (!region.contains(opposing(center, cell))) {
                return true;
            }
        }
        return false;
    }

    /** Returns the galaxy containing CENTER that has the following
     *  characteristics:
     *      - encloses CENTER completely,
     *      - is symmetric about CENTER,
     *      - is connected,
     *      - contains no stray boundary edges, and
     *      - contains no other centers aside from CENTER.
     *  Otherwise, returns null. Requires that CENTER is not on the
     *  periphery. */

    HashSet<Place> findGalaxy(Place center) {
        HashSet<Place> galaxy = new HashSet<>();
        HashSet<Place> enclosedregion = new HashSet<>(findEnclosed(center));
        if (offcenter(center, enclosedregion)) {
            return null;
        }
        if (mark(center) != 0) {
            markAll(enclosedregion, 0);
        }


        Set<Place> search = new HashSet<>(maxUnmarkedRegion(center));
        for (Place cell: enclosedregion) {
            if (search.contains(cell)) {
                galaxy.add(cell);
            }
        }

        if (isGalaxy(center, galaxy)) {
            markAll(galaxy, 1);
            return galaxy;
        } else {
            return null;
        }
    }

    /** Returns the largest, unmarked region around CENTER with the
     *  following characteristics:
     *      - contains all cells touching CENTER,
     *      - consists only of unmarked cells,
     *      - is symmetric about CENTER, and
     *      - is contiguous.
     *  The method ignores boundaries and other centers on the current board.
     *  If there is no such region, returns the empty set. */
    Set<Place> maxUnmarkedRegion(Place center) {

        HashSet<Place> region = new HashSet<>();
        region.addAll(unmarkedContaining(center));
        markAll(region, 1);
        for (int i = 0; i < Math.max(cols, rows); i++) {
            List<Place> tempRegion = new ArrayList<Place>(region);
            region.addAll(unmarkedSymAdjacent(center, tempRegion));
            markAll(region, 1);
        }
        markAll(region, 0);
        return region;
    }

    /** Marks all properly formed galaxies with value V. Unmarks all cells that
     *  are not contained in any of these galaxies. Requires that V is greater
     *  than or equal to 0. */
    void markGalaxies(int v) {
        assert v >= 0;
        markAll(0);
        for (Place c : centers()) {
            HashSet<Place> region = findGalaxy(c);
            if (region != null) {
                markAll(region, v);
            }
        }
    }

    /** Toggles the presence of a boundary at the edge (X, Y). That is, negates
     *  the value of isBoundary(X, Y) (from true to false or vice-versa).
     *  Requires that (X, Y) is an edge. */
    void toggleBoundary(int x, int y) {
        if (isEdge(x, y)) {
            if (listOfBoundaries.contains(pl(x, y))) {
                listOfBoundaries.remove(pl(x, y));
            } else {
                listOfBoundaries.add(pl(x, y));
            }
        }
    }

    /** Places a center at (X, Y). Requires that X and Y are within bounds of
     *  the board. */
    void placeCenter(int x, int y) {
        placeCenter(pl(x, y));
    }

    /** Places center at P. */
    void placeCenter(Place p) {
        listOfCenters.add(p);
    }

    /** Returns the current mark on cell (X, Y), or -1 if (X, Y) is not a valid
     *  cell address. */
    int mark(int x, int y) {
        if (!isCell(x, y)) {
            return -1;
        }
        return this.listOfMarks.get(y * xlim() + x);
    }

    /** Returns the current mark on cell P, or -1 if P is not a valid cell
     *  address. */
    int mark(Place p) {
        return mark(p.x, p.y);
    }

    /** Marks the cell at (X, Y) with value V. Requires that V must be greater
     *  than or equal to 0, and that (X, Y) is a valid cell address.
     *
     *  We'll use an IntArray to store all the marks;
     *  The index of the array will indicate the Place Index,
     *  as calculated by "y * xlim() + x"
     *  For example in a 2x2 board first row will have indices 0 & 1,
     *  and the second 2 and 3*/
    void mark(int x, int y, int v) {
        if (!isCell(x, y)) {
            throw new IllegalArgumentException("bad cell coordinates");
        }
        if (v < 0) {
            throw new IllegalArgumentException("bad mark value");
        }
        this.listOfMarks.set(y * xlim() + x, v);
    }

    /** Marks the cell at P with value V. Requires that V must be greater
     *  than or equal to 0, and that P is a valid cell address. */
    void mark(Place p, int v) {
        mark(p.x, p.y, v);
    }

    /** Sets the marks of all cells in CELLS to V. Requires that V must be
     *  greater than or equal to 0. */
    void markAll(Collection<Place> cells, int v) {
        assert v >= 0;
        for (Place placeToMark : cells) {
            mark(placeToMark, v);
        }
    }

    /** Sets the marks of all cells to V. Requires that V must be greater than
     *  or equal to 0.
     *  Instead of for loop, we create a new list
     *  and then point our list_of_mark to it
     *  to speed up the runtime. */
    void markAll(int v) {
        assert v >= 0;
        int m = xlim() * ylim();
        List<Integer> L = new ArrayList<Integer>(Collections.nCopies(m, v));
        this.listOfMarks.clear();
        this.listOfMarks.addAll(L);
    }

    /** Returns the position of the cell that is opposite P using P0 as the
     *  center, or null if that is not a valid cell address. */
    Place opposing(Place p0, Place p) {

        int xDif = p0.x - p.x, yDif = p0.y - p.y;
        if (p0.x + xDif < -1) {
            return null;
        }
        if (p0.y + yDif < -1) {
            return null;
        }
        Place p1 = pl(p0.x + xDif, p0.y + yDif);

        if (!isCell(p1.x, p1.y)) {
            return null;
        }
        return p1;
    }


    /** Returns a list of all cells "containing" PLACE if all of the cells are
     *  unmarked. A cell, c, "contains" PLACE if
     *      - c is PLACE itself,
     *      - PLACE is a corner of c, or
     *      - PLACE is an edge of c.
     *  Otherwise, returns an empty list. */
    List<Place> unmarkedContaining(Place place) {
        if (isCell(place)) {
            if (mark(place) == 0) {
                return asList(place);
            }
        } else if (isVert(place)) {
            Place left = place.move(-1, 0),
                  right = place.move(1, 0);
            if (mark(left) == 0 && mark(right) == 0) {
                return asList(left, right);
            }
        } else if (isHoriz(place)) {
            Place up = place.move(0, -1),
                  down = place.move(0, 1);
            if (mark(up) == 0 && mark(down) == 0) {
                return asList(up, down);
            }
        } else {
            Place upRight = place.move(1, 1),
                  downLeft = place.move(-1, -1),
                  leftUp = place.move(-1, 1),
                  rightDown = place.move(1, -1);
            if (mark(leftUp) == 0
                    && mark(rightDown) == 0
                    && mark(upRight) == 0
                    && mark(downLeft) == 0) {
                return asList(upRight, downLeft, leftUp, rightDown);
            }
        }

        return Collections.emptyList();
    }

    /** Returns a list of all cells, c, such that:
     *      - c is unmarked,
     *      - The opposite cell from c relative to CENTER exists and
     *        is unmarked, and
     *      - c is vertically or horizontally adjacent to a cell in REGION.
     *  CENTER and all cells in REGION must be valid cell positions.
     *  Each cell appears at most once in the resulting list. */
    List<Place> unmarkedSymAdjacent(Place center, List<Place> region) {
        ArrayList<Place> result = new ArrayList<>();
        for (Place r : region) {
            assert isCell(r);
            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1),
                        dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place p = r.move(2 * dx, 2 * dy);
                Place opp = opposing(center, p);
                if (p != null && opp != null) {
                    if (mark(p) == 0 && mark(opp) == 0) {
                        result.add(p);
                    }
                }

            }

        }
        return result;
    }

    /** Returns an unmodifiable view of the list of all centers. */
    List<Place> centers() {
        return Collections.unmodifiableList(listOfCenters);
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        int w = xlim(), h = ylim();
        for (int y = h - 1; y >= 0; y -= 1) {
            for (int x = 0; x < w; x += 1) {
                boolean cent = isCenter(x, y);
                boolean bound = isBoundary(x, y);
                if (isIntersection(x, y)) {
                    out.format(cent ? "o" : " ");
                } else if (isCell(x, y)) {
                    if (cent) {
                        out.format(mark(x, y) > 0 ? "O" : "o");
                    } else {
                        out.format(mark(x, y) > 0 ? "*" : " ");
                    }
                } else if (y % 2 == 0) {
                    if (cent) {
                        out.format(bound ? "O" : "o");
                    } else {
                        out.format(bound ? "=" : "-");
                    }
                } else if (cent) {
                    out.format(bound ? "O" : "o");
                } else {
                    out.format(bound ? "I" : "|");
                }
            }
            out.format("%n");
        }
        return out.toString();
    }
 /** Representation */

    /** Coloumns and Rows. */
    private int cols, rows;

    /** List of Centers. */
    private List<Place> listOfCenters;

    /** Initializes an empty ArrayList that will contain marks on the board. */
    private List<Integer> listOfMarks;

    /** A list containing places that are boundaries. */
    private List<Place> listOfBoundaries;

    /** A list containing places on the board. */
    private List<Place> board;

}
