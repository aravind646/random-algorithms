import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by Aravind Selvan on 10/10/14.
 */
public class MazeTraversal {
    private static String mazeIdentifier = "126201787089217298.8";//"123456.5"; //529793739585927847.23
    private static String startURL = "https://challenge.flipboard.com/step?s=" + mazeIdentifier + "&x=0&y=0";
    private static String rawURl = "https://challenge.flipboard.com/step?s=" + mazeIdentifier + "&x=";
    private static boolean[][] isVisited = new boolean[1000][1000]; //todo optimize

    public static void main(String[] args) throws IOException {
        URL url = new URL(startURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        JsonParser jsonParser = new JsonParser();
        String mazePath = null;
        mazePath = findMazePaths(jsonParser, isVisited, new Element(0, 0), new StringBuilder()).toString();
        if (null != mazePath) {
            System.out.println("Path for the given /start URL:" + startURL + "\n" + mazePath);
        }
        else {
            System.out.println("No path found for the given /start URL:" + startURL);
        }
    }

    private static StringBuilder findMazePaths(JsonParser jsonParser, boolean[][] isVisited, Element end, StringBuilder path) throws IOException {

        try {
            if (isVisited[end.getRow()][end.getColumn()] == true) {
                return null;
            }

            StringBuilder result = new StringBuilder(path);
            Element[] nextElements = new Element[4];
            int currentX = end.getRow();
            int currentY = end.getColumn();
            String constructedURLString = URLConstructor(currentX, currentY);
            URL url = new URL(constructedURLString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            JsonObject jsonObject = getJsonObject(jsonParser, httpConnection);
            if (null == jsonObject) {
                throw new Exception("ERROR: JSON parsing error for the URL:" + constructedURLString);
            }
            String letter = jsonObject.get("letter").getAsString();
            if (null == letter || letter.equals("")) {
                throw new Exception("ERROR: 'letter' element parsing error for the URL:" + constructedURLString);
            }

            String isEnd = jsonObject.get("end").getAsString(); 
            if (null == isEnd || isEnd.equals("")) {
                throw new Exception("ERROR: 'end' element parsing error for the URL:" + constructedURLString);
            }

            //System.out.println("Current X=" + currentX + ", Y=" + currentY);
            result.append(letter);
            if (isEnd.equals("true")) {
                //System.out.println(result);
                return result;
            }

            JsonArray jsonArray = jsonObject.get("adjacent").getAsJsonArray();

            int nextX, nextY, index = 0;
            if (null != jsonArray) {
                Iterator<JsonElement> nextCells = jsonArray.iterator();
                while (nextCells.hasNext()) {
                    JsonObject current = nextCells.next().getAsJsonObject(); //todo add error check
                    nextX = current.get("x").getAsInt();
                    nextY = current.get("y").getAsInt();
                    nextElements[index] = new Element(nextX, nextY);
                    ++index;
                }
            }
            isVisited[currentX][currentY] = true;
            for (Element element : nextElements) {
                if (element != null) {
                    StringBuilder foundPath = findMazePaths(jsonParser, isVisited, element, result);
                    if (foundPath != null) {
                        return foundPath;
                    }
                }
            }

            return result;

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    private static String URLConstructor(int x, int y) {
        String adjacentURL = new StringBuilder(rawURl).append(x).append("&y=").append(y).toString();
        return adjacentURL;
    }


    private static JsonObject getJsonObject(JsonParser jsonParser, HttpURLConnection httpConnection) throws IOException {
        try {
            JsonElement node = jsonParser.parse(new InputStreamReader((InputStream) httpConnection.getContent()));
            JsonObject jsonObject = node.getAsJsonObject();
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class Element {
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
}
