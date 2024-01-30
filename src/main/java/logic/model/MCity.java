package logic.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;

public class MCity {

    private String field;

    private static final String API_BASE_URL = "https://axqvoqvbfjpaamphztgd.functions.supabase.co";  // Sostituisci con il tuo URL di base dell'API

    private HttpClient httpClient = HttpClient.newHttpClient();
    private final Logger logger = Logger.getLogger("NightPlan");

    public CompletableFuture<Void> getCitiesByProvince(String provincia) {
        String apiUrl = API_BASE_URL + "/comuni/provincia/" + provincia;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        // da rimuovere System.out.println("" + provincia);

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200 && !response.body().isEmpty()) {
                        // La richiesta è andata a buon fine, stampa il JSON nel logger
                        String responseBody = response.body();
                        //logger.log(Level.INFO, "JSON Response for province {0}: {1}", new Object[]{provincia, responseBody});

                        // Processa il JSON e stampa i nomi delle città
                        printCityNames(responseBody);
                    } else {
                        // Se la richiesta non ha successo, stampa un messaggio di errore
                        logger.log(Level.WARNING, "Failed to get cities for province {0}. Status code: {1}",
                                new Object[]{provincia, response.statusCode()});
                    }
                });
    }

    private void printCityNames(String jsonString) {
        JSONArray firstLevelArray = new JSONArray(jsonString); // Getting the big array

        for(int i = 0 ; i < firstLevelArray.length(); i++)
        {
            String cityName = firstLevelArray.getJSONObject(i).getString("nome");
            //logger.log(Level.OFF, cityName);
            System.out.println(cityName);
            // But how sure are you that you won't have a null in there?
        }
    }


    public void setField(String field){
        this.field = field;
    }

    public String getField(){
        return this.field;
    }
}