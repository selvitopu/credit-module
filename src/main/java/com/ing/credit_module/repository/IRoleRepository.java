package com.ing.credit_module.repository;

import com.ing.credit_module.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.isDefault = true")
    Role findDefault();
}