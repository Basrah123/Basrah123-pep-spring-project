package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createMessage(Message message) {
        return messageRepository.save(message); 
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll(); 
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId); 
    }

    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId); 
    }

    public boolean deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId); 
            return true;
        }
        return false;
    }

    public boolean updateMessage(Integer messageId, String newMessageText) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent() && newMessageText != null && !newMessageText.isBlank()) {
            Message updatedMessage = message.get();
            updatedMessage.setMessageText(newMessageText);
            messageRepository.save(updatedMessage); 
            return true;
        }
        return false;
    }
}
