package com.rajveersingh.staysphere.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rajveersingh.staysphere.dto.property.CreatePropertyRequest;
import com.rajveersingh.staysphere.dto.property.PropertyResponse;
import com.rajveersingh.staysphere.entity.PgProperty;
import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.enums.property.PropertyStatus;
import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;
import com.rajveersingh.staysphere.repository.PgPropertyRepository;
import com.rajveersingh.staysphere.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PgPropertyService {

    private final PgPropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional
    public PropertyResponse createProperty(CreatePropertyRequest request, UUID ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != Role.PG_OWNER
                || owner.getApprovalStatus() != ApprovalStatus.APPROVED
                || owner.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Owner is not allowed to create properties");
        }

        PgProperty property = new PgProperty();

        property.setOwner(owner);
        property.setName(request.getName().trim());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress().trim());
        property.setCity(request.getCity().trim());
        property.setState(request.getState().trim());
        property.setPincode(request.getPincode());
        property.setPgType(request.getPgType());
        property.setStatus(PropertyStatus.DRAFT);

        PgProperty savedProperty = propertyRepository.save(property);

        return toPropertyResponse(savedProperty);
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getMyProperties(UUID ownerId) {

        List<PgProperty> properties = propertyRepository.findByOwner_Id(ownerId);

        return properties.stream()
                .map(this::toPropertyResponse)
                .toList();
    }

    private PropertyResponse toPropertyResponse(PgProperty property) {

        PropertyResponse response = new PropertyResponse();

        response.setId(property.getId());
        response.setOwnerId(property.getOwner().getId());
        response.setName(property.getName());
        response.setDescription(property.getDescription());
        response.setAddress(property.getAddress());
        response.setCity(property.getCity());
        response.setState(property.getState());
        response.setPincode(property.getPincode());
        response.setPgType(property.getPgType());
        response.setStatus(property.getStatus());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        return response;
    }

}
