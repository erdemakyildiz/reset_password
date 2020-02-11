package com.demo;

import com.demo.domain.User;
import com.demo.dto.ResetPassword;
import com.demo.exception.UserNotFoundException;
import com.demo.repository.UserRepository;
import com.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        User user = new User();
        user.setUserId("1");
        user.setPassword("123456");
        user.setResetToken("reset_token");
        user.setTokenCreateDate(new Date());

        Mockito.when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.findUserByResetTokenEquals(user.getResetToken()))
                .thenReturn(Optional.of(user));
    }

    @Test
    public void createResetToken() throws UserNotFoundException {
        String resetToken = userService.createResetToken("1");

        assertNotNull(resetToken);
    }

    @Test(expected = UserNotFoundException.class)
    public void createResetToken_error() throws UserNotFoundException {
        userService.createResetToken("2");
    }

    @Test
    public void checkResetToken() throws UserNotFoundException {
        boolean isValid = userService.checkResetToken("reset_token");

        assertTrue(isValid);
    }

    @Test
    public void checkResetToken_error() throws UserNotFoundException {
        boolean isValid = userService.checkResetToken("resetToken");

        assertFalse(isValid);
    }

    @Test
    public void updateUserPasswordWithResetToken() throws UserNotFoundException {
        ResetPassword rs = new ResetPassword();
        rs.setResetToken("reset_token");
        rs.setNewPassword("test");

        boolean isUpdated = userService.updateUserPasswordWithResetToken(rs);

        assertTrue(isUpdated);
    }

}
