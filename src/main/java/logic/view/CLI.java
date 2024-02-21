package logic.view;

import logic.beans.BEvent;
import logic.beans.BMessage;
import logic.beans.BNotification;
import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.exceptions.*;
import logic.utils.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class CLI implements NotificationView, ChatView {
    private static final Logger logger = Logger.getLogger("NightPlan");
    private static CFacade cFacade;
    private static BUserData bUserData;
    private static final String[] MUSIC_GENRES = {"Pop", "Rock", "Dance", "Electronic", "Techno", "Reggaeton", "Metal", "Disco", "Tech house", "House", "Rap", "Trap"};
    private static final List<String> commands = new ArrayList<>();
    private static final String[] COMMANDS_LIST = {"/commands", "/home", "/events", "/notifications", "/settings", "/quit"};
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
    private static String filePath;
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String CITY = "City: ";
    private static final String GOBACK = "1. Go back\n";
    private static final String COMMANDS_HELP = "Use /commands to view a list of commands which you can use to navigate into application pages";
    private static final String ASCII_LOGO =
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
                    "            +               #               -              #               -              #          \n";
    private static Integer timesClicked;
    private static Integer nParticipants;

    //TODO: DA RICONTROLLARE DA CIMA A FONDO, RENDERE CONSISTENTE CON LA GUI

    private static void initializeControllers() {
        CLI view = new CLI();
        cFacade = new CFacade();
        CFacade.setNotiGraphic(view);
        cFacade.setChatGraphic(view);
        bUserData = new BUserData();
        commands.addAll(List.of(COMMANDS_LIST));
    }


    public static void spacer(int times) {
        for (int i = 0; i < times; i++) {
            System.out.println();
        }
    }

    private static void handleCommand(String command) {
        switch (command) {
            case "/commands":
                spacer(1);
                System.out.println("Command list:");
                for (String s : COMMANDS_LIST) {
                    System.out.println(s);
                }
                spacer(1);
                break;
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
        spacer(1);
    }

    private static void loadSettings() {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            acquireInputCycleSettingsUser();
        } else {//UserType == ORGANIZER
            acquireInputCycleSettingsOrg();
        }
        spacer(2);
    }

    private static void acquireInputCycleSettingsOrg() {
        boolean valid = false;
        do {
            showSettings(UserTypes.ORGANIZER);
            String value = acquireInput();
            if (value != null) {
                //verifico comandi generali
                if (commands.contains(value)) {
                    handleCommand(value);
                }
                valid = handleSettingsInputOrg(value);
            }
        } while (!valid);
    }

    private static void acquireInputCycleSettingsUser() {
        boolean valid = false;
        do {
            showSettings(UserTypes.USER);
            spacer(1);
            String value = acquireInput();
            if (value != null) {
                //verifico comandi generali
                if (commands.contains(value)) {
                    handleCommand(value);
                }
                valid = handleSettingsInputUser(value);
            }
        } while (!valid);
    }

    private static boolean handleSettingsInputOrg(String value) {
        switch (value) {
            case "a":
                showProfileInfo();
                return true;
            case "b":
                showContacts();
                return true;
            case "c":
                showHelp();
                return true;
            case "d":
                signOut();
                return true;
            default:
                return false;
        }
    }

    private static boolean handleSettingsInputUser(String value) {
        switch (value) {
            case "a":
                showProfileInfo();
                return true;

            case "b":
                changeCity();
                return true;

            case "c":
                showContacts();
                return true;

            case "d":
                showHelp();
                return true;

            case "e":
                signOut();
                return true;

            default:
                return false;
        }
    }

    private static String acquireInput() {
        String input;
        try {
            input = CLI.READER.readLine();
            return input;
        } catch (IOException e) {
            logger.severe("Error during input acquisition");
            return null;
        }
    }

    private static void showSettings(UserTypes type) {
        if (type.equals(UserTypes.USER)) {
            System.out.println("Settings:   \n" +
                    "a. Your profile\n" +
                    "b. Change city\n" +
                    "c. Contact us\n" +
                    "d. Help & FAQs\n" +
                    "e. Sign Out");
        } else if (type.equals(UserTypes.ORGANIZER)) {
            System.out.println("Settings:   \n" +
                    "a. Your profile\n" +
                    "b. Contact us\n" +
                    "c. Help & FAQs\n" +
                    "d. Sign Out");
        }
        spacer(1);
        System.out.println(COMMANDS_HELP);
    }


    private static void signOut() {
        spacer(3);
        System.out.println(ASCII_LOGO);
        spacer(2);
        System.out.println("Logging out from NightPlan. Goodbye! ;)");
        spacer(3);
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
        System.out.println(CITY + LoggedUser.getCity());
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
        List<String> provinces = cFacade.getProvincesList();
        List<String> cities = null;
        boolean valid = false;

        spacer(1);
        System.out.println("+++++++++++ Change city +++++++++++");
        spacer(1);

        try {

            System.out.print("New province: ");
            do {
                String tempVal = READER.readLine();
                if (provinces.contains(tempVal)) {
                    newProvince = tempVal;
                    valid = true;
                }
            } while (!valid);
            valid = false;

            //faccio la retrieve delle città della nuova provincia scelta1
            cities = cFacade.getCitiesList(newProvince);

            System.out.print("New city: ");
            do {
                String tempVal = READER.readLine();
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
        } catch (IOException e) {
            logger.severe("IOException in ChangeCity");
        }

        //go back
        loadSettings();
    }

    private static void loadHome() {
        spacer(3);

        System.out.println("Welcome to NightPlan!");
        spacer(1);
        System.out.println(COMMANDS_HELP);
        spacer(1);

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            List<BEvent> eventList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCHomeUser");
            List<BEvent> futureEvents = fetchFutureEvents(eventList);

            if (!eventList.isEmpty()) {
                System.out.println("Events in your city. \nWrite event name to show event info!");
                printFutureEvents(futureEvents);
                acquireInputCycleHomeUser(futureEvents);
            } else {
                System.out.println("No events in your city");
                System.out.println("Please wait new events to be added in your city. Use /commands to navigate!");
                spacer(1);

                waitCommands();
            }
            spacer(3);
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println("You can add a new event or navigate in other menus:");
            System.out.println("1. Add new event");
            acquireInputCycleHomeOrg();

            spacer(3);
        }
    }

    private static void acquireInputCycleHomeOrg() {
        boolean valid = false;
        do {
            String value = acquireInput();
            if (value != null) {
                if (commands.contains(value)) {
                    handleCommand(value);
                }
                if (value.equals("1")) {
                    addEventPage();
                    valid = true;
                }
            }
        } while (!valid);
    }

    private static void acquireInputCycleHomeUser(List<BEvent> futureEvents) {
        boolean valid = false;
        do {
            String value = acquireInput();
            if (value != null) {
                if (commands.contains(value)) {
                    handleCommand(value);
                }
                valid = handleEventNameInput(value, futureEvents);
            }
        } while (!valid);
    }

    private static boolean handleEventNameInput(String value, List<BEvent> futureEvents) {
        for (BEvent bEvent : futureEvents) {
            if (bEvent.getEventName().equals(value)) {
                showEventInfo(bEvent, "Home",false);
                return true;
            }
        }
        return false;
    }

    private static void printFutureEvents(List<BEvent> futureEvents) {
        for (int i = 0; i < futureEvents.size(); i++) {
            System.out.println(i + 1 + ". " + futureEvents.get(i).getEventName());
        }
    }

    private static List<BEvent> fetchFutureEvents(List<BEvent> eventList) {
        List<BEvent> futureEvents = new ArrayList<>();
        for (BEvent event : eventList) {
            String eventDateString = event.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));
            if (LocalDate.now().isBefore(date)) {
                futureEvents.add(event);
            }
        }
        return futureEvents;
    }

    private static void addEventPage() {
        BEvent bEventBean = new BEvent();
        setEventBean(bEventBean);

        try {
            if (cFacade.addEvent(bEventBean)) {
                System.out.println("Event added successfully");
            } else {
                System.out.println("Event adding failed. Returning home...");
            }
        } catch (EventAlreadyAdded e) {
            logger.warning("Event already added with this name: " + e.getEventName());
        }
        loadHome();
    }

    private static void setEventBean(BEvent eventBean) {
        System.out.println("New event\n");
        List<String> provinces = cFacade.getProvincesList();
        List<String> cities = null;

        try {

            System.out.println("Event name:");
            eventBean.setEventName(acquireInput());

            System.out.println("Event province:");
            acquireProvinceInputAndSetBean(provinces, eventBean);

            cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: ");
            acquireCityInputAndSetBean(cities, eventBean);

            System.out.println("Event date:");
            acquireDateAndSetBean(eventBean);

            System.out.println("Event address:");
            acquireAddressAndSetBean(eventBean);

            System.out.println("Choose music genre from list:");
            printMusicGenres();
            acquireMusicGenreAndSetBean(eventBean);


            System.out.println("Event time: HH:mm");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            acquireTimeAndSetBean(eventBean, timeFormat);


            //prendi file immagine
            byte[] fileData = pickFileData();
            if (fileData != null && filePath != null) {
                eventBean.setEventPicData(fileData);
            }
            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
            eventBean.setEventPicPath(filePath);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
    }

    private static void acquireTimeAndSetBean(BEvent eventBean, SimpleDateFormat timeFormat) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            try {
                timeFormat.parse(val);
            } catch (ParseException e) {
                logger.severe("Invalid time format.");
                continue;
            }
            //qua ci arriva solo se il time viene parsato correttamente
            if (val != null) {
                String[] parts = val.split(":");
                String hours = parts[0];
                String minutes = parts[1];
                valid = setTimeToBean(eventBean, hours, minutes);
            }
        }


    }

    private static boolean setTimeToBean(BEvent eventBean, String hours, String minutes) {
        try {
            eventBean.setEventTime(hours, minutes);
            return true;
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    private static void acquireMusicGenreAndSetBean(BEvent eventBean) {
        boolean valid = false;
        boolean found = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null) {
                found = checkIfGenreIsValid(val);
            }
            if (found) {
                valid = setGenreToBean(val, eventBean);
            }
        }
    }

    private static boolean setGenreToBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventMusicGenre(val);
            return true;
        } catch (InvalidValueException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    private static boolean checkIfGenreIsValid(String val) {
        for (String musicGenre : MUSIC_GENRES) {
            if (musicGenre.equals(val)) {
                return true;
            }
        }
        return false;
    }

    private static void printMusicGenres() {
        for (int i = 0; i < MUSIC_GENRES.length; i++) {
            System.out.println(i + 1 + ". " + MUSIC_GENRES[i]);
        }
    }

    private static void acquireAddressAndSetBean(BEvent eventBean) {
        try {
            eventBean.setEventAddress(acquireInput());
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void acquireDateAndSetBean(BEvent eventBean) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            valid = convertDateAndSetEventBean(val, formatter, eventBean);
        }
    }

    private static boolean convertDateAndSetEventBean(String val, DateTimeFormatter formatter, BEvent eventBean) {
        try {
            // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
            LocalDate eventDate = LocalDate.parse(val, formatter);
            eventBean.setEventDate(eventDate.format(formatter));
            return true;
        } catch (Exception e) {
            // Gestire il caso in cui l'input non sia nel formato corretto
            logger.severe("Invalid date format.");
            return false;
        }
    }

    private static void acquireCityInputAndSetBean(List<String> cities, BEvent eventBean) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (cities.contains(val)) {
                setCityInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static void setCityInBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventCity(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void acquireProvinceInputAndSetBean(List<String> provinces, BEvent eventBean) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (provinces.contains(val)) {
                setProvinceInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static void setProvinceInBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventProvince(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void editEventBean(BEvent eventBean) {
        System.out.println("Edit event\n");
        spacer(1);
        System.out.println("Press enter to maintain previous value!");
        String newVal;

        List<String> provinces = cFacade.getProvincesList();
        List<String> cities;

        try {

            System.out.println("Event name: (previous: " + eventBean.getEventName() + ")");
            newVal = acquireInput();
            if (newVal != null && !newVal.isEmpty()) {
                eventBean.setEventName(newVal);
            }

            System.out.println("Event province: (previous: " + eventBean.getEventProvince() + ")");
            acquireProvinceInputAndEditBean(provinces, eventBean);

            cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: (previous: " + eventBean.getEventCity() + ")");
            acquireCityInputAndEditBean(cities, eventBean);

            System.out.println("Event date: (previous: " + eventBean.getEventDate() + ")");
            acquireDateAndEditBean(eventBean);

            System.out.println("Event address: (previous: " + eventBean.getEventAddress() + ")");
            newVal = acquireInput();
            if (newVal != null && !newVal.isEmpty()) {
                eventBean.setEventAddress(newVal);
            }

            System.out.println("Choose music genre from list: (previous: " + eventBean.getEventMusicGenre() + ")");
            printMusicGenres();
            acquireMusicGenreAndEditBean(eventBean);

            System.out.println("Event time: HH:mm (previous: " + eventBean.getEventTime() + ")");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            acquireTimeAndEditBean(eventBean, timeFormat);

            spacer(1);

            System.out.print("Do you want to change event image? Write 'y' or 'Y if you want to update event image");
            String decision = READER.readLine();
            if (decision.equalsIgnoreCase("y")) {
                //prendi file immagine
                byte[] fileData = pickFileData();
                if (fileData != null && filePath != null) {
                    eventBean.setEventPicData(fileData);
                }
                eventBean.setEventPicPath(filePath);
            }
            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
        } catch (IOException e) {
            logger.severe("IOException while changing event image");
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
    }

    private static void acquireTimeAndEditBean(BEvent eventBean, SimpleDateFormat timeFormat) {
        boolean valid = false;
        boolean toEdit = true;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                toEdit = false;
                valid = true;
            }
            if (toEdit) {
                try {
                    timeFormat.parse(val);
                } catch (ParseException e) {
                    logger.severe("Invalid time format.");
                    continue;
                }
                //qua ci arriva solo se il time viene parsato correttamente
                if (val != null) {
                    String[] parts = val.split(":");
                    String hours = parts[0];
                    String minutes = parts[1];
                    valid = setTimeToBean(eventBean, hours, minutes);
                }
            }
        }
    }

    private static void acquireMusicGenreAndEditBean(BEvent eventBean) {
        boolean valid = false;
        boolean found = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                break;
            }
            found = checkIfGenreIsValid(val);
            if (found) {
                valid = setGenreToBean(val, eventBean);
            }
        }
    }

    private static void acquireDateAndEditBean(BEvent eventBean) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        boolean toEdit = true;
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                valid = true;
                toEdit = false;
            }
            if (toEdit && convertDateAndSetEventBean(val, formatter, eventBean)) {
                valid = true;
            }
        }
    }

    private static void acquireCityInputAndEditBean(List<String> cities, BEvent eventBean) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                break;
            }
            if (cities.contains(val)) {
                setCityInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static void acquireProvinceInputAndEditBean(List<String> provinces, BEvent eventBean) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                break;
            }
            if (provinces.contains(val)) {
                setProvinceInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static byte[] pickFileData() {
        JFileChooser fileChooser = new JFileChooser();
        byte[] fileData = null;

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
                logger.severe(e.getMessage());
            }
        } else {
            System.out.println("Nessun file selezionato");
        }
        return fileData;
    }

    private static void showEventInfo(BEvent bEvent, String lastPage, boolean isPassed) {
        printEventInfo(bEvent);
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            spacer(1);
            boolean isEventParticipated = cFacade.checkPreviousEventParticipation(bEvent);
            if (isEventParticipated) {
                System.out.println(GOBACK +
                        "2. Remove event participation");
            } else {
                System.out.println(GOBACK +
                        "2. Plan event participation");
            }
            cycleForUserInputShowEvent(isEventParticipated, lastPage, bEvent);

            System.out.println("Press 1 to go back");

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER) && !isPassed) {
            spacer(1);
            System.out.println(GOBACK +
                    "2. Edit event\n" +
                    "3. Delete event");
            cycleForOrgInputFutureEvent(bEvent);

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println(GOBACK +
                    "2. Show analytics");
            cycleForOrgInputPastEvent(bEvent);
        }
    }

    private static void cycleForOrgInputPastEvent(BEvent bEvent) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null) {
                if (commands.contains(value)) {
                    handleCommand(value);
                }
                valid = handlePastEventOrgInput(value, bEvent);

            }

        }
    }

    private static boolean handlePastEventOrgInput(String value, BEvent bEvent) {
        if (value.equals("1")){
            loadEvents();
            return true;
        }else if(value.equals("2")){
            //shows past event analytics
            printEventAnalytics(bEvent);
            return true;
        }else{
            return false;
        }
    }

    private static void cycleForOrgInputFutureEvent(BEvent bEvent) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null) {
                valid = handleShowEventInfoOrgInput(value, bEvent);
            }
        }
    }

    private static void cycleForUserInputShowEvent(boolean isEventParticipated, String lastPage, BEvent bEvent) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null) {
                valid = handleShowEventInfoUserInput(value, isEventParticipated, lastPage, bEvent);
            }
        }
    }

    private static boolean handleShowEventInfoOrgInput(String value, BEvent bEvent) {
        switch (value) {
            case "1":
                loadEvents();
                return true;
            case "2":
                //opens procedure to edit event
                editEventBean(bEvent);
                if (cFacade.editEvent(bEvent)) {
                    System.out.println("Event edited successfully!");
                    loadEvents();
                    return true;
                } else {
                    logger.severe("Event edit failed!");
                    return false;
                }
            case "3":
                spacer(1);
                if (deleteEvent(bEvent.getEventID())) {
                    System.out.println("Event deleted successfully!");
                } else {
                    logger.severe("Event deletion failed!");
                }
                //sono organizer torno per forza nella pagina degli eventi creati
                loadEvents();
                return true;
            default:
                return false;
        }

    }


    private static boolean handleShowEventInfoUserInput(String value, boolean isEventParticipated, String lastPage, BEvent bEvent) {
        if (value.equals("1")) {
            showCorrectBackPageUser(lastPage);
            return true;

        } else if (value.equals("2") && isEventParticipated) {
            if (cFacade.removeEventParticipation(bEvent)) {
                System.out.println("Event participation removed successfully!");
                loadHome();
                return true;
            } else {
                logger.severe("Event participation removal failed!");
                return false;
            }
        } else if (value.equals("2")) {
            if (participateToEvent(bEvent)) {
                System.out.println("Event participation successful!");
                loadHome();
                return true;
            } else {
                logger.severe("Event participation failed!");
                return false;
            }
        } else {
            return false;
        }
    }

    private static void showCorrectBackPageUser(String lastPage) {
        if (lastPage.equals("Home")) {
            //torno nella home
            loadHome();
        } else if (lastPage.equals("YourEventsUser")) {
            loadEvents();
        }
    }

    private static boolean participateToEvent(BEvent bEvent) {
        try {
            return cFacade.participateToEvent(bEvent);
        } catch (EventAlreadyDeleted e) {
            logger.severe(e.getMessage());
            return false;
        }
    }


    private static void printEventInfo(BEvent bEvent) {
        System.out.println("Event info:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println(CITY + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());
    }

    private static boolean deleteEvent(int eventID) {
        System.out.println("Are you sure you want to delete this event?\nWrite 'y' to confirm or anything else to cancel.");
        spacer(1);
        try {
            String val = READER.readLine();
            if (val.equalsIgnoreCase("y")) {
                if (!cFacade.deleteEvent(eventID)) {
                    logger.severe("Failed to delete event. Retry...");
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return true;
    }

    private static void loadNotifications() {
        spacer(1);
        System.out.println("Notifications");


        List<BNotification> notifications = cFacade.retrieveNotifications(LoggedUser.getUserID());
        if (!notifications.isEmpty()) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getMessageType() == NotificationTypes.EVENT_ADDED) {
                    System.out.println(i + 1 + ". New event called " + cFacade.getEventNameByEventID(notifications.get(i).getEventID()) + " in your city!");
                } else if (notifications.get(i).getMessageType() == NotificationTypes.USER_EVENT_PARTICIPATION) {
                    System.out.println(i + 1 + ". New user " + cFacade.getUsernameByID(notifications.get(i).getNotifierID()) + " participating to your event " + cFacade.getEventNameByEventID(notifications.get(i).getEventID()));
                }
            }

            spacer(1);
            System.out.println("Press 1 to delete a notification or write /commands to view all possibile commands.");
            spacer(1);
        } else {
            System.out.println("No notifications to show.");
            System.out.println("Write /commands to view all possibile commands.");
        }

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //verifico comandi generali
                if (commands.contains(value)) {
                    handleCommand(value);
                }

                //cancello la notifica
                if (value.equals("1")) {
                    valid = true;
                    deleteNotification(notifications);

                    //ricarico le notifiche aggiornate
                    loadNotifications();
                }
            } catch (IOException e) {
                logger.severe("IOException in loadNotifications");
            }
        } while (!valid);
    }

    private static void deleteNotification(List<BNotification> notifications) {
        boolean valid = false;

        spacer(1);
        System.out.print("Choose the number of the notification you want to delete: ");

        try {
            do {
                String value = READER.readLine();

                // Converte la stringa in un numero intero
                int index = getNotificationIndex(value);
                if (index == -1) {
                    continue;
                }
                // Verifica se l'indice è valido
                if (index >= 1 && index <= notifications.size()) {
                    cFacade.deleteNotification(notifications.get(index - 1).getNotificationID(), notifications, index - 1);
                    valid = true;

                    //ricarica notifications
                    loadNotifications();
                } else {
                    logger.severe(() -> "Index range non valid. If must be between 1 and " + (notifications.size()));
                }
            } while (!valid);
        } catch (IOException e) {
            logger.severe("IOException in deleteNotification");
        }
    }

    private static int getNotificationIndex(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.severe("Index format not valid...");
            return -1;
        }
    }

    private static void loadEvents() {
        spacer(3);

        System.out.println("Your events");
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            System.out.println("1. Events with planned participation\n" + "2. Past events");
            cycleForUserLoadEvents();

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println("1. Events created by you\n" + "2. Past events\n" + "3. Analytics");
            cycleForOrgLoadEvents();

        }
    }

    private static void cycleForOrgLoadEvents() {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null && value.equals("1")) {
                valid = true;
                showEvents(false);
            } else if (value != null && value.equals("2")) {
                valid = true;
                showEvents(true);
            } else if (value != null && value.equals("3")) {
                //apro la pagina analytics con la lista degli eventi passati
                showAnalytics(showEvents(true));
            }
        }
    }

    private static void cycleForUserLoadEvents() {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null && value.equals("1")) {
                valid = true;
                showEvents(false);
            } else if (value != null && value.equals("2")) {
                valid = true;
                showEvents(true);
            }
        }
    }

    private static void showAnalytics(List<BEvent> pastEvents) {
        spacer(3);
        System.out.println("Analytics");
        spacer(1);
        printEventsList(pastEvents);
        spacer(1);
        System.out.print("Write event name to view its analytics:");
        System.out.print(COMMANDS_HELP);

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //vedo eventuali comandi generali
                if (commands.contains(value)) {
                    handleCommand(value);
                }

                for (BEvent bEvent : pastEvents) {
                    if (bEvent.getEventName().equals(value)) {
                        valid = true;
                        printEventAnalytics(bEvent);
                    }
                }
            } catch (IOException e) {
                logger.severe("IOException in showAnalytics");
            }
        } while (!valid);
        spacer(1);

    }

    private static void printEventAnalytics(BEvent bEvent) {
        System.out.println("Event analytics:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println(CITY + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());

        int participations = cFacade.retrieveParticipationsToEvent(bEvent.getEventID());
        if (timesClicked == null) {
            Random rand = new SecureRandom();
            timesClicked = rand.nextInt(5000 - participations + 1) + participations;
            nParticipants = rand.nextInt(participations + 1);
        }

        System.out.println("Participants: " + participations);
        System.out.println("Planned participations: " + nParticipants);
        System.out.println("Times clicked: " + timesClicked);

        spacer(1);
        System.out.println(GOBACK);

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //verifico comandi generali
                if (commands.contains(value)) {
                    handleCommand(value);
                }

                if (value.equals("1")) {
                    loadEvents();
                    valid = true;
                }
            } catch (IOException e) {
                logger.severe("IOException in analytics input acquisition");
            }
        } while (!valid);
    }

    private static List<BEvent> showEvents(boolean isPassed) {
        spacer(3);
        List<BEvent> eventList = new ArrayList<>();

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {

            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsUser");
            eventList = populateEventList(isPassed, tempList); //isPassed = true -> show past events; isPassed = false -> show upcoming events
            checksOnEventListUser(eventList, isPassed);

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsOrg");
            eventList = populateEventList(isPassed, tempList);
            checksOnEventListOrg(eventList, isPassed);

        }
        spacer(3);

        return eventList;
    }

    private static void checksOnEventListOrg(List<BEvent> eventList, boolean isPassed) {
        if (!eventList.isEmpty()) {

            if (!isPassed) {
                System.out.println("Your organized events. \nWrite event name to show event info, edit or delete it!");
            } else {
                System.out.println("Your past events. \nWrite event name to show event info!");
            }
            eventsPrintAndAcquireInput(eventList,isPassed);

        } else {
            System.out.println("No organized events!");
            System.out.println("To add a new event go to /home and write 1 to add event.\n Then follow instructions to finish the procedure.");
            spacer(1);

            waitCommands();
        }


    }

    private static void eventsPrintAndAcquireInput(List<BEvent> eventList, boolean isPassed) {
        printEventsList(eventList);
        spacer(1);
        System.out.println(COMMANDS_HELP);
        spacer(1);

        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            valid = checkIfEventNameEntered(value, eventList,isPassed);
        }
    }


    private static void checksOnEventListUser(List<BEvent> eventList, boolean isPassed) {
        if (!eventList.isEmpty()) {
            if (!isPassed) {
                System.out.println("Your events. \nWrite event name to show event info!");
            } else {
                System.out.println("Your past events. \nWrite event name to show event info!");
            }
            eventsPrintAndAcquireInput(eventList,isPassed);
        } else {
            System.out.println("No planned events!");
            System.out.println("To plan participation to an event in your city go to /home and write the name of an available event.\n Then follow instructions to Plan event participation.");
            spacer(1);

            waitCommands();
        }
    }

    private static boolean checkIfEventNameEntered(String value, List<BEvent> eventList, boolean isPassed) {
        for (BEvent bEvent : eventList) {
            if (bEvent.getEventName().equals(value)) {
                showEventInfo(bEvent, "YourEventsUser",isPassed);
                return true;
            }
        }
        return false;
    }

    private static List<BEvent> populateEventList(boolean isPassed, List<BEvent> tempList) {
        List<BEvent> eventList = new ArrayList<>();
        if (!isPassed) {
            for (BEvent bEvent : tempList) {
                String eventDateString = bEvent.getEventDate();
                LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));

                if (LocalDate.now().isBefore(date)) {
                    //aggiungo alla lista solo gli eventi futuri (today is before event date)
                    eventList.add(bEvent);
                }
            }
        } else {
            for (BEvent bEvent : tempList) {
                String eventDateString = bEvent.getEventDate();
                LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));

                if (!LocalDate.now().isBefore(date)) {
                    //aggiungo alla lista solo gli eventi passati (today is NOT before event date)
                    eventList.add(bEvent);
                }
            }
        }
        return eventList;
    }

    private static void printEventsList(List<BEvent> eventList) {
        for (int i = 0; i < eventList.size(); i++) {
            System.out.println(i + 1 + ". " + eventList.get(i).getEventName());
        }
    }

    private static void waitCommands() {
        boolean waiting = true;
        try {
            while (waiting) {
                String value = READER.readLine();
                if (commands.contains(value)) {
                    waiting = false;
                    handleCommand(value);
                }
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static void loadApp() {

        boolean valid = false;
        while (!valid) {
            spacer(3);
            System.out.println(ASCII_LOGO);
            System.out.println("Login or register");
            System.out.println("1. Login");
            System.out.println("2. Login with Google");
            System.out.println("3. Register new account");

            String inputLine = acquireInput();
            valid = handleFrontPageInput(inputLine);
            try {
                // Chiudi il BufferedReader
                READER.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }


    private static boolean handleFrontPageInput(String inputLine) {
        if (inputLine != null && inputLine.equals("1")) {
            return loginUsernameAndPassword();
        } else if (inputLine != null && inputLine.equals("2")) {
            return googleLogin();
        } else if (inputLine != null && inputLine.equals("3")) {
            return registerUserStandard();
        }
        return false;
    }

    private static boolean registerUserStandard() {
        try {
            if (registerUser(false) == 1) {
                spacer(1);
                System.out.println("Loading home page...");
                spacer(1);
                loadHome();
                return true;
            }
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            return false;
        }
        return false;
    }

    private static boolean googleLogin() {
        System.out.println("Opening google login page...");
        spacer(1);
        System.out.println("Write authorization code to continue login: ");

        //avvio la schermata di login
        GoogleLogin.initGoogleLogin();
        String authCode = acquireInput();
        try {
            //login user
            int res = cFacade.loginUser(bUserData, true, authCode);
            spacer(1);
            if (res == 1) {
                System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                loadHome();
                return true;
            } else {
                System.out.println("User not registered with Google. \nYou will be redirected to registration page.");
                if (registerUser(true) == 1) {
                    System.out.println("Successfully registered with Google");
                }
                //ricarico tutta l'interfaccia
                loadApp();
                return true;
            }
        } catch (InvalidTokenValue e) {
            logger.info("Invalid token value...");
            //ricarico l'interfaccia
            loadApp();
        }
        return false;
    }

    private static boolean loginUsernameAndPassword() {
        while (true) {
            System.out.println("Insert username: ");
            setUsernameToBean(acquireInput());

            System.out.println("Insert password: ");
            setPasswordToBean(acquireInput());
            int res = 0;
            try {
                res = cFacade.loginUser(bUserData, false, null);
                spacer(1);
                if (res == 1) {
                    System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                    loadHome();
                    return true;
                } else {
                    System.out.println("Wrong credentials. Retry...");
                }
            } catch (InvalidTokenValue e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private static void setPasswordToBean(String s) {
        try {
            bUserData.setPassword(s);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void setUsernameToBean(String s) {
        try {
            bUserData.setUsername(s);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeControllers();

        System.out.print("Choose the persistence logic!\n Type 'JDBC' or 'FileSystem' -> ");
        try {
            boolean valid = false;

            do {
                String val = READER.readLine();

                if (val.equalsIgnoreCase("jdbc") || val.equalsIgnoreCase("filesystem")) {
                    valid = true;
                    if (val.equalsIgnoreCase("jdbc")) {
                        PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
                    } else {
                        PersistenceClass.setPersistenceType(PersistenceTypes.FILE_SYSTEM);
                    }
                } else {
                    System.out.println("Invalid option. Please enter 'JDBC' or 'FileSystem'.");
                }
            } while (!valid);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }

        loadApp();
    }

    private static int registerUser(boolean isGoogleAuth) {
        try {


            spacer(1);
            System.out.println("Registration procedure");
            spacer(1);

            populateRegistrationBean(isGoogleAuth);


            System.out.println("First name: ");
            bUserData.setFirstName(READER.readLine());
            System.out.println("Last name: ");
            bUserData.setLastName(READER.readLine());
            System.out.println("Gender: ");
            System.out.println("a. Male\n" + "b. Female\n" + "c. Other");
            chooseGenderCycle();

            System.out.println("Province: ");
            List<String> provinces = cFacade.getProvincesList();
            boolean valid = false;
            do {
                String val = READER.readLine();
                if (provinces.contains(val)) {
                    bUserData.setProvince(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println(CITY);
            List<String> cities = cFacade.getCitiesList(bUserData.getProvince());
            do {
                String val = READER.readLine();
                if (cities.contains(val)) {
                    bUserData.setCity(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;


            System.out.println("Birth date: dd-MM-yyyy");
            acquireDateCycle();

            System.out.println("User type: ");
            System.out.println("a. USER\n" + "b. ORGANIZER");
            do {
                String val = READER.readLine();
                val = val.toUpperCase();
                if (val.equals(UserTypes.USER.toString()) || val.equals(UserTypes.ORGANIZER.toString())) {
                    bUserData.setType(UserTypes.valueOf(val));
                    valid = true;
                }
            } while (!valid);

            if (cFacade.registerUser(bUserData)) {
                System.out.println("User registration completed successfully! Return to login page.");
            } else {
                System.out.println("Error in registration procedure. Retry...");
                return 0;
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (UsernameAlreadyTaken e) {
            logger.warning("Username already taken! Change it and retry registration...");
            return 0;
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
            return 0;
        }
        return 1;
    }

    private static boolean convertDateAndSetUserBean(String dateString, DateTimeFormatter formatter) {
        try {
            // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
            LocalDate birthDate = LocalDate.parse(dateString, formatter);
            bUserData.setBirthDate(birthDate);
            return true;
        } catch (Exception e) {
            // Gestire il caso in cui l'input non sia nel formato corretto
            logger.severe("Invalid birth date format.");
            return false;
        }
    }

    private static void acquireDateCycle() {
        boolean valid = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        while (!valid) {
            String val = acquireInput();
            valid = convertDateAndSetUserBean(val, formatter);
        }
    }

    private static void chooseGenderCycle() {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null) {
                switch (val) {
                    case "a":
                        setGenderToBean("Male");
                        valid = true;
                        break;
                    case "b":
                        setGenderToBean("Female");
                        valid = true;
                        break;
                    case "c":
                        setGenderToBean("Other");
                        valid = true;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static void setGenderToBean(String val) {
        try {
            bUserData.setGender(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void populateRegistrationBean(boolean isGoogleAuth) {
        if (!isGoogleAuth) {
            //classic registration
            System.out.println("Username: ");
            setUsernameToBean(acquireInput());

            System.out.println("Password: ");
            setPasswordToBean(acquireInput());
        } else {
            setPasswordToBean("google_account_hidden_psw");
        }
    }

    @Override
    public void showNotification(NotificationTypes notificationType) {
        String value = null;
        if (notificationType.equals(NotificationTypes.EVENT_ADDED)) {
            value = "new event in your city!";
        } else if (notificationType.equals(NotificationTypes.USER_EVENT_PARTICIPATION)) {
            value = "new user participation to your event!";
        }
        spacer(1);
        System.out.println("New notification: " + value);
        spacer(1);
    }

    @Override
    public void addMessageToChat(BMessage messageBean) {
        //
    }
}
