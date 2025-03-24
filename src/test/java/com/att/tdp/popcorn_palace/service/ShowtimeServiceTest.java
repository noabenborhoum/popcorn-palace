package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.exception.ConflictException;
import com.att.tdp.popcorn_palace.exception.InvalidRequestException;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShowtimeServiceTest {

        @MockBean
        private ShowtimeRepository showtimeRepository;

        @MockBean
        private MovieRepository movieRepository;

        @Autowired
        private ShowtimeService showtimeService;

        @Test
        void addShowtime_WithValidData_ShouldSucceed() {
                // Arrange
                ShowtimeDTO dto = new ShowtimeDTO(null, 1L, "Theater", LocalDateTime.now().plusHours(1),
                                LocalDateTime.now().plusHours(2), 10.0);
                Movie movie = Movie.builder().id(1L).build();

                // Mock Movie Repository to return a valid Movie object
                when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

                // Mock Showtime Repository to return a Showtime with a valid Movie object
                Showtime showtime = Showtime.builder()
                                .id(5L)
                                .movie(movie) // Ensure the Movie object is set
                                .build();

                when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());
                when(showtimeRepository.save(any())).thenReturn(showtime);

                // Act
                ShowtimeDTO result = showtimeService.addShowtime(dto);

                // Assert
                assertEquals(5L, result.getId(), "The Showtime ID should be 5 after saving");
                assertEquals(1L, result.getMovieId(), "The movie ID should be 1");
        }

        @Test
        void addShowtime_WithNonExistentMovie_ShouldThrowException() {
                // Arrange
                ShowtimeDTO dto = new ShowtimeDTO(null, 1L, "Theater", LocalDateTime.now().plusHours(1),
                                LocalDateTime.now().plusHours(2), 10.0);
                when(movieRepository.findById(1L)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(ResourceNotFoundException.class, () -> showtimeService.addShowtime(dto),
                                "Adding a showtime with a non-existent movie should throw ResourceNotFoundException");
        }

        @Test
        void addShowtime_WithInvalidTimeRange_ShouldThrowException() {
                // Arrange
                ShowtimeDTO dto = new ShowtimeDTO(null, 1L, "Theater", LocalDateTime.now().plusHours(2),
                                LocalDateTime.now().plusHours(1), 10.0);
                when(movieRepository.findById(1L)).thenReturn(Optional.of(new Movie()));

                // Act & Assert
                assertThrows(InvalidRequestException.class, () -> showtimeService.addShowtime(dto),
                                "Adding a showtime with an invalid time range should throw InvalidRequestException");
        }

        @Test
        void addShowtime_WithPastStartTime_ShouldThrowException() {
                // Arrange
                ShowtimeDTO dto = new ShowtimeDTO(null, 1L, "Theater", LocalDateTime.now().minusHours(2),
                                LocalDateTime.now().plusHours(1), 10.0);
                when(movieRepository.findById(1L)).thenReturn(Optional.of(new Movie()));

                // Act & Assert
                assertThrows(InvalidRequestException.class, () -> showtimeService.addShowtime(dto),
                                "Adding a showtime with a start time in the past should throw InvalidRequestException");
        }

        @Test
        void addShowtime_WithOverlap_ShouldThrowException() {
                // Arrange
                ShowtimeDTO dto = new ShowtimeDTO(null, 1L, "Theater", LocalDateTime.now().plusMinutes(5),
                                LocalDateTime.now().plusHours(1), 10.0);
                when(movieRepository.findById(1L)).thenReturn(Optional.of(new Movie()));
                when(showtimeRepository.findOverlappingShowtimes(any(), any(), any()))
                                .thenReturn(List.of(new Showtime()));

                // Act & Assert
                assertThrows(ConflictException.class, () -> showtimeService.addShowtime(dto),
                                "Adding a showtime that overlaps with an existing showtime should throw ConflictException");
        }

        @Test
        void getShowtimeById_WhenExists_ShouldReturnShowtime() {
                // Arrange
                Showtime showtime = Showtime.builder().id(1L).movie(Movie.builder().id(1L).build()).build();
                when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

                // Act
                ShowtimeDTO result = showtimeService.getShowtimeById(1L);

                // Assert
                assertEquals(1L, result.getId(), "The Showtime ID should be 1");
        }

        @Test
        void getShowtimesByMovie_ShouldReturnShowtimes() {
                // Arrange
                Movie movie = Movie.builder().id(1L).build(); // Create a valid Movie object
                Showtime showtime = Showtime.builder()
                                .id(1L)
                                .movie(movie) // Associate the Movie object with Showtime
                                .build();

                // Mock repository methods to return a valid Showtime
                when(movieRepository.existsById(1L)).thenReturn(true);
                when(showtimeRepository.findByMovieId(1L)).thenReturn(List.of(showtime)); // Return a Showtime with
                                                                                          // Movie

                // Act
                List<ShowtimeDTO> result = showtimeService.getShowtimesByMovie(1L);

                // Assert
                assertFalse(result.isEmpty(), "The list of showtimes should not be empty");
                assertNotNull(result.get(0).getMovieId(), "The movie ID should not be null");
                assertEquals(1L, result.get(0).getMovieId(), "The movie ID should be 1");
        }

        @Test
        void deleteShowtime_WhenExists_ShouldSucceed() {
                // Arrange
                when(showtimeRepository.existsById(1L)).thenReturn(true);

                // Act & Assert
                assertDoesNotThrow(() -> showtimeService.deleteShowtime(1L),
                                "Deleting an existing showtime should not throw any exception");
        }
}
