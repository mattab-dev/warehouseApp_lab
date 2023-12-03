package org.warehouse;

import org.warehouse.model.exceptions.ClientNotFoundException;
import org.warehouse.model.exceptions.FullWarehouseException;
import org.warehouse.model.exceptions.ProhibitedMetalTypeException;
import org.warehouse.service.impl.WarehouseService;

import static org.warehouse.model.enums.SupportedMetalType.*;

public class Main {
    public static void main(String[] args) {
        WarehouseService service = new WarehouseService();
        String client1 = service.createNewClient("Jan", "Kowalski");
        String client2 =service.createNewClient("Antoni", "Kowalski");
        String client3 =service.createNewClient("Dariusz", "Kowalski");
        String client4 =service.createNewClient("Jan", "Kowalski");
        String client5 =service.createNewClient("Kamila", "Kowalska");
        String client6 =service.createNewClient("Daria", "Kowalska");

        System.out.println("number of clients: " + service.getNumberOfClients());
        System.out.println("number of premium clients: " + service.getNumberOfPremiumClients());

        service.activatePremiumAccount(client2);
        service.activatePremiumAccount(client4);

        System.out.println("number of clients: " + service.getNumberOfClients());
        System.out.println("number of premium clients: " + service.getNumberOfPremiumClients());

        try {
            service.activatePremiumAccount("46278dw");
        } catch (ClientNotFoundException ex) {
            System.out.println("client not found exception thrown and caught for not existing client");
        }

        try {
            service.addMetalIngot(client6, PLATINUM, 1100.0);
        } catch (ProhibitedMetalTypeException ex) {
            System.out.println("Prohibited metal exception caught for not premium account");
        }

        try {
            service.addMetalIngot(client4, PLATINUM, 1100.0);
        } catch (FullWarehouseException ex) {
            System.out.println("Full warehouse exception caught for too much mass on storage");
        }

        service.addMetalIngot(client4, PLATINUM, 700.0);
        service.addMetalIngot(client4, GOLD, 100.0);
        service.addMetalIngot(client4, LEAD, 100.0);

        System.out.println("total volume occupied by client4: " + service.getTotalVolumeOccupiedByClient(client4));
        System.out.println("stored metal types by client4: " + service.getStoredMetalTypesByClient(client4));
    }
}