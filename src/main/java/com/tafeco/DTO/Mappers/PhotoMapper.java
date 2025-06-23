package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.PhotoDTO;
import com.tafeco.Models.Entity.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {
    PhotoDTO toDTO(Photo photo);
    Photo toEntity(PhotoDTO dto);
    List<PhotoDTO> toDTOList(List<Photo> photos);
}
