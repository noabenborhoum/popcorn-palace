package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.exception.InvalidRequestException;
import com.att.tdp.popcorn_palace.exception.ConflictException;

@SpringBootTest
class BookingServiceTest {

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingService bookingService;

    @Test
    void bookTicket_WithValidData_ShouldSucceed() {
        UUID userId = UUID.randomUUID();
        BookingDTO dto = new BookingDTO(null, 1L, 15, userId);
        Showtime showtime = Showtime.builder().id(1L).startTime(LocalDateTime.now().plusHours(1)).build();

        // Mocking showtime existence
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        // Mocking the absence of a booking on this showtime and seat
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 15)).thenReturn(false);

        // Mocking the saving of a booking and returning a valid saved booking
        Booking savedBooking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .showtime(showtime)
                .seatNumber(15)
                .userId(userId)
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Now, calling the method under test
        UUID result = bookingService.bookTicket(dto);

        // Assertion to verify booking ID is returned
        assertNotNull(result, "Booking ID should not be null");
        assertEquals(savedBooking.getBookingId(), result, "The booking ID should match the saved booking");
    }

    // Test to handle booking when the showtime does not exist
    @Test
    void bookTicket_WithNonExistentShowtime_ShouldThrowException() {
        BookingDTO dto = new BookingDTO(null, 1L, 15, UUID.randomUUID());
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookTicket(dto),
                "Booking should throw ResourceNotFoundException if the showtime does not exist");
    }

    // Test to handle booking when the showtime has already started
    @Test
    void bookTicket_WithStartedShowtime_ShouldThrowException() {
        Showtime showtime = Showtime.builder().id(1L).startTime(LocalDateTime.now().minusHours(1)).build();
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        BookingDTO dto = new BookingDTO(null, 1L, 10, UUID.randomUUID());
        assertThrows(InvalidRequestException.class, () -> bookingService.bookTicket(dto),
                "Booking should throw InvalidRequestException if the showtime has already started");
    }

    // Test to handle booking when a seat is already booked
    @Test
    void bookTicket_WithBookedSeat_ShouldThrowException() {
        Showtime showtime = Showtime.builder().id(1L).startTime(LocalDateTime.now().plusHours(1)).build();
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 10)).thenReturn(true);
        BookingDTO dto = new BookingDTO(null, 1L, 10, UUID.randomUUID());
        assertThrows(ConflictException.class, () -> bookingService.bookTicket(dto),
                "Booking should throw ConflictException if the seat is already booked");
    }

    // Test to cancel booking when the booking exists
    @Test
    void cancelBooking_WhenExists_ShouldSucceed() {
        UUID id = UUID.randomUUID();
        Showtime showtime = Showtime.builder().id(1L).startTime(LocalDateTime.now().plusHours(1)).build();
        Booking booking = Booking.builder().bookingId(id).showtime(showtime).build();
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        assertDoesNotThrow(() -> bookingService.cancelBooking(id),
                "Canceling an existing booking should not throw any exception");
    }

    // Test to cancel booking when the booking does not exist
    @Test
    void cancelBooking_WhenNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(id),
                "Canceling a non-existing booking should throw ResourceNotFoundException");
    }

    // Test to get booking by ID when it exists
    @Test
    void getBookingById_WhenExists_ShouldReturnBooking() {
        UUID id = UUID.randomUUID();
        Booking booking = Booking.builder().bookingId(id).showtime(new Showtime()).seatNumber(10)
                .userId(UUID.randomUUID())
                .build();
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        BookingDTO dto = bookingService.getBookingById(id);
        assertEquals(10, dto.getSeatNumber(), "The seat number of the booking should be 10");
    }

    // Test to get bookings by user
    @Test
    void getBookingsByUser_ShouldReturnBookings() {
        UUID userId = UUID.randomUUID();
        List<Booking> bookings = List.of(
                Booking.builder().bookingId(UUID.randomUUID()).userId(userId).seatNumber(12).showtime(new Showtime())
                        .build());
        when(bookingRepository.findByUserId(userId)).thenReturn(bookings);
        List<BookingDTO> result = bookingService.getBookingsByUser(userId);
        assertEquals(1, result.size(), "There should be 1 booking for the user");
    }
}
