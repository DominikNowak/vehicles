package pl.deen.vehicles_api.service;

import org.springframework.stereotype.Service;
import pl.deen.vehicles_api.model.Vehicle;
import pl.deen.vehicles_api.repository.VehicleDaoImpl;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private VehicleDaoImpl vehicleDaoImpl;

    public VehicleService(VehicleDaoImpl vehicleDaoImpl) {
        this.vehicleDaoImpl = vehicleDaoImpl;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleDaoImpl.findAll();
    }

    public Optional<Vehicle> getVehicleById(long id) {
        return vehicleDaoImpl.findById(id);
    }

    public List<Vehicle> getVehiclesByColor(String color) {
        return vehicleDaoImpl.findAllByColor(color);
    }

    public boolean addVehicle(Vehicle newVehicle) {
        return vehicleDaoImpl.saveVehicle(newVehicle);
    }

    public boolean modVehicle(Vehicle newVehicle) {
        Optional<Vehicle> optionalVehicle = getVehicleById(newVehicle.getId());
        if (optionalVehicle.isPresent()) {
            vehicleDaoImpl.updateVehicle(newVehicle);
            return true;
        }
        return false;
    }

    public boolean patchVehicleColor(String updatedColor, long id) {
        Optional<Vehicle> optionalVehicle = getVehicleById(id);
        if (optionalVehicle.isPresent()) {
            optionalVehicle.get().setColor(updatedColor);
            return true;
        }
        return false;
    }

    public boolean removeVehicle(long id) {
        Optional<Vehicle> optionalVehicle = getVehicleById(id);
        if (optionalVehicle.isPresent()) {
            return vehicleDaoImpl.deleteVehicle(id);
        }
        return false;
    }

    public List<Vehicle> getVehiclesByYear(int yearFrom, int yearTo) {
        return vehicleDaoImpl.findAllBetweenYear(yearFrom, yearTo);
    }
}
