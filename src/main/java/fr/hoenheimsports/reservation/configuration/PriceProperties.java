package fr.hoenheimsports.reservation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "reservation.price")
public class PriceProperties {
    private int kid;
    private int teen;
    private int adult;

    private int meal;

    public int kid() {
        return kid;
    }

    public int teen() {
        return teen;
    }

    public int adult() {
        return adult;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public void setTeen(int teen) {
        this.teen = teen;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int meal() {
        return meal;
    }
    public void setMeal(int meal) {
        this.meal = meal;
    }
}
