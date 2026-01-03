package de.othr.event_hub.service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.CMYKColor;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.PaymentStatus;
import de.othr.event_hub.model.EventParticipant;

@Service
public class PdfService {

    @Autowired
    private PaymentService paymentService;
    
    public byte[] generatePaymentsPdf(User user) {
        List<Payment> payments = paymentService.getPaymentsByUser(user);
        payments.sort((p1, p2) -> p1.getTimestamp().compareTo(p2.getTimestamp()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            // Titel
            Paragraph title = new Paragraph("Meine Zahlungen", titleFont);
            title.setSpacingAfter(20);
            document.add(title);

            // Tabelle (2 Spalten)
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.5f, 1.5f});
            table.setSpacingBefore(10);

            Double totalSuccess = 0.0;

            for (Payment payment : payments) {

                boolean success = payment.getStatus() == PaymentStatus.COMPLETED;
                CMYKColor rowColor = success
                        ? new CMYKColor(30, 0, 40, 0)
                        : new CMYKColor(0, 35, 30, 0);

                if (success) {
                    totalSuccess += payment.getAmount();
                }

                Event event = payment.getEvent();
                String eventName = event != null ? event.getName() : "gelöschtes Event";
                // Linke Spalte (Info)
                PdfPCell infoCell = new PdfPCell(
                        new Phrase(
                            eventName + "\n" +
                            "Datum: " + payment.getTimestamp().format(formatter) + "\n" +
                            "Transaktions-ID (PayPal): " + payment.getPaypalTransactionId(),
                            normalFont
                        )
                );
                infoCell.setBackgroundColor(rowColor);
                infoCell.setPadding(8);
                infoCell.setBorder(Rectangle.NO_BORDER);

                // Rechte Spalte (Betrag)
                String formattedAmount = currencyFormat.format(payment.getAmount());
                PdfPCell amountCell = new PdfPCell(new Phrase(formattedAmount, boldFont));
                amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                amountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                amountCell.setBackgroundColor(rowColor);
                amountCell.setPadding(8);
                amountCell.setBorder(Rectangle.NO_BORDER);

                table.addCell(infoCell);
                table.addCell(amountCell);
            }

            // Trennlinie
            PdfPCell spacer = new PdfPCell(new Phrase(" "));
            spacer.setColspan(2);
            spacer.setBorder(Rectangle.TOP);
            spacer.setPaddingTop(10);
            table.addCell(spacer);

            // Gesamtsumme
            PdfPCell totalLabel = new PdfPCell(new Phrase("Gesamtsumme (erfolgreich)", boldFont));
            totalLabel.setBorder(Rectangle.NO_BORDER);
            totalLabel.setPadding(8);

            String formattedTotal = currencyFormat.format(totalSuccess);
            PdfPCell totalValue = new PdfPCell(new Phrase(formattedTotal, boldFont));
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setBorder(Rectangle.NO_BORDER);
            totalValue.setPadding(8);

            table.addCell(totalLabel);
            table.addCell(totalValue);

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public byte[] generateEventParticipantsPdf(Event event, List<EventParticipant> participants) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            Paragraph title = new Paragraph("Teilnehmerliste: " + event.getName(), titleFont);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4.0f, 3.0f, 2.0f});
            table.setSpacingBefore(10);

            PdfPCell nameHeader = new PdfPCell(new Phrase("Teilnehmer", boldFont));
            PdfPCell joinedHeader = new PdfPCell(new Phrase("Angemeldet am", boldFont));
            PdfPCell amountHeader = new PdfPCell(new Phrase("Bezahlt", boldFont));

            nameHeader.setBorder(Rectangle.BOTTOM);
            joinedHeader.setBorder(Rectangle.BOTTOM);
            amountHeader.setBorder(Rectangle.BOTTOM);

            table.addCell(nameHeader);
            table.addCell(joinedHeader);
            table.addCell(amountHeader);

            for (EventParticipant participant : participants) {
                String name = participant.getUser() != null ? participant.getUser().getUsername() : "-";
                String joinedAt = participant.getJoinedAt() != null ? participant.getJoinedAt().format(formatter) : "-";
                double paidAmount = 0.0;
                if (participant.getUser() != null) {
                    paidAmount = paymentService.getTotalPaidAmountForEventAndUser(event, participant.getUser());
                }

                Font nameFont = participant.isOrganizer() ? boldFont : normalFont;
                PdfPCell nameCell = new PdfPCell(new Phrase(name, nameFont));
                PdfPCell joinedCell = new PdfPCell(new Phrase(joinedAt, normalFont));
                PdfPCell amountCell = new PdfPCell(new Phrase(currencyFormat.format(paidAmount), normalFont));
                amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                nameCell.setBorder(Rectangle.NO_BORDER);
                joinedCell.setBorder(Rectangle.NO_BORDER);
                amountCell.setBorder(Rectangle.NO_BORDER);

                table.addCell(nameCell);
                table.addCell(joinedCell);
                table.addCell(amountCell);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
