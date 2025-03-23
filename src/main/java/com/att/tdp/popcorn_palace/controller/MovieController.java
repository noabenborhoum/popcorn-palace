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

    @GetMapping("/title/{title}")
    public ResponseEntity<MovieDTO> getMovieByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.getMovieByTitle(title));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<MovieDTO>> getMoviesByReleaseYear(@PathVariable Integer year) {
        return ResponseEntity.ok(movieService.getMoviesByReleaseYear(year));
    }

    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDTO) {
        return new ResponseEntity<>(movieService.addMovie(movieDTO), HttpStatus.CREATED);
    }

    @PostMapping("/update/{title}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable String title, @Valid @RequestBody MovieDTO movieDTO) {
        return ResponseEntity.ok(movieService.updateMovie(title, movieDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovieById(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @DeleteMapping("/title/{title}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovieByTitle(@PathVariable String title) {
        movieService.deleteMovie(title);
    }
}