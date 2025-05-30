package org.example.spendex.controller;

import org.example.spendex.model.Transaction;
import org.example.spendex.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.example.spendex.service.PdfTransactionExtractorService;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PdfTransactionExtractorService pdfTransactionExtractorService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTransactionsPdf(@RequestParam("file") MultipartFile file) {
        List<Transaction> transactions = pdfTransactionExtractorService.extractTransactions(file);
        transactionRepository.saveAll(transactions);
        return new ResponseEntity<>("Transactions extracted and saved!", HttpStatus.OK);
    }

    @GetMapping("/spend-by-category")
    public Map<String, Object> getSpendByCategory() {
        List<Object[]> results = transactionRepository.getTotalSpendByCategory();
        List<Map<String, Object>> categories = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("total", row[1]);
            categories.add(map);
        }
        return Map.of("categories", categories);
    }

    @GetMapping("/spend-over-time")
    public Map<String, Object> getSpendOverTime() {
        List<Object[]> results = transactionRepository.getTotalSpendOverTime();
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        
        // Group by date
        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            Double total = ((Number) row[1]).doubleValue();
            dailyTotals.put(date, total);
        }

        // Sort daily totals by date
        List<LocalDate> sortedDates = new ArrayList<>(dailyTotals.keySet());
        Collections.sort(sortedDates);

        // Convert to daily format (sorted)
        List<Map<String, Object>> daily = sortedDates.stream()
            .<Map<String, Object>>map(date -> {
                Map<String, Object> map = new HashMap<>();
                map.put("date", date.toString());
                map.put("total", dailyTotals.get(date));
                return map;
            })
            .collect(Collectors.toList());

        // Group by week (Week 1, 2, ... from earliest date)
        Map<Integer, Double> weeklyTotals = new LinkedHashMap<>();
        if (!sortedDates.isEmpty()) {
            LocalDate firstDate = sortedDates.get(0);
            for (LocalDate date : sortedDates) {
                long weekIndex = (java.time.temporal.ChronoUnit.WEEKS.between(firstDate, date)) + 1;
                weeklyTotals.merge((int) weekIndex, dailyTotals.get(date), Double::sum);
            }
        }
        List<Map<String, Object>> weekly = weeklyTotals.entrySet().stream()
            .<Map<String, Object>>map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("date", "Week " + entry.getKey());
                map.put("total", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());

        // Group by month (sorted)
        Map<Integer, Double> monthlyTotals = new LinkedHashMap<>();
        for (LocalDate date : sortedDates) {
            int month = date.getMonthValue();
            monthlyTotals.merge(month, dailyTotals.get(date), Double::sum);
        }
        List<Map<String, Object>> monthly = monthlyTotals.entrySet().stream()
            .<Map<String, Object>>map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("date", String.format("2024-%02d", entry.getKey()));
                map.put("total", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("daily", daily);
        response.put("weekly", weekly);
        response.put("monthly", monthly);
        return response;
    }

    @GetMapping("/top-merchants")
    public Map<String, Object> getTopMerchants() {
        List<Object[]> results = transactionRepository.getTopMerchants();
        List<Map<String, Object>> merchants = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("total", row[1]);
            merchants.add(map);
        }
        return Map.of("merchants", merchants);
    }
}