package com.att.tdp.popcorn_palace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.att.tdp.popcorn_palace.model.Movie;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    Optional<Movie> findByGenre(String genre);

    boolean existsByTitle(String title);

    void deleteByTitle(String title);

}
