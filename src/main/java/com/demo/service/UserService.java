package com.demo.service;

import com.demo.domain.User;
import com.demo.dto.ResetPassword;
import com.demo.exception.UserNotFoundException;
import com.demo.repository.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final static long MILLIS_TWO_HOUR = 7200000;

    @Autowired
    private UserRepository userRepository;

    public String createResetToken(String userId) throws UserNotFoundException {
        User user = getUserById(userId);

        UUID uuid = UUID.randomUUID();
        user.setResetToken(uuid.toString());
        user.setTokenCreateDate(new Date());

        userRepository.save(user);

        return user.getResetToken();
    }

    public boolean checkResetToken(String token) throws UserNotFoundException {
        User user = getUserByResetToken(token);

        if (user == null) return false;

        long tokenCreateTime = user.getTokenCreateDate().getTime();
        long now = new Date().getTime();

        return Math.abs(now - tokenCreateTime) < MILLIS_TWO_HOUR;
    }

    public boolean updateUserPasswordWithResetToken(ResetPassword resetPassword) throws UserNotFoundException {
        if (checkResetToken(resetPassword.getResetToken())){
            User user = getUserByResetToken(resetPassword.getResetToken());

            if (user == null) return false;

            user.setPassword(resetPassword.getNewPassword());
            user.setResetToken(Strings.EMPTY);
            user.setTokenCreateDate(null);

            userRepository.save(user);

            return true;
        }

        return false;
    }

    private User getUserById(String id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()){
            return userOptional.get();
        }else {
            throw new UserNotFoundException();
        }
    }

    private User getUserByResetToken(String token) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findUserByResetTokenEquals(token);

        return userOptional.orElse(null);
    }

}
