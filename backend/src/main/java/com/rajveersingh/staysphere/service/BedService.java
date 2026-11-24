package com.rajveersingh.staysphere.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rajveersingh.staysphere.dto.bed.BedResponse;
import com.rajveersingh.staysphere.dto.bed.CreateBedRequest;
import com.rajveersingh.staysphere.dto.bed.UpdateBedRequest;
import com.rajveersingh.staysphere.entity.Bed;
import com.rajveersingh.staysphere.entity.Room;
import com.rajveersingh.staysphere.enums.property.PropertyStatus;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;
import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceAlreadyExistsException;
import com.rajveersingh.staysphere.repository.BedRepository;
import com.rajveersingh.staysphere.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BedService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public BedResponse createBed(UUID roomId, UUID propertyId, UUID ownerId, CreateBedRequest request) {

        Room room = roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getProperty().getStatus() == PropertyStatus.ACTIVE) {
            throw new BadRequestException("Cannot add beds to an active property");
        }

        String bedNumber = request.getBedNumber().trim();

        if (bedRepository.existsByRoom_IdAndBedNumber(roomId, bedNumber)) {
            throw new ResourceAlreadyExistsException("Bed number already exists in this room");
        }

        Bed bed = new Bed();

        bed.setRoom(room);
        bed.setBedNumber(bedNumber);
        bed.setMonthlyRent(request.getMonthlyRent());

        Bed savedBed = bedRepository.save(bed);

        return toBedResponse(savedBed);
    }

    @Transactional(readOnly = true)
    public List<BedResponse> getAllBeds(UUID propertyId, UUID roomId, UUID ownerId) {
        roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        List<Bed> beds = bedRepository.findByRoom_IdAndRoom_Property_Owner_Id(roomId, ownerId);

        return beds.stream()
                .map(this::toBedResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BedResponse getBedById(UUID propertyId, UUID ownerId, UUID roomId, UUID bedId) {

        roomRepository.findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Bed bed = bedRepository.findByIdAndRoom_IdAndRoom_Property_Owner_Id(bedId, roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));

        return toBedResponse(bed);
    }

    @Transactional
    public BedResponse updateBed(UUID propertyId, UUID ownerId, UUID roomId, UUID bedId, UpdateBedRequest request) {

        Room room = roomRepository
                .findByIdAndProperty_IdAndProperty_Owner_Id(
                        roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Bed bed = bedRepository
                .findByIdAndRoom_IdAndRoom_Property_Owner_Id(
                        bedId, roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));

        String bedNumber = request.getBedNumber().trim();

        if (!bed.getBedNumber().equals(bedNumber)) {

            if (room.getProperty().getStatus() == PropertyStatus.ACTIVE) {
                throw new BadRequestException("Cannot change bed number in an active property");
            }

            if (bedRepository.existsByRoom_IdAndBedNumber(roomId, bedNumber)) {
                throw new ResourceAlreadyExistsException("Bed number already exists in this room");
            }

            bed.setBedNumber(bedNumber);
        }

        bed.setMonthlyRent(request.getMonthlyRent());

        Bed updatedBed = bedRepository.save(bed);

        return toBedResponse(updatedBed);
    }

    public void deleteBed(UUID propertyId, UUID ownerId, UUID roomId, UUID bedId) {

        Room room = roomRepository
                .findByIdAndProperty_IdAndProperty_Owner_Id(roomId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Bed bed = bedRepository
                .findByIdAndRoom_IdAndRoom_Property_Owner_Id(bedId, roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));

        if (room.getProperty().getStatus() == PropertyStatus.ACTIVE) {
            throw new BadRequestException("Cannot delete beds from an active property");
        }

        bedRepository.delete(bed);
    }

    private BedResponse toBedResponse(Bed bed) {

        BedResponse response = new BedResponse();

        response.setId(bed.getId());
        response.setRoomId(bed.getRoom().getId());
        response.setBedNumber(bed.getBedNumber());
        response.setMonthlyRent(bed.getMonthlyRent());
        response.setCreatedAt(bed.getCreatedAt());
        response.setUpdatedAt(bed.getUpdatedAt());

        return response;
    }

}
