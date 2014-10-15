package flipboard.maze;

/**
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
