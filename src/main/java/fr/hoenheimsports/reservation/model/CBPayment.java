package fr.hoenheimsports.reservation.model;

import jakarta.persistence.Entity;

@Entity
public class CBPayment extends Payment{
    private static final String TYPE = "cb";

    public CBPayment() {
        this.setType(TYPE);
    }
}
