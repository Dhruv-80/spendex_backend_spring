package org.example.spendex.service;

import org.example.spendex.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import technology.tabula.Page;
import technology.tabula.ObjectExtractor;
import technology.tabula.RectangularTextContainer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfTransactionExtractorService {

    private static final DateTimeFormatter SHORT_MONTH = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final DateTimeFormatter LONG_MONTH = DateTimeFormatter.ofPattern("d MMMM yyyy");

    public List<Transaction> extractTransactions(MultipartFile file) {
        List<Transaction> transactions = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            for (int pageIndex = 1; pageIndex <= document.getNumberOfPages(); pageIndex++) {
                Page page = extractor.extract(pageIndex);
                List<Table> tables = sea.extract(page);
                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    // Skip header row, start from 1
                    for (int i = 1; i < rows.size(); i++) {
                        List<RectangularTextContainer> cells = rows.get(i);
                        if (cells.size() < 5) continue; // skip incomplete rows

                        System.out.println("Extracted row: " + cells.stream().map(RectangularTextContainer::getText).collect(Collectors.joining(", ")));

                        Transaction t = new Transaction();
                        t.setName(cells.get(0).getText());
                        t.setBank(cells.get(1).getText());
                        double amount = Double.parseDouble(cells.get(2).getText().replace(",", ""));
                        t.setAmount(amount); // Store original amount with sign
                        String status = cells.get(4).getText();
                        t.setStatus(status);
                        
                        // Determine transaction type based on status first
                        if (status.contains("Debit") || status.contains("Paid")) {
                            t.setTransactionType("DEBIT");
                            // Make amount positive for debit transactions
                            t.setAmount(Math.abs(amount));
                        } else if (status.contains("Credit") || status.contains("Received")) {
                            t.setTransactionType("CREDIT");
                            // Make amount positive for credit transactions
                            t.setAmount(Math.abs(amount));
                        } else {
                            // If status is unclear, determine based on amount sign
                            if (amount < 0) {
                                t.setTransactionType("DEBIT");
                                t.setAmount(Math.abs(amount));
                            } else {
                                t.setTransactionType("CREDIT");
                                t.setAmount(amount);
                            }
                        }

                        String dateString = cells.get(3).getText();
                        LocalDate date;
                        try {
                            date = LocalDate.parse(dateString, SHORT_MONTH);
                        } catch (DateTimeParseException e) {
                            date = LocalDate.parse(dateString, LONG_MONTH);
                        }
                        t.setDate(date);
                        t.setCategory(categorize(t.getName()));
                        transactions.add(t);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Simple keyword-based categorization
    private String categorize(String name) {
        String n = name.toLowerCase();
        if (n.contains("swiggy") || n.contains("smartq") || n.contains("chinese wok")) return "Food";
        if (n.contains("inox") || n.contains("pvr")) return "Entertainment";
        if (n.contains("bazaar") || n.contains("mall")) return "Shopping";
        if (n.contains("university") || n.contains("deemed")) return "Education";
        return "Other";
    }
} 