package fr.hoenheimsports.reservation.model;

import jakarta.persistence.Entity;

@Entity
public class DirectPayment extends Payment{
    private static final String TYPE = "direct";
    public DirectPayment() {
        this.setType(TYPE);
    }

    private String personWhoReceivedPayment;

    public String getPersonWhoReceivedPayment() {
        return personWhoReceivedPayment;
    }

    public void setPersonWhoReceivedPayment(String personWhoReceivedPayment) {
        this.personWhoReceivedPayment = personWhoReceivedPayment;
    }


}
