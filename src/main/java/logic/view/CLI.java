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
    private static final String CITY = "City: ";
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
        boolean valid = false;
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            do {
                showSettings(UserTypes.USER);
                spacer(1);
                String value = acquireInput();
                if (value!=null){
                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }
                    valid = handleSettingsInputUser(value);
                }
            } while (!valid);

        } else { //UserType == ORGANIZER
            do {
                showSettings(UserTypes.ORGANIZER);
                String value = acquireInput();
                if (value!=null){
                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }
                    valid = handleSettingsInputOrg(value);
                }
            } while (!valid);
        }
        spacer(2);
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
        String input = "";
        try {
            input = CLI.READER.readLine();
        } catch (IOException e) {
            logger.severe("Error during input acquisition");
            return null;
        }
        return input;
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
            if (value!=null){
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
            if (value!=null){
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
                showEventInfo(bEvent, "Home");
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
            LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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

        boolean valid = false;
        List<String> provinces = cFacade.getProvincesList();
        List<String> cities = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {

            System.out.println("Event name:");
            eventBean.setEventName(READER.readLine());

            System.out.println("Event province:");
            do {
                String val = READER.readLine();
                if (provinces.contains(val)) {
                    eventBean.setEventProvince(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: ");
            do {
                String val = READER.readLine();
                if (cities.contains(val)) {
                    eventBean.setEventCity(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event date:");
            do {
                String val = READER.readLine();
                try {
                    // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
                    LocalDate eventDate = LocalDate.parse(val, formatter);
                    eventBean.setEventDate(eventDate.format(formatter));
                    valid = true;
                } catch (Exception e) {
                    // Gestire il caso in cui l'input non sia nel formato corretto
                    logger.severe("Invalid date format.");
                }
            } while (!valid);
            valid = false;

            System.out.println("Event address:");
            eventBean.setEventAddress(READER.readLine());

            System.out.println("Choose music genre from list:");
            for (int i = 0; i < MUSIC_GENRES.length; i++) {
                System.out.println(i + 1 + ". " + MUSIC_GENRES[i]);
            }
            do {
                String val = READER.readLine();
                for (int i = 0; i < MUSIC_GENRES.length; i++) {

                    if (MUSIC_GENRES[i].contains(val)) {
                        valid = true;
                        eventBean.setEventMusicGenre(val);
                    }
                }
            } while (!valid);
            valid = false;

            System.out.println("Event time: HH:mm");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            do {
                String val = READER.readLine();
                try {
                    timeFormat.parse(val);
                } catch (ParseException e) {
                    logger.severe("Invalid time format.");
                    continue;
                }

                //qua ci arriva solo se il time viene parsato correttamente
                String[] parts = val.split(":");

                if (parts.length == 2) {
                    String hours = parts[0];
                    String minutes = parts[1];
                    eventBean.setEventTime(hours, minutes);
                    valid = true;
                }
            } while (!valid);

            //prendi file immagine
            byte[] fileData = pickFileData();
            if (fileData != null && filePath != null) {
                eventBean.setEventPicData(fileData);
            }
            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
            eventBean.setEventPicPath(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
    }

    private static void editEventBean(BEvent eventBean) {
        System.out.println("Edit event\n");
        spacer(1);
        System.out.println("Press enter to maintain previous value!");
        String newVal;

        boolean valid = false;
        List<String> provinces = cFacade.getProvincesList();
        List<String> cities = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {

            System.out.println("Event name: (previous: " + eventBean.getEventName() + ")");
            if (!(newVal = READER.readLine()).isEmpty()) {
                eventBean.setEventName(newVal);
            }

            System.out.println("Event province: (previous: " + eventBean.getEventProvince() + ")");
            do {
                if (!(newVal = READER.readLine()).isEmpty()) {
                    if (provinces.contains(newVal)) {
                        eventBean.setEventProvince(newVal);
                        valid = true;
                    }
                } else {
                    valid = true;
                }
            } while (!valid);
            valid = false;

            cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: (previous: " + eventBean.getEventCity() + ")");
            do {
                if (!(newVal = READER.readLine()).isEmpty()) {
                    if (cities.contains(newVal)) {
                        eventBean.setEventCity(newVal);
                        valid = true;
                    }
                } else {
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event date: (previous: " + eventBean.getEventDate() + ")");
            do {
                String val = READER.readLine();
                if (!val.isEmpty()) {
                    try {
                        // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
                        LocalDate eventDate = LocalDate.parse(val, formatter);
                        eventBean.setEventDate(eventDate.format(formatter));
                        valid = true;
                    } catch (Exception e) {
                        // Gestire il caso in cui l'input non sia nel formato corretto
                        logger.severe("Invalid date format.");
                    }
                } else {
                    //nothing changed
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event address: (previous: " + eventBean.getEventAddress() + ")");
            if (!(newVal = READER.readLine()).isEmpty()) {
                eventBean.setEventAddress(newVal);
            }

            System.out.println("Choose music genre from list: (previous: " + eventBean.getEventMusicGenre() + ")");
            for (int i = 0; i < MUSIC_GENRES.length; i++) {
                System.out.println(i + 1 + ". " + MUSIC_GENRES[i]);
            }
            do {
                String val = READER.readLine();
                if (!val.isEmpty()) {
                    for (int i = 0; i < MUSIC_GENRES.length; i++) {

                        if (MUSIC_GENRES[i].contains(val)) {
                            valid = true;
                            eventBean.setEventMusicGenre(val);
                        }
                    }
                } else {
                    //nothing changed
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Event time: HH:mm (previous: " + eventBean.getEventTime() + ")");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            do {
                String val = READER.readLine();

                if (!val.isEmpty()) {
                    try {
                        timeFormat.parse(val);
                    } catch (ParseException e) {
                        logger.severe("Invalid time format.");
                        continue;
                    }

                    //qua ci arriva solo se il time viene parsato correttamente
                    String[] parts = val.split(":");

                    if (parts.length == 2) {
                        String hours = parts[0];
                        String minutes = parts[1];
                        eventBean.setEventTime(hours, minutes);
                        valid = true;
                    }
                } else {
                    //nothing changed
                    valid = true;
                }
            } while (!valid);

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
            throw new RuntimeException(e);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
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

    private static void showEventInfo(BEvent bEvent, String lastPage) {
        System.out.println("Event info:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println(CITY + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            spacer(1);
            boolean isEventParticipated = cFacade.checkPreviousEventParticipation(bEvent);
            if (isEventParticipated) {
                System.out.println("1. Go back\n" +
                        "2. Remove event participation");
            } else {
                System.out.println("1. Go back\n" +
                        "2. Plan event participation");
            }

            boolean valid = false;
            do {
                try {
                    String value = READER.readLine();

                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }

                    switch (value) {
                        case "1":
                            valid = true;
                            if (lastPage.equals("Home")) {
                                //torno nella home
                                loadHome();
                            } else if (lastPage.equals("YourEventsUser")) {
                                loadEvents();
                            }
                            break;
                        case "2":
                            valid = true;
                            if (isEventParticipated) {
                                if (cFacade.removeEventParticipation(bEvent)) {
                                    System.out.println("Event participation remove successfully!");
                                } else {
                                    logger.severe("Event participation removal failed!");
                                }
                            } else {
                                if (cFacade.participateToEvent(bEvent)) {
                                    System.out.println("Event participation successful!");
                                } else {
                                    logger.severe("Event participation failed!");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (EventAlreadyDeleted e) {
                    logger.severe(e.getMessage());
                    System.out.println("Press 1 to go back");
                }
                System.out.println("Press 1 to go back");
            } while (!valid);
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            spacer(1);
            System.out.println("1. Go back\n" +
                    "2. Edit event\n" +
                    "3. Delete event");

            boolean valid = false;
            do {
                try {
                    String value = READER.readLine();

                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }

                    switch (value) {
                        case "1":
                            loadEvents();
                            valid = true;
                            break;
                        case "2":
                            //opens procedure to edit event
                            editEventBean(bEvent);
                            try {
                                if (cFacade.editEvent(bEvent)) {
                                    System.out.println("Event edited successfully!");
                                    valid = true;
                                } else {
                                    logger.severe("Event edit failed!");
                                }
                            } finally {
                                //sono organizer torno per forza nella pagina degli eventi creati
                                loadEvents();
                            }
                            break;
                        case "3":
                            spacer(1);
                            try {
                                if (deleteEvent(bEvent.getEventID())) {
                                    System.out.println("Event deleted successfully!");
                                } else {
                                    logger.severe("Event deletion failed!");
                                }
                            } finally {
                                //sono organizer torno per forza nella pagina degli eventi creati
                                loadEvents();
                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (!valid);
        }
    }

    private static boolean deleteEvent(int eventID) {
        System.out.println("Are you sure to delete this event? Write 'y' to confirm or anything else to cancel.");
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
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
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
                int index;
                try {
                    index = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    logger.severe("Index format not valid...");
                    continue;
                }

                // Verifica se l'indice è valido
                if (index >= 1 && index <= notifications.size()) {
                    cFacade.deleteNotification(notifications.get(index - 1).getNotificationID(), notifications, index - 1);
                    valid = true;

                    //ricarica notifications
                    loadNotifications();
                } else {
                    logger.severe("Index range non valid. If must be between 1 and " + (notifications.size()));
                }
            } while (!valid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadEvents() {
        spacer(3);
        ArrayList<BEvent> eventList;
        spacer(1);

        System.out.println("Your events");
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            System.out.println("1. Events with planned participation\n" + "2. Past events");

            boolean valid = false;
            do {
                try {
                    String value = READER.readLine();

                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }

                    if (value.equals("1")) {
                        valid = true;
                        showEvents(false);
                    } else if (value.equals("2")) {
                        valid = true;
                        showEvents(true);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (!valid);

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println("1. Events created by you\n" + "2. Past events\n" + "3. Analytics");

            boolean valid = false;
            do {
                try {
                    String value = READER.readLine();

                    //verifico comandi generali
                    if (commands.contains(value)) {
                        handleCommand(value);
                    }

                    switch (value) {
                        case "1":
                            valid = true;
                            showEvents(false);
                            break;
                        case "2":
                            valid = true;
                            showEvents(true);
                            break;
                        case "3":
                            //apro la pagina analytics con la lista degli eventi passati
                            showAnalytics(showEvents(true));
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (!valid);
        }


    }

    private static void showAnalytics(ArrayList<BEvent> pastEvents) {
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
                throw new RuntimeException(e);
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
        System.out.println("1. Go back\n");

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
                throw new RuntimeException(e);
            }
        } while (!valid);
    }

    private static ArrayList<BEvent> showEvents(boolean isPassed) {
        spacer(3);
        ArrayList<BEvent> eventList = new ArrayList<>();
        spacer(1);

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {

            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsUser");

            if (!isPassed) {
                for (BEvent bEvent : tempList) {
                    String eventDateString = bEvent.getEventDate();
                    LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (LocalDate.now().isBefore(date)) {
                        //aggiungo alla lista solo gli eventi passati (today is NOT before event date)
                        eventList.add(bEvent);
                    }
                }
            } else {
                for (BEvent bEvent : tempList) {
                    String eventDateString = bEvent.getEventDate();
                    LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (!LocalDate.now().isBefore(date)) {
                        //aggiungo alla lista solo gli eventi futuri (today is before event date)
                        eventList.add(bEvent);
                    }
                }
            }

            if (!eventList.isEmpty()) {
                if (!isPassed) {
                    System.out.println("Your events. \nWrite event name to show event info!");
                } else {
                    System.out.println("Your past events. \nWrite event name to show event info!");
                }
                printEventsList(eventList);

                spacer(1);
                System.out.println(COMMANDS_HELP);
                spacer(1);

                boolean valid = false;
                do {
                    try {
                        String value = READER.readLine();

                        //vedo eventuali comandi generali
                        if (commands.contains(value)) {
                            handleCommand(value);
                        }

                        for (BEvent bEvent : eventList) {
                            if (bEvent.getEventName().equals(value)) {
                                valid = true;
                                showEventInfo(bEvent, "YourEventsUser");
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } while (!valid);
            } else {
                System.out.println("No planned events!");
                System.out.println("To plan participation to an event in your city go to /home and write the name of an available event.\n Then follow instructions to Plan event participation.");
                spacer(1);

                waitCommands();
            }
            spacer(3);
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsOrg");

            if (!isPassed) {
                for (BEvent bEvent : tempList) {
                    String eventDateString = bEvent.getEventDate();
                    LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (LocalDate.now().isBefore(date)) {
                        //aggiungo alla lista solo gli eventi passati (today is NOT before event date)
                        eventList.add(bEvent);
                    }
                }
            } else {
                for (BEvent bEvent : tempList) {
                    String eventDateString = bEvent.getEventDate();
                    LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (!LocalDate.now().isBefore(date)) {
                        //aggiungo alla lista solo gli eventi futuri (today is before event date)
                        eventList.add(bEvent);
                    }
                }
            }

            if (!eventList.isEmpty()) {
                System.out.println("Your organized events. \nWrite event name to show event info, edit or delete it!");
                printEventsList(eventList);

                spacer(1);
                System.out.println(COMMANDS_HELP);
                spacer(1);

                boolean valid = false;
                do {
                    try {
                        String value = READER.readLine();

                        //vedo eventuali comandi generali
                        if (commands.contains(value)) {
                            handleCommand(value);
                        }

                        for (BEvent bEvent : eventList) {
                            if (bEvent.getEventName().equals(value)) {
                                valid = true;
                                showEventInfo(bEvent, "YourEventsOrg");
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } while (!valid);
            } else {
                System.out.println("No organized events!");
                System.out.println("To add a new event go to /home and write 1 to add event.\n Then follow instructions to finish the procedure.");
                spacer(1);

                waitCommands();
            }
            spacer(3);
        }
        return eventList;
    }

    private static void printEventsList(ArrayList<BEvent> eventList) {
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
            throw new RuntimeException(e);
        }
    }

    public static void loadApp() {
        try {
            boolean valid = false;
            while (!valid) {
                spacer(3);
                System.out.println(ASCII_LOGO);
                System.out.println("Login or register");
                System.out.println("1. Login");
                System.out.println("2. Login with Google");
                System.out.println("3. Register new account");

                String inputLine = READER.readLine();
                switch (inputLine) {
                    case "1":
                        while (true) {
                            System.out.println("Insert username: ");
                            bUserData.setUsername(READER.readLine());
                            System.out.println("Insert password: ");
                            bUserData.setPassword(READER.readLine());


                            //login user
                            int res = 0;
                            try {
                                res = cFacade.loginUser(bUserData, false, null);

                                System.out.println();
                                if (res == 1) {
                                    System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                                    valid = true;
                                    break;
                                } else {
                                    System.out.println("Wrong credentials. Retry...");
                                }
                            } catch (RuntimeException e) {
                                logger.severe("Runtime exception: " + e.getMessage());
                                break;
                            } catch (InvalidTokenValue e) {
                                throw new RuntimeException(e);
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

                                String authCode = READER.readLine();

                                try {

                                    //login user
                                    int res = cFacade.loginUser(bUserData, true, authCode);

                                    System.out.println();
                                    if (res == 1) {
                                        System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                                        valid = true;
                                        break;
                                    } else {
                                        System.out.println("User not registered with Google. \nYou will be redirected to registration page.");
                                        if (registerUser(true) == 1) {
                                            System.out.println("Successfully registered with Google");
                                        }

                                        //ricarico tutta l'interfaccia
                                        loadApp();
                                    }
                                } catch (InvalidTokenValue e) {
                                    logger.info("Invalid token value...");

                                    //ricarico l'interfaccia
                                    loadApp();
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        loadHome();
                        break;
                    case "3":
                        try {
                            if (registerUser(false) == 1) {
                                spacer(1);
                                System.out.println("Loading home page...");
                                spacer(1);
                                loadHome();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (InvalidValueException e) {
            throw new RuntimeException(e);
        } catch (TextTooLongException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // Chiudi il BufferedReader
                READER.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
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
            throw new RuntimeException(e);
        }

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
                bUserData.setUsername(READER.readLine());
                System.out.println("Password: ");
                bUserData.setPassword(READER.readLine());
            } else {
                bUserData.setPassword("google_account_hidden_psw");
            }

            System.out.println("First name: ");
            bUserData.setFirstName(READER.readLine());
            System.out.println("Last name: ");
            bUserData.setLastName(READER.readLine());
            System.out.println("Gender: ");
            System.out.println("a. Male\n" + "b. Female\n" + "c. Other");
            do {
                String val = READER.readLine();
                bUserData.setGender(val);
                if (val.equals("a") || val.equals("b") || val.equals("c")) {
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println("Province: ");
            List<String> provinces = cFacade.getProvincesList();
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            do {
                String val = READER.readLine();
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
            throw new RuntimeException(e);
        } catch (UsernameAlreadyTaken e) {
            logger.warning("Username already taken! Change it and retry registration...");
            return 0;
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
        return 1;
    }

    @Override
    public void showNotification(NotificationTypes notificationType) {
        String value = null;
        switch (notificationType) {
            case EVENT_ADDED:
                value = "new event in your city!";
                break;
            case USER_EVENT_PARTICIPATION:
                value = "new user participation to your event!";
                break;
        }
        logger.info("New notification: " + value);

    }

    @Override
    public void addMessageToChat(BMessage messageBean) {
        //
    }
}
