package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
@Tag(name = "Showtime", description = "Showtime management APIs")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @Autowired
    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @Operation(summary = "Get all showtimes", description = "Retrieve a list of all available showtimes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all showtimes")
    })
    @GetMapping
    public ResponseEntity<List<ShowtimeDTO>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @Operation(summary = "Get showtime by ID", description = "Retrieve a showtime by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the showtime"),
            @ApiResponse(responseCode = "404", description = "Showtime not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDTO> getShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getShowtimeById(id));
    }

    @Operation(summary = "Get showtimes by movie", description = "Retrieve all showtimes for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved showtimes"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.getShowtimesByMovie(movieId));
    }

    @Operation(summary = "Get showtimes by theater", description = "Retrieve all showtimes for a specific theater")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved showtimes"),
            @ApiResponse(responseCode = "404", description = "No showtimes found for this theater")
    })
    @GetMapping("/theater/{theater}")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByTheater(@PathVariable String theater) {
        return ResponseEntity.ok(showtimeService.getShowtimesByTheater(theater));
    }

    @Operation(summary = "Add a showtime", description = "Create a new showtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Showtime created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid showtime data"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "Overlapping showtime in theater")
    })
    @PostMapping
    public ResponseEntity<ShowtimeDTO> addShowtime(@Valid @RequestBody ShowtimeDTO showtimeDTO) {
        return new ResponseEntity<>(showtimeService.addShowtime(showtimeDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a showtime", description = "Update an existing showtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Showtime updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid showtime data"),
            @ApiResponse(responseCode = "404", description = "Showtime not found"),
            @ApiResponse(responseCode = "409", description = "Overlapping showtime in theater")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<ShowtimeDTO> updateShowtime(@PathVariable Long id,
            @Valid @RequestBody ShowtimeDTO showtimeDTO) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtimeDTO));
    }

    @Operation(summary = "Delete a showtime", description = "Delete an existing showtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Showtime deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Showtime not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }
}