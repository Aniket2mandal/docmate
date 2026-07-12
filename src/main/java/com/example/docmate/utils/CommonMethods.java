package com.example.docmate.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.payload.request.PaginationInfo;
import com.example.docmate.payload.response.CloudinaryUploadResponse;
import com.example.docmate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommonMethods {

    private final Cloudinary cloudinary;

    public String getAuthenticatedUserEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new GlobalException(" User is not authenticated ", HttpStatus.UNAUTHORIZED);
        }
        //getName() returns the "principal" (the main identifier), which YOU set as the email when creating the JWT token! so it returns email
        String email = authentication.getName();
        return email;
    }

    public static PaginationInfo getPaginationInfo(Page<?> page) {
        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setTotal(page.getTotalElements());
        paginationInfo.setPageSize(page.getSize());
        paginationInfo.setPageNo(page.getNumber());
        paginationInfo.setTotalPages(page.getTotalPages());
        return paginationInfo;
    }

    public String buildPath(String folderName,String id, String documentType) {
        String path = "docmate/"+folderName+ "/" + id ;
        if (documentType != null && !documentType.isBlank()) {
            path += "/" + documentType;
        }
        return path;
    }

    public CloudinaryUploadResponse uploadDoctorDocument(
            MultipartFile file,String path,String oldPublicId) {
        try {

            if (file == null || file.isEmpty()) {
                throw new GlobalException(
                        "Please select a document",
                        HttpStatus.BAD_REQUEST
                );
            }

            String contentType = file.getContentType();

            if (contentType == null ||
                    (!contentType.startsWith("image/") &&
                            !contentType.equals("application/pdf"))) {

                throw new GlobalException(
                        "Only image and PDF files are allowed",
                        HttpStatus.BAD_REQUEST
                );
            }


            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", path,
                            "resource_type", "auto"
                    )
            );


            String newUrl = uploadResult.get("secure_url").toString();
            String newPublicId = uploadResult.get("public_id").toString();


            // delete old document after successful upload
            if (oldPublicId != null && !oldPublicId.isBlank()) {

                cloudinary.uploader().destroy(
                        oldPublicId,
                        ObjectUtils.emptyMap()
                );
            }


            return CloudinaryUploadResponse.builder()
                    .url(newUrl)
                    .publicId(newPublicId)
                    .build();


        } catch (IOException e) {

            throw new GlobalException(
                    "Failed to upload document",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public CloudinaryUploadResponse moveDocument(
            String documentUrl,
            String oldPublicId,
            String newPath) {

        try {

            URL url = new URL(documentUrl);

            byte[] bytes = url.openStream().readAllBytes();

            Map uploadResult = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                            "folder", newPath,
                            "resource_type", "auto"
                    )
            );

            String newUrl = uploadResult.get("secure_url").toString();
            String newPublicId = uploadResult.get("public_id").toString();

            deleteFiles(oldPublicId);

            return CloudinaryUploadResponse.builder()
                    .url(newUrl)
                    .publicId(newPublicId)
                    .build();

        } catch (Exception e) {
            throw new GlobalException(
                    "Failed to move document",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public void deleteFiles(String oldPublicId){

        try {
            if (oldPublicId != null && !oldPublicId.isBlank()) {
                cloudinary.uploader().destroy(oldPublicId, ObjectUtils.emptyMap());
            }
        }catch (Exception e) {
            throw new GlobalException(
                    "Failed to move document",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public void deleteSubFolder(String subFolderName,String id){
        try {
            cloudinary.api().deleteFolder(
                    "docmate/"+subFolderName+"/" +id,
                    ObjectUtils.emptyMap()
            );
        } catch (Exception e) {
            throw new GlobalException(
                    "Failed to delete Cloudinary folder",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
