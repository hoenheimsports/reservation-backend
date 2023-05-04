package fr.hoenheimsports.reservation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    @GetMapping("/login")
    public ResponseEntity<Boolean> testSecurity()  {
        return ResponseEntity.ok(true);
    }
}
