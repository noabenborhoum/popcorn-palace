package com.att.tdp.popcorn_palace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.model.Movie;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    Optional<Movie> findByGenre(String genre);

    Optional<Movie> findByReleaseYear(Integer releaseYear);
    
    boolean existsByTitle(String title);

    void deleteByTitle(String title);

}
