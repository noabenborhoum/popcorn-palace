package com.att.tdp.popcorn_palace.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.exception.ConflictException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShowtimeControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ShowtimeService showtimeService;

        @Test
        void getAllShowtimes_ShouldReturnShowtimes() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                List<ShowtimeDTO> showtimes = Arrays.asList(
                                ShowtimeDTO.builder()
                                                .id(1L)
                                                .movieId(1L)
                                                .theater("Theater A")
                                                .startTime(now)
                                                .endTime(now.plusHours(1))
                                                .price(25.0)
                                                .build());
                when(showtimeService.getAllShowtimes()).thenReturn(showtimes);

                mockMvc.perform(get("/showtimes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].theater", is("Theater A")));
        }

        @Test
        void getShowtimeById_WhenExists_ShouldReturnShowtime() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                ShowtimeDTO dto = ShowtimeDTO.builder()
                                .id(1L)
                                .movieId(1L)
                                .theater("Theater A")
                                .startTime(now)
                                .endTime(now.plusHours(1))
                                .price(30.0)
                                .build();
                when(showtimeService.getShowtimeById(1L)).thenReturn(dto);

                mockMvc.perform(get("/showtimes/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.theater", is("Theater A")));
        }

        @Test
        void getShowtimesByMovie_ShouldReturnShowtimes() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                List<ShowtimeDTO> showtimes = Arrays.asList(
                                ShowtimeDTO.builder()
                                                .id(1L)
                                                .movieId(1L)
                                                .theater("Theater B")
                                                .startTime(now)
                                                .endTime(now.plusHours(1))
                                                .price(22.0)
                                                .build());
                when(showtimeService.getShowtimesByMovie(1L)).thenReturn(showtimes);

                mockMvc.perform(get("/showtimes/movie/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].movieId", is(1)));
        }

        @Test
        void getShowtimesByTheater_ShouldReturnShowtimes() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                List<ShowtimeDTO> showtimes = Arrays.asList(
                                ShowtimeDTO.builder()
                                                .id(1L)
                                                .movieId(2L)
                                                .theater("Cinema 1")
                                                .startTime(now)
                                                .endTime(now.plusHours(1))
                                                .price(20.0)
                                                .build());
                when(showtimeService.getShowtimesByTheater("Cinema 1")).thenReturn(showtimes);

                mockMvc.perform(get("/showtimes/theater/Cinema 1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].theater", is("Cinema 1")));
        }

        @Test
        void addShowtime_WithValidData_ShouldReturnCreated() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                ShowtimeDTO input = ShowtimeDTO.builder()
                                .movieId(1L)
                                .theater("Theater X")
                                .startTime(now)
                                .endTime(now.plusHours(1))
                                .price(19.99)
                                .build();
                ShowtimeDTO saved = ShowtimeDTO.builder()
                                .id(10L)
                                .movieId(1L)
                                .theater("Theater X")
                                .startTime(now)
                                .endTime(now.plusHours(1))
                                .price(19.99)
                                .build();

                when(showtimeService.addShowtime(any())).thenReturn(saved);

                mockMvc.perform(post("/showtimes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(10)));
        }

        @Test
        void updateShowtime_WhenExists_ShouldReturnOk() throws Exception {
                LocalDateTime now = LocalDateTime.now();
                ShowtimeDTO update = ShowtimeDTO.builder()
                                .id(1L)
                                .movieId(1L)
                                .theater("Updated Theater")
                                .startTime(now)
                                .endTime(now.plusHours(1))
                                .price(27.0)
                                .build();
                when(showtimeService.updateShowtime(eq(1L), any())).thenReturn(update);

                mockMvc.perform(post("/showtimes/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(update)))
                                .andExpect(status().isOk()); // Only check for status OK
        }

        @Test
        void deleteShowtime_WhenExists_ShouldReturnOk() throws Exception {
                doNothing().when(showtimeService).deleteShowtime(1L);

                mockMvc.perform(delete("/showtimes/1"))
                                .andExpect(status().isOk());
        }
}
