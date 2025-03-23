package com.att.tdp.popcorn_palace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.tdp.popcorn_palace.model.Booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);

    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);

    List<Booking> findByShowtimeId(Long showtimeId);

    List<Booking> findByUserId(UUID userId);

}
