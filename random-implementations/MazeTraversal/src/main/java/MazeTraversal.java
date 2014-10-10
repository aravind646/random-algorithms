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
import java.util.List;

/**
 * Created by Aravind Selvan on 10/10/14.
 */
public class MazeTraversal {
    private static String mazeIdentifier = "123456.5"; //529793739585927847.23
    private static String startURL = "https://challenge.flipboard.com/step?s=" + mazeIdentifier + "&x=0&y=0";
    private static String rawURl = "https://challenge.flipboard.com/step?s=" + mazeIdentifier + "&x=";
    private static int adjacentX, adjacentY;
    private static boolean[][] isVisited = new boolean[1000][1000]; //todo optimize

    public static void main(String[] args) throws IOException {

        URL url = new URL(startURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = getJsonObject(jsonParser, httpConnection, startURL);
        String letter = jsonObject.get("letter").getAsString();
        String isEnd = jsonObject.get("end").getAsString();
        JsonArray jsonArray = jsonObject.get("adjacent").getAsJsonArray();

        if (null != jsonArray) { //todo don't use loop, just one element
            Iterator<JsonElement> nextCells = jsonArray.iterator();
            while (nextCells.hasNext()) {
                JsonObject current = nextCells.next().getAsJsonObject();
                adjacentX = current.get("x").getAsInt();
                adjacentY = current.get("y").getAsInt();
            }
        }
        isVisited[0][0] = true;
        findMazePaths(jsonParser, httpConnection, isVisited, new Element(adjacentX, adjacentY), new StringBuilder(letter));


        int l = 0;
    }

    private static StringBuilder findMazePaths(JsonParser jsonParser, HttpURLConnection httpConnection, boolean[][] isVisited, Element end, StringBuilder r) throws IOException {

        if (isVisited[end.row][end.column] == true) return null;

        StringBuilder result = new StringBuilder(r);
        Element[] nextElements = new Element[4];
        int currentX = end.row;
        int currentY = end.column;
        String currentURL = URLConstructor(currentX, currentY);
        URL url = new URL(currentURL);
        httpConnection = (HttpURLConnection) url.openConnection();
        JsonObject jsonObject = getJsonObject(jsonParser, httpConnection, currentURL);
        String letter = jsonObject.get("letter").getAsString();
        String isEnd = jsonObject.get("end").getAsString();

        System.out.println("Current X=" + currentX + ", Y=" + currentY);
        result.append(letter);
        if (isEnd.equals("true")) {
            System.out.println(result);
            return result;
        }

        JsonArray jsonArray = jsonObject.get("adjacent").getAsJsonArray();


        int nextX, nextY, index = 0;
        if (null != jsonArray) {
            Iterator<JsonElement> nextCells = jsonArray.iterator();
            while (nextCells.hasNext()) {
                JsonObject current = nextCells.next().getAsJsonObject();
                nextX = current.get("x").getAsInt();
                nextY = current.get("y").getAsInt();
                nextElements[index] = new Element(nextX, nextY);
                ++index;
            }
        }
        isVisited[currentX][currentY] = true;
        int i = 0;
        for (Element element : nextElements) {
            /*System.out.println(i);
            ++i;*/
            if (element != null) {
                StringBuilder paths = findMazePaths(jsonParser, httpConnection, isVisited, element, result);
                if (paths != null) {

                }
            }

        }


        return result;
    }

    private static String URLConstructor(int x, int y) {
        String adjacentURL = new StringBuilder(rawURl).append(x).append("&y=").append(y).toString();
        return adjacentURL;
    }


    private static JsonObject getJsonObject(JsonParser jsonParser, HttpURLConnection httpConnection, String url) throws IOException {
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

        public Element(int x, int y) {
            row = x;
            column = y;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        private int column;

    }
}
