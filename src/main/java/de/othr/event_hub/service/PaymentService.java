package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;

public interface PaymentService {
    
    Payment createPayment(Payment payment);

    Page<Payment> getAllPayments(Pageable pageable);

    Optional<Payment> getPaymentById(Long id);

    Payment updatePayment(Payment payment);

    void deletePayment(Payment payment);

    void deleteAllPayments();

    Page<Payment> getPaymentsByUser(User user, Pageable pageable);

    Page<Payment> getPaymentsByEvent(Event event, Pageable pageable);

    List<Payment> getPaymentsByUser(User user);

    double getTotalPaidAmountForEventAndUser(Event event, User user);
}
