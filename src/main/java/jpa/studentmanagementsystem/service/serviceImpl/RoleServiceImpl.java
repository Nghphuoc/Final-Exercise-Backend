package jpa.studentmanagementsystem.service.serviceImpl;

import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.repository.RoleRepository;
import jpa.studentmanagementsystem.service.RoleService;
import jpa.studentmanagementsystem.variable.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void CreateRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void UpdateRole(Long roleId, Role role) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            roleOptional.get().setRoleName(role.getRoleName());
            roleRepository.save(roleOptional.get());
        }
    }

    @Override
    public void DeleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()->new RuntimeException("Role not found: "));
        roleRepository.delete(role);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role resolveUserRole(Set<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return roleRepository.findByRoleName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        }

        for (String roleStr : requestedRoles) {
            if ("admin".equalsIgnoreCase(roleStr)) {
                return roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            }
        }

        return roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
    }


}
