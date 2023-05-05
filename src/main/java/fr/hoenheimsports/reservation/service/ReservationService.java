package fr.hoenheimsports.reservation.service;

import fr.hoenheimsports.reservation.controller.dto.FormReservationDTO;
import fr.hoenheimsports.reservation.exception.NotFoundException;
import fr.hoenheimsports.reservation.model.*;
import fr.hoenheimsports.reservation.repository.ReservationRepository;
import fr.hoenheimsports.reservation.util.IQrCodeBuilder;
import fr.hoenheimsports.reservation.util.IdReservationGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    private final IdReservationGenerator idReservationGenerator;

    private final EmailService emailService;

    private final IQrCodeBuilder QrCodeBuilder;


    public ReservationService(ReservationRepository reservationRepository, PaymentService paymentService, IdReservationGenerator idReservationGenerator, EmailService emailService, IQrCodeBuilder qrCodeBuilder) {
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
        this.idReservationGenerator = idReservationGenerator;
        this.emailService = emailService;
        this.QrCodeBuilder = qrCodeBuilder;
    }

    public String createReservation(FormReservationDTO FormReservationDTO) {
        Reservation reservation = new Reservation(this.idReservationGenerator.generateId(6));
        reservation.setName(FormReservationDTO.name());
        reservation.setEmail(FormReservationDTO.email());
        reservation.setCreateDate(LocalDate.now());
        reservation.setTel(FormReservationDTO.tel());
        reservation.setNbrAdult(FormReservationDTO.nbrAdult());
        reservation.setNbrTeen(FormReservationDTO.nbrTeen());
        reservation.setNbrKid(FormReservationDTO.nbrKid());
        reservation.setNbrMeal(FormReservationDTO.nbrMeal());
        reservation.setComments(FormReservationDTO.comments());
        String urlValidate = "https://reservation.hoenheimsports.club/reservation/ma-reservation${reservation.getId()}/validate";
        try {
            reservation.setQrCodeBase64(this.QrCodeBuilder.createQrCodeBase64(urlValidate));
        } catch (Exception e) {
            reservation.setQrCodeBase64(null);
        }
        Payment payment = switch (FormReservationDTO.payment().type()) {
            case "direct" -> new DirectPayment();
            case "cb" -> new CBPayment();
            case "cheque" -> new ChequePayment();
            default -> throw new IllegalArgumentException("Invalid type payment");
        };
        payment.setAmount(this.paymentService.calculateInvoiceAmount(reservation));
        reservation.setPayment(payment);
        this.paymentService.modifyState(reservation, PaymentState.PENDING);
        this.modifyState(reservation, ReservationState.PENDING);
        return this.reservationRepository.save(reservation).getId();
    }

    public Reservation findById(String id) throws NotFoundException {
        return this.reservationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Reservation> findAll() {
        return this.reservationRepository.findAll();
    }

    public Reservation collect(String id, String name) throws NotFoundException {
        Reservation reservation = this.findById(id);
        Payment payment = this.paymentService.modifyState(reservation, PaymentState.ACCEPTED);
        reservation.setPayment(payment);
        reservation.getPayment().setDateTimePayment(LocalDateTime.now());
        this.modifyState(reservation, ReservationState.ACCEPTED);
        if (reservation.getPayment() instanceof ChequePayment p) {
            p.setPersonWhoReceivedPayment(name);
        }
        if (reservation.getPayment() instanceof DirectPayment p) {
            p.setPersonWhoReceivedPayment(name);
        }
        return this.reservationRepository.save(reservation);
    }

    public Reservation refund(String id, String name) throws NotFoundException {
        Reservation reservation = this.findById(id);
        Payment payment = this.paymentService.modifyState(reservation, PaymentState.REFUNDED);
        reservation.setPayment(payment);
        this.modifyState(reservation, ReservationState.CANCELED);
        reservation.getPayment().setPersonWhoRefundPayment(name);
        return this.reservationRepository.save(reservation);
    }

    public Reservation cancel(String id) throws NotFoundException {
        Reservation reservation = this.findById(id);
        reservation.setState(ReservationState.CANCELED);
        return this.reservationRepository.save(reservation);
    }

    public Reservation validate(String id) throws NotFoundException {
        Reservation reservation = this.findById(id);
        this.modifyState(reservation, ReservationState.ONGOING);
        return this.reservationRepository.save(reservation);
    }

    private void modifyState(Reservation reservation, ReservationState nextState) {
        ReservationState previousState = reservation.getState();
        reservation.setState(nextState);
        if (previousState == null) {
            this.emailService.generateHtmlBody("html-email-body-creation", reservation).thenAccept(
                    htmlBody -> this.emailService.sendEmail(reservation.getEmail(), "Changement d'état de votre réservation", htmlBody)
            );
        } else {
            this.emailService.generateHtmlBody("html-email-body-modification", reservation).thenAccept(
                    htmlBody -> this.emailService.sendEmail(reservation.getEmail(), "Changement d'état de votre réservation", htmlBody)
            );
        }

        logger.info("Modification de la reservation " + reservation.getId() + " de " + previousState + " à " + nextState);
    }


    private final static String templateEmailCreation = """
            Bonjour,
                        
            Votre réservation a bien été enregistrée au nom de %s.
                        
            Vous pouvez retrouver votre reservation à https://reservation.hoenheimsports.club/reservation/ma-reservation/%s .
                        
            Pour toutes questions sur la reservation, vous pouvez me joindre à reservation@hoenheimsports.fr
            """;

    private final static String templateEmailModification = """
            Bonjour,
                       
            Votre reservation au nom de %s a été modifiée, elle est passée du status de %s à %s.
                       
            Vous pouvez retrouver votre reservation à https://reservation.hoenheimsports.club/reservation/ma-reservation/%s .

            Pour toutes questions sur la reservation, vous pouvez me joindre à reservation@hoenheimsports.fr
             """;
}
