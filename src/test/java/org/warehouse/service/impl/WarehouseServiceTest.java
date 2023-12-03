package org.warehouse.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.warehouse.model.enums.SupportedMetalType;
import org.warehouse.model.exceptions.ClientNotFoundException;
import org.warehouse.model.exceptions.FullWarehouseException;
import org.warehouse.model.exceptions.ProhibitedMetalTypeException;
import org.warehouse.model.pojos.MetalIngot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.warehouse.model.enums.SupportedMetalType.*;

class WarehouseServiceTest {

    @InjectMocks
    private WarehouseService warehouseService = new WarehouseService();

    @Test
    public void createClients_theSameName_uniqueId() {
        // given
        final String name = "Andrzej";
        final String surname = "Test";

        // when
        final String client1Id = this.warehouseService.createNewClient(name, surname);
        final String client2Id = this.warehouseService.createNewClient(name, surname);

        // then
        assertNotNull(client1Id);
        assertNotNull(client2Id);
        assertNotEquals(client1Id, client2Id);
    }

    @Test
    public void activatePremium_existingId() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        final String activatedPremium = this.warehouseService.activatePremiumAccount(clientId);

        // then
        assertTrue(this.warehouseService.isPremiumClient(activatedPremium));
    }

    @Test
    public void activatePremium_notExistingId() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test"); // just to provide existing customer

        // when
        Exception expectedException = assertThrows(ClientNotFoundException.class, () -> this.warehouseService.activatePremiumAccount("Andrzej_Test_00001"));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void getClientFullName_noException() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        final String fullName = this.warehouseService.getClientFullName(clientId);

        // then
        assertEquals(fullName, "Andrzej Test");
    }

    @Test
    public void getClientFullName_exceptionThrown() {
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test"); // just to provide existing customer

        // when
        Exception expectedException = assertThrows(ClientNotFoundException.class, () -> this.warehouseService.getClientFullName("Andrzej_Test_00001"));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void getNumberOfClients_expectedFour() {
        // when
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId2 = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId3 = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId4 = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        final int numberOfClients = this.warehouseService.getNumberOfClients();

        // then
        assertEquals(numberOfClients, 4);
    }

    @Test
    public void getNumberOfPremiumClients_expectedOne() {
        // when
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId2 = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId3 = this.warehouseService.createNewClient("Andrzej", "Test");
        final String clientId4 = this.warehouseService.createNewClient("Andrzej", "Test");

        final String premiumAccount = this.warehouseService.activatePremiumAccount(clientId3);

        // when
        final int numberOfClients = this.warehouseService.getNumberOfPremiumClients();

        // then
        assertEquals(numberOfClients, 1);
        assertTrue(this.warehouseService.isPremiumClient(premiumAccount));
    }


    @Test
    public void addMetalIngot_existingClient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // then
        assertDoesNotThrow(() -> this.warehouseService.addMetalIngot(clientId, IRON, 20.5));
    }

    @Test
    public void addMetalIngot_clientNotExisting() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        Exception expectedException = assertThrows(ClientNotFoundException.class, () -> this.warehouseService.addMetalIngot("Andrzej_Test_00001", IRON, 20.5));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void addMetalIngot_prohibitedMetalType() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        Exception expectedException = assertThrows(ProhibitedMetalTypeException.class, () -> this.warehouseService.addMetalIngot(clientId, GOLD, 20.5));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void addMetalIngot_warehouseFull() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        this.warehouseService.activatePremiumAccount(clientId);

        // when
        Exception expectedException = assertThrows(FullWarehouseException.class, () -> this.warehouseService.addMetalIngot(clientId, PLATINUM, 1000.5));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void getMetalTypesToMassStoredByClient_notExistingClient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");

        // when
        Exception expectedException = assertThrows(ClientNotFoundException.class, () -> this.warehouseService.getMetalTypesToMassStoredByClient("Andrzej_Test_00001"));

        // then
        assertNotNull(expectedException);
    }

    @Test
    public void getMetalTypesToMassStoredByClient_existingClient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, COPPER, 100.0);
        this.warehouseService.addMetalIngot(clientId, TUNGSTEN, 10.0);

        // when
        final Map<SupportedMetalType, Double> values = this.warehouseService.getMetalTypesToMassStoredByClient(clientId);

        // then
        assertEquals(values.get(IRON), 41.0);
        assertEquals(values.get(COPPER), 100.0);
        assertEquals(values.get(TUNGSTEN), 10.0);
        assertEquals(values.size(), 3);
    }

    @Test
    public void getStoredMetalTypesByClient_existingClient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, COPPER, 100.0);
        this.warehouseService.addMetalIngot(clientId, TUNGSTEN, 10.0);
        this.warehouseService.addMetalIngot(clientId, TUNGSTEN, 15.0);
        this.warehouseService.addMetalIngot(clientId, SILVER, 10.0);

        // when
        final List<SupportedMetalType> values = this.warehouseService.getStoredMetalTypesByClient(clientId);

        // then
        assertEquals(values.size(), 4);
    }

    @Test
    public void getTotalVolumeOccupiedByClient_existingCLient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, IRON, 20.5);
        this.warehouseService.addMetalIngot(clientId, COPPER, 100.0);
        this.warehouseService.addMetalIngot(clientId, TUNGSTEN, 10.0);
        this.warehouseService.addMetalIngot(clientId, TUNGSTEN, 15.0);
        this.warehouseService.addMetalIngot(clientId, SILVER, 10.0);

        final MetalIngot ironIngot = new MetalIngot(IRON, 41.0);
        final MetalIngot copperIngot = new MetalIngot(COPPER, 100.0);
        final MetalIngot platinumIngot = new MetalIngot(TUNGSTEN, 25.0);
        final MetalIngot silverIngot = new MetalIngot(SILVER, 10.0);

        Double totalVolume = ironIngot.getVolume() + copperIngot.getVolume() + platinumIngot.getVolume() + silverIngot.getVolume();

        // when
        final Double value = this.warehouseService.getTotalVolumeOccupiedByClient(clientId);

        // then
        assertEquals(totalVolume, value);
    }

    @Test
    public void getClientCreationDate_existingClient() {
        // given
        final String clientId = this.warehouseService.createNewClient("Andrzej", "Test");
        final LocalDate now = now();

        // when
        final LocalDate createDate = this.warehouseService.getClientCreationDate(clientId);

        // then
        assertEquals(now, createDate);
    }

}