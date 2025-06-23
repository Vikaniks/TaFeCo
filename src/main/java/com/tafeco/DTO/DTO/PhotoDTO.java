package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private Long id;         // если null — это новое фото
    private String photo;

    public static PhotoDTO fromEntity(Photo photo) {
        return new PhotoDTO(photo.getId(), photo.getPhoto());
    }
}