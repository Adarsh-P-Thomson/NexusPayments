package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.CardDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardDetailRepository extends JpaRepository<CardDetail, Long> {
    List<CardDetail> findByUserId(Long userId);
    Optional<CardDetail> findByUserIdAndIsDefault(Long userId, Boolean isDefault);
}
