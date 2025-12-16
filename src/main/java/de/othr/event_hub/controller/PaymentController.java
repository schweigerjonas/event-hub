package de.othr.event_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import de.othr.event_hub.service.PaypalService;
import org.springframework.web.bind.annotation.PostMapping;

// for paypal API integration compare https://www.youtube.com/watch?v=_eTcseS410E

@Controller
@RequestMapping("/events/{id}/payments")
public class PaymentController {
    
    private final PaypalService paypalService;

    public PaymentController(PaypalService paypalService) {
        super();
        this.paypalService = paypalService;
    }

    @GetMapping
    public String listPaymentsOfEvent(@PathVariable("id") Long id) {
        return "payments/paypal";
    }

    @PostMapping("/create")
    public RedirectView createPayment(
        @PathVariable("id") Long id, 
        @RequestParam("method") String method, 
        @RequestParam("amount") String amount, 
        @RequestParam("currency") String currency,
        @RequestParam("description") String description
    ) {
        try {
            String cancelUrl = "http://localhost:8080/events/" + id + "/payments/cancel";
            String successUrl = "http://localhost:8080/events/" + id + "/payments/success";
            Payment payment = paypalService.createPayment(Double.valueOf(amount), currency, method, "Event " + id, description, cancelUrl, successUrl);
            
            for (Links links: payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return new RedirectView("/events/" + id + "/payments/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "payments/success";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "payments/success";
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "payments/cancel";
    }
    
    @GetMapping("/error")
    public String paymentError() {
        return "payments/error";
    }
}
