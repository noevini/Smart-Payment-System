package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.dto.LoginRequestDTO;
import com.smartpaymentsystem.api.dto.LoginResponseDTO;
import com.smartpaymentsystem.api.dto.RegisterRequestDTO;
import com.smartpaymentsystem.api.exceptionhandler.ConflictException;
import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.BusinessRepository;
import com.smartpaymentsystem.repository.UserRepository;
import com.smartpaymentsystem.security.CurrentUserService;
import com.smartpaymentsystem.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BusinessRepository businessRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private CurrentUserService currentUserService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO ownerRequest;
    private RegisterRequestDTO staffRequest;
    private User savedOwner;
    private User currentOwner;
    private Business business;

    @BeforeEach
    void setUp() {
        ownerRequest = new RegisterRequestDTO();
        ownerRequest.setName("Alice Owner");
        ownerRequest.setEmail("alice@example.com");
        ownerRequest.setPhone("07700000001");
        ownerRequest.setPassword("secret123");
        ownerRequest.setRole(UserRole.OWNER);

        business = new Business();
        business.setId(5L);
        business.setName("Alice's Shop");

        currentOwner = new User();
        currentOwner.setId(1L);
        currentOwner.setRole(UserRole.OWNER);

        staffRequest = new RegisterRequestDTO();
        staffRequest.setName("Bob Staff");
        staffRequest.setEmail("bob@example.com");
        staffRequest.setPhone("07700000002");
        staffRequest.setPassword("pass456");
        staffRequest.setRole(UserRole.STAFF);
        staffRequest.setBusinessId(5L);

        savedOwner = new User();
        savedOwner.setId(10L);
        savedOwner.setEmail("alice@example.com");
        savedOwner.setRole(UserRole.OWNER);
    }

    // ── register ──

    @Test
    void register_owner_savesUserSuccessfully() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedOwner);

        User result = authService.register(ownerRequest);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void register_duplicateEmail_throwsConflictException() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(ownerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_emailIsTrimmedAndLowercased() {
        ownerRequest.setEmail("  Alice@EXAMPLE.COM  ");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register(ownerRequest);

        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void register_nullRole_throwsConflictException() {
        ownerRequest.setRole(null);

        assertThrows(ConflictException.class, () -> authService.register(ownerRequest));
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void register_staff_withValidBusiness_savesUser() {
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(currentUserService.getCurrentUser()).thenReturn(currentOwner);
        when(businessRepository.findByIdAndOwners_Id(5L, 1L)).thenReturn(Optional.of(business));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register(staffRequest);

        assertNotNull(result);
        assertEquals(business, result.getBusiness());
    }

    @Test
    void register_staff_withoutBusinessId_throwsConflictException() {
        staffRequest.setBusinessId(null);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);

        assertThrows(ConflictException.class, () -> authService.register(staffRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_staff_currentUserIsNotOwner_throwsForbidden() {
        User staffCurrentUser = new User();
        staffCurrentUser.setId(2L);
        staffCurrentUser.setRole(UserRole.STAFF);

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(currentUserService.getCurrentUser()).thenReturn(staffCurrentUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(staffRequest));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void register_staff_businessNotOwnedByCurrentUser_throwsNotFound() {
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(currentUserService.getCurrentUser()).thenReturn(currentOwner);
        when(businessRepository.findByIdAndOwners_Id(5L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(staffRequest));
        verify(userRepository, never()).save(any());
    }

    // ── login ──

    @Test
    void login_validCredentials_returnsTokenAndUserId() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        User user = new User();
        user.setId(10L);
        user.setEmail("alice@example.com");
        user.setPasswordHash("hashed");
        user.setRole(UserRole.OWNER);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(jwtTokenService.generateToken(user)).thenReturn("jwt-token-abc");

        LoginResponseDTO result = authService.login(req);

        assertEquals("jwt-token-abc", result.getToken());
        assertEquals(10L, result.getUserId());
        assertEquals(UserRole.OWNER, result.getRole());
    }

    @Test
    void login_wrongPassword_throws401() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail("alice@example.com");
        req.setPassword("wrongpass");

        User user = new User();
        user.setPasswordHash("hashed");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_unknownEmail_throws401() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail("nobody@example.com");
        req.setPassword("pass");

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_emailIsTrimmedAndLowercased() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail("  Alice@EXAMPLE.COM  ");
        req.setPassword("secret123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authService.login(req));
        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void login_staffUser_returnsBusinessId() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail("bob@example.com");
        req.setPassword("pass456");

        User staff = new User();
        staff.setId(20L);
        staff.setEmail("bob@example.com");
        staff.setPasswordHash("hashed2");
        staff.setRole(UserRole.STAFF);
        staff.setBusiness(business);

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(staff));
        when(passwordEncoder.matches("pass456", "hashed2")).thenReturn(true);
        when(jwtTokenService.generateToken(staff)).thenReturn("staff-token");

        LoginResponseDTO result = authService.login(req);

        assertEquals(5L, result.getBusinessId());
        assertEquals(UserRole.STAFF, result.getRole());
    }
}
