package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.BillGenerationRequestDTO;
import com.apiserver.apinexus.dto.GeneratedBillDTO;
import com.apiserver.apinexus.model.Bill;
import com.apiserver.apinexus.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {
    
    private final BillService billService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bill>> getUserBills(@PathVariable Long userId) {
        return ResponseEntity.ok(billService.getUserBills(userId));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Bill>> getPendingBills() {
        return ResponseEntity.ok(billService.getPendingBills());
    }
    
    @PostMapping("/generate-from-sales")
    public ResponseEntity<GeneratedBillDTO> generateBillFromSales(@RequestBody BillGenerationRequestDTO request) {
        GeneratedBillDTO bill = billService.generateBillFromSales(request);
        return ResponseEntity.ok(bill);
    }
}
