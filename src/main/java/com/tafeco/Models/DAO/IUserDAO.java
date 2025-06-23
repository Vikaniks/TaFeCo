package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserDAO extends JpaRepository<User, Long> {

    List<User> findAllByActive(boolean active);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        select u from User u
        where (:username is null or lower(u.username) like lower(concat('%', :username, '%')))
          and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
    """)
    Page<User> findWithFilters(
            @Param("username") String username,
            @Param("email") String email,
            Pageable pageable
    );

    Optional<User> findByEmail(String email);
}

