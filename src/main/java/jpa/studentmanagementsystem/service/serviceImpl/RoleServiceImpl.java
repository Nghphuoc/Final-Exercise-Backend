package jpa.studentmanagementsystem.service.serviceImpl;

import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.repository.RoleRepository;
import jpa.studentmanagementsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Role role = roleRepository.findById(roleId).orElseThrow(()->new RuntimeException("Role not found: "));
        roleRepository.delete(role);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }
}
