package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
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
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
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
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }
}
