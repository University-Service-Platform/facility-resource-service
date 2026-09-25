package com.university.facility.repository;

import com.university.facility.model.Resource;
import com.university.facility.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByCode(String code);

    boolean existsByCode(String code);

    List<Resource> findByFacilityId(Long facilityId);

    List<Resource> findByResourceType(ResourceType resourceType);

    @Query("SELECT r FROM Resource r WHERE " +
           "(:facilityId IS NULL OR r.facilityId = :facilityId) AND " +
           "(:resourceType IS NULL OR r.resourceType = :resourceType) AND " +
           "(:minCapacity IS NULL OR r.capacity >= :minCapacity) AND " +
           "(:active IS NULL OR r.active = :active) AND " +
           "(:available IS NULL OR r.available = :available)")
    List<Resource> searchResources(
            @Param("facilityId") Long facilityId,
            @Param("resourceType") ResourceType resourceType,
            @Param("minCapacity") Integer minCapacity,
            @Param("active") Boolean active,
            @Param("available") Boolean available
    );
}
