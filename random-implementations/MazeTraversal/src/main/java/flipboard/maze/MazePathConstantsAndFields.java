package flipboard.maze;

/**
 * Helper class to store the constants and fields, used by MazeTraversal.java.
 * Created by Aravind Selvan on 10/14/14.
 */
public class MazePathConstantsAndFields {
    protected static final int numberOfNeighbours = 4;
    protected static final String XAttribute = "x";
    protected static final String YAttribute = "y";
    protected static final String letterAttribute = "letter";
    protected static final String adjacentAttribute = "adjacent";
    protected static final String successAttribute = "success";
    protected static final String endAttribute = "end";
    protected static final String isTRUE = "true";
    protected static final String emptyString = "";
    protected static final String checkURLPrefix = "https://challenge.flipboard.com/check?";
    protected static final String checkURLSuffix = "&guess=";
    protected static final String sIndexer = "?s=";
    protected static final String xIndexer = "&x=";
    protected static final String yIndexer = "&y=";
    protected static final String calculatingMazePaths = "INFO: Calculating maze paths";
    protected static final String malformedStartURLError = "ERROR: Given start URL ";
    protected static final String malformedLetterAttributeError = "ERROR: 'letter' element parsing is malformed in the URL ";
    protected static final String malformedEndAttributeError = "ERROR: 'end' element parsing is malformed in the URL ";
    protected static final String jsonArrayError = "ERROR: 'letter' element parsing is malformed (or) of size 0 in the URL ";
    protected static final String mazePathNotFound = "INFO: No path found for the start URL ";
    protected static final String mazePathFound = "Path found for the start URL ";
    protected static final String malformedURLError = "ERROR: Parsed URL ";
    protected static final String checkSuccess = "SUCCESS: The check endpoint returns a successful response for ";
    protected static final String checkUnSuccess = "FAILURE: The check endpoint returns an unsuccessful response using ";
}
