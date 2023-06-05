package fr.hoenheimsports.reservation.service;

import fr.hoenheimsports.reservation.controller.dto.*;
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
        logger.info("Creation d'une reservation pour : " + reservation.getId());
        reservation.setName(FormReservationDTO.name());
        reservation.setEmail(FormReservationDTO.email());
        reservation.setCreateDate(LocalDate.now());
        reservation.setTel(FormReservationDTO.tel());
        reservation.setNbrAdult(FormReservationDTO.nbrAdult());
        reservation.setNbrTeen(FormReservationDTO.nbrTeen());
        reservation.setNbrKid(FormReservationDTO.nbrKid());
        reservation.setNbrMeal(FormReservationDTO.nbrMeal());
        reservation.setComments(FormReservationDTO.comments());
        String urlValidate = "https://reservation.hoenheimsports.club/reservation/"+reservation.getId()+"/validate";
        try {
            reservation.setQrCodeBase64(this.QrCodeBuilder.createQrCodeBase64(urlValidate));
        } catch (Exception e) {
            logger.warn("La création du qr code n'a pas reussi |- " + e.getMessage() );
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
        logger.info("La reservation " + id + " a été payée et récupérée par " + name);
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
        logger.info("La reservation " + id + " a été remboursée et récupérée par " + name);
        Reservation reservation = this.findById(id);
        Payment payment = this.paymentService.modifyState(reservation, PaymentState.REFUNDED);
        reservation.setPayment(payment);
        reservation.getPayment().setPersonWhoRefundPayment(name);
        return this.reservationRepository.save(reservation);
    }

    public Reservation cancel(String id, String message) throws NotFoundException {
        logger.info("La reservation " + id + " a été annulée à cause de : " + message);
        Reservation reservation = this.findById(id);
        reservation.setCancelMessage(message);
        this.modifyState(reservation, ReservationState.CANCELED);
        return this.reservationRepository.save(reservation);
    }

    public Reservation validate(String id) throws NotFoundException {

        Reservation reservation = this.findById(id);
        if(reservation.getState() == ReservationState.ACCEPTED) {
            logger.info("La reservation " + id + " a été validée");
            this.modifyState(reservation, ReservationState.ONGOING);
        } else{
            logger.warn("La reservation " + id + " n'a pas été validée à cause de son état non accepté |- ETAT :" + reservation.getState());
        }
        return this.reservationRepository.save(reservation);
    }

    public StatistiqueDTO getStat() {

        StatAdultDTO statAdultDTO = new StatAdultDTO(
                this.reservationRepository.sumNbrAdultByReservationState(ReservationState.PENDING),
                this.reservationRepository.sumNbrAdultByReservationState(ReservationState.CANCELED),
                this.reservationRepository.sumNbrAdultByReservationState(ReservationState.ACCEPTED),
                this.reservationRepository.sumNbrAdultByReservationState(ReservationState.ONGOING)
        );
        StatTeenDTO statTeenDTO = new StatTeenDTO(
                this.reservationRepository.sumNbrTeenByReservationState(ReservationState.PENDING),
                this.reservationRepository.sumNbrTeenByReservationState(ReservationState.CANCELED),
                this.reservationRepository.sumNbrTeenByReservationState(ReservationState.ACCEPTED),
                this.reservationRepository.sumNbrTeenByReservationState(ReservationState.ONGOING)
        );
        StatKidDTO statKidDTO = new StatKidDTO(
                this.reservationRepository.sumNbrKidByReservationState(ReservationState.PENDING),
                this.reservationRepository.sumNbrKidByReservationState(ReservationState.CANCELED),
                this.reservationRepository.sumNbrKidByReservationState(ReservationState.ACCEPTED),
                this.reservationRepository.sumNbrKidByReservationState(ReservationState.ONGOING)
        );

        StatistiqueDTO statistiqueDTO = new StatistiqueDTO(
                this.reservationRepository.sumAmountByPendingOrOngoingReservationStates(ReservationState.ACCEPTED,ReservationState.ONGOING),
                statAdultDTO,
                statTeenDTO,
                statKidDTO
        );

        return statistiqueDTO;
    }

    private void modifyState(Reservation reservation, ReservationState nextState) {
        ReservationState previousState = reservation.getState();
        if(!previousState.equals(nextState)) {
            reservation.setState(nextState);
            if (previousState == null) {
                this.emailService.generateHtmlBody("template-html-email", reservation, true, "bodyFile", "html-email-body-creation").thenAccept(
                        htmlBody -> this.emailService.sendEmail(reservation.getEmail(), "Inscription soirée année 80", htmlBody)
                );
            } else {
                this.emailService.generateHtmlBody("template-html-email", reservation, false, "bodyFile", "html-email-body-modification", "previousState", previousState.name(), "nextState", nextState.name(), "previousStateFrench", previousState.getFrenchState(), "nextStateFrench", nextState.getFrenchState()).thenAccept(
                        htmlBody -> this.emailService.sendEmail(reservation.getEmail(), "Changement d'état de votre réservation - Soirée année 80", htmlBody)
                );
            }

            logger.info("Modification de la reservation " + reservation.getId() + " de " + previousState + " à " + nextState);
        } else {
            logger.warn("L'etat final et initial était le même.");
        }
    }

}
