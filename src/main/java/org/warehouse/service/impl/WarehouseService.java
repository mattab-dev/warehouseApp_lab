package org.warehouse.service.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.warehouse.model.enums.SupportedMetalType;
import org.warehouse.model.exceptions.ClientNotFoundException;
import org.warehouse.model.exceptions.FullWarehouseException;
import org.warehouse.model.exceptions.ProhibitedMetalTypeException;
import org.warehouse.model.pojos.Client;
import org.warehouse.model.pojos.MetalIngot;
import org.warehouse.service.api.Clients;
import org.warehouse.service.api.Warehouse;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Setter
@Getter
public class WarehouseService implements Clients, Warehouse {

    private List<Client> clientsData = new ArrayList<>();
    private Map<String, List<MetalIngot>> assignedMetals = new HashMap<>();
    @Override
    public String createNewClient(String firstName, String lastName) {
        final Client newClient = new Client(firstName, lastName);
        clientsData.add(newClient);
        assignedMetals.put(newClient.getClientId(), new ArrayList<>());
        return newClient.getClientId();
    }

    @Override
    public String activatePremiumAccount(String clientId) throws ClientNotFoundException {
        final Client client = this.findClientOnTheList(clientId);
        if(client != null) {
            client.setPremiumAccount(true);
            return client.getClientId();
        }
        throw new ClientNotFoundException();
    }

    @Override
    public String getClientFullName(String clientId) throws ClientNotFoundException {
        final Client client = this.findClientOnTheList(clientId);
        if(client != null) {
            return client.getFirstName() + " " + client.getLastName();
        }
        throw new ClientNotFoundException();
    }

    @Override
    public LocalDate getClientCreationDate(String clientId) throws ClientNotFoundException {
        final Client client = this.findClientOnTheList(clientId);
        if(client != null) {
            return client.getCreationDate();
        }
        throw new ClientNotFoundException();
    }

    @Override
    public boolean isPremiumClient(String clientId) throws ClientNotFoundException {
        final Client client = this.findClientOnTheList(clientId);
        if(client != null) {
            return client.isPremiumAccount();
        }
        throw new ClientNotFoundException();
    }

    @Override
    public int getNumberOfClients() {
        return clientsData.size();
    }

    @Override
    public int getNumberOfPremiumClients() {
        return (int) clientsData.stream().filter(Client::isPremiumAccount).count();
    }

    private Client findClientOnTheList(final String clientId) {
        final Optional<Client> optClient = clientsData.stream().filter(client -> client.getClientId().equals(clientId)).findFirst();
        return optClient.orElse(null);
    }

    @Override
    public void addMetalIngot(String clientId, SupportedMetalType metalType, double mass) throws ClientNotFoundException, ProhibitedMetalTypeException, FullWarehouseException {
        final List<MetalIngot> metalList = assignedMetals.get(clientId);
        if(metalList == null) {
            throw new ClientNotFoundException();
        }
        metalList.add(new MetalIngot(metalType, mass));
    }

    @Override
    public Map<SupportedMetalType, Double> getMetalTypesToMassStoredByClient(String clientId) {
        final Map<SupportedMetalType, Double> result = new HashMap<>();
        final List<MetalIngot> metalList = assignedMetals.get(clientId);
        if(metalList != null) {
            metalList.forEach(ingot -> result.merge(ingot.getMetalType(), ingot.getMass(), Double::sum));
            return result;
        }
        throw new ClientNotFoundException();
    }

    @Override
    public double getTotalVolumeOccupiedByClient(String clientId) {
        AtomicReference<Double> occupiedVolume = new AtomicReference<>(0.0);
        final List<MetalIngot> metalList = assignedMetals.get(clientId);
        if(metalList != null) {
            metalList.forEach(ingot -> occupiedVolume.set(occupiedVolume.get() + ingot.getVolume()));
            return occupiedVolume.get();
        }
        throw new ClientNotFoundException();
    }

    @Override
    public List<SupportedMetalType> getStoredMetalTypesByClient(String clientId) {
        final List<MetalIngot> metalList = assignedMetals.get(clientId);
        return metalList.stream().map(MetalIngot::getMetalType).distinct().collect(toList());
    }
}
