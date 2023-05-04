package fr.hoenheimsports.reservation;

import fr.hoenheimsports.reservation.configuration.PriceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PriceProperties.class)
public class ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
    }

}
