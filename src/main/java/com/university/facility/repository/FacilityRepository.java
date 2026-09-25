package com.university.facility.repository;

import com.university.facility.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Optional<Facility> findByCode(String code);
    boolean existsByCode(String code);
    List<Facility> findByActiveTrue();
}
