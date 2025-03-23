package com.att.tdp.popcorn_palace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.model.Showtime;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
        List<Showtime> findByMovieId(Long movieId);

        List<Showtime> findByTheater(String theater);

        @Query("SELECT s FROM Showtime s WHERE s.theater = :theater AND " +
                        "NOT (s.endTime <= :startTime OR s.startTime >= :endTime)")
        List<Showtime> findOverlappingShowtimes(
                        @Param("theater") String theater,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
}