package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Movie title is required and cannot be empty")
    @Column(nullable = false, unique = true)
    private String title;

    @NotBlank(message = "Movie genre is required and cannot be empty")
    @Column(nullable = false)
    private String genre;

    @NotNull(message = "Movie duration is required and cannot be empty")
    @Min(value = 1, message = "Movie duration must be at least 1 minute")
    @Column(nullable = false)
    private Integer duration;

    @NotNull(message = "Movie rating is required and cannot be empty")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "10.0", message = "Rating must be no more than 10")
    @Column(nullable = false)
    private Double rating;

    @NotNull(message = "Movie release year is required and cannot be empty")
    @Min(value = 1888, message = "Release year must be at least 1888") // the first movie was made in 1888
    @Max(value = 2100, message = "Release year must not exceed 2100")
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;
}