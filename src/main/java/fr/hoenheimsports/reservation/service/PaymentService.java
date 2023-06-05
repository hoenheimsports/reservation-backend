package fr.hoenheimsports.reservation.service;

import fr.hoenheimsports.reservation.configuration.PriceProperties;
import fr.hoenheimsports.reservation.model.Payment;
import fr.hoenheimsports.reservation.model.PaymentState;
import fr.hoenheimsports.reservation.model.Reservation;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Transactional
@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private PriceProperties priceProperties;
    private EmailService emailService;

    public PaymentService(PriceProperties priceProperties, EmailService emailService) {
        this.priceProperties = priceProperties;
        this.emailService = emailService;
    }


    public BigDecimal calculateInvoiceAmount(Reservation reservation) {
        BigDecimal amount = new BigDecimal(0);
        amount = amount.add(new BigDecimal(reservation.getNbrAdult() * this.priceProperties.adult()));
        amount = amount.add(new BigDecimal(reservation.getNbrTeen() * this.priceProperties.teen()));
        amount = amount.add(new BigDecimal(reservation.getNbrKid() * this.priceProperties.kid()));
        amount = amount.add(new BigDecimal(reservation.getNbrMeal() * this.priceProperties.meal()));
        return amount;
    }

    public Payment modifyState(Reservation reservation, PaymentState nextState) {
        Payment payment = reservation.getPayment();
        PaymentState previousState = payment.getPaymentState();
        if (!previousState.equals(nextState)) {
            payment.setPaymentState(nextState);
            if (previousState != null) {
                this.emailService.generateHtmlBody("template-html-email", reservation, false, "bodyFile", "html-email-body-modification-payment", "previousState", previousState.name(), "nextState", nextState.name(), "previousStateFrench", previousState.getFrenchState(), "nextStateFrench", nextState.getFrenchState()).thenAccept(
                        htmlBody -> this.emailService.sendEmail(reservation.getEmail(), "Changement d'état de votre paiement - Soirée année 80", htmlBody)
                );
            }
            logger.info("Modification du paiement " + payment.getId() + " de " + previousState + " à " + nextState);
        } else {
            logger.warn("L'etat final et initial était le même.");
        }
        return payment;
    }


}
