package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
