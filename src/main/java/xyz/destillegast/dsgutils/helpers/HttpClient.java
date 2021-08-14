package xyz.destillegast.dsgutils.helpers;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by DeStilleGast 6-6-2021
 */
public class HttpClient {

    private final Map<String, String> customProperties = new HashMap<>();

    public HttpClient() {
        this.customProperties.put("User-Agent", "DSG-Utils/1");
    }

    public String get(String url) throws IOException {
        return get(new URL(url));
    }

    public String get(URL url) throws IOException {
        if(Bukkit.isPrimaryThread()) throw new IOException("Networking on main thread");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        this.customProperties.keySet().forEach(key -> connection.setRequestProperty(key, this.customProperties.get(key)));


        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String readBuffer;
            StringBuilder response = new StringBuilder();
            while ((readBuffer = inputReader.readLine()) != null) {
                response.append(readBuffer);
                response.append('\n');
            }
            inputReader.close();

            return response.toString();
        }

        return null;
    }

    public String post(String url, Map<String, Object> postData) throws IOException {
        return post(new URL(url), postData);
    }

    public String post(URL url, Map<String, Object> postData) throws IOException {
        if(Bukkit.isPrimaryThread()) throw new IOException("Networking on main thread");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        this.customProperties.keySet().forEach(key -> connection.setRequestProperty(key, this.customProperties.get(key)));

        connection.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(mapToString(postData));

        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String readBuffer;
            StringBuilder response = new StringBuilder();

            while ((readBuffer = inputReader.readLine()) != null) {
                response.append(readBuffer);
                response.append('\n');
            }
            inputReader.close();

            return response.toString();
        }

        return null;
    }

    private String mapToString(final Map<String, Object> map) {
        return map.keySet().stream().map(it -> {
            try {
                return it + "=" + URLEncoder.encode(map.get(it).toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }).collect(Collectors.joining("&"));
    }

    public void setRequestProperty(String key, String value){
        this.customProperties.put(key, value);
    }
}
