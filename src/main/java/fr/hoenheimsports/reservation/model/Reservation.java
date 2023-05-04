package fr.hoenheimsports.reservation.model;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.IdGeneratorType;

import java.sql.Clob;
import java.time.LocalDate;

@Entity
public class Reservation {
    @Id
    @Column(nullable = false)
    private String id;
    private String name;
    private String email;
    private String tel;
    private int nbrKid;
    private int nbrTeen;
    private int nbrAdult;
    private int nbrMeal;
    private LocalDate createDate;
    private String comments;
    private ReservationState state;
    @Lob
    private String qrCodeBase64;

    public String getId() {
        return id;
    }

    public Reservation(String id) {
        this.id = id;
    }

    public Reservation() {
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_ID")
    private Payment payment;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getNbrKid() {
        return nbrKid;
    }

    public void setNbrKid(int nbrKid) {
        this.nbrKid = nbrKid;
    }

    public int getNbrTeen() {
        return nbrTeen;
    }

    public void setNbrTeen(int nbrTeen) {
        this.nbrTeen = nbrTeen;
    }

    public int getNbrAdult() {
        return nbrAdult;
    }

    public void setNbrAdult(int nbrAdult) {
        this.nbrAdult = nbrAdult;
    }

    public int getNbrMeal() {
        return nbrMeal;
    }

    public void setNbrMeal(int nbrMeal) {
        this.nbrMeal = nbrMeal;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ReservationState getState() {
        return state;
    }

    public void setState(ReservationState state) {
        this.state = state;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
