package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.Archive;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ArchiveDTO {

    private Long id;         // если null — это новый файл
    private String archive;
    private String description;

    public static ArchiveDTO fromEntity(Archive archive) {
        return new ArchiveDTO(archive.getId(), archive.getArchive(), archive.getDescription());
    }
}
