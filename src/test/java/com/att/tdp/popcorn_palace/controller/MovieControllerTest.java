package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.exception.ConflictException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private MovieService movieService;

        @Test
        void getAllMovies_ShouldReturnMovies() throws Exception {
                List<MovieDTO> movies = List.of(
                                new MovieDTO(1L, "Movie One", "Action", 120, 8.0, 2024),
                                new MovieDTO(2L, "Movie Two", "Comedy", 90, 7.5, 2025));
                when(movieService.getAllMovies()).thenReturn(movies);

                mockMvc.perform(get("/movies/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].title", is("Movie One")))
                                .andExpect(jsonPath("$[1].title", is("Movie Two")));
        }

        @Test
        void getMovieById_WhenExists_ShouldReturnMovie() throws Exception {
                MovieDTO movie = new MovieDTO(1L, "Movie One", "Action", 120, 8.0, 2024);
                when(movieService.getMovieById(1L)).thenReturn(movie);

                mockMvc.perform(get("/movies/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title", is("Movie One")));
        }

        @Test
        void getMovieByTitle_WhenExists_ShouldReturnMovie() throws Exception {
                MovieDTO movie = new MovieDTO(1L, "Movie One", "Action", 120, 8.0, 2024);
                when(movieService.getMovieByTitle("Movie One")).thenReturn(movie);

                mockMvc.perform(get("/movies/title/{title}", "Movie One"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.genre", is("Action")));
        }

        @Test
        void getMoviesByGenre_ShouldReturnMovies() throws Exception {
                List<MovieDTO> movies = List.of(
                                new MovieDTO(1L, "Action Movie", "Action", 120, 8.0, 2024));
                when(movieService.getMoviesByGenre("Action")).thenReturn(movies);

                mockMvc.perform(get("/movies/genre/{genre}", "Action"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].title", is("Action Movie")));
        }

        @Test
        void getMoviesByReleaseYear_ShouldReturnMovies() throws Exception {
                List<MovieDTO> movies = List.of(
                                new MovieDTO(11L, "Old Movie", "Drama", 110, 7.0, 2020));
                when(movieService.getMoviesByReleaseYear(2020)).thenReturn(movies);

                mockMvc.perform(get("/movies/year/{year}", 2020))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id", is(11)))
                                .andExpect(jsonPath("$[0].title", is("Old Movie")))
                                .andExpect(jsonPath("$[0].genre", is("Drama")))
                                .andExpect(jsonPath("$[0].duration", is(110)))
                                .andExpect(jsonPath("$[0].rating", is(7.0)))
                                .andExpect(jsonPath("$[0].releaseYear", is(2020)));
        }

        @Test
        void updateMovie_WhenTitleIsModified_ShouldReturnConflict() throws Exception {
                MovieDTO originalMovie = new MovieDTO(1L, "Original Movie", "Action", 120, 8.0, 2024);
                MovieDTO updateRequest = new MovieDTO(1L, "Updated Movie", "Action", 130, 8.5, 2024); // Trying to
                                                                                                      // change title

                when(movieService.getMovieByTitle("Original Movie")).thenReturn(originalMovie);
                when(movieService.updateMovie(eq("Original Movie"), any(MovieDTO.class)))
                                .thenThrow(new ConflictException("The title of the movie cannot be updated."));

                mockMvc.perform(post("/movies/update/{title}", "Original Movie")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isConflict()) // Expect 409 Conflict status
                                .andExpect(jsonPath("$.message", is("The title of the movie cannot be updated.")));
        }

        @Test
        void addMovie_WithValidData_ShouldReturnCreated() throws Exception {
                MovieDTO movie = new MovieDTO(null, "New Movie", "Sci-Fi", 130, 9.0, 2026);
                MovieDTO savedMovie = new MovieDTO(10L, "New Movie", "Sci-Fi", 130, 9.0, 2026);

                when(movieService.addMovie(any(MovieDTO.class))).thenReturn(savedMovie);

                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(movie)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(10)))
                                .andExpect(jsonPath("$.title", is("New Movie")));
        }

        void deleteMovie_WhenExists_ShouldReturnOk() throws Exception {
                doNothing().when(movieService).deleteMovie("Some Movie");

                mockMvc.perform(delete("/movies/{movieTitle}", "Some Movie"))
                                .andExpect(status().isOk());
        }
}