package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Ticket;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketService {

    private final List<Ticket> tickets;
    private final List<Ticket> filteredTickets;
    private static final String FILE_PATH = "src/main/resources/tickets.json";

    public TicketService(String origin, String destination) {
        tickets = readTickets();
        filteredTickets = filterTickets(origin, destination);
    }

    private List<Ticket> readTickets() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, List<Ticket>> ticketData = mapper.readValue(new File(FILE_PATH), new TypeReference<>() {
            });
            return ticketData.getOrDefault("tickets", List.of());
        } catch (IOException e) {
            throw new RuntimeException("Error reading tickets from file", e);
        }
    }

    private List<Ticket> filterTickets(String origin, String destination) {
        if (origin == null || destination == null || origin.isBlank() || destination.isBlank()) {
            throw new IllegalArgumentException("Carrier names are incorrect");
        }

        return tickets.stream()
                .filter(ticket -> ticket.getOrigin().equals(origin) && ticket.getDestination().equals(destination))
                .collect(Collectors.toList());
    }

    public void calculateMinFlightTimeByCarrier() {
        Map<String, Duration> minFlightTimeByCarrier = new HashMap<>();
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("dd.MM.yy ")
                .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NORMAL)
                .appendPattern(":mm")
                .toFormatter();

        for (Ticket ticket : filteredTickets) {
            LocalDateTime departure = LocalDateTime.parse(ticket.getDepartureDate() + " " + ticket.getDepartureTime(), dateTimeFormatter);
            LocalDateTime arrival = LocalDateTime.parse(ticket.getArrivalDate() + " " + ticket.getArrivalTime(), dateTimeFormatter);
            Duration flightDuration = Duration.between(departure, arrival);

            minFlightTimeByCarrier.merge(ticket.getCarrier(), flightDuration, (existing, newDuration) -> existing.compareTo(newDuration) < 0 ? existing : newDuration);
        }

        System.out.println("Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика:");
        for (Map.Entry<String, Duration> entry : minFlightTimeByCarrier.entrySet()) {
            System.out.println("Авиаперевозчик: " + entry.getKey() + ", Время полета: " + entry.getValue().toHours() + " часов " + entry.getValue().toMinutesPart() + " минут");
        }

    }

    public double calculatePriceDifference() {
        double averagePrice = filteredTickets.stream()
                .map(Ticket::getPrice)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        double medianPrice = 0, diff = Math.abs(filteredTickets.get(0).getPrice() - averagePrice);

        for (Ticket ticket : filteredTickets) {
            if (Math.abs(ticket.getPrice() - averagePrice) < diff) {
                diff = Math.abs(ticket.getPrice() - averagePrice);
                medianPrice = ticket.getPrice();
            }
        }

        return averagePrice - medianPrice;
    }
}