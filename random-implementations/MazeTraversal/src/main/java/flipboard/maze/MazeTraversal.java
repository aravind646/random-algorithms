package flipboard.maze;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Main class that performs the traversal of the given maze using a recursive DFS.
 * Created by Aravind Selvan on 10/14/14.
 * <p/>
 * Usage (1): java -jar MazeTraversal.jar input_file_path output_ file_path Program arguments: (1)
 * input_file_path - reads from the input_file_path (args[0]) and the default behaviour is to look
 * in the current working directory. (2) output_file_path - writes to the output_file_path (args[1])
 * and the default behaviour is to create/overwrite the file in the current working directory.
 * <p/>
 * The input file contains the list of start URLs (separated by new line), the program calculates
 * the maze path until the values from the input file is exhausted.
 * The JSON parsing is done with the help of 'gson' library.
 */
public class MazeTraversal {
    static String startURL, rawURl;
    private static HashMap<Integer, Boolean> isVisited;
    private static String inputFilePath, outputFilePath;
    private static StringBuilder flushToOutputFile;

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = null;
        PrintWriter writer = null;
        try {
            printUsage();
            if (args.length == 2) {
                inputFilePath = args[0];
                outputFilePath = args[1];
            } else {
                inputFilePath = new StringBuilder(System.getProperty("user.dir")).append("/input.txt").toString();
                outputFilePath = new StringBuilder(System.getProperty("user.dir")).append("/output.txt").toString();
            }
            bufferedReader = new BufferedReader(new FileReader(inputFilePath));
            writer = new PrintWriter(outputFilePath, "UTF-8");
            flushToOutputFile = new StringBuilder();
            startURL = bufferedReader.readLine();
            System.out.println(MazePathConstantsAndFields.calculatingMazePaths);
            while (startURL != null) {
                int indexCheckURL = startURL.indexOf(MazePathConstantsAndFields.sIndexer);
                int indexToExtractRawIURL = startURL.indexOf(MazePathConstantsAndFields.xIndexer);
                if (-1 != indexToExtractRawIURL) {
                    rawURl = startURL.substring(0, indexToExtractRawIURL + 3);
                } else {
                    throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedStartURLError).append(startURL).append(" is malformed").toString());
                }
                URL url = new URL(startURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = getJsonObject(jsonParser, httpConnection);
                if (null == jsonObject) {
                    throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedStartURLError).append(startURL).append(" is malformed").toString());
                }
                String letter = jsonObject.get(MazePathConstantsAndFields.letterAttribute).getAsString();
                if (null == letter || letter.equals(MazePathConstantsAndFields.emptyString)) {
                    throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedLetterAttributeError).append(startURL).toString());
                }
                JsonArray jsonArray = jsonObject.get(MazePathConstantsAndFields.adjacentAttribute).getAsJsonArray();
                int adjacentX = 0, adjacentY = 0;
                if (null == jsonArray || jsonArray.size() == 0) {
                    throw new Exception(new StringBuilder(MazePathConstantsAndFields.jsonArrayError).append(startURL).toString());
                }
                Iterator<JsonElement> nextCells = jsonArray.iterator();
                while (nextCells.hasNext()) {
                    JsonObject current = nextCells.next().getAsJsonObject();
                    adjacentX = current.get(MazePathConstantsAndFields.XAttribute).getAsInt();
                    adjacentY = current.get(MazePathConstantsAndFields.YAttribute).getAsInt();
                }
                isVisited = new HashMap<Integer, Boolean>();
                isVisited.put(0, true);
                StringBuilder mazePath = findMazePath(jsonParser, isVisited, new Element(adjacentX, adjacentY), new StringBuilder(letter));
                String currentOutput;
                if (null == mazePath) {
                    currentOutput = new StringBuilder(MazePathConstantsAndFields.mazePathNotFound).append(startURL).toString();
                    System.out.println(currentOutput);
                    flushToOutputFile.append(currentOutput).append("\n");
                } else {
                    currentOutput = new StringBuilder(MazePathConstantsAndFields.mazePathFound).append(startURL).append("\nMaze Path:").append(mazePath).toString();
                    System.out.println(currentOutput);
                    flushToOutputFile.append(currentOutput).append("\n");
                    String mazeIdentifier = startURL.substring(indexCheckURL + 1, indexToExtractRawIURL);
                    String checkURL = new StringBuilder(MazePathConstantsAndFields.checkURLPrefix).append(mazeIdentifier).append(MazePathConstantsAndFields.checkURLSuffix).append(mazePath).toString();
                    url = new URL(checkURL);
                    httpConnection = (HttpURLConnection) url.openConnection();
                    jsonParser = new JsonParser();
                    JsonElement node = jsonParser.parse(new InputStreamReader((InputStream) httpConnection.getContent()));
                    JsonObject jsonObject1 = node.getAsJsonObject();
                    letter = jsonObject1.get(MazePathConstantsAndFields.successAttribute).getAsString();
                    if (null == letter || letter.equals(MazePathConstantsAndFields.emptyString)) {
                        throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedLetterAttributeError).append(startURL).toString());
                    }
                    if (letter.equals(MazePathConstantsAndFields.isTRUE)) {
                        currentOutput = new StringBuilder(MazePathConstantsAndFields.checkSuccess).append(checkURL).toString();
                        System.out.println(currentOutput);
                        flushToOutputFile.append(currentOutput).append("\n");
                    } else {
                        currentOutput = new StringBuilder(MazePathConstantsAndFields.checkUnSuccess).append(checkURL).toString();
                        System.out.println(currentOutput);
                        flushToOutputFile.append(currentOutput).append("\n");
                    }
                }
                System.out.println();
                flushToOutputFile.append("\n");
                startURL = bufferedReader.readLine();
            }
            writer.print(flushToOutputFile);
            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) bufferedReader.close();
            if (null != writer) writer.close();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:\njava -jar MazeTraversal.jar input_file_path output_file_path\n" +
                "Program arguments: \n" +
                "(1) input_file_path - reads from the input_file_path (args[0]) and \n" +
                "the default behaviour is to look in the current working directory.   \n" +
                "(2) output_file_path - writes to the output_file_path (args[1]) and \n" +
                "the default behaviour is to create/overwrite the file in the current working directory.\n" +
                "The input file contains the list of start URLs (separated by new line), the program calculates \n" +
                "the maze path until the values from the input file is exhausted.\n");
    }

    private static StringBuilder findMazePath(JsonParser jsonParser, HashMap<Integer, Boolean> isHash, Element end, StringBuilder inputString) throws Exception {

        Boolean checkVisited = isHash.get((Integer.parseInt(end.getRow() + MazePathConstantsAndFields.emptyString + end.getColumn())));
        if (null != checkVisited && checkVisited == true) {
            return null;
        }

        StringBuilder result = new StringBuilder(inputString);
        Element[] nextElements = new Element[MazePathConstantsAndFields.numberOfNeighbours];
        int currentX = end.getRow();
        int currentY = end.getColumn();
        String currentURL = URLConstructor(currentX, currentY);
        URL url = new URL(currentURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        JsonObject jsonObject = getJsonObject(jsonParser, httpConnection);
        if (null == jsonObject) {
            throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedURLError).append(currentURL).append(" is malformed").toString());
        } else {
            String letter = jsonObject.get(MazePathConstantsAndFields.letterAttribute).getAsString();
            if (null == letter || letter.equals(MazePathConstantsAndFields.emptyString)) {
                throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedLetterAttributeError).append(currentURL).toString());
            } else {
                String isEnd = jsonObject.get(MazePathConstantsAndFields.endAttribute).getAsString();
                if (null == letter || letter.equals(MazePathConstantsAndFields.emptyString)) {
                    throw new Exception(new StringBuilder(MazePathConstantsAndFields.malformedEndAttributeError).append(currentURL).toString());
                } else {
                    result.append(letter);
                    if (isEnd.equals(MazePathConstantsAndFields.isTRUE)) {
                        return result;
                    }

                    JsonArray jsonArray = jsonObject.get(MazePathConstantsAndFields.adjacentAttribute).getAsJsonArray();
                    int nextX, nextY, index = 0;
                    if (null != jsonArray) {
                        Iterator<JsonElement> nextCells = jsonArray.iterator();
                        while (nextCells.hasNext()) {
                            JsonObject current = nextCells.next().getAsJsonObject();
                            nextX = current.get(MazePathConstantsAndFields.XAttribute).getAsInt();
                            nextY = current.get(MazePathConstantsAndFields.YAttribute).getAsInt();
                            nextElements[index++] = new Element(nextX, nextY);
                        }
                    }
                    isHash.put((Integer.parseInt(currentX + MazePathConstantsAndFields.emptyString + currentY)), true);
                    StringBuilder path = null;
                    for (Element element : nextElements) {
                        if (element != null) {
                            path = findMazePath(jsonParser, isHash, element, result);
                            if (path != null) {
                                return path;
                            }
                        }
                    }

                    return path;
                }
            }
        }
    }

    private static String URLConstructor(int x, int y) {
        String adjacentURL = new StringBuilder(rawURl).append(x).append(MazePathConstantsAndFields.yIndexer).append(y).toString();
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
}