package fr.hoenheimsports.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fr.hoenheimsports.reservation.controller.dto.FormReservationDTO;
import fr.hoenheimsports.reservation.exception.NotFoundException;
import fr.hoenheimsports.reservation.model.*;
import fr.hoenheimsports.reservation.repository.ReservationRepository;
import fr.hoenheimsports.reservation.util.IdReservationGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
@Transactional
@Service
public class ReservationService {
    private static final Logger logger= LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    private final IdReservationGenerator idReservationGenerator;

    private final EmailService emailService;


    public ReservationService(ReservationRepository reservationRepository, PaymentService paymentService, IdReservationGenerator idReservationGenerator, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
        this.idReservationGenerator = idReservationGenerator;
        this.emailService = emailService;
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
            reservation.setQrCodeBase64(this.createQrCodeBase64(urlValidate));
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
        payment = this.paymentService.modifyState(reservation,PaymentState.PENDING);
        reservation.setPayment(payment);
        this.modifyState(reservation, ReservationState.PENDING);
        return this.reservationRepository.save(reservation).getId();
    }

    public Reservation findById(String id) throws NotFoundException {
        return this.reservationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Reservation> findAll() {
        return this.reservationRepository.findAll();
    }

    public Reservation collect(String id,String name) throws NotFoundException {
        Reservation reservation = this.findById(id);
        Payment payment = reservation.getPayment();
        payment = this.paymentService.modifyState(reservation,PaymentState.ACCEPTED);
        reservation.setPayment(payment);
        reservation.getPayment().setDateTimePayment(LocalDateTime.now());
        this.modifyState(reservation, ReservationState.ACCEPTED);
        if(reservation.getPayment() instanceof ChequePayment p) {
            p.setPersonWhoReceivedPayment(name);
        }
        if(reservation.getPayment() instanceof DirectPayment p) {
            p.setPersonWhoReceivedPayment(name);
        }
        return this.reservationRepository.save(reservation);
    }

    public Reservation refund(String id, String name) throws NotFoundException {
        Reservation reservation = this.findById(id);
        Payment payment = reservation.getPayment();
        payment = this.paymentService.modifyState(reservation,PaymentState.REFUNDED);
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
        String messageToSend ;
        if(previousState == null) {
            messageToSend = templateEmailCreation.formatted(reservation.getName(),reservation.getId());
        } else {
            messageToSend = templateEmailModification.formatted(reservation.getName(),previousState.getFrenchState(),nextState.getFrenchState(),reservation.getId());

        }
        this.emailService.sendEmail(reservation.getEmail(),"Changement d'état de votre réservation", messageToSend );
        logger.info("Modification de la reservation " + reservation.getId() + " de " + previousState + " à " + nextState);
    }

    private String createQrCodeBase64(String data) throws WriterException, IOException, SQLException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convertir l'image en une chaîne Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(bytes);
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
