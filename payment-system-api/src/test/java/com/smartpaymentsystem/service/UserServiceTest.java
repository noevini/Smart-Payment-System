package com.smartpaymentsystem.service;

import com.smartpaymentsystem.api.exceptionhandler.ResourceNotFoundException;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setPhone("07700000001");
        user.setEmail("alice@example.com");
        user.setRole(UserRole.OWNER);
        user.setPasswordHash("hashed");
    }

    // ── getById ──

    @Test
    void getById_found_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    // ── updateUser ──

    @Test
    void updateUser_nameChanged_savesAndReturns() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, "Alice Updated", null);

        assertEquals("Alice Updated", result.getName());
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_phoneChanged_savesAndReturns() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, null, "07799999999");

        assertEquals("07799999999", result.getPhone());
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_noChanges_doesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.updateUser(1L, "Alice", "07700000001");

        verify(userRepository, never()).save(any());
        assertEquals("Alice", result.getName());
    }

    @Test
    void updateUser_nullFields_doesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUser(1L, null, null);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(99L, "Name", null));
    }

    // ── deleteUser ──

    @Test
    void deleteUser_found_deletesSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).delete(any());
    }

    // ── existsByEmail ──

    @Test
    void existsByEmail_exists_returnsTrue() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("alice@example.com"));
    }

    @Test
    void existsByEmail_notExists_returnsFalse() {
        when(userRepository.existsByEmail("nobody@example.com")).thenReturn(false);

        assertFalse(userService.existsByEmail("nobody@example.com"));
    }
}
