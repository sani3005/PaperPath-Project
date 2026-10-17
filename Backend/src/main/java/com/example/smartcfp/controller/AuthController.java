package com.example.smartcfp.controller;

import com.example.smartcfp.model.User;
import com.example.smartcfp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
   private final  AuthService authService;

   public AuthController(AuthService authService){

       this.authService=authService;
   }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody Map<String, String> request) {

        try {
            String message = authService.signup(
                    request.get("name"),
                    request.get("email"),
                    request.get("password")
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", message));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        try {
            String message = authService.login(
                    request.get("email"),
                    request.get("password")
            );

            return ResponseEntity.ok(
                    Map.of("message", message)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
