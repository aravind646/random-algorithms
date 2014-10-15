package flipboard.maze;

/**
 * Created by Aravind Selvan on 10/14/14.
 */
public class MazePathConstantsAndFields {
    public static final int numberOfJSONMazeElements = 4;
    public static final String XAttribute = "x";
    public static final String YAttribute = "y";
    public static final String letterAttribute = "letter";
    public static final String adjacentAttribute = "adjacent";
    public static final String successAttribute = "success";
    public static final String endAttribute = "end";
    public static final String isTRUE = "true";
    public static final String emptyString = "";
    public static final String checkURLPrefix = "https://challenge.flipboard.com/check?";
    public static final String checkURLGuess = "&guess=";
    public static final String sIndexer = "?s=";
    public static final String xIndexer = "&x=";
    public static final String yIndexer = "&y=";
    public static final String malformedStartURLError = "ERROR: Given start URL ";
    public static final String malformedLetterAttributeError = "ERROR: 'letter' element parsing is malformed in the URL ";
    public static final String malformedEndAttributeError = "ERROR: 'end' element parsing is malformed in the URL ";
    public static final String jsonArrayError = "ERROR: 'letter' element parsing is malformed (or) of size 0 in the URL ";
    public static final String mazePathNotFound = "Info: No path found for the start URL ";
    public static final String mazePathFound = "SUCCESS: Path found for the start URL ";
    public static final String malformedURLError = "ERROR: Parsed URL ";
    public static final String checkSuccess = "The check endpoint returns a successful response for ";
    public static final String checkUnSuccess = "The check endpoint returns an unsuccessful response using ";
}
