package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.PaymentService;
import de.othr.event_hub.service.PdfService;
import de.othr.event_hub.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/payments")
public class MyPaymentsController {
    
    private final EventService eventService;
    private final PaymentService paymentService;
    private final PdfService pdfService;
    private final UserService userService;

    public MyPaymentsController(EventService eventService, PaymentService paymentService, PdfService pdfService, UserService userService) {
        super();
        this.eventService = eventService;
        this.paymentService = paymentService;
        this.pdfService = pdfService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String getMyPayments(
        Model model, 
        @RequestParam(required = false, defaultValue = "asc") String direction, 
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long eventId,
        @RequestParam(required = false, defaultValue = "1") int page, 
        @RequestParam(required = false, defaultValue = "5") int size, 
        @AuthenticationPrincipal AccountUserDetails details
    ) {
        User user = details.getUser();
        List<Authority> userAuthorities = user.getAuthorities();
        List<String> authorityDescriptions = userAuthorities.stream().map(Authority::getDescription).toList();

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page - 1, size, Sort.by(sortDirection, "timestamp"));

        model.addAttribute("userId", userId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("direction", direction);
        model.addAttribute("pageSize", size);

        Page<Payment> payments;

        if (authorityDescriptions.contains("ADMIN")) {
            if (userId == null) {
                payments = paymentService.getAllPayments(paging);
            } else {
                payments = paymentService.getPaymentsByUser(userService.getUserById(userId), paging);
            }
            model.addAttribute("users", userService.getAllUsers());
        } else if (authorityDescriptions.contains("ORGANISATOR")) {
            if (eventId == null) {
                payments = paymentService.getAllPayments(paging);
            } else {
                payments = paymentService.getPaymentsByEvent(eventService.getEventById(eventId).get(), paging);
            }
            model.addAttribute("events", eventService.getAllEvents());
        } else {
            payments = paymentService.getPaymentsByUser(user, paging);
        }

        model.addAttribute("payments", payments.getContent());
        model.addAttribute("currentPage", payments.getNumber() + 1);
        model.addAttribute("totalItems", payments.getTotalElements());
        model.addAttribute("totalPages", payments.getTotalPages());
        return "payments/payments-all";
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPaymentsAsPdf(@AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        byte[] pdfBytes = pdfService.generatePaymentsPdf(user);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    
    @GetMapping("/adminpdf/{userId}")
    public ResponseEntity<byte[]> downloadPaymentsAsPdfAdmin(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        byte[] pdfBytes = pdfService.generatePaymentsPdf(user);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
