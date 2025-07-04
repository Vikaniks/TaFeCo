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

    boolean existsByEmail(String email);

    @Query("""
        select u from User u
        where (:name is null or lower(u.name) like lower(concat('%', :name, '%')))
          and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
    """)
    Page<User> findWithFilters(
            @Param("name") String name,
            @Param("email") String email,
            Pageable pageable
    );

    Optional<User> findByEmail(String email);

    // метод для поиска пользователей с временным паролем, который ещё действителен
    @Query("""
        select u from User u
        where u.tempPasswordExpiration is not null
          and u.tempPasswordExpiration > current_timestamp
    """)
    List<User> findUsersWithActiveTempPassword();


}

