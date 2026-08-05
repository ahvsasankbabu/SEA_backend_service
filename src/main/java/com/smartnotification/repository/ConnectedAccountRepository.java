package com.smartnotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.entity.User;

public interface ConnectedAccountRepository extends JpaRepository<ConnectedAccount, Long> {

    List<ConnectedAccount> findByUser(User user);

    List<ConnectedAccount> findByUserAndActiveTrue(User user);

    Optional<ConnectedAccount> findByGmailEmail(String gmailEmail);

    boolean existsByGmailEmail(String gmailEmail);
    
   
}