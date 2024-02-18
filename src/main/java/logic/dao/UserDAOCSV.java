package logic.dao;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import logic.controllers.NotiObserverClass;
import logic.controllers.ObserverClass;
import logic.exceptions.DuplicateRecordException;
import logic.model.MUser;
import logic.utils.LoggedUser;
import logic.utils.UserTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import static logic.view.EssentialGUI.logger;

public class UserDAOCSV implements UserDAO {
    private static final String CSV_DB_NAME = "DBUsers.csv";

    private static int entriesNumber = 0;

    //file descriptor
    private File fd;


    private static class UserAttributesOrder {
        public static int getIndex_UserID() {
            return 0;
        }

        public static int getIndex_UserName() {
            return 1;
        }

        public static int getIndex_Password() {
            return 2;
        }

        public static int getIndex_FirstName() {
            return 3;
        }

        public static int getIndex_LastName() {
            return 4;
        }

        public static int getIndex_BirthDate() {
            return 5;
        }

        public static int getIndex_Gender() {
            return 6;
        }

        public static int getIndex_Province() {
            return 7;
        }

        public static int getIndex_City() {
            return 8;
        }

        public static int getIndex_UserType() {
            return 9;
        }

        public static int getIndex_UserStatus() {
            return 10;
        }
    }

    public UserDAOCSV() throws IOException {
        //inizializzo il file descriptor
        this.fd = new File(CSV_DB_NAME);

        //creo il file se non esiste
        if (!fd.exists()) {
            boolean res = fd.createNewFile();
            if (res) {
                logger.finest("Created new file");
            } else {
                logger.finest("File already exists");
            }
        }
    }

    @Override
    public int checkLoginInfo(MUser usrMod, boolean isGoogleAccount) {
        int ret = 0;

        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            if (!isGoogleAccount) {
                //cerco le credenziali username e password nel file CSV
                while ((record = csvReader.readNext()) != null) {
                    boolean recordFound = (record[UserAttributesOrder.getIndex_UserName()].equals(usrMod.getUserName()) && record[UserAttributesOrder.getIndex_Password()].equals(usrMod.getPassword()));
                    if (recordFound) {
                        //setto lo user model che gli ho passato
                        getLoggedUser(record, usrMod);
                        //imposto il valore di ritorno ed esco dal while
                        ret = 1;
                        break;
                    }
                }
            } else {
                //cerco solo l'email Google se è presente nel file CSV
                while ((record = csvReader.readNext()) != null) {
                    boolean recordFound = record[UserAttributesOrder.getIndex_UserName()].equals(usrMod.getUserName());
                    if (recordFound) {
                        //setto lo user model che gli ho passato
                        getLoggedUser(record, usrMod);
                        //imposto il valore di ritorno ed esco dal while
                        ret = 1;
                        break;
                    }
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    private void getLoggedUser(String[] record, MUser usrMod) {
        usrMod.setId(Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]));
        usrMod.setFirstName(record[UserAttributesOrder.getIndex_FirstName()]);
        usrMod.setLastName(record[UserAttributesOrder.getIndex_LastName()]);
        usrMod.setBirthDate(record[UserAttributesOrder.getIndex_BirthDate()]);
        usrMod.setGender(record[UserAttributesOrder.getIndex_Gender()]);
        usrMod.setProvince(record[UserAttributesOrder.getIndex_Province()]);
        usrMod.setCity(record[UserAttributesOrder.getIndex_City()]);

        String usrtypeString = record[UserAttributesOrder.getIndex_UserType()];
        if (usrtypeString.equals(UserTypes.USER.toString())) {
            usrMod.setUserType(UserTypes.USER);
        } else {
            usrMod.setUserType(UserTypes.ORGANIZER);
        }
    }

    @Override
    public String getUserCityByID(int usrId) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]) == usrId) {
                    return record[UserAttributesOrder.getIndex_City()];
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void registerUser(MUser usrModel) throws DuplicateRecordException {
        boolean duplicatedUserName = checkDuplicatedUserName(usrModel.getUserName());

        try {
            if (!duplicatedUserName) {
                CSVWriter csvWriter;
                try {
                    csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String[] record = new String[11];

                record[UserAttributesOrder.getIndex_UserID()] = String.valueOf(entriesNumber++);
                record[UserAttributesOrder.getIndex_UserName()] = usrModel.getUserName();
                record[UserAttributesOrder.getIndex_Password()] = usrModel.getPassword();
                record[UserAttributesOrder.getIndex_FirstName()] = usrModel.getFirstName();
                record[UserAttributesOrder.getIndex_LastName()] = usrModel.getLastName();
                record[UserAttributesOrder.getIndex_BirthDate()] = usrModel.getBirthDate();
                record[UserAttributesOrder.getIndex_Gender()] = usrModel.getGender();
                record[UserAttributesOrder.getIndex_Province()] = usrModel.getProvince();
                record[UserAttributesOrder.getIndex_City()] = usrModel.getCity();
                record[UserAttributesOrder.getIndex_UserType()] = usrModel.getUserType().toString();
                record[UserAttributesOrder.getIndex_UserStatus()] = "Offline";

                //chiudo il csvWrites e flusho
                csvWriter.writeNext(record);
                csvWriter.flush();
                csvWriter.close();
            } else {
                throw new DuplicateRecordException("User with same username already registered!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkDuplicatedUserName(String userName) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (record[UserAttributesOrder.getIndex_UserName()].equals(userName)) {
                    return true;
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public int getUserIDByUsername(String username) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (record[UserAttributesOrder.getIndex_UserName()].equals(username)) {
                    return Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]);
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void setStatus(int userID) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]) == userID) {
                    record[UserAttributesOrder.getIndex_UserStatus()] = LoggedUser.getStatus();
                    break;
                }
            }

            CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, false)));

            //chiudo il csvWrites e flusho
            csvWriter.writeNext(record);
            csvWriter.flush();
            csvWriter.close();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int changeCity(int userID, String province, String city) {
        int res = 0;
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]) == userID) {
                    record[UserAttributesOrder.getIndex_Province()] = province;
                    record[UserAttributesOrder.getIndex_City()] = city;
                    res = 1;
                    break;
                }
            }

            CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, false)));

            //chiudo il csvWrites e flusho
            csvWriter.writeNext(record);
            csvWriter.flush();
            csvWriter.close();


        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public String getUsernameByID(int userID) {
        String userName = null;
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]) == userID) {
                    userName = record[UserAttributesOrder.getIndex_UserName()];
                    break;
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return userName;
    }

    //gestiti dal server

    @Override
    public void populateObsByCity(Map<String, List<NotiObserverClass>> obsByCity) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (record[UserAttributesOrder.getIndex_UserType()].equals("USER")) {
                    NotiObserverClass usrObs = new NotiObserverClass(Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]), null);
                    obsByCity.computeIfAbsent(record[UserAttributesOrder.getIndex_City()], k -> new ArrayList<>()).add(usrObs);
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void populateConnUsers(Map<Integer, Boolean> connUsers) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (record[UserAttributesOrder.getIndex_UserType()].equals("USER")) {
                    connUsers.put(Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]), false);
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void populateConnOrganizers(Map<Integer, Boolean> connOrganizers) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)));
            String[] record;

            while ((record = csvReader.readNext()) != null) {
                if (record[UserAttributesOrder.getIndex_UserType()].equals("ORGANIZER")) {
                    connOrganizers.put(Integer.parseInt(record[UserAttributesOrder.getIndex_UserID()]), false);
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
