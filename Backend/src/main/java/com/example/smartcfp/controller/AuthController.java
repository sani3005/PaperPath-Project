package com.example.smartcfp.controller;

import com.example.smartcfp.model.User;
import com.example.smartcfp.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
   private final  AuthService authService;

   public AuthController(AuthService authService){

       this.authService=authService;
   }

    //@GetMapping("/signup");
    public String signup(@RequestBody User user){

        return "Signup Successfull for"+ user.getName();
    }

    //@PostMapping("/login");
    public String login(@RequestBody User user){
        return authService.login(user.getEmail(),user.getPassword());

    }
}
