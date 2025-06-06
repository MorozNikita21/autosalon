package com.autosalon.backend.profile.controller;

import com.autosalon.backend.profile.dto.ClientUpdateDto;
import com.autosalon.backend.profile.service.ProfileService;
import com.autosalon.backend.general.entity.Client;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping
    public ResponseEntity<Client> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ClientUpdateDto dto) {

        String currentLogin = userDetails.getUsername();

        Client updated = profileService.updateProfileByLogin(currentLogin, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            String currentLogin = userDetails.getUsername();
            String path = profileService.updatePhotoByLogin(currentLogin, file);
            return ResponseEntity.ok(path);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении фото");
        }
    }
}
