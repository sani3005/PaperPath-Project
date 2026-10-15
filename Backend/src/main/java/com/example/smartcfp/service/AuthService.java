package com.example.smartcfp.service;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public String login(String email,String password){
        return "Login api is working "+email;

    }

    public String signup(String email,String password){
        return "Login api is working "+email;

    }

}
