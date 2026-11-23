package com.rajveersingh.staysphere.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.room.CreateRoomRequest;
import com.rajveersingh.staysphere.dto.room.RoomResponse;
import com.rajveersingh.staysphere.dto.room.UpdateRoomRequest;
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

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(UUID propertyId, UUID ownerId) {

        propertyRepository.findByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        List<Room> rooms = roomRepository.findByProperty_IdAndProperty_Owner_Id(propertyId, ownerId);

        return rooms.stream()
                .map(this::toRoomResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(UUID propertyId, UUID ownerId, UUID roomId) {

        Room room = roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        return toRoomResponse(room);

    }

    @Transactional
    public RoomResponse updateRoom(UUID propertyId, UUID ownerId, UUID roomId, UpdateRoomRequest request) {

        PgProperty property = propertyRepository.findByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        Room room = roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        String roomNumber = request.getRoomNumber().trim();

        if (property.getStatus() == PropertyStatus.ACTIVE) {

            if (!room.getRoomNumber().equals(roomNumber)
                    || !room.getFloorNumber().equals(request.getFloorNumber())) {

                throw new BadRequestException(
                        "Only room description can be updated for an active property");
            }

        } else {

            if (!room.getRoomNumber().equals(roomNumber)) {

                if (roomRepository.existsByProperty_IdAndRoomNumber(propertyId, roomNumber)) {
                    throw new ResourceAlreadyExistsException(
                            "Room number already exists in this property");
                }
            }

            room.setRoomNumber(roomNumber);
            room.setFloorNumber(request.getFloorNumber());
        }

        room.setDescription(request.getDescription());

        Room savedRoom = roomRepository.save(room);

        return toRoomResponse(savedRoom);
    }

    @Transactional
    public void deleteRoom(UUID propertyId, UUID ownerId, UUID roomId) {

        PgProperty property = propertyRepository.findByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property does not exist"));

        if (property.getStatus() == PropertyStatus.ACTIVE) {
            throw new BadRequestException("Cannot delete rooms from an active property");
        }

        Room room = roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        roomRepository.delete(room);
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
