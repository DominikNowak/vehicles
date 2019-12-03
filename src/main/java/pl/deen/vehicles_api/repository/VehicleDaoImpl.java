package pl.deen.vehicles_api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.deen.vehicles_api.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class VehicleDaoImpl implements VehicleDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public VehicleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean saveVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles(mark, model, color, year) VALUES(?,?,?,?)";
        if (jdbcTemplate.update(sql, vehicle.getMark(), vehicle.getModel(), vehicle.getColor(), vehicle.getYear())==1){
            return true;
        }
        return false;
    }

    @Override
    public List<Vehicle> findAll() {
        String sql = "SELECT * FROM vehicles";
        return getVehicleListFromQuery(sql);
    }

    @Override
    public boolean deleteVehicle(long id) {
        String sql = "DELETE FROM vehicles WHERE vehicles.vehicle_id=?";
        jdbcTemplate.update(sql, id);
        return true;
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        String sql = "SELECT * FROM vehicles WHERE vehicles.vehicle_id=?";
        try {
            Vehicle vehicle = jdbcTemplate.queryForObject(sql, (rs, i) -> new Vehicle(
                    rs.getLong("vehicle_id"),
                    rs.getString("mark"),
                    rs.getString("model"),
                    rs.getString("color"),
                    rs.getInt("year")), id);
            return Optional.ofNullable(vehicle);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public void updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET vehicles.mark=?, vehicles.model=?, vehicles.color=?, vehicles.year=? WHERE vehicles.vehicle_id=?";
        jdbcTemplate.update(sql, vehicle.getMark(), vehicle.getModel(), vehicle.getColor(), vehicle.getYear());
    }

    @Override
    public List<Vehicle> findAllByColor(String color) {
        String sql = "SELECT * FROM vehicles WHERE vehicles.color='" + color + "'";
        return getVehicleListFromQuery(sql);
    }

    @Override
    public List<Vehicle> findAllBetweenYear(int fromYear, int toYear) {
        String sql = "SELECT * FROM vehicles WHERE vehicles.year BETWEEN " + fromYear + " AND " + toYear;
        return getVehicleListFromQuery(sql);
    }

    private List<Vehicle> getVehicleListFromQuery(String sql) {
        List<Vehicle> vehicleList = new ArrayList<>();
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        maps.stream().forEach(e -> vehicleList.add(new Vehicle(
                Long.parseLong(String.valueOf(e.get("vehicle_id"))),
                String.valueOf(e.get("mark")),
                String.valueOf(e.get("model")),
                String.valueOf(e.get("color")),
                Integer.parseInt(String.valueOf(e.get("year")))
        )));
        return vehicleList;
    }
}
