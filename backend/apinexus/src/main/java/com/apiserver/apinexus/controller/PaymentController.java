package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.PaymentRequest;
import com.apiserver.apinexus.dto.PaymentResponse;
import com.apiserver.apinexus.model.Transaction;
import com.apiserver.apinexus.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestBody PaymentRequest request) {
        try {
            Transaction transaction = paymentService.initiatePayment(request.getBillId(), request.getPaymentMethod());
            
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(transaction.getStatus() == Transaction.TransactionStatus.SUCCESS);
            response.setTransactionId(transaction.getTransactionId());
            
            if (transaction.getStatus() == Transaction.TransactionStatus.SUCCESS) {
                response.setMessage("Payment processed successfully");
            } else {
                response.setMessage("Payment failed. Scheduled for retry on " + transaction.getScheduledRetryDate());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new PaymentResponse(false, null, "Error: " + e.getMessage())
            );
        }
    }
    
    @PostMapping("/retry/{transactionId}")
    public ResponseEntity<PaymentResponse> retryPayment(@PathVariable String transactionId) {
        try {
            Transaction transaction = paymentService.retryPayment(transactionId);
            
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(transaction.getStatus() == Transaction.TransactionStatus.SUCCESS);
            response.setTransactionId(transaction.getTransactionId());
            
            if (transaction.getStatus() == Transaction.TransactionStatus.SUCCESS) {
                response.setMessage("Payment retry successful");
            } else {
                response.setMessage("Payment retry failed. Scheduled for next retry on " + transaction.getScheduledRetryDate());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new PaymentResponse(false, null, "Error: " + e.getMessage())
            );
        }
    }
}
