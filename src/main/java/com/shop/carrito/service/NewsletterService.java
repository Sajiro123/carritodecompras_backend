package com.shop.carrito.service;

import com.shop.carrito.entity.Newsletter;
import com.shop.carrito.repository.NewsletterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @Transactional
    public boolean subscribe(String email) {
        String cleanEmail = email.trim().toLowerCase();
        if (newsletterRepository.existsByEmail(cleanEmail)) {
            return false;
        }
        newsletterRepository.save(new Newsletter(cleanEmail));
        return true;
    }
}
