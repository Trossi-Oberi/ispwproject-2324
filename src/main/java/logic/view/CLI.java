package logic.view;

import logic.beans.BEvent;
import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.exceptions.DuplicateEventParticipation;
import logic.exceptions.InvalidTokenValue;
import logic.utils.GoogleLogin;
import logic.utils.LoggedUser;
import logic.utils.MusicGenres;
import logic.utils.UserTypes;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI {
    private static final Logger logger = Logger.getLogger("NightPlan");
    private static boolean isAuthenticated = false;
    private static CFacade cFacade;
    private static BUserData bUserData;
    private static ArrayList<String> commands = new ArrayList<>();
    private static String[] commandsList= {"/home", "/events", "/notifications", "/settings", "/quit"};
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static String filePath;

    private static void initializeController(){
        cFacade = new CFacade();
        bUserData = new BUserData();
        commands.addAll(List.of(commandsList));
    }


    public static void clearScreen() {

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            try {
                new ProcessBuilder("pwsh", "/c", "cls").inheritIO().start().waitFor();
                Process process = Runtime.getRuntime().exec("color 01");
                process.waitFor();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Clear screen failed");
            }
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } else {
            System.out.println("Sistema operativo non riconosciuto");
        }

    }

    public static void spacer(int times){
        for (int i = 0; i < times; i++) {
            System.out.println();
        }
    }

    private static void handleCommand(String command){
        switch (command) {
            case "/home":
                loadHome();
                break;
            case "/events":
                loadEvents();
                break;
            case "/notifications":
                loadNotifications();
                break;
            case "/settings":
                loadSettings();
                break;
            case "/quit":
                signOut();
                break;
            default:
                break;
        }
        spacer(3);
    }

    private static void loadSettings() {
        boolean valid = false;

        switch (LoggedUser.getUserType()){
            case USER:
                do {
                    System.out.println("Settings:   \n" +
                            "a. Your profile\n" +
                            "b. Your groups\n" +
                            "c. Change city\n" +
                            "d. Contact us\n" +
                            "e. Help & FAQs\n" +
                            "f. Sign Out");
                    try {
                        String value = reader.readLine();

                        //verifico comandi generali
                        if(commands.contains(value)){
                            handleCommand(value);
                        }

                        switch (value) {
                            case "a":
                                valid = true;
                                showProfileInfo();
                                break;
                            case "b":
                                valid = true;
                                System.out.println("Not implemented :S");
                                break;
                            case "c":
                                valid = true;
                                changeCity();
                                break;
                            case "d":
                                valid = true;
                                showContacts();
                                break;
                            case "e":
                                valid = true;
                                showHelp();
                                break;
                            case "f":
                                valid = true;
                                signOut();
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } while(!valid);

                break;
            case ORGANIZER:
                do {
                    System.out.println("Settings:   \n" +
                            "a. Your profile\n" +
                            "b. Contact us\n" +
                            "c. Help & FAQs\n" +
                            "d. Sign Out");
                    try {
                        String value = reader.readLine();

                        //verifico comandi generali
                        if(commands.contains(value)){
                            handleCommand(value);
                        }

                        switch (value) {
                            case "a":
                                valid = true;
                                showProfileInfo();
                                break;
                            case "b":
                                valid = true;
                                showContacts();
                                break;
                            case "c":
                                valid = true;
                                showHelp();
                                break;
                            case "d":
                                valid = true;
                                signOut();
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } while(!valid);
                break;
        }

        spacer(2);
    }

    private static void signOut() {
        cFacade.signOut();
        loadApp();
    }

    private static void showHelp() {
        spacer(1);
        System.out.println("+++++++++++ Help & FAQs +++++++++++");
        spacer(1);
        System.out.println("Nicolas Oberi - https://github.com/snyppololo");
        System.out.println("Matteo Trossi - https://github.com/DeveloperMatt02");

        //torno indietro
        loadSettings();
    }

    private static void showContacts() {
        spacer(1);
        System.out.println("+++++++++++ Contact us +++++++++++");
        spacer(1);
        System.out.println("Nicolas Oberi - https://github.com/snyppololo");
        System.out.println("Matteo Trossi - https://github.com/DeveloperMatt02");

        //torno indietro
        loadSettings();
    }

    private static void showProfileInfo() {
        spacer(1);
        System.out.println("+++++++++++ Profile info +++++++++++");
        spacer(1);
        System.out.println("Username: " + LoggedUser.getUserName());
        System.out.println("First name: " + LoggedUser.getFirstName());
        System.out.println("Last name: " + LoggedUser.getLastName());
        System.out.println("Province: " + LoggedUser.getProvince());
        System.out.println("City: " + LoggedUser.getCity());
        System.out.println("Birth date: " + LoggedUser.getBirthDate());
        System.out.println("Gender: " + LoggedUser.getGender());
        System.out.println("User type: " + LoggedUser.getUserType());
        spacer(1);

        //go back
        loadSettings();
    }

    private static void changeCity() {
        //change city page
        String newProvince = null;
        String newCity = null;
        ArrayList<String> provinces = cFacade.getProvincesList();
        ArrayList<String> cities = cFacade.getCitiesList(bUserData.getProvince());
        boolean valid = false;

        spacer(1);
        System.out.println("+++++++++++ Change city +++++++++++");
        spacer(1);

        try {

            System.out.print("New province: ");
            do {
                String tempVal = reader.readLine();
                if (provinces.contains(tempVal)) {
                    newProvince = tempVal;
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("New city: ");
            do {
                String tempVal = reader.readLine();
                if (cities.contains(tempVal)) {
                    newCity = tempVal;
                    valid = true;
                }
            } while (!valid);

            spacer(1);

            if (cFacade.changeUserCity(LoggedUser.getUserID(), newProvince, newCity) == 1) {
                System.out.println("City updated successfully");
            } else {
                System.out.println("City update failed. Retry...");
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }

        //go back
        loadSettings();
    }

    private static void loadHome(){
        clearScreen();
        ArrayList<BEvent> eventList;

        System.out.println("Welcome to NightPlan!");
        spacer(1);

        if(LoggedUser.getUserType().equals(UserTypes.USER)){
            eventList = cFacade.retrieveEvents(LoggedUser.getUserType(), "Home");
            if(!eventList.isEmpty()){
                System.out.println("Events in your city. \nWrite event name to show event info!");
                for (int i = 0; i < eventList.size(); i++) {
                    System.out.println(i+1 + ". " + eventList.get(i).getEventName());
                }

                boolean valid = false;
                do{
                    try {
                        String value = reader.readLine();

                        if(commands.contains(value)){
                            handleCommand(value);
                        }

                        for (BEvent bEvent : eventList) {
                            if (bEvent.getEventName().equals(value)) {
                                valid = true;
                                showEventInfo(bEvent);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } while (!valid);
            } else {
                System.out.println("No events in your city");
            }
            spacer(3);
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)){
            System.out.println("You can add a new event or navigate in other menus:");
            System.out.println("1. Add new event");

            boolean valid = false;
            do{
                try {
                    String value = reader.readLine();

                    if(commands.contains(value)){
                        handleCommand(value);
                    }

                    if (value.equals("1")) {
                        addEventPage();
                        valid = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } while (!valid);
            spacer(3);
        }
    }

    private static void addEventPage() {
        BEvent bEventBean = new BEvent();
        setEventBean(bEventBean);

        if(cFacade.addEvent(bEventBean)){
            System.out.println("Event added successfully");
        } else {
            System.out.println("Event adding failed. Returning home...");
        }
        loadHome();
    }

    private static void setEventBean(BEvent eventBean){
        System.out.println("New event\n");

        boolean valid = false;
        ArrayList<String> provinces = cFacade.getProvincesList();
        ArrayList<String> cities = cFacade.getCitiesList(bUserData.getProvince());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {

            System.out.println("Event name:");
            eventBean.setEventName(reader.readLine());

            System.out.println("Event province:");
            do {
                String val = reader.readLine();
                if (provinces.contains(val)) {
                    eventBean.setEventProvince(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event city: ");
            do {
                String val = reader.readLine();
                if (cities.contains(val)) {
                    eventBean.setEventCity(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event date:");
            do {
                String val = reader.readLine();
                try {
                    // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
                    LocalDate eventDate = LocalDate.parse(val, formatter);
                    eventBean.setEventDate(String.valueOf(eventDate));
                    valid = true;
                } catch (Exception e) {
                    // Gestire il caso in cui l'input non sia nel formato corretto
                    logger.severe("Invalid date format.");
                }
            } while (!valid);
            valid = false;

            System.out.println("Event address:");
            eventBean.setEventAddress(reader.readLine());

            System.out.println("Choose music genre from list:");
            for (int i = 0; i < MusicGenres.MUSIC_GENRES.length; i++) {
                System.out.println(i+1 + ". " + MusicGenres.MUSIC_GENRES[i]);
            }
            do {
                String val = reader.readLine();
                for (int i = 0; i < MusicGenres.MUSIC_GENRES.length; i++) {

                    if (MusicGenres.MUSIC_GENRES[i].contains(val)) {
                        valid = true;
                        eventBean.setEventMusicGenre(val);
                    }
                }
            } while (!valid);

            String hour;
            String minutes;

            System.out.println("Event time: hh:mm");
            System.out.println("Hour -> " + (hour = reader.readLine()));
            System.out.println("Minutes -> " + (minutes = reader.readLine()));
            eventBean.setEventTime(hour, minutes);

            byte[] fileData = pickFileData();
            if (fileData != null && filePath != null) {
                eventBean.setEventPicData(fileData);
            }
            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
            eventBean.setEventPicPath(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] pickFileData(){
        JFileChooser fileChooser = new JFileChooser();
        byte[] fileData = new byte[0];

        // Mostrare la finestra di dialogo per la selezione del file
        int result = fileChooser.showOpenDialog(null);

        // Verificare se l'utente ha selezionato un file
        if (result == JFileChooser.APPROVE_OPTION) {
            // Ottenere il percorso del file selezionato
            File fileSelezionato = fileChooser.getSelectedFile();
            filePath = fileSelezionato.getAbsolutePath();
            try {
                // Leggere i dati del file in un array di byte
                fileData = Files.readAllBytes(fileSelezionato.toPath());

                // Ora puoi utilizzare il percorso del file (filePath) e i dati del file (fileData) come desiderato
                System.out.println("Percorso del file: " + filePath);
                System.out.println("Dati del file in byte: " + fileData.length + " byte");
            } catch (IOException e) {
                // Gestire eventuali eccezioni durante la lettura del file
                e.printStackTrace();
            }
        } else {
            System.out.println("Nessun file selezionato");
        }
        return fileData;
    }

    private static void showEventInfo(BEvent bEvent) {
        System.out.println("Event info:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println("City: " + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());

        spacer(1);
        System.out.println("1. Go back\n" +
                "2. Plan event participation");

        boolean valid = false;
        do{
            try {
                String value = reader.readLine();

                //verifico comandi generali
                if(commands.contains(value)){
                    handleCommand(value);
                }

                switch (value){
                    case "1":
                        loadHome();
                        valid = true;
                        break;
                    case "2":
                        if(cFacade.participateToEvent(bEvent)){
                            valid = true;
                        } else {
                            logger.severe("Event participation failed!");
                            valid = false;
                        }
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (DuplicateEventParticipation e){
                logger.log(Level.INFO, "Event participation already planned. Choose another event...");
            }

        } while (!valid);

    }

    private static void loadNotifications() {
        //TODO
    }

    private static void loadEvents() {
        //TODO
    }

    public static void loadApp(){
        String asciiLogo = "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "           -#####   #####                                ##########                                  \n" +
                "          #      # ##    #                 -            #          #  .-                             \n" +
                "          #      ###    +##  .          #    #      ## -#    ##.    #    #                           \n" +
                "         ##       ##    # #  #  ######.+#   ##+   #    #     ##    ##   ## +#####   .+-.+#           \n" +
                "         #              ##   ##        #        ##                ###   ##        #        #         \n" +
                "        .#    ##       ##    #   ##    #    #    #    ##    #####+ #   --   ##   -#   ##    #        \n" +
                "        #     ##+      ##   ##   ##   ##   ##   ##   ##    #+      #   #   +#    #    #+   #         \n" +
                "        #    #. #     ##    ##        #    ##   ##   ##    #      -    ##        #   -#    #         \n" +
                "     # + ####+ + ##### + ####   #    -#-###++##+.#-## # #### .+ #  ###+ +######## #### ####  # #     \n" +
                "    +# #  +    #  + +  # +  ###. +###-+  #    #  + +  # +  # -# # .+ +  #    +  + +  # +  + .# # -   \n" +
                "      +    .++.             #   #          ++.       .     #   +    -     +++       -     #   +      \n" +
                "            +               #               -              #               -              #          \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     \n" +
                "                                                                                                     ";

        try {
            initializeController();

            boolean valid = false;
            while (!valid) {
                clearScreen();
                System.out.println(asciiLogo);
                System.out.println("Login or register");
                System.out.println("1. Login");
                System.out.println("2. Login with Google");
                System.out.println("3. Register new account");

                String inputLine = reader.readLine();
                switch (inputLine) {
                    case "1":
                        while (true) {
                            System.out.println("Insert username: ");
                            bUserData.setUsername(reader.readLine());
                            System.out.println("Insert password: ");
                            bUserData.setPassword(reader.readLine());

                            //login user
                            int res = cFacade.loginUser(bUserData, false, null);

                            System.out.println();
                            if (res == 1) {
                                System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                                valid = true;
                                break;
                            } else {
                                System.out.println("Wrong credentials. Retry...");
                            }
                        }
                        loadHome();
                        break;
                    case "2":
                        try {
                            while (true) {
                                System.out.println("Opening google login page...");
                                spacer(1);
                                System.out.println("Write authorization code to continue login: ");

                                //avvio la schermata di login
                                GoogleLogin.initGoogleLogin();

                                String authCode = reader.readLine();

                                //login user
                                int res = cFacade.loginUser(bUserData, true, authCode);

                                System.out.println();
                                if (res == 1) {
                                    System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                                    valid = true;
                                    loadHome();
                                } else {
                                    System.out.println("User not registered with Google. \nYou will be redirected to registration page.");
                                    if (registerUser(true) == 1) {
                                        System.out.println("Successfully registered with Google");
                                    }
                                }
                                break;
                            }
                        } catch (RuntimeException e) { //TODO: sostituire anche qui InvalidTokenValue exception
                            logger.severe("Invalid token value");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "3":
                        try {
                            if (registerUser(false) == 1) {
                                spacer(3);
                                System.out.println("Loading home page...");
                                spacer(3);
                                loadHome();
                            }
                        } catch (RuntimeException e) { //TODO: sostituire anche qui InvalidTokenValue exception
                            logger.severe("Invalid token value");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } finally {
            try {
                // Chiudi il BufferedReader
                reader.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        loadApp();
    }

    private static int registerUser(boolean isGoogleAuth) {
        try {
            boolean valid = false;

            spacer(1);
            System.out.println("Registration procedure");
            spacer(1);

            if (!isGoogleAuth) {
                //classic registration
                System.out.println("Username: ");
                bUserData.setUsername(reader.readLine());
                System.out.println("Password: ");
                bUserData.setPassword(reader.readLine());
            } else {
                bUserData.setPassword("google_account_hidden_psw");
            }

            System.out.println("First name: ");
            bUserData.setFirstName(reader.readLine());
            System.out.println("Last name: ");
            bUserData.setLastName(reader.readLine());
            System.out.println("Gender: ");
            System.out.println("a. Male\n" + "b. Female\n" + "c. Other");
            do {
                String val = reader.readLine();
                bUserData.setGender(val);
                if(val.equals("a") || val.equals("b") || val.equals("c")){
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Province: ");
            ArrayList<String> provinces = cFacade.getProvincesList();
            do {
                String val = reader.readLine();
                if(provinces.contains(val)){
                    bUserData.setProvince(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("City: ");
            ArrayList<String> cities = cFacade.getCitiesList(bUserData.getProvince());
            do {
                String val = reader.readLine();
                if(cities.contains(val)){
                    bUserData.setCity(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;


            System.out.println("Birth date: dd-MM-yyyy");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            do {
                String val = reader.readLine();
                try {
                    // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
                    LocalDate birthDate = LocalDate.parse(val, formatter);
                    bUserData.setBirthDate(birthDate);
                    valid = true;
                } catch (Exception e) {
                    // Gestire il caso in cui l'input non sia nel formato corretto
                    logger.severe("Invalid birth date format.");
                }
            } while (!valid);
            valid = false;

            System.out.println("User type: ");
            System.out.println("a. USER\n" + "b. ORGANIZER");
            do {
                String val = reader.readLine();
                val = val.toUpperCase();
                if(val.equals(UserTypes.USER.toString()) || val.equals(UserTypes.ORGANIZER.toString())){
                    bUserData.setType(UserTypes.valueOf(val));
                    valid = true;
                }
            } while (!valid);

            if(cFacade.registerUser(bUserData)){
                System.out.println("User registration completed successfully! Return to login page.");
            } else {
                System.out.println("Error in registration procedure. Retry...");
                return 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
