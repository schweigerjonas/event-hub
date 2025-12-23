package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;

public interface PaymentService {
    
    Payment createPayment(Payment payment);

    List<Payment> getAllPayments();

    Optional<Payment> getPaymentById(Long id);

    Payment updatePayment(Payment payment);

    void deletePayment(Payment payment);

    void deleteAllPayments();

    List<Payment> getPaymentsByUser(User user);
}
