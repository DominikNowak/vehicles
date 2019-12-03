package pl.deen.vehicles_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.deen.vehicles_api.model.Vehicle;
import pl.deen.vehicles_api.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping(value = "/vehicles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class VehicleApi {

    private VehicleService vehicleService;

    @Autowired
    public VehicleApi(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> getVehicleList() {
        return vehicleService.getVehicleList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable("id") long id) {
        return vehicleService.getVehicleById(id).map(vehicle -> new ResponseEntity(vehicle, HttpStatus.OK)).orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<List<Vehicle>> getVehiclesByColor(@PathVariable("color") String color) {
        List<Vehicle> vehiclesByColor = vehicleService.getVehiclesByColor(color);
        if (!vehiclesByColor.isEmpty()) {
            return new ResponseEntity(vehiclesByColor, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity addVehicle(@RequestBody Vehicle newVehicle) {
        if (vehicleService.addVehicle(newVehicle)) {
            return new ResponseEntity(HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.IM_USED);
    }

    @PutMapping
    public ResponseEntity modVehicle(@RequestBody Vehicle newVehicle) {
        if (vehicleService.modVehicle(newVehicle)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchVehicleColor(@RequestBody String updatedColor, @PathVariable("id") long id) {
        if (vehicleService.patchVehicleColor(updatedColor, id)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeVehicle(@PathVariable("id") long id) {
        if (vehicleService.removeVehicle(id)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
