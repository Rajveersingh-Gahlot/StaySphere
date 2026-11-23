package com.rajveersingh.staysphere.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.room.CreateRoomRequest;
import com.rajveersingh.staysphere.dto.room.RoomResponse;
import com.rajveersingh.staysphere.entity.PgProperty;
import com.rajveersingh.staysphere.entity.Room;
import com.rajveersingh.staysphere.enums.property.PropertyStatus;
import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceAlreadyExistsException;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;
import com.rajveersingh.staysphere.repository.PgPropertyRepository;
import com.rajveersingh.staysphere.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PgPropertyRepository propertyRepository;

    @Transactional
    public RoomResponse createRoom(UUID propertyId, UUID ownerId, CreateRoomRequest request) {

        PgProperty property = propertyRepository.findByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getStatus() == PropertyStatus.ACTIVE) {
            throw new BadRequestException("Cannot add rooms to an active property");
        }

        String roomNumber = request.getRoomNumber().trim();

        if (roomRepository.existsByProperty_IdAndRoomNumber(propertyId, roomNumber)) {
            throw new ResourceAlreadyExistsException("Room number already exists in this property");
        }

        Room room = new Room();

        room.setProperty(property);
        room.setRoomNumber(roomNumber);
        room.setDescription(request.getDescription());
        room.setFloorNumber(request.getFloorNumber());

        Room savedRoom = roomRepository.save(room);

        return toRoomResponse(savedRoom);
    }

    private RoomResponse toRoomResponse(Room room) {

        RoomResponse response = new RoomResponse();

        response.setId(room.getId());
        response.setPropertyId(room.getProperty().getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setDescription(room.getDescription());
        response.setFloorNumber(room.getFloorNumber());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());

        return response;
    }

}
