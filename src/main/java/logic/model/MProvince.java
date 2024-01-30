package logic.model;

import java.lang.reflect.Array;
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

public class MProvince {

    private String selectedProvince;

    private ArrayList<String> provincesList = new ArrayList<String>();

    private static final String API_BASE_URL = "https://axqvoqvbfjpaamphztgd.functions.supabase.co";  // Sostituisci con il tuo URL di base dell'API

    private HttpClient httpClient = HttpClient.newHttpClient();
    private final Logger logger = Logger.getLogger("NightPlan");

    public CompletableFuture<Void> getProvinces() {
        String apiUrl = API_BASE_URL + "/province";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();


        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200 && !response.body().isEmpty()) {
                        // La richiesta è andata a buon fine, stampa il JSON nel logger
                        String responseBody = response.body();
                        //logger.log(Level.INFO, "JSON Response (provinces list): {0}", new Object[]{responseBody});

                        // Processa il JSON e salva i nomi delle province nel Model
                        setProvincesList(responseBody);
                    } else {
                        // Se la richiesta non ha successo, stampa un messaggio di errore
                        logger.log(Level.WARNING, "Failed to get provinces. Status code: {0}. API Error",
                                new Object[]{response.statusCode()});
                    }
                });
    }
    public void setSelectedProvince(String province){
        this.selectedProvince = province;
    }

    public String getSelectedProvince(){
        return this.selectedProvince;
    }

    public void setProvincesList(String jsonString){
        JSONArray getArray = new JSONArray(jsonString); // Getting the big array

        for(int i = 0 ; i < getArray.length(); i++)
        {
            String provinceName = getArray.getJSONObject(i).getString("nome");
            this.provincesList.add(provinceName);
        }
    }

    public ArrayList<String> getProvincesList(){
        return this.provincesList;
    }

}