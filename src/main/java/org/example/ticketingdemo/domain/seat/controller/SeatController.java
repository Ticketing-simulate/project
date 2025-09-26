package org.example.ticketingdemo.domain.seat.controller;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.dto.response.ApiResponse;
import org.example.ticketingdemo.domain.seat.dto.request.SeatBuyRequest;
import org.example.ticketingdemo.domain.seat.dto.request.SeatCancelRequest;
import org.example.ticketingdemo.domain.seat.dto.response.SeatBuyResponse;
import org.example.ticketingdemo.domain.seat.dto.response.SeatCancelResponse;
import org.example.ticketingdemo.domain.seat.service.SeatInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatInternalService seatInternalService;

    // 좌석 구매
    @PostMapping("/{concertId}/buy")
    public ResponseEntity<ApiResponse<SeatBuyResponse>> buySeat(
            @RequestParam Long userId, // 수정 사항
            @PathVariable Long concertId,
            @RequestBody SeatBuyRequest seatBuyRequest
    ) {
        SeatBuyResponse response = seatInternalService.buySeat(seatBuyRequest, userId, concertId);
        return ApiResponse.ok(response);
    }

    // 좌석 취소
    @PatchMapping("/{concertId}/cancel")
    public ResponseEntity<ApiResponse<SeatCancelResponse>> cancelSeat(
            @RequestParam Long userId, // 수정 사항
            @PathVariable Long concertId,
            @RequestBody SeatCancelRequest seatCancelRequest
    ) {
        SeatCancelResponse response = seatInternalService.cancelSeat(seatCancelRequest, userId, concertId);
        return ApiResponse.ok(response);
    }

    // 사용 가능한 좌석 조회
    @GetMapping("/{concertId}/available")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableSeats(
            @PathVariable Long concertId
    ) {
        List<String> seats = seatInternalService.getAvailableSeatNumbers(concertId);
        return ApiResponse.ok(seats);
    }
}

