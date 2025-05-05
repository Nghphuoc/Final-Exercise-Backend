package jpa.studentmanagementsystem.service;

import jpa.studentmanagementsystem.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public interface RoleService {

    void CreateRole(Role role);

    void UpdateRole(Long roleId, Role role);

    void DeleteRole(Long roleId);

    Optional<Role> findRoleById(Long id);

    Role resolveUserRole(Set<String> requestedRoles);
}
