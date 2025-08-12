package com.example.demo.Controller;


import com.example.demo.Service.AuthService;
import com.example.demo.Service.UserService;
import com.example.demo.model.request.AuthenticationResponse;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/rest/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Login with email and password ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,token generated it last for an hour ")
            ,@ApiResponse(responseCode = "404",description = "Not found , no user with email entered")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest )  {
        System.out.println("login controller");
        System.out.println("Card Number: " + loginRequest.getEmail());
        System.out.println("login controller");
       return ResponseEntity.ok(authService.login(loginRequest));
    }


    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest)
    {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Operation(summary = "Activate user via id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK, User activated enabled = true ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No user with this id")
            ,@ApiResponse(responseCode = "409",description = "CONFLICT, User already active")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
    })
    @PostMapping("/verify")
    public boolean activateUser(@RequestParam String email, @RequestParam String otp)
    {
        return userService.activateUser(email ,otp);
    }

//    public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String otp) {
//       // System.out.println("Verify endpoint called with email: " + email + " and OTP: " + otp);
//
//        boolean result = userService.activateUser(email, otp);
//
//        if (result) {
//            return ResponseEntity.ok("User verified successfully");
//        } else {
//            return ResponseEntity.badRequest().body("Invalid OTP or user not found");
//        }
//    }
}