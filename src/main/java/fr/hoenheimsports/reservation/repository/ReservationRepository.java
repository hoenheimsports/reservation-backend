package fr.hoenheimsports.reservation.repository;

import fr.hoenheimsports.reservation.model.Reservation;
import fr.hoenheimsports.reservation.model.ReservationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    @Query("SELECT COALESCE(SUM(r.nbrAdult),0) FROM Reservation r WHERE r.state = :reservationState")
    int sumNbrAdultByReservationState(@Param("reservationState") ReservationState reservationState);

    @Query("SELECT COALESCE(SUM(r.nbrTeen),0) FROM Reservation r WHERE r.state = :reservationState")
    int sumNbrTeenByReservationState(@Param("reservationState") ReservationState reservationState);
    @Query("SELECT COALESCE(SUM(r.nbrKid),0) FROM Reservation r WHERE r.state = :reservationState")
    int sumNbrKidByReservationState(@Param("reservationState") ReservationState reservationState);

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Reservation r JOIN r.payment p WHERE r.state IN :states")
    BigDecimal sumAmountByPendingOrOngoingReservationStates(@Param("states") ReservationState... states);

}