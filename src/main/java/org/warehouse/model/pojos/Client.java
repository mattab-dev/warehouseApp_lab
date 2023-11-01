package org.warehouse.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;

@NoArgsConstructor
@Setter
@Getter
public class Client {

    private String firstName;
    private String lastName;
    private String clientId;
    private LocalDate creationDate;
    private boolean premiumAccount;

    public Client(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.creationDate = now();
        this.clientId = firstName + "_" + lastName + "_" + randomUUID();
    }
}
