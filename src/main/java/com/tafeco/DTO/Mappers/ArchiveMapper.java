package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.ArchiveDTO;
import com.tafeco.Models.Entity.Archive;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArchiveMapper {


    ArchiveDTO toDTO(Archive archive);

    Archive toEntity(ArchiveDTO archiveDTO);

    List<ArchiveDTO> toDTOList(List<Archive> all);

}

