package com.autosalon.backend.profile.controller;

import com.autosalon.backend.auth.security.AuthTokenFilter;
import com.autosalon.backend.general.entity.Client;
import com.autosalon.backend.profile.dto.ClientUpdateDto;
import com.autosalon.backend.profile.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "userLogin")
    @Test
    void testUpdateProfileSuccess() throws Exception {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("userLogin");
        dto.setName("Name");
        dto.setEmail("email@mail.com");
        dto.setBirthday(LocalDate.of(2000, 1, 1));
        dto.setAddress("Address");
        dto.setPassport("P1111111");
        dto.setDriverLicense("DL111");
        dto.setFirstLicenseDate(LocalDate.of(2018, 2, 2));

        Client returnedClient = new Client();
        returnedClient.setClientId(1L);
        returnedClient.setLogin("userLogin");

        when(profileService.updateProfileByLogin(eq("userLogin"), any(ClientUpdateDto.class)))
                .thenReturn(returnedClient);

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @WithMockUser(username = "userLogin")
    @Test
    void testUpdateProfileErrorLoginTaken() throws Exception {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("userLogin");

        doThrow(new IllegalArgumentException("Login уже занят"))
                .when(profileService).updateProfileByLogin(eq("userLogin"), any(ClientUpdateDto.class));

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @WithMockUser(username = "userLogin")
    @Test
    void testUpdateProfileValidationError() throws Exception {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("userLogin");
        dto.setName(null);

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @WithMockUser(username = "userLogin")
    @Test
    void testUploadPhotoSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );

        when(profileService.updatePhotoByLogin(eq("userLogin"), any()))
                .thenReturn("/uploads/clients/1/photo.jpg");

        mockMvc.perform(multipart("/api/profile/photo")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @WithMockUser(username = "userLogin")
    @Test
    void testUploadPhotoBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "data.txt", "text/plain", "not an image".getBytes()
        );
        doThrow(new IllegalArgumentException("Неподдерживаемый тип файла"))
                .when(profileService).updatePhotoByLogin(eq("userLogin"), any());

        mockMvc.perform(multipart("/api/profile/photo")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @WithMockUser(username = "userLogin")
    @Test
    void testUploadPhotoServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );
        doThrow(new RuntimeException("Disk error"))
                .when(profileService).updatePhotoByLogin(eq("userLogin"), any());

        mockMvc.perform(multipart("/api/profile/photo")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
