package org.warehouse.service.api;

import java.time.LocalDate;

public interface Clients {

    /**
     * Creates a new client and stores their personal information.
     * @param firstName Client first name.
     * @param lastName Client last name.
     * @return Created user's identifier.
     */
    String createNewClient(String firstName, String lastName);

    /**
     * Sets the customer account as a premium account.
     * @param clientId Client identifier returned after its creation.
     * @return Client identifier returned after its creation.
     * @throws ClientNotFoundException Thrown when the client doesn't exists.
     */
    String activatePremiumAccount(String clientId);

    /**
     * @param clientId Client identifier returned after its creation.
     * @return Client full name consisting of client's first and last name eg. "John Doe".
     * @throws ClientNotFoundException Thrown when the client doesn't exists.
     */
    String getClientFullName(String clientId);

    /**
     * @param clientId Client identifier returned after its creation.
     * @return LocalDate when the client was created.
     * @throws ClientNotFoundException Thrown when the client doesn't exists.
     */
    LocalDate getClientCreationDate(String clientId);

    boolean isPremiumClient(String clientId);

    int getNumberOfClients();

    int getNumberOfPremiumClients();
}