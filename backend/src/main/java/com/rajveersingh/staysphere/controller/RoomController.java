package com.rajveersingh.staysphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.room.CreateRoomRequest;
import com.rajveersingh.staysphere.dto.room.RoomResponse;
import com.rajveersingh.staysphere.dto.room.UpdateRoomRequest;
import com.rajveersingh.staysphere.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/owner/properties/{propertyId}/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@PathVariable UUID propertyId,
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        RoomResponse response = roomService.createRoom(propertyId, ownerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms(@PathVariable UUID propertyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        List<RoomResponse> response = roomService.getAllRooms(propertyId, ownerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable UUID roomId, @PathVariable UUID propertyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        RoomResponse response = roomService.getRoomById(propertyId, ownerId, roomId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable UUID propertyId,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        RoomResponse response = roomService.updateRoom(propertyId, ownerId, roomId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId,
            @PathVariable UUID propertyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID ownerId = getOwnerId(userDetails);

        roomService.deleteRoom(propertyId, ownerId, roomId);

        return ResponseEntity.noContent().build();
    }

    private UUID getOwnerId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
