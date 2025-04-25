package jpa.studentmanagementsystem.repository;

import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.variable.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName name);
}
