import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by Aravind Selvan on 10/10/14.
 */
public class MazeTraversal {
    private static String startURL = "https://challenge.flipboard.com/step?s=529793739585927847.23&x=0&y=0";
    private static int startX, startY;

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
                startX = current.get("x").getAsInt();
                startY = current.get("y").getAsInt();
            }
        }

        int l = 0;
    }

    private static JsonObject getJsonObject(JsonParser jsonParser, HttpURLConnection httpConnection, String url) throws IOException {
        JsonElement node = jsonParser.parse(new InputStreamReader((InputStream) httpConnection.getContent()));
        JsonObject jsonObject = node.getAsJsonObject();
        return jsonObject;

    }
}
