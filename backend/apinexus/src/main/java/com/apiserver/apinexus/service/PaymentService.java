package com.apiserver.apinexus.service;

import com.apiserver.apinexus.model.Bill;
import com.apiserver.apinexus.model.Transaction;
import com.apiserver.apinexus.repository.BillRepository;
import com.apiserver.apinexus.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final BillRepository billRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();
    
    @Transactional
    public Transaction initiatePayment(Long billId, String paymentMethod) {
        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(bill.getUser().getId());
        transaction.setBillId(billId);
        transaction.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        transaction.setAmount(bill.getAmount());
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        // Simulate payment processing with 80-20 success rate
        boolean paymentSuccess = random.nextInt(100) < 80; // 80% success rate
        
        if (paymentSuccess) {
            transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            bill.setStatus(Bill.BillStatus.PAID);
            bill.setPaidDate(LocalDateTime.now());
            billRepository.save(bill);
        } else {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason("Payment declined by provider");
            transaction.setRetryCount(0);
            // Schedule retry for next day
            transaction.setScheduledRetryDate(LocalDateTime.now().plusDays(1));
            bill.setStatus(Bill.BillStatus.FAILED);
            billRepository.save(bill);
        }
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction retryPayment(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != Transaction.TransactionStatus.FAILED) {
            throw new RuntimeException("Only failed transactions can be retried");
        }
        
        Bill bill = billRepository.findById(transaction.getBillId())
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        // Create new transaction for retry
        Transaction retryTransaction = new Transaction();
        retryTransaction.setUserId(transaction.getUserId());
        retryTransaction.setBillId(transaction.getBillId());
        retryTransaction.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        retryTransaction.setAmount(transaction.getAmount());
        retryTransaction.setPaymentMethod(transaction.getPaymentMethod());
        retryTransaction.setTransactionDate(LocalDateTime.now());
        retryTransaction.setRetryCount(transaction.getRetryCount() + 1);
        retryTransaction.setStatus(Transaction.TransactionStatus.RETRYING);
        
        // Simulate retry payment with 80-20 success rate
        boolean paymentSuccess = random.nextInt(100) < 80;
        
        if (paymentSuccess) {
            retryTransaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            bill.setStatus(Bill.BillStatus.PAID);
            bill.setPaidDate(LocalDateTime.now());
            billRepository.save(bill);
        } else {
            retryTransaction.setStatus(Transaction.TransactionStatus.FAILED);
            retryTransaction.setFailureReason("Payment declined on retry");
            retryTransaction.setScheduledRetryDate(LocalDateTime.now().plusDays(1));
            bill.setStatus(Bill.BillStatus.FAILED);
            billRepository.save(bill);
        }
        
        return transactionRepository.save(retryTransaction);
    }
}
