package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Image;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;

/**
 * Image DTO which returns all image details
 */
@Data
public class BasicImageDto {

    private int id;
    private String filename;
    private String thumbnailFilename;

    public BasicImageDto(String publicPathPrefix, Image image) {
        this.id = image.getId();
        // TODO get rid of double slash in a better way
        // Path.of doesn't work on windows
        this.filename = publicPathPrefix + image.getPath().substring(1);
        System.out.println(this.filename);
        this.thumbnailFilename = publicPathPrefix + image.getThumbnailPath().substring(1);
    }

    public BasicImageDto() {

    }

}
