package com.university.facility.repository;

import com.university.facility.model.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
    List<AvailabilityRule> findByResourceId(Long resourceId);
    List<AvailabilityRule> findByResourceIdAndActiveTrue(Long resourceId);
}
