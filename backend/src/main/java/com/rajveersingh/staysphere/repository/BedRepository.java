
package com.rajveersingh.staysphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rajveersingh.staysphere.entity.Bed;

public interface BedRepository extends JpaRepository<Bed, UUID> {

    List<Bed> findByRoom_IdAndRoom_Property_Owner_Id(UUID roomId, UUID ownerId);

    Optional<Bed> findByIdAndRoom_IdAndRoom_Property_Owner_Id(UUID bedId, UUID roomId, UUID ownerId);

    boolean existsByRoom_IdAndBedNumber(UUID roomId, String bedNumber);
}
