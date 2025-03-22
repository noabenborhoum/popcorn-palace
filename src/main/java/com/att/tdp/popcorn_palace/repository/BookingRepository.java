package com.att.tdp.popcorn_palace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.att.tdp.popcorn_palace.model.Booking;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
