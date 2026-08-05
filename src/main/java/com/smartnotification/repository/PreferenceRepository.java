package com.smartnotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.Preference;
import com.smartnotification.entity.User;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    List<Preference> findByUser(User user);

    List<Preference> findByUserAndActiveTrue(User user);

    Optional<Preference> findByIdAndUser(Long id, User user);

    long countByUserAndActiveTrue(User user);
}