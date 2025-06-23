package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IArchiveDAO extends JpaRepository<Archive, Long> {
}
