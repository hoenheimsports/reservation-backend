package fr.hoenheimsports.reservation.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    private BigDecimal amount;

    private LocalDateTime dateTimePayment;

    private String type;
    private String personWhoRefundPayment;

    public Payment() {
    }

    public long getId() {
        return id;
    }

    public PaymentState getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTimePayment() {
        return dateTimePayment;
    }

    public void setDateTimePayment(LocalDateTime dateTimePayment) {
        this.dateTimePayment = dateTimePayment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPersonWhoRefundPayment() {
        return personWhoRefundPayment;
    }

    public void setPersonWhoRefundPayment(String personWhoRefundPayment) {
        this.personWhoRefundPayment = personWhoRefundPayment;
    }
}
