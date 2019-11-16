package pl.deen.vehicles_api.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.deen.vehicles_api.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/vehicles", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class VehicleApi {
    private List<Vehicle> vehicleList;

    public VehicleApi() {
        this.vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle(1L, "Volkswagen", "Passat", "black"));
        vehicleList.add(new Vehicle(2L, "Honda", "Civic", "black"));
        vehicleList.add(new Vehicle(3L, "Peugeot", "307", "red"));
    }

    @GetMapping
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<EntityModel<Vehicle>> getVehicleById(@PathVariable("id") long id) {
        Link link = linkTo(VehicleApi.class).slash(id).withSelfRel();
        Optional<Vehicle> vehicleById = vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        EntityModel<Vehicle> vehicleResource = new EntityModel<>(vehicleById.get(), link);
        return vehicleById.map(vehicle -> new ResponseEntity(vehicleResource, HttpStatus.OK)).orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/color/{color}")
    public ResponseEntity getVehiclesByColor(@PathVariable("color") String color) {
        List<Vehicle> vehicles = vehicleList.stream().filter(vehicle -> vehicle.getColor().equalsIgnoreCase(color)).collect(Collectors.toList());
        if (!vehicles.isEmpty()) {
            return new ResponseEntity(vehicles, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity addVehicle(@RequestBody Vehicle newVehicle) {
        Optional<Vehicle> first = vehicleList.stream().filter(vehicle -> vehicle.getId() == newVehicle.getId()).findFirst();
        if (first.isPresent()) {
            return new ResponseEntity(HttpStatus.IM_USED);
        }
        vehicleList.add(newVehicle);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity modVehicle(@RequestBody Vehicle newVehicle) {
        Optional<Vehicle> first = vehicleList.stream().filter(vehicle -> vehicle.getId() == newVehicle.getId()).findFirst();
        if (first.isPresent()) {
            vehicleList.remove(first.get());
            vehicleList.add(newVehicle);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchVehicleColor(@RequestBody String updatedColor, @PathVariable("id") long id) {
        Optional<Vehicle> first = vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        if (first.isPresent()) {
            first.get().setColor(updatedColor);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeVehicle(@PathVariable("id") long id) {
        Optional<Vehicle> first = vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        if (first.isPresent()) {
            vehicleList.remove(first.get());
            return new ResponseEntity(first.get(), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
