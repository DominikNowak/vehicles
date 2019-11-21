package pl.deen.vehicles_api.service;

import org.springframework.stereotype.Service;
import pl.deen.vehicles_api.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private List<Vehicle> vehicleList;

    public VehicleService() {
        this.vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle(1L, "Volkswagen", "Passat", "black"));
        vehicleList.add(new Vehicle(2L, "Honda", "Civic", "black"));
        vehicleList.add(new Vehicle(3L, "Peugeot", "307", "red"));
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public Optional<Vehicle> getVehicleById(long id) {
        return vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
    }

    public List<Vehicle> getVehiclesByColor(String color) {
        return vehicleList.stream().filter(vehicle -> vehicle.getColor().equalsIgnoreCase(color)).collect(Collectors.toList());
    }

    public boolean addVehicle(Vehicle newVehicle) {
        Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(vehicle -> vehicle.getId() == newVehicle.getId()).findFirst();
        if (vehicleOptional.isPresent()){
            return false;
        }
        return vehicleList.add(newVehicle);
    }

    public boolean modVehicle(Vehicle newVehicle){
        Optional<Vehicle> optionalVehicle = vehicleList.stream().filter(vehicle -> vehicle.getId() == newVehicle.getId()).findFirst();
        if (optionalVehicle.isPresent()){
            if (!optionalVehicle.get().getMark().equals(newVehicle.getMark())) {
                optionalVehicle.get().setMark(newVehicle.getMark());
            }
            if (!optionalVehicle.get().getModel().equals(newVehicle.getModel())) {
                optionalVehicle.get().setModel(newVehicle.getModel());
            }
            if (!optionalVehicle.get().getColor().equals(newVehicle.getColor())) {
                optionalVehicle.get().setColor(newVehicle.getColor());
            }
            return true;
        }
        return false;
    }

    public boolean patchVehicleColor(String updatedColor, long id){
        Optional<Vehicle> optionalVehicle = vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        if (optionalVehicle.isPresent()){
            optionalVehicle.get().setColor(updatedColor);
            return true;
        }
        return false;
    }

    public boolean removeVehicle(long id){
        Optional<Vehicle> optionalVehicle = vehicleList.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        return optionalVehicle.map(vehicle -> vehicleList.remove(vehicle)).orElse(false);
    }
}
