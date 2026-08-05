package com.smartnotification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.repository.ConnectedAccountRepository;

@RestController
@RequestMapping("/accounts")
public class ConnectedAccountController {

    private final ConnectedAccountRepository repository;

    public ConnectedAccountController(
            ConnectedAccountRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    public List<ConnectedAccount> getAccounts() {

        return repository.findAll();
    }


    @DeleteMapping("/{id}")
    public String disconnect(
            @PathVariable Long id) {

        ConnectedAccount account =
                repository.findById(id)
                .orElseThrow();

        account.setActive(false);

        repository.save(account);

        return "Disconnected";
    }
}