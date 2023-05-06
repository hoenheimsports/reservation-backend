package fr.hoenheimsports.reservation.controller;

import fr.hoenheimsports.reservation.controller.dto.FormCollectDTO;
import fr.hoenheimsports.reservation.controller.dto.FormRefundDTO;
import fr.hoenheimsports.reservation.controller.dto.FormReservationDTO;
import fr.hoenheimsports.reservation.exception.NotFoundException;
import fr.hoenheimsports.reservation.model.Reservation;
import fr.hoenheimsports.reservation.service.PaymentService;
import fr.hoenheimsports.reservation.service.ReservationService;
import fr.hoenheimsports.reservation.util.IQrCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private ReservationService reservationService;
    private PaymentService paymentService;
    private IQrCodeBuilder iQrCodeBuilder;



    public ReservationController(ReservationService reservationService, PaymentService paymentService, IQrCodeBuilder iQrCodeBuilder) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.iQrCodeBuilder = iQrCodeBuilder;
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


    @GetMapping(value = "/qr-code/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable("id") String id) {
        try {
            logger.info("création d'un QR Code pour " + id);
            String qrCodeBase64 = this.reservationService.findById(id).getQrCodeBase64();
            byte[] qrCode = Base64.getDecoder().decode(qrCodeBase64);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
        } catch (NotFoundException e) {
            logger.warn("création d'un QR Code  : Utilisateur inconnu");
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.warn("création d'un QR Code  : Erreur |- " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
