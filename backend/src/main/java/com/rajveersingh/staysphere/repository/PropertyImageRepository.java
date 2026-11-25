package com.rajveersingh.staysphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rajveersingh.staysphere.entity.PropertyImage;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, UUID> {

    List<PropertyImage> findByProperty_IdAndProperty_Owner_Id(UUID propertyId, UUID ownerId);

    Optional<PropertyImage> findByIdAndProperty_IdAndProperty_Owner_Id(UUID imageId, UUID propertyId, UUID ownerId);

    long countByProperty_Id(UUID propertyId);

    Optional<PropertyImage> findByProperty_IdAndIsCoverTrue(UUID propertyId);

    Optional<PropertyImage> findFirstByProperty_IdOrderByCreatedAtAscIdAsc(UUID propertyId);
}
