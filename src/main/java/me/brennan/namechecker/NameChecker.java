package me.brennan.namechecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 1/27/2021
 **/
public class NameChecker {
    private final static Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) throws Exception {
        final List<String> unused = new LinkedList<>();

        Files.readAllLines(new File("usernames.txt").toPath()).forEach(username -> {
            try {
                boolean taken = isTaken(username);

                if(!taken) {
                    unused.add(username);
                }

                System.out.printf("%s : %s%n", username, taken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        final FileWriter fileWriter = new FileWriter("unused.txt");
        unused.forEach(uname -> {
            try {
                fileWriter.write(uname + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fileWriter.close();
    }

    private static boolean isTaken(String username) throws Exception {
        final URL url = new URL(String.format("http://api.hiven.io/v1/users/%s", username));
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1\t");

        final StringBuilder stringBuilder = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            stringBuilder.append(reader.readLine());
        }

        final JsonObject jsonObject = GSON.fromJson(stringBuilder.toString(), JsonObject.class);
        return jsonObject.get("success").getAsBoolean();
    }

}
