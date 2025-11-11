package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.SuggestionDTO;
import com.apiserver.apinexus.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suggestions")
@CrossOrigin(origins = "*")
public class SuggestionsController {
    
    @Autowired
    private SuggestionService suggestionService;
    
    /**
     * Get all suggestions
     * GET /api/suggestions
     */
    @GetMapping
    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority) {
        
        List<SuggestionDTO> suggestions = suggestionService.getAllSuggestions();
        
        // Filter by category if provided
        if (category != null && !category.isEmpty()) {
            suggestions = suggestions.stream()
                .filter(s -> category.equalsIgnoreCase(s.getCategory()))
                .collect(Collectors.toList());
        }
        
        // Filter by priority if provided
        if (priority != null && !priority.isEmpty()) {
            suggestions = suggestions.stream()
                .filter(s -> priority.equalsIgnoreCase(s.getPriority()))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(suggestions);
    }
    
    /**
     * Get inventory-specific suggestions
     * GET /api/suggestions/inventory
     */
    @GetMapping("/inventory")
    public ResponseEntity<List<SuggestionDTO>> getInventorySuggestions() {
        return ResponseEntity.ok(suggestionService.getInventorySuggestions());
    }
    
    /**
     * Get pricing suggestions
     * GET /api/suggestions/pricing
     */
    @GetMapping("/pricing")
    public ResponseEntity<List<SuggestionDTO>> getPricingSuggestions() {
        return ResponseEntity.ok(suggestionService.getPricingSuggestions());
    }
    
    /**
     * Get marketing suggestions
     * GET /api/suggestions/marketing
     */
    @GetMapping("/marketing")
    public ResponseEntity<List<SuggestionDTO>> getMarketingSuggestions() {
        return ResponseEntity.ok(suggestionService.getMarketingSuggestions());
    }
    
    /**
     * Get regional suggestions
     * GET /api/suggestions/regional
     */
    @GetMapping("/regional")
    public ResponseEntity<List<SuggestionDTO>> getRegionalSuggestions() {
        return ResponseEntity.ok(suggestionService.getRegionalSuggestions());
    }
    
    /**
     * Get product bundling suggestions
     * GET /api/suggestions/bundles
     */
    @GetMapping("/bundles")
    public ResponseEntity<List<SuggestionDTO>> getBundlingSuggestions() {
        return ResponseEntity.ok(suggestionService.getProductBundlingSuggestions());
    }
    
    /**
     * Get high priority suggestions only
     * GET /api/suggestions/high-priority
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<SuggestionDTO>> getHighPrioritySuggestions() {
        List<SuggestionDTO> suggestions = suggestionService.getAllSuggestions().stream()
            .filter(s -> "HIGH".equals(s.getPriority()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(suggestions);
    }
}
