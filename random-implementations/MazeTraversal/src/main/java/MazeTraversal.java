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
    private static final String XAttribute = "x";
    private static final String YAttribute = "y";
    private static final String letterAttribute = "letter";
    private static final String adjacentAttribute = "adjacent";
    private static final String successAttribute = "success";
    static boolean[][] isVisited;

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/home/goku/Dropbox/algorithms-and-coding/random-implementations/MazeTraversal/src/main/java/inputURLs.txt"));
            startURL = bufferedReader.readLine();
            while (startURL != null) {
                int indexCheckURL = startURL.indexOf("?s=");
                int indexToExtractRawIURL = startURL.indexOf("&x=");
                if (-1 != indexToExtractRawIURL) {
                    rawURl = startURL.substring(0, indexToExtractRawIURL + 3);
                }
                URL url = new URL(startURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();


                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = getJsonObject(jsonParser, httpConnection);
                if (null == jsonObject) {
                    //throw new Exception("ERROR: JSON parsing error for the URL:" + constructedURLString);
                } else {
                    String letter = jsonObject.get(letterAttribute).getAsString();
                    JsonArray jsonArray = jsonObject.get(adjacentAttribute).getAsJsonArray();

                    if (null != jsonArray) { //todo don't use loop, just one element
                        Iterator<JsonElement> nextCells = jsonArray.iterator();
                        while (nextCells.hasNext()) {
                            JsonObject current = nextCells.next().getAsJsonObject();
                            adjacentX = current.get(XAttribute).getAsInt();
                            adjacentY = current.get(YAttribute).getAsInt();
                        }
                    }
                    isVisited = new boolean[1000][1000]; //todo optimize
                    isVisited[0][0] = true;
                    StringBuilder mazePath = findMazePaths(jsonParser, httpConnection, isVisited, new Element(adjacentX, adjacentY), new StringBuilder(letter));
                    if (null == mazePath) {
                        System.out.println("No path found for the given /sOtart URL:" + startURL);

                    } else {
                        System.out.println("Path for the given /start URL:" + startURL + "\n" + mazePath);
                        String checkURL = startURL.substring(indexCheckURL + 1, indexToExtractRawIURL);
                        String toCheckURL = "https://challenge.flipboard.com/check?" + checkURL +
                                "&guess=" + mazePath;
                        url = new URL(toCheckURL);
                        httpConnection = (HttpURLConnection) url.openConnection();
                        jsonParser = new JsonParser();
                        JsonElement node = jsonParser.parse(new InputStreamReader((InputStream) httpConnection.getContent()));
                        jsonObject = node.getAsJsonObject();
                        letter = jsonObject.get(successAttribute).getAsString();
                        System.out.println(letter);
                    }
                }
                startURL = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) bufferedReader.close();
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
        JsonObject jsonObject = getJsonObject(jsonParser, httpConnection);
        String letter = jsonObject.get(letterAttribute).getAsString();
        String isEnd = jsonObject.get("end").getAsString();

        //System.out.println("Current X=" + currentX + ", Y=" + currentY);
        result.append(letter);
        if (isEnd.equals("true")) {
            return result;
        }

        JsonArray jsonArray = jsonObject.get(adjacentAttribute).getAsJsonArray();
        int nextX, nextY, index = 0;
        if (null != jsonArray) {
            Iterator<JsonElement> nextCells = jsonArray.iterator();
            while (nextCells.hasNext()) {
                JsonObject current = nextCells.next().getAsJsonObject();
                nextX = current.get(XAttribute).getAsInt();
                nextY = current.get(YAttribute).getAsInt();
                nextElements[index] = new Element(nextX, nextY);
                ++index;
            }
        }
        isVisited[currentX][currentY] = true;
        StringBuilder paths = null;
        for (Element element : nextElements) {
            if (element != null) {
                paths = findMazePaths(jsonParser, httpConnection, isVisited, element, result);
                if (paths != null) {
                    return paths;
                }
            }
        }

        return paths;
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