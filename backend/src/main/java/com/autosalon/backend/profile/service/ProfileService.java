package com.autosalon.backend.profile.service;

import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthClientRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.profile.dto.ClientUpdateDto;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private AuthClientRepository changeClientRepository;

    @Autowired
    private AuthAccountRepository changeAccountRepository;

    @Autowired
    private AuthRoleRepository changeRoleRepository;

    private final String uploadDir = "uploads/clients";

    @Transactional
    public Client updateProfile(Long clientId, ClientUpdateDto dto) {
        Client client = changeClientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));

        String oldLogin = client.getLogin();
        String newLogin = dto.getLogin().trim();

        if (!oldLogin.equals(newLogin)) {
            if (changeAccountRepository.existsByLogin(newLogin)) {
                throw new IllegalArgumentException("Логин уже занят");
            }
            int updatedAccountRows = changeAccountRepository.updateLoginInAccount(oldLogin, newLogin);
            if (updatedAccountRows != 1) {
                throw new IllegalStateException("Не удалось обновить логин в таблице account");
            }
            changeRoleRepository.updateAccountLoginInAccountRole(oldLogin, newLogin);

            client.setLogin(newLogin);
        }

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setBirthday(dto.getBirthday());
        client.setAddress(dto.getAddress());
        client.setPassport(dto.getPassport());
        client.setDriverLicense(dto.getDriverLicense());
        client.setFirstLicenseDate(dto.getFirstLicenseDate());

        return changeClientRepository.save(client);
    }

    @Transactional
    public String updatePhoto(Long clientId, MultipartFile file) throws IOException {
        Client client = changeClientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Неподдерживаемый тип файла: " + contentType);
        }

        File dir = Paths.get(uploadDir, String.valueOf(clientId)).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        File dest = new File(dir, filename);
        file.transferTo(dest);

        String relativePath = "/uploads/clients/" + clientId + "/" + filename;
        client.setPhotoUrl(relativePath);

        changeClientRepository.save(client);
        return relativePath;
    }

    @Transactional
    public Client updateProfileByLogin(String currentLogin, ClientUpdateDto dto) {
        Client client = changeClientRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
        return updateProfile(client.getClientId(), dto);
    }

    @Transactional
    public String updatePhotoByLogin(String currentLogin, MultipartFile file) throws IOException {
        Client client = changeClientRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
        return updatePhoto(client.getClientId(), file);
    }
}
