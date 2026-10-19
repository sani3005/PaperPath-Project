package com.example.paperpath;

public class LoginReq {

    private  String email;
    private  String password;

        public LoginReq( String email, String password){
            this.email=email;
            this.password=password;
        }



        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }


