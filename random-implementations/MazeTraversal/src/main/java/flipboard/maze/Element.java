package flipboard.maze;

/**
 * A data-structure to store the cell (row, column) information of the maze.
 * Created by Aravind Selvan on 10/14/14.
 */
public class Element {
    private int row;
    private int column;

    public Element(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
