package com.example.docmate.service;

public interface MailService {
    void sendMail(String to, String subject, String body);
}
