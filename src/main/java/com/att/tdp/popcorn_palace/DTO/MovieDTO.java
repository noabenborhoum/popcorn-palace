package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MovieDTO {
    private Long id;

    @NotBlank(message = "Title is required and cannot be empty")
    private String title;

    @NotBlank(message = "Genre is required and cannot be empty")
    private String genre;

    @NotNull(message = "Duration is required and cannot be empty")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotNull(message = "Rating is required and cannot be empty")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "10.0", message = "Rating must be no more than 10")
    private Double rating;

    @NotNull(message = "Release year is required and cannot be empty")
    @Min(value = 1888, message = "Release year must be at least 1888") // the first movie was made in 1888
    @Max(value = 2100, message = "Release year must not exceed 2100")
    private Integer releaseYear;
}
