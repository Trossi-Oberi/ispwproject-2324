package logic.utils;

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

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class GoogleLogin {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CLIENT_SECRETS_FILE_PATH = "client_secrets.json";
    private static final String SCOPES = "https://www.googleapis.com/auth/userinfo.email";

    private static GoogleAuthorizationCodeFlow flow;

    public static int initGoogleLogin() throws Exception{
        // Load client secrets
        InputStream in = GoogleLogin.class.getClassLoader().getResourceAsStream(CLIENT_SECRETS_FILE_PATH);
        GoogleClientSecrets clientSecrets = null;
        if (in != null) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        }

        // Set up the OAuth flow
        GoogleAuthorizationCodeFlow authflow;
        if (clientSecrets != null) {
            authflow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList(SCOPES))
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
        } else {
            throw new Exception();
        }

        // Get authorization URL
        AuthorizationCodeRequestUrl authorizationUrl = authflow.newAuthorizationUrl();
        authorizationUrl.setRedirectUri("urn:ietf:wg:oauth:2.0:oob");

        // Open the authorization URL in the default browser
        openBrowser(authorizationUrl.toString());
        flow = authflow;
        return 1;
    }

    public static GoogleAuthorizationCodeFlow getGoogleAuthFlow(){
        return flow;
    }

    public static Credential getGoogleAccountCredentials(GoogleAuthorizationCodeFlow flow, String code) throws Exception{
        flow.loadCredential("user");
        Credential cred;
        try {
            GoogleTokenResponse resp = flow.newTokenRequest(code).setRedirectUri("urn:ietf:wg:oauth:2.0:oob").execute();
            cred = flow.createAndStoreCredential(resp, "user");
        } catch (TokenResponseException e){
            throw new RuntimeException(e.getDetails().getErrorDescription());
        }

        return cred;
    }

    public static String getGoogleAccountEmail(Credential cred) throws Exception {
        JsonFactory jsonFactory = cred.getJsonFactory();

        return getEmailFromGoogle(cred, jsonFactory);
    }

    private static String getEmailFromGoogle(Credential credential, JsonFactory jsonFactory) throws IOException {
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
        //Map<String, Object> userInfo = parser.parseAndClose(userInfoResponse.getContent(), Map.class);
        Map userInfo = parser.parseAndClose(userInfoResponse.getContent(), Charset.defaultCharset(), Map.class);

        return (String) userInfo.get("email");
    }

    private static void openBrowser(String url) throws IOException {
        // Open the default browser with the authorization URL
        Desktop.getDesktop().browse(URI.create(url));
    }

}