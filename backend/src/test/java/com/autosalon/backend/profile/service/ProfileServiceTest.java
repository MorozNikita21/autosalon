package com.autosalon.backend.profile.service;

import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthClientRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.Client;
import com.autosalon.backend.profile.dto.ClientUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ProfileServiceTest {

    @Mock
    private AuthClientRepository changeClientRepository;

    @Mock
    private AuthAccountRepository changeAccountRepository;

    @Mock
    private AuthRoleRepository changeRoleRepository;

    @InjectMocks
    private ProfileService profileService;

    @TempDir
    File tempDir;

    private Client existingClient;
    private Account existingAccount;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        existingClient = new Client();
        existingClient.setClientId(1L);
        existingClient.setLogin("oldLogin");
        existingClient.setName("Old Name");
        existingClient.setEmail("old@example.com");
        existingClient.setBirthday(LocalDate.of(2000, 1, 1));
        existingClient.setAddress("Old Address");
        existingClient.setPassport("A1234567");
        existingClient.setDriverLicense("DL123");
        existingClient.setFirstLicenseDate(LocalDate.of(2018, 5, 20));

        existingAccount = new Account();
        existingAccount.setLogin("oldLogin");
        existingAccount.setPassword("password");
        existingAccount.setRoles(Set.of());

        Field uploadDirField = ProfileService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(profileService, tempDir.toPath().resolve("clients").toString());
    }

    @Test
    void testUpdateProfileNoLoginChangeSuccess() {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("oldLogin");
        dto.setName("New Name");
        dto.setEmail("new@example.com");
        dto.setBirthday(LocalDate.of(1999, 12, 31));
        dto.setAddress("New Address");
        dto.setPassport("B7654321");
        dto.setDriverLicense("DL999");
        dto.setFirstLicenseDate(LocalDate.of(2019, 6, 15));

        when(changeClientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(changeClientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = profileService.updateProfile(1L, dto);

        assertEquals("oldLogin", result.getLogin());
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(LocalDate.of(1999, 12, 31), result.getBirthday());
        assertEquals("New Address", result.getAddress());
        assertEquals("B7654321", result.getPassport());
        assertEquals("DL999", result.getDriverLicense());
        assertEquals(LocalDate.of(2019, 6, 15), result.getFirstLicenseDate());

        verify(changeClientRepository).findById(1L);
        verify(changeClientRepository).save(any(Client.class));
        verify(changeAccountRepository, never()).existsByLogin(anyString());
        verify(changeAccountRepository, never()).save(any(Account.class));
        verify(changeAccountRepository, never()).delete(any(Account.class));
    }

    @Test
    void testUpdateProfileChangeLoginSuccess() {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("newLogin");
        dto.setName("Name");
        dto.setEmail("email@example.com");
        dto.setBirthday(LocalDate.of(1995, 3, 10));
        dto.setAddress("Address");
        dto.setPassport("P5555555");
        dto.setDriverLicense("DL555");
        dto.setFirstLicenseDate(LocalDate.of(2020, 1, 1));

        when(changeClientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(changeAccountRepository.existsByLogin("newLogin")).thenReturn(false);

        when(changeClientRepository.findByLogin("oldLogin")).thenReturn(Optional.of(existingClient));

        when(changeAccountRepository.updateLoginInAccount("oldLogin", "newLogin"))
                .thenReturn(1);

        when(changeRoleRepository.updateAccountLoginInAccountRole("oldLogin", "newLogin"))
                .thenReturn(1);

        when(changeClientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Client result = profileService.updateProfile(1L, dto);

        assertEquals("newLogin", result.getLogin());
        assertEquals("Name", result.getName());

        // 8. Проверяем, что вызвались все новые методы:
        verify(changeAccountRepository).existsByLogin("newLogin");
        verify(changeAccountRepository).updateLoginInAccount("oldLogin", "newLogin");
        verify(changeRoleRepository).updateAccountLoginInAccountRole("oldLogin", "newLogin");
        verify(changeClientRepository).save(any(Client.class));
    }

    @Test
    void testUpdateProfileChangeLoginLoginAlreadyExistsShouldThrow() {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("takenLogin");
        dto.setName("Name");
        dto.setEmail("email@example.com");
        dto.setBirthday(LocalDate.of(1995, 3, 10));
        dto.setAddress("Address");
        dto.setPassport("P5555555");
        dto.setDriverLicense("DL555");
        dto.setFirstLicenseDate(LocalDate.of(2020, 1, 1));

        when(changeClientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(changeAccountRepository.existsByLogin("takenLogin")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(1L, dto));
        assertEquals("Логин уже занят", ex.getMessage());

        verify(changeAccountRepository).existsByLogin("takenLogin");
        verify(changeAccountRepository, never()).findByLogin(anyString());
        verify(changeClientRepository, never()).save(any(Client.class));
    }

    @Test
    void testUpdateProfileClientNotFoundShouldThrow() {
        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("any");
        when(changeClientRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(2L, dto));
        assertEquals("Клиент не найден", ex.getMessage());

        verify(changeClientRepository).findById(2L);
        verifyNoMoreInteractions(changeClientRepository);
    }

    @Test
    void testUpdatePhotoInvalidClientShouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg",
                "image/jpeg", "dummy".getBytes());
        when(changeClientRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updatePhoto(2L, file));
        assertEquals("Клиент не найден", ex.getMessage());
    }

    @Test
    void testUpdatePhotoUnsupportedContentTypeShouldThrow() {
        existingClient.setClientId(3L);
        when(changeClientRepository.findById(3L)).thenReturn(Optional.of(existingClient));
        MockMultipartFile file = new MockMultipartFile("file", "data.txt",
                "text/plain", "not an image".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updatePhoto(3L, file));
        assertTrue(ex.getMessage().startsWith("Неподдерживаемый тип файла"));
    }

    @Test
    void testUpdatePhotoSuccess() throws IOException {
        existingClient.setClientId(4L);
        when(changeClientRepository.findById(4L)).thenReturn(Optional.of(existingClient));

        byte[] imageBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", imageBytes
        );

        String relativePath = profileService.updatePhoto(4L, file);

        assertNotNull(relativePath);
        assertTrue(relativePath.startsWith("/uploads/clients/4/"));

        File savedFile = new File("." + relativePath);
        assertTrue(savedFile.exists(), "Ожидаем, что файл будет существовать: " + savedFile.getAbsolutePath());
        assertTrue(savedFile.length() > 0, "Файл не должен быть пустым");

        String filename = relativePath.substring(relativePath.lastIndexOf("/") + 1);
        File clientDir = new File("." + "/uploads/clients/4/");
        File toDelete = new File(clientDir, filename);
        toDelete.delete();
        clientDir.delete();
        new File("." + "/uploads/clients").delete();
        new File("." + "/uploads").delete();
    }

    @Test
    void testUpdatePhotoByLoginSuccess() throws IOException {
        existingClient.setClientId(5L);
        existingClient.setLogin("userLogin");
        when(changeClientRepository.findByLogin("userLogin")).thenReturn(Optional.of(existingClient));
        when(changeClientRepository.findById(5L)).thenReturn(Optional.of(existingClient));

        byte[] imageBytes = "fakeimage".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", imageBytes
        );

        String relativePath = profileService.updatePhotoByLogin("userLogin", file);

        assertNotNull(relativePath);
        assertTrue(relativePath.startsWith("/uploads/clients/5/"));

        File savedFile = new File("." + relativePath);
        assertTrue(savedFile.exists(), "Ожидаем, что файл будет существовать: " + savedFile.getAbsolutePath());
        assertTrue(savedFile.length() > 0);

        String filename = relativePath.substring(relativePath.lastIndexOf("/") + 1);
        File clientDir = new File("." + "/uploads/clients/5/");
        new File(clientDir, filename).delete();
        clientDir.delete();
        new File("." + "/uploads/clients").delete();
        new File("." + "/uploads").delete();
    }

    @Test
    void testUpdatePhotoByLoginClientNotFoundShouldThrow() {
        when(changeClientRepository.findByLogin("unknown")).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg",
                "image/jpeg", "dummy".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updatePhotoByLogin("unknown", file));
        assertEquals("Клиент не найден", ex.getMessage());
    }

    @Test
    void testUpdateProfileByLoginClientNotFoundShouldThrow() {
        when(changeClientRepository.findByLogin("noUser")).thenReturn(Optional.empty());

        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setLogin("noUser");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfileByLogin("noUser", dto));
        assertEquals("Клиент не найден", ex.getMessage());
    }
}
