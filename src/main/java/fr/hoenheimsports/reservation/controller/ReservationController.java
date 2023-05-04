package fr.hoenheimsports.reservation.controller;

import fr.hoenheimsports.reservation.controller.dto.FormCollectDTO;
import fr.hoenheimsports.reservation.controller.dto.FormRefundDTO;
import fr.hoenheimsports.reservation.controller.dto.FormReservationDTO;
import fr.hoenheimsports.reservation.exception.NotFoundException;
import fr.hoenheimsports.reservation.model.Reservation;
import fr.hoenheimsports.reservation.service.PaymentService;
import fr.hoenheimsports.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://reservation.hoenheimsports.club", "https://reservation-frontend-384215.web.app","https://reservation-frontend-384215.firebaseapp.com"})
public class ReservationController {


    private ReservationService reservationService;
    private PaymentService paymentService;



    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }
    @GetMapping("/reservation")
    public ResponseEntity<List<Reservation>> getAllReservation() {
        return ResponseEntity.ok(this.reservationService.findAll());
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable String id) {
        Reservation reservation = null;
        try {
            reservation = this.reservationService.findById(id);
            return ResponseEntity.ok(reservation);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reservation/{id}/collect")
    public ResponseEntity<Reservation> collect(@PathVariable String id,@RequestBody FormCollectDTO formCollectDTO) {
        Reservation reservation = null;
        try {
            reservation = this.reservationService.collect(id,formCollectDTO.name());
            return ResponseEntity.ok(reservation);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reservation/{id}/cancel")
    public ResponseEntity<Reservation> refund(@PathVariable String id) {
        Reservation reservation = null;
        try {
            reservation = this.reservationService.cancel(id);
            return ResponseEntity.ok(reservation);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reservation/{id}/refund")
    public ResponseEntity<Reservation> refund(@PathVariable String id, @RequestBody FormRefundDTO formCollectDTO) {
        Reservation reservation = null;
        try {
            reservation = this.reservationService.refund(id,formCollectDTO.name());
            return ResponseEntity.ok(reservation);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reservation/{id}/validate")
    public ResponseEntity<Reservation> validate(@PathVariable String id) {
        Reservation reservation = null;
        try {
            reservation = this.reservationService.validate(id);
            return ResponseEntity.ok(reservation);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/reservation")
    public ResponseEntity<Map<String,String>> createReservation(@RequestBody FormReservationDTO formReservationDTO) {
        return ResponseEntity.ok(Map.of("id",this.reservationService.createReservation(formReservationDTO)));
    }

}
