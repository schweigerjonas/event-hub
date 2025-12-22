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

import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.PaymentStatus;

@Service
public class PdfService {

    @Autowired
    private PaymentService paymentService;
    
    public byte[] generatePaymentsPdf(User user) {
        List<Payment> payments = paymentService.getPaymentsByUser(user);
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

                // Linke Spalte (Info)
                PdfPCell infoCell = new PdfPCell(
                        new Phrase(
                            payment.getEvent().getName() + "\n" +
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
}
