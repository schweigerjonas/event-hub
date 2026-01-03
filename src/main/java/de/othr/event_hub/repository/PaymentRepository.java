package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUser(User user);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.event = :event and p.user = :user and p.status = :status")
    Double sumAmountByEventAndUserAndStatus(
        @Param("event") Event event,
        @Param("user") User user,
        @Param("status") PaymentStatus status
    );
}
