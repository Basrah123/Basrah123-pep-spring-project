package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account register(Account account) {
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            return null;  
        }
        return accountRepository.save(account); 
    }

    public Optional<Account> login(String username, String password) {
        return accountRepository.findByUsername(username)
                .filter(account -> account.getPassword().equals(password)); 
    }

    public Optional<Account> getAccountById(Integer accountId) {
        return accountRepository.findById(accountId);
    }
    
}
