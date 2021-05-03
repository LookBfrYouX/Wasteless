package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class BasicImageDto {

    private int id;
    private String filename;
    private String thumbnailFilename;

    public BasicImageDto(Image image) {
        this.id = image.getId();
        this.filename = image.getFilename();
        this.thumbnailFilename = image.getThumbnailFilename();
    }

}
