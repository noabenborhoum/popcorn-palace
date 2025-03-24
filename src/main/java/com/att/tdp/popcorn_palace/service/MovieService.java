package com.att.tdp.popcorn_palace.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.exception.ConflictException;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(Long id) {
        return convertToDTO(movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found")));
    }

    public MovieDTO getMovieByTitle(String title) {
        return convertToDTO(
                movieRepository.findByTitle(title)
                        .orElseThrow(() -> new ResourceNotFoundException("Movie", "title", title)));
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("Movie", "genre", genre);
        }
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByReleaseYear(Integer year) {
        List<Movie> movies = movieRepository.findByReleaseYear(year);

        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("Movie", "release year", year);
        }

        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO addMovie(MovieDTO movieDTO) {
        if (movieRepository.existsByTitle(movieDTO.getTitle())) {
            throw new ConflictException("Movie with title " + movieDTO.getTitle() + " already exists");
        }

        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    @Transactional
    public MovieDTO updateMovie(String movieTitle, MovieDTO movieDTO) {
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "title", movieTitle));

        // Check if new title already exists and is not the same movie
        if (!movieTitle.equals(movieDTO.getTitle()) && movieRepository.existsByTitle(movieDTO.getTitle())) {
            throw new ConflictException("Movie with title " + movieDTO.getTitle() + " already exists");
        }

        // Update the existing movie with new values
        movie.setTitle(movieDTO.getTitle());
        movie.setGenre(movieDTO.getGenre());
        movie.setDuration(movieDTO.getDuration());
        movie.setRating(movieDTO.getRating());
        movie.setReleaseYear(movieDTO.getReleaseYear());

        Movie updatedMovie = movieRepository.save(movie);
        return convertToDTO(updatedMovie);
    }

    @Transactional
    public void deleteMovie(String movieTitle) {
        if (!movieRepository.existsByTitle(movieTitle)) {
            throw new ResourceNotFoundException("Movie", "title", movieTitle);
        }

        movieRepository.deleteByTitle(movieTitle);
    }

    // convert movie entity to movie dto
    private MovieDTO convertToDTO(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .releaseYear(movie.getReleaseYear())
                .build();
    }

    // convert movie dto to movie entity
    private Movie convertToEntity(MovieDTO movieDTO) {
        return Movie.builder()
                .title(movieDTO.getTitle())
                .genre(movieDTO.getGenre())
                .duration(movieDTO.getDuration())
                .rating(movieDTO.getRating())
                .releaseYear(movieDTO.getReleaseYear())
                .build();
    }
}
