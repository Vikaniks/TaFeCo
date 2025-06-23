package com.tafeco.Models.DAO;

import com.tafeco.Models.Entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPhotoDAO extends JpaRepository<Photo, Long> {
}
