package com.apiserver.apinexus.util;

import com.apiserver.apinexus.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ClearSalesData implements CommandLineRunner {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if --clear-sales argument is provided
        for (String arg : args) {
            if ("--clear-sales".equals(arg)) {
                long count = saleRepository.count();
                saleRepository.deleteAll();
                System.out.println("\n========================================");
                System.out.println("CLEARED " + count + " SALES FROM DATABASE");
                System.out.println("========================================\n");
                return;
            }
        }
    }
}
