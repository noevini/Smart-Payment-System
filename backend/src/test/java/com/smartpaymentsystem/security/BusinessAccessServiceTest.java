package com.smartpaymentsystem.security;

import com.smartpaymentsystem.domain.Business;
import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import com.smartpaymentsystem.repository.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessAccessServiceTest {

    @Mock private CurrentUserService currentUserService;
    @Mock private BusinessRepository businessRepository;

    @InjectMocks
    private BusinessAccessService businessAccessService;

    private User ownerUser;
    private User staffUser;
    private Business assignedBusiness;

    @BeforeEach
    void setUp() {
        assignedBusiness = new Business();
        assignedBusiness.setId(1L);
        assignedBusiness.setName("Test Business");

        ownerUser = new User();
        ownerUser.setId(100L);
        ownerUser.setRole(UserRole.OWNER);

        staffUser = new User();
        staffUser.setId(200L);
        staffUser.setRole(UserRole.STAFF);
        staffUser.setBusiness(assignedBusiness);
    }

    // ── OWNER tests ──

    @Test
    void owner_accessingOwnBusiness_passes() {
        when(currentUserService.getCurrentUser()).thenReturn(ownerUser);
        when(businessRepository.existsByIdAndOwners_Id(1L, 100L)).thenReturn(true);

        assertDoesNotThrow(() -> businessAccessService.assertCanAccessBusiness(1L));
        verify(businessRepository).existsByIdAndOwners_Id(1L, 100L);
    }

    @Test
    void owner_accessingAnotherBusiness_throws403() {
        when(currentUserService.getCurrentUser()).thenReturn(ownerUser);
        when(businessRepository.existsByIdAndOwners_Id(2L, 100L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> businessAccessService.assertCanAccessBusiness(2L));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatusCode().value());
    }

    @Test
    void owner_notOwnerOfBusiness_throws403WithMessage() {
        when(currentUserService.getCurrentUser()).thenReturn(ownerUser);
        when(businessRepository.existsByIdAndOwners_Id(5L, 100L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> businessAccessService.assertCanAccessBusiness(5L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("access"));
    }

    // ── STAFF tests ──

    @Test
    void staff_accessingAssignedBusiness_passes() {
        when(currentUserService.getCurrentUser()).thenReturn(staffUser);

        assertDoesNotThrow(() -> businessAccessService.assertCanAccessBusiness(1L));
        verifyNoInteractions(businessRepository);
    }

    @Test
    void staff_accessingDifferentBusiness_throws403() {
        when(currentUserService.getCurrentUser()).thenReturn(staffUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> businessAccessService.assertCanAccessBusiness(2L));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatusCode().value());
    }

    @Test
    void staff_withNoBusiness_throws403() {
        staffUser.setBusiness(null);
        when(currentUserService.getCurrentUser()).thenReturn(staffUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> businessAccessService.assertCanAccessBusiness(1L));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatusCode().value());
    }

    @Test
    void staff_accessingExactAssignedId_doesNotQueryRepository() {
        when(currentUserService.getCurrentUser()).thenReturn(staffUser);

        businessAccessService.assertCanAccessBusiness(1L);

        verifyNoInteractions(businessRepository);
    }
}
