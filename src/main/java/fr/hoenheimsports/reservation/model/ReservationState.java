package fr.hoenheimsports.reservation.model;

public enum ReservationState {
    CANCELED("Annulé"),
    ACCEPTED("Accepté"),
    PENDING("En attente"),
    ONGOING("En cours");


    private final String frenchState;
    ReservationState(String frenchState) {
        this.frenchState = frenchState;
    }

    public String getFrenchState() {
        return this.frenchState;
    }

}
