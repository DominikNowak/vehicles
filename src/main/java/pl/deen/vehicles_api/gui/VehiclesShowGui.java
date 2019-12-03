package pl.deen.vehicles_api.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import pl.deen.vehicles_api.model.Vehicle;
import pl.deen.vehicles_api.service.VehicleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Route("vehicles-gui")
public class VehiclesShowGui extends VerticalLayout {
    private VehicleService vehicleService;
    private Grid<Vehicle> grid;
    private Dialog dialog;
    private NumberField idSearchField;
    private Button idSearchButton;
    private ComboBox colorSearchComboBox;
    private Button colorSearchButton;
    private NumberField yearFromSearchField;
    private NumberField yearToSearchField;
    private Button yearSearchButton;
    private NumberField idDeleteField;
    private Button idDeleteButton;
    private Button showAllButton;
    private Label addNewLabel;
    private TextField textFieldMark;
    private TextField textFieldModel;
    private ComboBox comboBoxColor;
    private NumberField numberFieldYear;
    private Button buttonAdd;
    private LocalDate localDate = LocalDate.now();
    private List<String> colorList = new ArrayList<>(Arrays.asList(
            "biały", "czarny", "czerwony", "granatowy", "niebieski", "srebrny", "zielony", "złoty", "żółty"));

    public VehiclesShowGui(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
        grid = new Grid<>();
        refreshGrid(getAllVehicles());
        dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("50px");


        //SEARCH ID
        idSearchField = new NumberField("Szukane ID:");
        idSearchButton = new Button("Wyszukaj", e -> findVehicleById());
        Div idSearchDiv = new Div(idSearchField, idSearchButton);


        //SEARCH COLOR
        colorSearchComboBox = new ComboBox("Szukany kolor:");
        colorSearchComboBox.setItems(colorList);
        colorSearchButton = new Button("Wyszukaj", e -> findVehiclesByColor());
        Div colorSearchDiv = new Div(colorSearchComboBox, colorSearchButton);


        //SEARCH YEAR
        yearFromSearchField = new NumberField("Wyszukaj od roku:");
        yearFromSearchField.setValue(1950d);
        yearFromSearchField.setHasControls(true);
        yearFromSearchField.setMin(1950);
        yearFromSearchField.setMax(localDate.getYear());
        yearToSearchField = new NumberField("do roku:");
        yearToSearchField.setValue((double)localDate.getYear());
        yearToSearchField.setHasControls(true);
        yearToSearchField.setMin(1950);
        yearToSearchField.setMax(localDate.getYear());
        yearSearchButton = new Button("Wyszukaj", e -> findVehiclesByYear());
        Div yearSearchDiv = new Div(yearFromSearchField, yearToSearchField, yearSearchButton);


        //DELETE
        idDeleteField = new NumberField("Usuwane ID:");
        idDeleteButton = new Button("Usuń", e -> deleteVehicleById());
        Div deleteDiv = new Div(idDeleteField, idDeleteButton);


        //SHOW ALL
        showAllButton = new Button("Pokaż wszystkie", e -> refreshGrid(getAllVehicles()));


        //ADD NEW
        addNewLabel = new Label("Dodaj nowy pojazd");
        textFieldMark = new TextField("marka");
        textFieldModel = new TextField("model");
        numberFieldYear = new NumberField("rok");
        numberFieldYear.setValue((double)localDate.getYear());
        numberFieldYear.setHasControls(true);
        numberFieldYear.setMin(1950);
        numberFieldYear.setMax(localDate.getYear());
        comboBoxColor = new ComboBox("kolor");
        comboBoxColor.setItems(colorList);
        buttonAdd = new Button("Dodaj");

        Div addDiv = new Div(textFieldMark, textFieldModel, comboBoxColor, numberFieldYear, buttonAdd);

        buttonAdd.addClickListener(buttonClickEvent -> addVehicle());

        add(idSearchDiv, colorSearchDiv, yearSearchDiv, deleteDiv, showAllButton, addNewLabel, addDiv, grid);


        //TABLE
        Grid.Column<Vehicle> idColumn = grid.addColumn(Vehicle::getId).setHeader("ID");
        Grid.Column<Vehicle> markColumn = grid.addColumn(Vehicle::getMark).setHeader("Marka");
        Grid.Column<Vehicle> modelColumn = grid.addColumn(Vehicle::getModel).setHeader("Model");
        Grid.Column<Vehicle> colorColumn = grid.addColumn(Vehicle::getColor).setHeader("Kolor");
        Grid.Column<Vehicle> yearColumn = grid.addColumn(Vehicle::getYear).setHeader("Rok produkcji");
    }

    private void refreshGrid(List<Vehicle> vehicleList) {
        grid.setItems(vehicleList);
        grid.getDataProvider().refreshAll();
    }

    private void deleteVehicleById() {
        if (vehicleService.removeVehicle(idDeleteField.getValue().longValue())) {
            grid.setItems(getAllVehicles());
            setNewMessageOnDialog("Usunięto pojazd o ID " + idDeleteField.getValue().longValue());
            idDeleteField.clear();
        } else {
            setNewMessageOnDialog("Nie znaleziono pojazdu o podanym ID");
        }
        dialog.open();
    }

    private void findVehiclesByColor() {
        List<Vehicle> vehiclesByColorList = vehicleService.getVehiclesByColor(colorSearchComboBox.getValue().toString());
        if (vehiclesByColorList.isEmpty()) {
            setNewMessageOnDialog("Nie znaleziono żadnego pojazdu w kolorze:" + colorSearchComboBox.getValue());
            dialog.open();
        } else {
            grid.setItems(vehiclesByColorList);
            colorSearchComboBox.clear();
        }
    }

    private void findVehiclesByYear() {
        List<Vehicle> vehiclesByYearList = vehicleService.getVehiclesByYear(yearFromSearchField.getValue().intValue(), yearToSearchField.getValue().intValue());
        if (vehiclesByYearList.isEmpty()) {
            setNewMessageOnDialog("Nie znaleziono żadnego pojazdu między " + yearFromSearchField.getValue() + " a " + yearToSearchField.getValue() + " rokiem");
            dialog.open();
        } else {
            grid.setItems(vehiclesByYearList);
            yearFromSearchField.clear();
            yearToSearchField.clear();
        }
    }

    private void findVehicleById() {
        Optional<Vehicle> optionalVehicle = vehicleService.getVehicleById(idSearchField.getValue().longValue());
        if (optionalVehicle.isPresent()) {
            grid.setItems(optionalVehicle.get());
            idSearchField.clear();
        } else {
            setNewMessageOnDialog("Nie znaleziono pojazdu z takim id :(");
            dialog.open();
        }
    }

    private void addVehicle() {
        Vehicle vehicle = new Vehicle(textFieldMark.getValue(), textFieldModel.getValue(), comboBoxColor.getValue().toString(), numberFieldYear.getValue().intValue());
        String message = "Pojazd o takim id już istnieje :(";
        if (vehicleService.addVehicle(vehicle)) {
            message = "Dodano nowy pojazd!";
            textFieldMark.clear();
            textFieldModel.clear();
            comboBoxColor.clear();
            numberFieldYear.clear();
            refreshGrid(getAllVehicles());
        }
        setNewMessageOnDialog(message);
        dialog.open();
    }

    private List<Vehicle> getAllVehicles() {
        return vehicleService.getVehicleList();
    }

    private void setNewMessageOnDialog(String message) {
        dialog.removeAll();
        dialog.add(message);
    }
}
