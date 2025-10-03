package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUserId(Long userId);
    Optional<UserSubscription> findByUserIdAndStatus(Long userId, UserSubscription.SubscriptionStatus status);
    List<UserSubscription> findByStatus(UserSubscription.SubscriptionStatus status);
}
