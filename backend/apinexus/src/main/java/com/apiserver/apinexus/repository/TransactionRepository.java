package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    List<Transaction> findByBillId(Long billId);
}
