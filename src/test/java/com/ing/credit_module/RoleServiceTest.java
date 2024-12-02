package com.ing.credit_module;

import com.ing.credit_module.model.Role;
import com.ing.credit_module.repository.IRoleRepository;
import com.ing.credit_module.service.impl.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetDefaultRole() {
        // Arrange
        Role expectedRole = new Role();
        expectedRole.setId(1L);
        expectedRole.setName("DEFAULT");
        when(roleRepository.findDefault()).thenReturn(expectedRole);

        // Act
        Role actualRole = roleService.getDefaultRole();

        // Assert
        assertEquals(expectedRole, actualRole);
        verify(roleRepository, times(1)).findDefault(); // Ensure method is called once
    }
}