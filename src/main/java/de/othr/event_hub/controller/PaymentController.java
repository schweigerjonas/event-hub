package de.othr.event_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.PaypalService;
import org.springframework.web.bind.annotation.PostMapping;

// for paypal API integration compare https://www.youtube.com/watch?v=_eTcseS410E

@Controller
@RequestMapping("/events/{id}/payments")
public class PaymentController {
    
    private final EventService eventService;
    private final PaypalService paypalService;

    public PaymentController(EventService eventService, PaypalService paypalService) {
        super();
        this.eventService = eventService;
        this.paypalService = paypalService;
    }

    @GetMapping
    public String listPaymentsOfEvent(Model model, @PathVariable("id") Long id) {
        Event event = eventService.getEventById(id).get();
        model.addAttribute("event", event);
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
            String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String cancelUrl = baseURL + "/events/" + id + "/payments/cancel";
            String successUrl = baseURL + "/events/" + id + "/payments/success";
            Payment payment = paypalService.createPayment(Double.valueOf(amount), currency, method, "sale", description, cancelUrl, successUrl);
            
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
    @ResponseBody
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "Show event detail page, user is participant of event";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "Show event detail page, user is participant of event";
    }

    @GetMapping("/cancel")
    @ResponseBody
    public String paymentCancel() {
        return "Show event detail page, user is not participant of event";
    }
    
    @GetMapping("/error")
    public String paymentError() {
        return "payments/error";
    }
}
