package com.smartnotification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPrimaryEmail(String primaryEmail);

    boolean existsByPrimaryEmail(String primaryEmail);
}