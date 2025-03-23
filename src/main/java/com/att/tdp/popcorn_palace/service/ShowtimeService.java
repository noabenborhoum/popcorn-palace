package com.att.tdp.popcorn_palace.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    public List<ShowtimeDTO> getAllShowtimes() {
        return showtimeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ShowtimeDTO getShowtimeById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Showtime with id " + showtimeId + " not found"));
    }

    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        // Validate movie exists
        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Movie with ID " + movieId + " not found");
        }

        return showtimeRepository.findByMovieId(movieId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByTheater(String theater) {
        List<Showtime> showtimes = showtimeRepository.findByTheater(theater);

        if (showtimes.isEmpty()) {
            throw new EntityNotFoundException("No showtimes found for theater '" + theater + "'");
        }

        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeDTO addShowtime(ShowtimeDTO showtimeDTO) {
        // Validate that the movie exists
        Movie movie = movieRepository.findById(showtimeDTO.getMovieId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Movie with ID " + showtimeDTO.getMovieId() + " not found"));

        if (showtimeDTO.getStartTime().isAfter(showtimeDTO.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        // Validate showtime is in the future
        LocalDateTime now = LocalDateTime.now();
        if (showtimeDTO.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("Showtime must be scheduled for future dates");
        }

        // Check for overlapping showtimes in the same theater
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtimeDTO.getTheater(),
                showtimeDTO.getStartTime(),
                showtimeDTO.getEndTime());

        if (!overlappingShowtimes.isEmpty()) {
            throw new IllegalStateException(
                    "There is already a showtime scheduled in this theater at the selected time");
        }

        // Create and save the new showtime
        Showtime showtime = convertToEntity(showtimeDTO);
        showtime.setMovie(movie);
        Showtime savedShowtime = showtimeRepository.save(showtime);

        return convertToDTO(savedShowtime);
    }

    @Transactional
    public ShowtimeDTO updateShowtime(Long showtimeId, ShowtimeDTO showtimeDTO) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new EntityNotFoundException("Showtime with id " + showtimeId + " not found"));

        if (showtimeDTO.getStartTime().isAfter(showtimeDTO.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        // Validate showtime is in the future
        LocalDateTime now = LocalDateTime.now();
        if (showtimeDTO.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("Showtime must be scheduled for future dates");
        }
        showtime.setMovie(movieRepository.findById(showtimeDTO.getMovieId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Movie with ID " + showtimeDTO.getMovieId() + " not found")));
        // Check for overlapping showtimes in the same theater (excluding this showtime)
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(),
                showtimeDTO.getStartTime(),
                showtimeDTO.getEndTime());

        if (!overlappingShowtimes.isEmpty()) {
            throw new IllegalStateException(
                    "There is already a showtime scheduled in this theater at the selected time");
        }
        showtime.setTheater(showtimeDTO.getTheater());
        showtime.setStartTime(showtimeDTO.getStartTime());
        showtime.setEndTime(showtimeDTO.getEndTime());
        showtime.setPrice(showtimeDTO.getPrice());

        Showtime updatedShowtime = showtimeRepository.save(showtime);
        return convertToDTO(updatedShowtime);
    }

    public void deleteShowtime(Long showtimeId) {
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new EntityNotFoundException("Showtime with ID " + showtimeId + " not found");
        }

        showtimeRepository.deleteById(showtimeId);
    }

    private ShowtimeDTO convertToDTO(Showtime showtime) {
        return ShowtimeDTO.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .theater(showtime.getTheater())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .price(showtime.getPrice())
                .build();
    }

    private Showtime convertToEntity(ShowtimeDTO showtimeDTO) {
        return Showtime.builder()
                .id(showtimeDTO.getId())
                .movie(movieRepository.findById(showtimeDTO.getMovieId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Movie with ID " + showtimeDTO.getMovieId() + " not found")))
                .theater(showtimeDTO.getTheater())
                .startTime(showtimeDTO.getStartTime())
                .endTime(showtimeDTO.getEndTime())
                .price(showtimeDTO.getPrice())
                .build();
    }
}