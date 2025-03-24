package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.service.BookingService;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private BookingService bookingService;

        @Test
        public void bookTicket_ShouldReturnBookingId() throws Exception {
                UUID userId = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");
                UUID bookingId = UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae");

                BookingDTO inputBooking = BookingDTO.builder()
                                .showtimeId(1L)
                                .seatNumber(15)
                                .userId(userId)
                                .build();

                when(bookingService.bookTicket(any(BookingDTO.class))).thenReturn(bookingId);

                mockMvc.perform(post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputBooking)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.bookingId", is(bookingId.toString())));
        }

        @Test
        public void bookTicket_WithMissingUserId_ShouldReturnBadRequest() throws Exception {
                BookingDTO inputBooking = BookingDTO.builder()
                                .showtimeId(1L)
                                .seatNumber(15)
                                .build();

                mockMvc.perform(post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputBooking)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void bookTicket_WithMissingShowtimeId_ShouldReturnBadRequest() throws Exception {
                UUID userId = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");

                BookingDTO inputBooking = BookingDTO.builder()
                                .seatNumber(15)
                                .userId(userId)
                                .build();

                mockMvc.perform(post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputBooking)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void bookTicket_WithInvalidSeatNumber_ShouldReturnBadRequest() throws Exception {
                UUID userId = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");

                BookingDTO inputBooking = BookingDTO.builder()
                                .showtimeId(1L)
                                .seatNumber(0) // Invalid seat number
                                .userId(userId)
                                .build();

                mockMvc.perform(post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputBooking)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void getBookingById_WhenExists_ShouldReturnBooking() throws Exception {
                UUID bookingId = UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae");
                UUID userId = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");
                BookingDTO expectedBooking = BookingDTO.builder()
                                .bookingId(bookingId)
                                .showtimeId(1L)
                                .seatNumber(15)
                                .userId(userId)
                                .build();

                when(bookingService.getBookingById(bookingId)).thenReturn(expectedBooking);

                mockMvc.perform(get("/bookings/{id}", bookingId))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.bookingId", is(bookingId.toString())))
                                .andExpect(jsonPath("$.showtimeId", is(1)))
                                .andExpect(jsonPath("$.seatNumber", is(15)))
                                .andExpect(jsonPath("$.userId", is(userId.toString())));
        }

        @Test
        public void getBookingById_WhenNotExists_ShouldReturnNotFound() throws Exception {
                UUID bookingId = UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae");

                when(bookingService.getBookingById(bookingId))
                                .thenThrow(new ResourceNotFoundException("Booking", "id", bookingId));

                mockMvc.perform(get("/bookings/{id}", bookingId))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void getBookingsByUser_ShouldReturnBookings() throws Exception {
                UUID userId = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");
                java.util.List<BookingDTO> expectedBookings = Arrays.asList(
                                BookingDTO.builder()
                                                .bookingId(UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae"))
                                                .showtimeId(1L)
                                                .seatNumber(15)
                                                .userId(userId)
                                                .build());

                when(bookingService.getBookingsByUser(userId)).thenReturn(expectedBookings);

                mockMvc.perform(get("/bookings/user/{id}", userId))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].bookingId",
                                                is(expectedBookings.get(0).getBookingId().toString())))
                                .andExpect(jsonPath("$[0].showtimeId", is(1)))
                                .andExpect(jsonPath("$[0].seatNumber", is(15)))
                                .andExpect(jsonPath("$[0].userId", is(userId.toString())));
        }

        @Test
        public void cancelBooking_WhenExists_ShouldReturnOk() throws Exception {
                UUID bookingId = UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae");

                doNothing().when(bookingService).cancelBooking(bookingId);

                mockMvc.perform(delete("/bookings/{id}", bookingId))
                                .andExpect(status().isOk());
        }

        @Test
        public void cancelBooking_WhenNotExists_ShouldReturnNotFound() throws Exception {
                UUID bookingId = UUID.fromString("d1a6423b-4469-4b00-8c5f-e3cfc42eacae");

                doThrow(new ResourceNotFoundException("Booking", "id", bookingId))
                                .when(bookingService).cancelBooking(bookingId);

                mockMvc.perform(delete("/bookings/{id}", bookingId))
                                .andExpect(status().isNotFound());
        }

}