import service.TicketService;

public class TicketTask {
    public static void main(String[] args) {
        TicketService ticketService = new TicketService("VVO", "TLV");
        ticketService.calculateMinFlightTimeByCarrier();
        System.out.println("Разница между средней ценой и медианой между городами Владивосток и Тель-Авив: " + ticketService.calculatePriceDifference());
    }
}
