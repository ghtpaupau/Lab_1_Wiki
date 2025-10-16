import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

import com.google.gson.Gson;

public class WikiSearchResult {
    private Query query;
    
    public Query getQuery() {
        return this.query;
    }

    public static String makeFullUrl(String request, String standardUrl) {
        String urlParameter = "?action=query&list=search&format=json&srsearch=";
        String encodeRequest;
        try {
            encodeRequest = URLEncoder.encode(request, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("Ошибка кодировки запроса: " + e.getMessage());
            return "";

        }
        return standardUrl + urlParameter + encodeRequest;
    }

    public static StringBuilder httpRequest(String fullUrl) {
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "MyApplication/1.0 (infodamir4ik@gmail.com)");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            int serverResponse = connection.getResponseCode();
            if (serverResponse == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder gsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    gsonResponse.append(line);
                }
                reader.close();
                return gsonResponse;
            } else if (serverResponse == 404) {
                System.out.println("Статья не найдена (404)");
                return null;

            } else if (serverResponse == 500) {
                System.out.println("Ошибка сервера Википедии (500)");
                return null;

            } else {
                System.out.println("Неизвестная ошибка: " + serverResponse);
                return null;
            }
        } catch (java.net.MalformedURLException e) {
            System.out.println("Ошибка в отправке запроса: " + e.getMessage());
            return null;
        } catch (java.io.IOException e) {
            System.out.println("Ошибка при установке соединения: " + e.getMessage());
            return null;
        }
    }

    public static List<SearchItems> gsonParsing(StringBuilder gsonResponse) {
        Gson gson = new Gson();
        WikiSearchResult wikiSearchResult = gson.fromJson(gsonResponse.toString(), WikiSearchResult.class);
        List<SearchItems> result = wikiSearchResult.getQuery().getSearch();
        return result;
    }

    public static void browsSelectedArticle(String articleUrl) {
        try {
            Desktop.getDesktop().browse(new URI(articleUrl));
        } catch (java.net.URISyntaxException e) {
            System.out.println("Ошибка в формате ссылки: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.out.println("Ошибка в открытии браузера: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }
}