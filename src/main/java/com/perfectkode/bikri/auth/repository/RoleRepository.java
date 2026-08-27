package com.perfectkode.bikri.auth.repository;

import com.perfectkode.bikri.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}