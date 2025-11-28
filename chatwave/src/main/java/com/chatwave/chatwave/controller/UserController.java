package com.chatwave.chatwave.controller;



import com.chatwave.chatwave.model.User;
import com.chatwave.chatwave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; // Import this!
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // This listens for a POST request at: http://localhost:8080/api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User registerRequest) {
        User user = userService.createUser(registerRequest);
        if(user!=null){
            return ResponseEntity.ok(user);
        }else {
            // Failure: Return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }


    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

        if (user != null) {
            // Success
            return ResponseEntity.ok(user);
        } else {
            // Failure: Return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}