package com.kafetzisthomas.springvault.rest;

import com.kafetzisthomas.springvault.dao.EncryptionKeyRepository;
import com.kafetzisthomas.springvault.security.AesGcmEncryptor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsManager userDetailsManager;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EncryptionKeyRepository encryptionKeyRepository;

    @Test
    void whenValidRegistration_thenCreateUserAndRedirect() throws Exception {
        Mockito.when(userDetailsManager.userExists("testuser")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        try (MockedStatic<AesGcmEncryptor> mock = mockStatic(AesGcmEncryptor.class)) {
            mock.when(AesGcmEncryptor::generateKey).thenReturn("key123");

            mockMvc.perform(post("/register")
                            .with(Objects.requireNonNull(csrf()))
                            .param("username", "testuser")
                            .param("password", "password123")
                            .param("confirmPassword", "password123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("registered", true));

            verify(userDetailsManager).createUser(any(UserDetails.class));
            verify(encryptionKeyRepository).save(Objects.requireNonNull(any()));
        }
    }

    @Test
    void whenUsernameOrPasswordMissing_thenReturnError() throws Exception {
        mockMvc.perform(post("/register")
                        .with(Objects.requireNonNull(csrf()))
                        .param("username", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attribute("error", "Username and password are required."));

        verifyNoInteractions(userDetailsManager, encryptionKeyRepository);
    }

    @Test
    void whenPasswordsDoNotMatch_thenReturnError() throws Exception {
        mockMvc.perform(post("/register")
                        .with(Objects.requireNonNull(csrf()))
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attribute("error", "Passwords do not match."));

        verifyNoInteractions(userDetailsManager, encryptionKeyRepository);
    }

    @Test
    void whenUserAlreadyExists_thenReturnError() throws Exception {
        when(userDetailsManager.userExists("testuser")).thenReturn(true);

        mockMvc.perform(post("/register")
                        .with(Objects.requireNonNull(csrf()))
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attribute("error", "User already exists."));

        verify(userDetailsManager, never()).createUser(any());
        verify(encryptionKeyRepository, never()).save(Objects.requireNonNull(any()));
    }

}
