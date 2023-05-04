package fr.hoenheimsports.reservation.controller.dto;

public record FormReservationDTO(
        String name,
        String email,
        String tel,
        int nbrKid,
        int nbrTeen,
        int nbrAdult,

        int nbrMeal,
        FormPaymentDTO payment,

        String comments
) {
}
