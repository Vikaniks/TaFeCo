package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleUserDAO extends JpaRepository<RoleUser, Long> {

    Optional<RoleUser> findByRole(String role);
}


