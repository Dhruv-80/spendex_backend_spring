package org.example.spendex.repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.spendex.model.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t.category as category, SUM(CASE WHEN t.transactionType = 'DEBIT' THEN t.amount ELSE -t.amount END) as total " +
           "FROM Transaction t GROUP BY t.category")
    List<Object[]> getTotalSpendByCategory();

    @Query("SELECT t.name as name, SUM(CASE WHEN t.transactionType = 'DEBIT' THEN t.amount ELSE -t.amount END) as total " +
           "FROM Transaction t GROUP BY t.name ORDER BY total DESC LIMIT 5")
    List<Object[]> getTopMerchants();

    @Query("SELECT t.date as date, SUM(CASE WHEN t.transactionType = 'DEBIT' THEN t.amount ELSE -t.amount END) as total " +
           "FROM Transaction t GROUP BY t.date ORDER BY t.date")
    List<Object[]> getTotalSpendOverTime();

    @Query("SELECT SUM(CASE WHEN t.transactionType = 'DEBIT' THEN t.amount ELSE -t.amount END) as netAmount " +
           "FROM Transaction t")
    Double getNetAmount();
}