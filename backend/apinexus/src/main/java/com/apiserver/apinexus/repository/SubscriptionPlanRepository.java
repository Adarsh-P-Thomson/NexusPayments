package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByActiveTrue();
    List<SubscriptionPlan> findByActiveTrueAndIsDefaultTrue();
    List<SubscriptionPlan> findByActiveTrueAndIsDefaultFalse();
    List<SubscriptionPlan> findByPlanType(String planType);
    List<SubscriptionPlan> findByActiveTrueAndPlanType(String planType);
    Optional<SubscriptionPlan> findByName(String name);
}
