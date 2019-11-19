package pl.deen.vehicles_api.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import pl.deen.vehicles_api.model.Vehicle;
import pl.deen.vehicles_api.service.VehicleService;

import java.util.*;

@Route("vehicles-gui")
public class VehiclesShowGui extends VerticalLayout {
    private VehicleService vehicleService;

    @Autowired
    public VehiclesShowGui(VehicleService vehicleService) {
        this.vehicleService = vehicleService;

        List<Vehicle> vehiclesToShow = vehicleService.getVehicleList();
        Grid<Vehicle> grid = new Grid<>();
        NumberField idSearchField = new NumberField("Szukane ID:");
        Button idSearchButton = new Button("Wyszukaj", e -> {
            Optional<Vehicle> optionalVehicle = vehicleService.getVehicleById(idSearchField.getValue().longValue());
            if (optionalVehicle.isPresent()) {
                grid.setItems(optionalVehicle.get());
            } else {
                Dialog dialog = new Dialog();
                dialog.setWidth("400px");
                dialog.setHeight("50px");
                dialog.add("Nie znaleziono pojazdu z takim id :(");
                dialog.open();
            }
        });
        Div idSearchDiv = new Div(idSearchField, idSearchButton);

        TextField colorSearchField = new TextField("Szukany kolor:");
        Button colorSearchButton = new Button("Wyszukaj", e -> grid.setItems(vehicleService.getVehiclesByColor(colorSearchField.getValue())));
        Div colorSearchDiv = new Div(colorSearchField, colorSearchButton);

        NumberField idDeleteField = new NumberField("Usuwane ID:");
        Button idDeleteButton = new Button("Usuń", e -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("400px");
            dialog.setHeight("50px");
            if (vehicleService.removeVehicle(idDeleteField.getValue().longValue())) {
                grid.setItems(vehicleService.getVehicleList());
                dialog.add("Usunięto pojazd o ID " + idDeleteField.getValue().longValue());
                idDeleteField.clear();
            } else {
                dialog.add("Nie znaleziono pojazdu o podanym ID");
            }
            dialog.open();
        });
        Div deleteDiv = new Div(idDeleteField, idDeleteButton);

        Button showAllButton = new Button("Pokaż wszystkie", e -> grid.setItems(vehicleService.getVehicleList()));

        Label addNewLabel = new Label("Dodaj nowy pojazd");
        NumberField numberFieldId = new NumberField("Id");
        TextField textFieldMark = new TextField();
        textFieldMark.setLabel("marka");
        TextField textFieldModel = new TextField();
        textFieldModel.setLabel("model");
        TextField textFieldColor = new TextField();
        textFieldColor.setLabel("kolor");
        Button buttonAdd = new Button("Dodaj");

        Div addDiv = new Div(numberFieldId, textFieldMark, textFieldModel, textFieldColor, buttonAdd);

        buttonAdd.addClickListener(buttonClickEvent -> {
            Vehicle vehicle = new Vehicle(numberFieldId.getValue().longValue(), textFieldMark.getValue(), textFieldModel.getValue(), textFieldColor.getValue());
            Label message = new Label("Pojazd o takim id już istnieje :(");
            if (vehicleService.addVehicle(vehicle)) {
                message.setText("Dodano nowy pojazd!");
                numberFieldId.clear();
                textFieldMark.clear();
                textFieldModel.clear();
                textFieldColor.clear();
                grid.setItems(vehiclesToShow);
            }
            Dialog dialog = new Dialog();
            dialog.setWidth("400px");
            dialog.setHeight("50px");
            dialog.add(message);
            dialog.open();
        });

        grid.setItems(vehiclesToShow);
        add(idSearchDiv, colorSearchDiv, deleteDiv, showAllButton, addNewLabel, addDiv, grid);

        Grid.Column<Vehicle> idColumn = grid.addColumn(Vehicle::getId).setHeader("ID");
        Grid.Column<Vehicle> markColumn = grid.addColumn(Vehicle::getMark).setHeader("Marka");
        Grid.Column<Vehicle> modelColumn = grid.addColumn(Vehicle::getModel).setHeader("Model");
        Grid.Column<Vehicle> colorColumn = grid.addColumn(Vehicle::getColor).setHeader("Kolor");

        Binder<Vehicle> binder = new Binder<>(Vehicle.class);
        Editor<Vehicle> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        TextField markField = new TextField();
        binder.forField(markField)
                .withValidator(new StringLengthValidator("Nazwa marki musi mieć pomiędzy 2 a 15 liter.", 2, 15))
                .withStatusLabel(validationStatus).bind("mark");
        markColumn.setEditorComponent(markField);

        TextField modelField = new TextField();
        binder.forField(modelField)
                .withValidator(new StringLengthValidator("Nazwa modelu musi mieć pomiędzy 1 a 15 liter.", 1, 15))
                .withStatusLabel(validationStatus).bind("model");
        modelColumn.setEditorComponent(modelField);

        TextField colorField = new TextField();
        binder.forField(colorField)
                .withValidator(new StringLengthValidator("Nazwa marki musi mieć pomiędzy 3 a 15 liter.", 3, 15))
                .withStatusLabel(validationStatus).bind("color");
        colorColumn.setEditorComponent(colorField);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<Vehicle> editorColumn = grid.addComponentColumn(vehicle -> {
            Button edit = new Button("Edytuj");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(vehicle);
                markField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Zapisz", e -> {
            editor.save();
            grid.getDataProvider().refreshAll();
        });
        save.addClassName("save");

        Button cancel = new Button("Rezygnuj", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }
}
