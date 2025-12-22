package de.othr.event_hub.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.PaymentService;
import de.othr.event_hub.service.PdfService;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/payments")
public class MyPaymentsController {
    
    private final PaymentService paymentService;
    private final PdfService pdfService;

    public MyPaymentsController(PaymentService paymentService, PdfService pdfService) {
        super();
        this.paymentService = paymentService;
        this.pdfService = pdfService;
    }

    @GetMapping("/all")
    public String getMyPayments(Model model, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        model.addAttribute("payments", paymentService.getPaymentsByUser(user));
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
    
}
