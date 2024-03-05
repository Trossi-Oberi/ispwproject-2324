package logic.controllers;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import logic.exceptions.InvalidTokenValue;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static logic.view.EssentialGUI.logger;

public class GoogleLogin {
    private GoogleLogin(){
        //empty
    }

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CLIENT_SECRETS_FILE_PATH = "client_secrets.json";
    private static final String SCOPES = "https://www.googleapis.com/auth/userinfo.email";

    private static GoogleAuthorizationCodeFlow flow;

    public static int initGoogleLogin(){
        try {
            // Load client secrets
            InputStream in = GoogleLogin.class.getClassLoader().getResourceAsStream(CLIENT_SECRETS_FILE_PATH);
            GoogleClientSecrets clientSecrets = null;
            if (in != null) {
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            }
            if (clientSecrets != null) {
                flow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList(SCOPES))
                        .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build();
            } else {
                logger.severe("ClientSecrets is null!");
                return 0;
            }

            // Get authorization URL
            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
            authorizationUrl.setRedirectUri("urn:ietf:wg:oauth:2.0:oob");

            // Open the authorization URL in the default browser
            openBrowser(authorizationUrl.toString());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return 0;
        }
        return 1;
    }

    public static GoogleAuthorizationCodeFlow getGoogleAuthFlow(){
        return flow;
    }

    public static Credential getGoogleAccountCredentials(GoogleAuthorizationCodeFlow flow, String code) throws InvalidTokenValue{
        try {
            flow.loadCredential("user");
            GoogleTokenResponse resp = flow.newTokenRequest(code).setRedirectUri("urn:ietf:wg:oauth:2.0:oob").execute();
            return flow.createAndStoreCredential(resp, "user");
        } catch (TokenResponseException e){
            //gestione token non valido
            throw new InvalidTokenValue("Invalid credentials token: ", e);
        } catch (Exception e){
            logger.severe(e.getMessage());
            return null;
        }
    }

    public static String getGoogleAccountEmail(Credential cred){
        JsonFactory jsonFactory = cred.getJsonFactory();

        return getEmailFromGoogle(cred, jsonFactory);
    }

    private static String getEmailFromGoogle(Credential credential, JsonFactory jsonFactory){
        try {
            //Make a request to Google's UserInfo API to get the user's email
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

            // Crea una richiesta GET all'endpoint UserInfo
            HttpRequest userInfoRequest = credential.getTransport().createRequestFactory()
                    .buildGetRequest(new GenericUrl(userInfoEndpoint));

            // Aggiunge l'intestazione di autorizzazione alle richieste
            credential.initialize(userInfoRequest);

            // Esegui la richiesta e ottieni la risposta
            HttpResponse userInfoResponse = userInfoRequest.execute();

            // Parsa la risposta JSON per ottenere l'indirizzo email
            JsonObjectParser parser = new JsonObjectParser(jsonFactory);
            Map<String, Object> userInfo = parser.parseAndClose(userInfoResponse.getContent(), Charset.defaultCharset(), Map.class);

            return (String) userInfo.get("email");
        } catch (IOException e){
            //getEmailFromGoogle failed
            logger.severe(e.getMessage());
            return null;
        }
    }

    private static void openBrowser(String url) throws IOException {
        // Open the default browser with the authorization URL
        Desktop.getDesktop().browse(URI.create(url));
    }

}