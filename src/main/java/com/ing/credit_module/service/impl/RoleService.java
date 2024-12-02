package com.ing.credit_module.service.impl;

import com.ing.credit_module.model.Role;
import com.ing.credit_module.repository.IRoleRepository;
import com.ing.credit_module.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ing.credit_module.constants.ServiceConstants.ROLE_SERVICE;

@RequiredArgsConstructor
@Service(value = ROLE_SERVICE)
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;

    @Override
    public Role getDefaultRole() {
        return roleRepository.findDefault();
    }
}
