package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        Account registeredAccount = accountService.register(account);
        if (registeredAccount == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Username already exists
        }
        return new ResponseEntity<>(registeredAccount, HttpStatus.OK);
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> loginAccount = accountService.login(account.getUsername(), account.getPassword());
        return loginAccount.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED)); // Unauthorized if login fails
    }

    // Create a new message
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        }

        Optional<Account> account = accountService.getAccountById(message.getPostedBy());
        if (!account.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Message createdMessage = messageService.createMessage(message);
        return new ResponseEntity<>(createdMessage, HttpStatus.OK);
    }
    

    // Get all messages
    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages(); // Return all messages
    }

    // Get a specific message by ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        
        if (message.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);  
        }
        return new ResponseEntity<>(message.get(), HttpStatus.OK);
}

    // Delete a message by ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        if (message.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK); 
        }

        boolean deleted = messageService.deleteMessage(messageId);
        if (deleted) {
            return new ResponseEntity<>(1, HttpStatus.OK); 
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    // Update a message by ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody String jsonBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonBody);
            String newMessageText = rootNode.path("messageText").asText();

            if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
            }

            Optional<Message> message = messageService.getMessageById(messageId);
            if (message.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  
            }

            if (messageService.updateMessage(messageId, newMessageText)) {
                return new ResponseEntity<>(1, HttpStatus.OK);  
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  

        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  
        }
    }


    




    // Get all messages by user
    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByUser(@PathVariable Integer accountId) {
        return messageService.getMessagesByAccountId(accountId); // Retrieve messages by accountId
    }
}
