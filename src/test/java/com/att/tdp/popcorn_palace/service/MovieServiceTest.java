package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.exception.ConflictException;

@SpringBootTest
class MovieServiceTest {

    @MockBean
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    // Test to verify that all movies are returned
    @Test
    void getAllMovies_ShouldReturnMovies() {
        List<Movie> movies = List.of(Movie.builder().id(1L).title("Test Movie").build());
        when(movieRepository.findAll()).thenReturn(movies);
        List<MovieDTO> result = movieService.getAllMovies();
        assertEquals(1, result.size(), "The number of movies should be 1");
    }

    // Test to verify movie by ID when it exists
    @Test
    void getMovieById_WhenExists_ShouldReturnMovie() {
        Movie movie = Movie.builder().id(1L).title("Movie 1").build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        MovieDTO dto = movieService.getMovieById(1L);
        assertEquals("Movie 1", dto.getTitle(), "The movie title should match");
    }

    // Test to verify movie by title when it exists
    @Test
    void getMovieByTitle_WhenExists_ShouldReturnMovie() {
        Movie movie = Movie.builder().id(1L).title("Comedy").build();
        when(movieRepository.findByTitle("Comedy")).thenReturn(Optional.of(movie));
        MovieDTO dto = movieService.getMovieByTitle("Comedy");
        assertEquals("Comedy", dto.getTitle(), "The movie title should match");
    }

    // Test to verify movies by genre
    @Test
    void getMoviesByGenre_ShouldReturnMovies() {
        List<Movie> movies = List.of(Movie.builder().id(2L).genre("Action").build());
        when(movieRepository.findByGenre("Action")).thenReturn(movies);
        List<MovieDTO> result = movieService.getMoviesByGenre("Action");
        assertEquals(1, result.size(), "There should be 1 movie in the Action genre");
    }

    // Test to verify movies by release year
    @Test
    void getMoviesByReleaseYear_ShouldReturnMovies() {
        List<Movie> movies = List.of(Movie.builder().id(3L).releaseYear(2022).build());
        when(movieRepository.findByReleaseYear(2022)).thenReturn(movies);
        List<MovieDTO> result = movieService.getMoviesByReleaseYear(2022);
        assertEquals(1, result.size(), "There should be 1 movie released in 2022");
    }

    // Test to add a movie with valid data
    @Test
    void addMovie_WithValidData_ShouldSucceed() {
        MovieDTO dto = new MovieDTO(null, "New", "Action", 120, 7.5, 2025);
        when(movieRepository.existsByTitle("New")).thenReturn(false);
        when(movieRepository.save(any())).thenReturn(Movie.builder().id(10L).title("New").build());
        MovieDTO result = movieService.addMovie(dto);
        assertEquals("New", result.getTitle(), "The movie title should be 'New'");
    }

    // Test to add a movie with a duplicate title
    @Test
    void addMovie_WithDuplicateTitle_ShouldThrowException() {
        MovieDTO dto = new MovieDTO(null, "Dup", "Drama", 100, 6.0, 2023);
        when(movieRepository.existsByTitle("Dup")).thenReturn(true);
        assertThrows(ConflictException.class, () -> movieService.addMovie(dto),
                "Adding a movie with a duplicate title should throw a ConflictException");
    }

    // Test to update a movie when it exists
    @Test
    void updateMovie_WhenExists_ShouldSucceed() {
        Movie existing = Movie.builder().id(1L).title("Old").build();
        MovieDTO dto = new MovieDTO(null, "Old", "Drama", 100, 7.0, 2024);
        when(movieRepository.findByTitle("Old")).thenReturn(Optional.of(existing));
        when(movieRepository.save(any())).thenReturn(existing);
        MovieDTO result = movieService.updateMovie("Old", dto);
        assertEquals("Old", result.getTitle(), "The movie title should be 'Old' after updating");
    }

    // Test to delete a movie when it exists
    @Test
    void deleteMovie_WhenExists_ShouldSucceed() {
        when(movieRepository.existsByTitle("ToDelete")).thenReturn(true);
        assertDoesNotThrow(() -> movieService.deleteMovie("ToDelete"),
                "Deleting a movie that exists should not throw any exception");
    }
}
