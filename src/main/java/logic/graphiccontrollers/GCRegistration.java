package logic.graphiccontrollers;

        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.scene.input.MouseEvent;

        import logic.beans.BCity;
        import logic.beans.BUserData;
        import logic.controllers.CRegistration;
        import logic.model.MCity;
        import logic.utils.Alerts;
        import logic.view.AlertPopup;
        import logic.view.EssentialGUI;

        import java.util.logging.Logger;

public class GCRegistration {

    @FXML
    private DatePicker birthDate;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private ChoiceBox<String> gender;

    @FXML
    private TextField cityField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button returnLogin;

    @FXML
    private RadioButton userRadio;

    @FXML
    private RadioButton organizerRadio;

    @FXML
    private ToggleGroup group;

    private AlertPopup alert;
    private EssentialGUI gui;
    private CRegistration registrationCtrl;
    private BUserData dataBean;

    private BCity cityBean;

    @FXML
    private ListView<String> cityListView;

    private static final Logger logger = Logger.getLogger(GCRegistration.class.getName());

    @FXML
    public void initialize() {
        this.alert = new AlertPopup();
        this.gui = new EssentialGUI();
        this.registrationCtrl = new CRegistration();
        this.gender.getItems().addAll("Male", "Female", "Other");
        this.group = new ToggleGroup();
        this.dataBean = new BUserData();

        this.cityBean = new BCity();
        setupCityInputListener();

        userRadio.setSelected(true);
        userRadio.setToggleGroup(group);
        organizerRadio.setToggleGroup(group);
    }
    @FXML
    public void registerControl(MouseEvent event) {
        try{
            this.dataBean.setUsername(this.emailField.getText());
            this.dataBean.setPassword(this.passwordField.getText());
            this.dataBean.setFirstName(this.firstNameField.getText());
            this.dataBean.setLastName(this.lastNameField.getText());
            this.dataBean.setGender(this.gender.getValue());
            this.dataBean.setBirthDate(this.birthDate.getValue());
            this.dataBean.setCity(this.cityField.getText());
            register(event);
        } catch (Exception e){
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }

    @FXML
    public void returnBack(MouseEvent event){
        gui.changeGUI(event, "Login.fxml");
    }

    private void register(MouseEvent event){
        try {
            if(userRadio.isSelected()){
                this.dataBean.setType(this.userRadio.getText());
            } else {
                this.dataBean.setType(this.organizerRadio.getText());
            }
            if(this.registrationCtrl.registerUserControl(this.dataBean)){
                this.alert.displayAlertPopup(Alerts.INFORMATION, "Successfully registered to NightPlan");
                this.gui.changeGUI(event, "Login.fxml");
            } else {
                this.alert.displayAlertPopup(Alerts.WARNING, "Registration procedure failed. Please retry...");
            }
        } catch (Exception e){
            this.alert.displayAlertPopup(Alerts.INFORMATION, "Cannot complete registration!");
        }
    }


    private void setupCityInputListener(){
        this.cityField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCityListView(newValue);
        });
    }
    private void updateCityListView(String input){
        if (input.isEmpty()) {
            // Se l'input è vuoto, svuota la lista
            this.cityBean.setField("");
        } else {
            // Altrimenti, chiedi al modello di ottenere le città corrispondenti all'input
            this.cityBean.setField(input);
            this.registrationCtrl.retrieveCities(cityBean);
        }
    }

}

