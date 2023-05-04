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
    private static final Logger logger= LoggerFactory.getLogger(PaymentService.class);
    private PriceProperties priceProperties;
    private EmailService emailService;

    public PaymentService(PriceProperties priceProperties, EmailService emailService) {
        this.priceProperties = priceProperties;
        this.emailService = emailService;
    }


    public BigDecimal calculateInvoiceAmount(Reservation reservation) {
        BigDecimal amount = new BigDecimal(0);
        amount = amount.add(new BigDecimal(reservation.getNbrAdult()*this.priceProperties.adult()));
        amount = amount.add(new BigDecimal(reservation.getNbrTeen()*this.priceProperties.teen()));
        amount = amount.add(new BigDecimal(reservation.getNbrKid()*this.priceProperties.kid()));
        amount = amount.add(new BigDecimal(reservation.getNbrMeal()*this.priceProperties.meal()));
        return amount;
    }

    public Payment modifyState(Reservation reservation, PaymentState nextState) {
        Payment payment = reservation.getPayment();
        PaymentState previousState = payment.getPaymentState();
        payment.setPaymentState(nextState);
        String messageToSend ;
        if(previousState == null) {
            messageToSend = templateEmailCreation.formatted(nextState.getFrenchState());
        } else {
            messageToSend = templateEmailModification.formatted(previousState.getFrenchState(),nextState.getFrenchState());
        }
        this.emailService.sendEmail(reservation.getEmail(),"Changement d'état de votre paiement", messageToSend);
        logger.info("Modification du paiement " + payment.getId() + " de " + previousState + " à " + nextState);
        return payment;
    }


    private final static String templateEmailCreation = """
            Bonjour,
            
            Votre paiement a actuellement le status de %s.            
            
            Pour toutes questions sur la reservation, vous pouvez me joindre à reservation@hoenheimsports.fr
            """;

    private final static String templateEmailModification = """
           Bonjour,
           
           Votre paiement a été modifié, il est passé du statue de %s à %s.       

           Pour toutes questions sur la reservation, vous pouvez me joindre à reservation@hoenheimsports.fr
            """;

}
