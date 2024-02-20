package logic.dao;

import com.opencsv.CSVWriter;
import logic.controllers.NotificationFactory;
import logic.model.Notification;
import logic.utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import static logic.view.EssentialGUI.logger;


public class NotificationDAOCSV implements NotificationDAO {

    private static final String CSV_DB_NAME = "DBNotifications.csv";

    private static int entriesNumber = 0;

    //file descriptor
    private File fd;

    private NotificationFactory notiFactory;

    //TODO: Fixare rimozione notifica dal DAO CSV
    private static class NotificationAttributesOrder {
        public static int getIndexNotificationID() {
            return 0;
        }

        public static int getIndexNotifiedID() {
            return 1;
        }

        public static int getIndexNotiType() {
            return 2;
        }

        public static int getIndexEventID() {
            return 3;
        }

        public static int getIndexNotifierID() {
            return 4;
        }
    }

    public NotificationDAOCSV() throws IOException {
        //creo la cartella che conterrà il CSV DB
        String folderName = "csvData";
        File folder = new File(folderName);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                logger.finest("New folder created successfully.");
            } else {
                logger.finest("Folder creation failed.");
            }
        } else {
            logger.finest("Folder already exists: " + folderName);
        }

        String filePath = folderName + File.separator + CSV_DB_NAME;

        //inizializzo il file descriptor
        this.fd = new File(filePath);

        //creo il file se non esiste
        if (!this.fd.exists()) {
            try {
                boolean res = this.fd.createNewFile();
                if (res) {
                    logger.finest("Created new file.");
                } else {
                    logger.finest("Creation of CSV file failed.");
                }
            } catch (IOException e) {
                logger.severe("Error while creating new file: " + e.getMessage());
            }
        } else {
            logger.finest("File CSV already exists in path: " + folderName + "/" + CSV_DB_NAME);
        }

        //aggiorno l'indice del file all'ultima entry aggiunta
        updateLastIndex();

        //inizializzo una notificationFactory
        this.notiFactory = new NotificationFactory();
    }

    private void updateLastIndex() {
        int count = 0;
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            //cerco le credenziali username e password nel file CSV
            while (csvReader.readNext() != null) {
                count++;
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        entriesNumber = count;
    }

    @Override
    public void addNotification(List<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID) {
        try (CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)))){
            String[] record = new String[5];

            for (Integer notifiedID : notifiedIDs) {
                record[NotificationAttributesOrder.getIndexNotificationID()] = String.valueOf(++entriesNumber);
                record[NotificationAttributesOrder.getIndexNotifiedID()] = String.valueOf(notifiedID);
                record[NotificationAttributesOrder.getIndexNotiType()] = notificationTypes.toString();
                record[NotificationAttributesOrder.getIndexEventID()] = String.valueOf(eventID);
                record[NotificationAttributesOrder.getIndexNotifierID()] = String.valueOf(LoggedUser.getUserID());

                //scrivo e aggiorno il file csv
                csvWriter.writeNext(record);
                csvWriter.flush();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while adding notification to user (csv)");
        }
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> notifications = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            String[] record;
            Notification msg;

            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[NotificationAttributesOrder.getIndexNotifiedID()]) == usrID) {
                    //id, type, event_id, notifier_id
                    if ((record[NotificationAttributesOrder.getIndexNotiType()]).equals(NotificationTypes.EVENT_ADDED.toString())) {
                        msg = notiFactory.createNotification(SituationType.LOCAL, NotificationTypes.EVENT_ADDED, usrID, Integer.parseInt(record[NotificationAttributesOrder.getIndexNotifierID()]), Integer.parseInt(record[NotificationAttributesOrder.getIndexEventID()]), Integer.parseInt(record[NotificationAttributesOrder.getIndexNotificationID()]), null, null, null);
                    } else {
                        msg = notiFactory.createNotification(SituationType.LOCAL, NotificationTypes.USER_EVENT_PARTICIPATION, usrID, Integer.parseInt(record[NotificationAttributesOrder.getIndexNotifierID()]), Integer.parseInt(record[NotificationAttributesOrder.getIndexEventID()]), Integer.parseInt(record[NotificationAttributesOrder.getIndexNotificationID()]), null, null, null);
                    }
                    notifications.add(msg);
                }
            }
        } catch (CsvValidationException | IOException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting notifications list by userID (csv)");
        }
        return notifications;
    }

    @Override
    public boolean deleteNotification(int notificationID) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter("csvData/temp.csv", false)));

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[NotificationAttributesOrder.getIndexNotificationID()]) != notificationID) {
                    // Scrivi solo le voci che non corrispondono all'ID della notifica da eliminare
                    csvWriter.writeNext(record);
                }
            }

            csvReader.close();
            csvWriter.close();

            Files.move(Paths.get("csvData/temp.csv"), Paths.get(fd.toURI()), StandardCopyOption.REPLACE_EXISTING);
        } catch (CsvValidationException | IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while removing notification from db (csv)");
            return false;
        }
        return true;
    }
}
