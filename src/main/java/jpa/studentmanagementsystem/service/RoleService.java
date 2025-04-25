package jpa.studentmanagementsystem.service;

import jpa.studentmanagementsystem.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface RoleService {

    void CreateRole(Role role);

    void UpdateRole(Long roleId, Role role);

    void DeleteRole(Long roleId);

    Optional<Role> findRoleById(Long id);
}
