package com.demo.repository;

import com.demo.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findUserByResetTokenEquals(String token);

}
