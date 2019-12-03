package pl.deen.vehicles_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle {
    private long id;
    private String mark;
    private String model;
    private String color;
    private int year;

    public Vehicle(String mark, String model, String color, int year) {
        this.mark = mark;
        this.model = model;
        this.color = color;
        this.year = year;
    }
}
