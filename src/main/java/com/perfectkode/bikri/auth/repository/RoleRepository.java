package com.perfectkode.bikri.auth.repository;

import com.perfectkode.bikri.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByRoleCode(Integer roleCode);
}