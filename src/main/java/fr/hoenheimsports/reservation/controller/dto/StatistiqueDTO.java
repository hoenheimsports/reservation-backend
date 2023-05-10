package fr.hoenheimsports.reservation.controller.dto;

import java.math.BigDecimal;

public record StatistiqueDTO(BigDecimal amountTotal, StatAdultDTO statAdult, StatTeenDTO statTeen, StatKidDTO statKid) {
}
