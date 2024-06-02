package model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Ticket {
    private String origin;
    private String originName;
    private String destination;
    private String destinationName;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private String carrier;
    private int stops;
    private int price;
}
