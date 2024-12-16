package com.example.xventure.repository;

import com.example.xventure.model.Role;
import com.example.xventure.model.RolePermissions;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionsRepository extends JpaRepository<RolePermissions, Integer> {

    Boolean existsByRoleAndPermissions_Id(Role role, Integer permissionId);
    void deleteByRoleAndPermissions_Id(Role role, Integer permissionId);
}
