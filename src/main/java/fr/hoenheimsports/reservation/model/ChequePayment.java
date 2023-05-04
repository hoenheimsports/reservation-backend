package fr.hoenheimsports.reservation.model;

import jakarta.persistence.Entity;

@Entity
public class ChequePayment extends Payment{
    private static final String TYPE = "cheque";

    public ChequePayment() {
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
