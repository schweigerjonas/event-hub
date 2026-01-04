package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.PaymentStatus;
import de.othr.event_hub.repository.PaymentRepository;
import de.othr.event_hub.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }

    @Override
    public void deleteAllPayments() {
        paymentRepository.deleteAll();
    }

    @Override
    public Page<Payment> getPaymentsByUser(User user, Pageable pageable) {
        return paymentRepository.findByUser(user, pageable);
    }

    @Override
    public Page<Payment> getPaymentsByEvent(Event event, Pageable pageable) {
        return paymentRepository.findByEvent(event, pageable);
    }

    @Override
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }

    @Override
    public double getTotalPaidAmountForEventAndUser(Event event, User user) {
        Double total = paymentRepository.sumAmountByEventAndUserAndStatus(event, user, PaymentStatus.COMPLETED);
        return total == null ? 0.0 : total;
    }
}
