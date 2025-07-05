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

    @Query("SELECT u FROM User u\n" +
            "WHERE (:fullName IS NULL OR CONCAT(u.name, ' ', u.surname) LIKE :fullName)\n" +
            "AND (:phone IS NULL OR u.phone LIKE :phone)\n" +
            "AND (\n" +
            "    :address IS NULL OR CONCAT(\n" +
            "        COALESCE(u.address.region.name, ''), ' ',\n" +
            "        COALESCE(u.address.district.name, ''), ' ',\n" +
            "        COALESCE(u.address.locality.name, ''), ' ',\n" +
            "        COALESCE(u.address.street.name, ''), ' ',\n" +
            "        COALESCE(u.address.house, ''), ' ',\n" +
            "        COALESCE(u.address.apartment, ''), ' ',\n" +
            "        COALESCE(u.address.addressExtra, '')\n" +
            "    ) LIKE :address\n" +
            ")")
    Page<User> findUsersWithFilters(@Param("fullName") String fullName,
                                    @Param("phone") String phone,
                                    @Param("address") String address,
                                    Pageable pageable);




    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    // метод для поиска пользователей с временным паролем, который ещё действителен
    @Query("""
        select u from User u
        where u.tempPasswordExpiration is not null
          and u.tempPasswordExpiration > current_timestamp
    """)
    List<User> findUsersWithActiveTempPassword();


}

