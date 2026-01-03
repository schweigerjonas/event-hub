package de.othr.event_hub.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.enums.ChatMembershipRole;
import de.othr.event_hub.model.enums.PaymentStatus;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.EmailService;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.PaymentService;
import de.othr.event_hub.service.PaypalService;
import de.othr.event_hub.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;

// for paypal API integration compare https://www.youtube.com/watch?v=_eTcseS410E

@Controller
@RequestMapping("/events/{id}/payments")
public class PaymentController {
    
    private final ChatMembershipService chatMembershipService;
    private final EmailService emailService;
    private final EventService eventService;
    private final EventParticipantService eventParticipantService;
    private final PaymentService paymentService;
    private final PaypalService paypalService;
    private final UserService userService;

    public PaymentController(ChatMembershipService chatMembershipService, EmailService emailService, EventService eventService, EventParticipantService eventParticipantService, PaymentService paymentService, PaypalService paypalService, UserService userService) {
        super();
        this.chatMembershipService = chatMembershipService;
        this.emailService = emailService;
        this.eventService = eventService;
        this.eventParticipantService = eventParticipantService;
        this.paymentService = paymentService;
        this.paypalService = paypalService;
        this.userService = userService;
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
        @RequestParam("description") String description,
        @AuthenticationPrincipal AccountUserDetails details
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
        // if no approval url found => error
        // insert Payment into database
        de.othr.event_hub.model.Payment paymentEntity = new de.othr.event_hub.model.Payment();
        paymentEntity.setPaypalTransactionId(null);
        paymentEntity.setAmount(Double.valueOf(amount));
        paymentEntity.setStatus(PaymentStatus.FAILED);
        paymentEntity.setTimestamp(LocalDateTime.now());
        paymentEntity.setUser(userService.getUserByUsername(details.getUsername()));
        paymentEntity.setEvent(eventService.getEventById(id).get());
        paymentService.createPayment(paymentEntity);

        return new RedirectView("/events/" + id + "/payments/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(
        @PathVariable("id") Long id, 
        @RequestParam("paymentId") String paymentId, 
        @RequestParam("PayerID") String payerId,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes
    ) {
        Event event = eventService.getEventById(id).get();
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                // insert Payment into database
                de.othr.event_hub.model.Payment paymentEntity = new de.othr.event_hub.model.Payment();
                paymentEntity.setPaypalTransactionId(paymentId);
                paymentEntity.setAmount(Double.valueOf(payment.getTransactions().get(0).getAmount().getTotal()));
                paymentEntity.setStatus(PaymentStatus.COMPLETED);
                paymentEntity.setTimestamp(LocalDateTime.now());
                paymentEntity.setUser(userService.getUserByUsername(details.getUsername()));
                paymentEntity.setEvent(event);
                paymentService.createPayment(paymentEntity);

                // send payment confirmation to user
                emailService.sendPaymentConfirmation(paymentEntity);

                LocalDateTime now = LocalDateTime.now();

                EventParticipant participant = new EventParticipant();
                participant.setEvent(event);
                participant.setUser(details.getUser());
                participant.setOrganizer(false);
                participant.setJoinedAt(now);
                eventParticipantService.createParticipant(participant);

                // join event chat room
                ChatMembership chatMembership = new ChatMembership();
                chatMembership.setChatRoom(event.getEventChatRoom());
                chatMembership.setUser(details.getUser());
                chatMembership.setRole(ChatMembershipRole.MEMBER);
                chatMembership.setJoinedAt(now);
                chatMembershipService.createChatMembership(chatMembership);

                redirectAttributes.addFlashAttribute(
                    "success",
                    "Du hast dich zum Event \"" + event.getName() + "\" angemeldet."
                );
                return "redirect:/events/" + id;
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                "error",
                "Bei der Zahlung ist ein Fehler aufgetreten."
            );
            return "redirect:/events/" + id;
        }
        return "redirect:/events/" + id;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public String paymentCancel() {
        return "redirect:/events";
    }
    
    @GetMapping("/error")
    public String paymentError() {
        return "payments/error";
    }
}
