package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.model.CardDetail;
import com.apiserver.apinexus.repository.CardDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CardDetailController {
    
    private final CardDetailRepository cardDetailRepository;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardDetail>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardDetailRepository.findByUserId(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CardDetail> getCardById(@PathVariable Long id) {
        return cardDetailRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CardDetail> createCard(@RequestBody CardDetail cardDetail) {
        return ResponseEntity.ok(cardDetailRepository.save(cardDetail));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CardDetail> updateCard(@PathVariable Long id, @RequestBody CardDetail cardDetail) {
        return cardDetailRepository.findById(id)
            .map(existing -> {
                cardDetail.setId(id);
                return ResponseEntity.ok(cardDetailRepository.save(cardDetail));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        if (cardDetailRepository.existsById(id)) {
            cardDetailRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
