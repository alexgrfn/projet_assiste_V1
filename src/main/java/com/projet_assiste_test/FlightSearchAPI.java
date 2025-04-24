package com.projet_assiste_test;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

// TEST DE L'API
// LA FONCTION MARCHE, ON DOIT JUSTE SAISIR UNE CLE VALIDE à LA LIGNE 13

public class FlightSearchAPI {

    private static final String API_KEY = "YOUR_API_KEY_HERE"; //REMPLACEZ PAR SA CLE
    private static final String API_URL = "https://test.api.amadeus.com/v2/shopping/flight-offers";

    public static void rechercherVols() {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL).newBuilder();
        urlBuilder.addQueryParameter("originLocationCode", "CDG");
        urlBuilder.addQueryParameter("destinationLocationCode", "JFK");
        urlBuilder.addQueryParameter("departureDate", "2025-06-01");
        urlBuilder.addQueryParameter("adults", "1");

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Réponse API :\n" + response.body().string());
            } else {
                System.err.println("Erreur API : Code " + response.code());
            }
        } catch (IOException e) {
            System.err.println("Erreur de connexion API : " + e.getMessage());
        }
    }
}