package pl.deen.vehicles_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle extends RepresentationModel {
    private long id;
    private String mark;
    private String model;
    private String color;

}
