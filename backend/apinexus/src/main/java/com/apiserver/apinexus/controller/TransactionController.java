package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.model.Transaction;
import com.apiserver.apinexus.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final TransactionRepository transactionRepository;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionRepository.findByUserId(userId));
    }
    
    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<Transaction>> getBillTransactions(@PathVariable Long billId) {
        return ResponseEntity.ok(transactionRepository.findByBillId(billId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        Transaction.TransactionStatus transactionStatus = Transaction.TransactionStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(transactionRepository.findByStatus(transactionStatus));
    }
}
