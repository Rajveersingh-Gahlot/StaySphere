package com.rajveersingh.staysphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rajveersingh.staysphere.entity.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room>findByProperty_IdAndProperty_Owner_Id(UUID propertyId,UUID ownerId);

    Optional<Room>findByIdAndProperty_IdAndProperty_Owner_Id(UUID roomId,UUID propertyId,UUID ownerId);

    boolean existsByProperty_IdAndRoomNumber(UUID propertyId,String roomNumber);
}
