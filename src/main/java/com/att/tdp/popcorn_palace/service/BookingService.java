package com.att.tdp.popcorn_palace.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional
    public UUID bookTicket(BookingDTO bookingDTO) {
        // Validate that the showtime exists
        Showtime showtime = showtimeRepository.findById(bookingDTO.getShowtimeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Showtime with ID " + bookingDTO.getShowtimeId() + " not found"));

        // Additional validation using the showtime object
        LocalDateTime now = LocalDateTime.now();
        if (showtime.getStartTime().isBefore(now)) {
            throw new IllegalStateException("Cannot book tickets for a showtime that has already started");
        }

        // Check if the seat is already booked
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(
                bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber())) {
            throw new EntityExistsException("Seat " + bookingDTO.getSeatNumber() +
                    " is already booked for showtime " + bookingDTO.getShowtimeId());
        }

        // Create booking entity
        Booking booking = Booking.builder()
                .showtimeId(bookingDTO.getShowtimeId())
                .seatNumber(bookingDTO.getSeatNumber())
                .userId(bookingDTO.getUserId())
                .build();

        try {
            // Attempt to save the booking
            Booking savedBooking = bookingRepository.save(booking);
            return savedBooking.getBookingId();
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("Could not create booking: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + bookingId + " not found"));

        // Check if showtime has already started
        Showtime showtime = booking.getShowtime();
        if (showtime == null) {
            throw new EntityNotFoundException("Showtime for booking " + bookingId + " not found");
        }

        LocalDateTime now = LocalDateTime.now();
        if (showtime.getStartTime().isBefore(now)) {
            throw new IllegalStateException("Cannot cancel tickets for a showtime that has already started");
        }

        bookingRepository.deleteById(bookingId);
    }

    public BookingDTO getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + bookingId + " not found"));

        return convertToDTO(booking);
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByUser(UUID userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .bookingId(booking.getBookingId())
                .showtimeId(booking.getShowtime().getId()) // Direct access to showtime's ID
                .seatNumber(booking.getSeatNumber())
                .userId(booking.getUserId())
                .build();
    }
}