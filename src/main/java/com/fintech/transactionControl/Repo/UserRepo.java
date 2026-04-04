package com.fintech.transactionControl.Repo;

import com.fintech.transactionControl.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public  interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String Email);
}
