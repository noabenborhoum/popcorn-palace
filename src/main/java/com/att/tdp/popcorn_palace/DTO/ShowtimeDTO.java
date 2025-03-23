package com.att.tdp.popcorn_palace.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ShowtimeDTO {
    private Long id;

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotBlank(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Price is required")
    @Min(value = (long) 0.0, message = "Price must be at least 0")
    private Double price;
}
