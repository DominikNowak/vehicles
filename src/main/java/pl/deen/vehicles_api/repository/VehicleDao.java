package pl.deen.vehicles_api.repository;

import pl.deen.vehicles_api.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleDao {

    boolean saveVehicle(Vehicle vehicle);

    List<Vehicle> findAll();

    boolean deleteVehicle(long id);

    Optional<Vehicle> findById(long id);

    void updateVehicle(Vehicle vehicle);

    List<Vehicle> findAllByColor(String color);

    List<Vehicle> findAllBetweenYear(int fromYear, int toYear);
}
