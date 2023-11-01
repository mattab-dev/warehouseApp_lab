package org.warehouse.service.api;

import org.warehouse.model.enums.SupportedMetalType;
import org.warehouse.model.exceptions.ClientNotFoundException;
import org.warehouse.model.exceptions.FullWarehouseException;
import org.warehouse.model.exceptions.ProhibitedMetalTypeException;

import java.util.List;
import java.util.Map;

public interface Warehouse {

    void addMetalIngot(String clientId, SupportedMetalType metalType, double mass)
            throws ClientNotFoundException, ProhibitedMetalTypeException, FullWarehouseException;

    Map<SupportedMetalType, Double> getMetalTypesToMassStoredByClient(String clientId);

    double getTotalVolumeOccupiedByClient(String clientId);

    List<SupportedMetalType> getStoredMetalTypesByClient(String clientId);

}
