package com.demo.controller;

import com.demo.dto.ResetPassword;
import com.demo.exception.UserNotFoundException;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("createResetToken")
    public ResponseEntity createResetToken(@RequestParam("userId") String userId){
        try {
            String resetToken = userService.createResetToken(userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(resetToken);
        } catch (UserNotFoundException e1) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("updatePassword")
    public ResponseEntity updatePassword(@RequestBody ResetPassword resetPassword){
        try {
            boolean isUpdated = userService.updateUserPasswordWithResetToken(resetPassword);

            if (isUpdated)
                return ResponseEntity.ok().build();

        } catch (UserNotFoundException e1) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("checkResetToken")
    public ResponseEntity checkResetToken(@RequestParam("token") String token){
        try {
            boolean isValid = userService.checkResetToken(token);

            if (isValid)
                return ResponseEntity.ok().build();

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.badRequest().build();
    }

}
