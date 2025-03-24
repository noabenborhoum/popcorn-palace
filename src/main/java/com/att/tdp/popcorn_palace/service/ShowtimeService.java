package com.att.tdp.popcorn_palace.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.exception.ConflictException;
import com.att.tdp.popcorn_palace.exception.InvalidRequestException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));
    }

    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        // Validate movie exists
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie", "id", movieId);
        }

        return showtimeRepository.findByMovieId(movieId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByTheater(String theater) {
        List<Showtime> showtimes = showtimeRepository.findByTheater(theater);

        if (showtimes.isEmpty()) {
            throw new ResourceNotFoundException("Showtime", "theater", theater);
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
                        () -> new ResourceNotFoundException("Movie", "id", showtimeDTO.getMovieId()));

        if (showtimeDTO.getStartTime().isAfter(showtimeDTO.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }

        // Validate showtime is in the future
        LocalDateTime now = LocalDateTime.now();
        if (showtimeDTO.getStartTime().isBefore(now)) {
            throw new InvalidRequestException("Showtime must be scheduled for future dates");
        }

        // Check for overlapping showtimes in the same theater
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtimeDTO.getTheater(),
                showtimeDTO.getStartTime(),
                showtimeDTO.getEndTime());

        if (!overlappingShowtimes.isEmpty()) {
            throw new ConflictException("There is already a showtime scheduled in this theater at the selected time");
        }

        // Create and save the new showtime
        Showtime showtime = convertToEntity(showtimeDTO, movie);
        Showtime savedShowtime = showtimeRepository.save(showtime);

        return convertToDTO(savedShowtime);
    }

    @Transactional
    public ShowtimeDTO updateShowtime(Long showtimeId, ShowtimeDTO showtimeDTO) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));
        if (showtimeDTO.getStartTime().isAfter(showtimeDTO.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        // Validate showtime is in the future
        LocalDateTime now = LocalDateTime.now();
        if (showtimeDTO.getStartTime().isBefore(now)) {
            throw new InvalidRequestException("Showtime must be scheduled for future dates");
        }
        Movie movie = movieRepository.findById(showtimeDTO.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", showtimeDTO.getMovieId()));
        showtime.setMovie(movie);

        // Only check for overlapping showtimes if we're changing the time or theater
        if (!showtime.getTheater().equals(showtimeDTO.getTheater()) ||
                !showtime.getStartTime().equals(showtimeDTO.getStartTime()) ||
                !showtime.getEndTime().equals(showtimeDTO.getEndTime())) {

            List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                    showtimeDTO.getTheater(),
                    showtimeDTO.getStartTime(),
                    showtimeDTO.getEndTime());

            // Filter out the current showtime from the overlapping list
            overlappingShowtimes = overlappingShowtimes.stream()
                    .filter(s -> !s.getId().equals(showtimeId))
                    .collect(Collectors.toList());

            if (!overlappingShowtimes.isEmpty()) {
                throw new ConflictException(
                        "There is already a showtime scheduled in this theater at the selected time");
            }
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
            throw new ResourceNotFoundException("Showtime", "id", showtimeId);
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

    private Showtime convertToEntity(ShowtimeDTO showtimeDTO, Movie movie) {
        return Showtime.builder()
                .id(showtimeDTO.getId())
                .movie(movie)
                .theater(showtimeDTO.getTheater())
                .startTime(showtimeDTO.getStartTime())
                .endTime(showtimeDTO.getEndTime())
                .price(showtimeDTO.getPrice())
                .build();
    }
}
