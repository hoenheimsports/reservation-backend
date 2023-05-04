package fr.hoenheimsports.reservation.util;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RandomIdReservationGenerator implements IdReservationGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public String generateId(int length) {
        Random random = new Random();
        String randomString = IntStream.range(0, length)
                .mapToObj(i -> CHARACTERS.charAt(random.nextInt(CHARACTERS.length())))
                .map(String::valueOf)
                .collect(Collectors.joining());

        return "SA8" + randomString;
    }
}
