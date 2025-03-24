package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.service.MovieService;
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
@RequestMapping("/movies")
@Tag(name = "Movie", description = "Movie management APIs")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Get all movies", description = "Retrieve a list of all available movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of movies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @Operation(summary = "Get movie by ID", description = "Retrieve a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the movie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(summary = "Get movie by title", description = "Retrieve a movie by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the movie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<MovieDTO> getMovieByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.getMovieByTitle(title));
    }

    @Operation(summary = "Get movies by genre", description = "Retrieve a list of movies by genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the movies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class)))
    })
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }

    @Operation(summary = "Get movies by release year", description = "Retrieve a list of movies by release year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the movies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class)))
    })
    @GetMapping("/year/{year}")
    public ResponseEntity<List<MovieDTO>> getMoviesByReleaseYear(@PathVariable Integer year) {
        return ResponseEntity.ok(movieService.getMoviesByReleaseYear(year));
    }

    @Operation(summary = "Add a movie", description = "Add a new movie to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added the movie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class)))
    })
    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDTO) {
        return new ResponseEntity<>(movieService.addMovie(movieDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update a movie", description = "Update an existing movie in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the movie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDTO.class)))
    })
    @PostMapping("/update/{title}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable String title, @Valid @RequestBody MovieDTO movieDTO) {
        movieService.updateMovie(title, movieDTO); // Add this line to actually update the movie
        return ResponseEntity
                .ok()
                .header("message", "Movie '" + title + "' was successfully updated")
                .build();
    }

    @DeleteMapping("/{title}")
    @Operation(summary = "Delete a movie by title", description = "Delete a movie from the database by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the movie")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteMovieByTitle(@PathVariable String title) {
        movieService.deleteMovie(title);
        return ResponseEntity.ok()
                .header("message", "Movie '" + title + "' was successfully deleted")
                .build();
    }
}