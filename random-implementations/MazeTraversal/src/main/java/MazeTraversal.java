import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
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
    static String startURL, rawURl;
    private static int adjacentX, adjacentY;
    static boolean[][] isVisited;

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/home/goku/Dropbox/algorithms-and-coding/random-implementations/MazeTraversal/src/main/java/inputURLs.txt"));
            startURL = bufferedReader.readLine();
            while (startURL != null) {
                int indexToExtractRawIURL = startURL.indexOf("&x=");
                if (-1 != indexToExtractRawIURL) {
                    rawURl = startURL.substring(0, indexToExtractRawIURL + 3);
                }
                URL url = new URL(startURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                /*httpConnection.connect();
                httpConnection.disconnect();*/

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
                isVisited = new boolean[1000][1000]; //todo optimize
                isVisited[0][0] = true;
                StringBuilder result = findMazePaths(jsonParser, httpConnection, isVisited, new Element(adjacentX, adjacentY), new StringBuilder(letter));

//                https://challenge.flipboard.com/check?s=5877569860164052107.9&guess=xxwdmmxmoxtiyqarctvvwqujne
                startURL = bufferedReader.readLine();            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) bufferedReader.clocse();
        }
    }

    private static StringBuilder findMazePaths(JsonParser jsonParser, HttpURLConnection httpConnection, boolean[][] isVisited, Element end, StringBuilder r) throws IOException {

        if (isVisited[end.getRow()][end.getColumn()] == true) return null;

        StringBuilder result = new StringBuilder(r);
        Element[] nextElements = new Element[4];
        int currentX = end.getRow();
        int currentY = end.getColumn();
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