package com.rajveersingh.staysphere.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.rajveersingh.staysphere.dto.property.PropertyImageResponse;
import com.rajveersingh.staysphere.entity.PgProperty;
import com.rajveersingh.staysphere.entity.PropertyImage;
import com.rajveersingh.staysphere.exception.BadRequestException;
import com.rajveersingh.staysphere.exception.ResourceNotFoundException;
import com.rajveersingh.staysphere.repository.PgPropertyRepository;
import com.rajveersingh.staysphere.repository.PropertyImageRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PgPropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final CloudinaryService cloudinaryService;

    private static final int MAX_IMAGES = 10;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    @Transactional
    public PropertyImageResponse uploadImage(UUID propertyId, UUID ownerId, MultipartFile file) {

        validateImage(file);

        PgProperty property = propertyRepository.findOneByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        long imageCount = propertyImageRepository.countByProperty_Id(propertyId);

        if (imageCount >= MAX_IMAGES) {
            throw new BadRequestException("A property can have a maximum of 10 images");
        }

        String folder = "staysphere/properties/" + propertyId;

        Map<?, ?> uploadResult;

        try {
            uploadResult = cloudinaryService.uploadImage(file, folder);
        } catch (IOException | RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Image upload failed",
                    ex);
        }

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        if (imageUrl == null || imageUrl.isBlank() || publicId == null || publicId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Invalid response from image storage");
        }

        PropertyImage image = new PropertyImage();

        image.setProperty(property);
        image.setImageUrl(imageUrl);
        image.setPublicId(publicId);
        image.setCover(imageCount == 0);

        PropertyImage savedImage = propertyImageRepository.saveAndFlush(image);

        return toPropertyImageResponse(savedImage);
    }

    @Transactional(readOnly = true)
    public List<PropertyImageResponse> getAllImages(UUID propertyId, UUID ownerId) {

        propertyRepository.findByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        List<PropertyImage> images = propertyImageRepository
                .findByProperty_IdAndProperty_Owner_Id(propertyId, ownerId);

        return images.stream()
                .map(this::toPropertyImageResponse)
                .toList();
    }

    @Transactional
    public PropertyImageResponse setCoverImage(UUID propertyId, UUID ownerId, UUID imageId) {

        propertyRepository.findOneByIdAndOwner_Id(propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        PropertyImage selectedImage = propertyImageRepository
                .findByIdAndProperty_IdAndProperty_Owner_Id(imageId, propertyId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (selectedImage.isCover()) {
            return toPropertyImageResponse(selectedImage);
        }

        propertyImageRepository.findByProperty_IdAndIsCoverTrue(propertyId)
                .ifPresent(currentCover -> {
                    currentCover.setCover(false);
                    propertyImageRepository.saveAndFlush(currentCover);
                });

        selectedImage.setCover(true);

        PropertyImage savedImage = propertyImageRepository.saveAndFlush(selectedImage);

        return toPropertyImageResponse(savedImage);
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Image size cannot exceed 5 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPEG, PNG and WebP images are allowed");
        }
    }

    private PropertyImageResponse toPropertyImageResponse(PropertyImage image) {

        PropertyImageResponse response = new PropertyImageResponse();

        response.setId(image.getId());
        response.setPropertyId(image.getProperty().getId());
        response.setImageUrl(image.getImageUrl());
        response.setCover(image.isCover());
        response.setCreatedAt(image.getCreatedAt());
        response.setUpdatedAt(image.getUpdatedAt());

        return response;
    }

}
