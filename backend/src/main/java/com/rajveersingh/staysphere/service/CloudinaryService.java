package com.rajveersingh.staysphere.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<?, ?> uploadImage(MultipartFile file, String folder) throws IOException {

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "asset_folder", folder,
                        "allowed_formats", List.of("jpg", "jpeg", "png", "webp")));
        return uploadResult;
    }

    public void deleteImage(String publicId) throws IOException {

        Map<?, ?> deleteResult = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "invalidate", true));

        Object result = deleteResult.get("result");

        if (!"ok".equals(result) && !"not found".equals(result)) {
            throw new IOException("Image deletion failed");
        }

    }

}
