package org.example.ticketingdemo.domain.seat.controller;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.dto.response.ApiResponse;
import org.example.ticketingdemo.domain.seat.dto.request.SeatBuyRequest;
import org.example.ticketingdemo.domain.seat.dto.response.SeatBuyResponse;
import org.example.ticketingdemo.domain.seat.service.SeatInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatInternalService seatInternalService;

    @PostMapping("/buy/{concertId}")
    public ResponseEntity<ApiResponse<SeatBuyResponse>> buySeat(
            @PathVariable Long concertId,
            @RequestParam Long userId, // 수정 사항
            @RequestBody SeatBuyRequest seatBuyRequest
    ) {
        SeatBuyResponse response = seatInternalService.buySeat(seatBuyRequest, userId, concertId);
        return ApiResponse.ok(response);
    }


    @PostMapping("/cancel/{seatId}")
    public ResponseEntity<ApiResponse<SeatBuyResponse>> cancelSeat(
            @PathVariable Long seatId,
            @RequestParam Long userId // 수정 사항
    ) {
        SeatBuyResponse response = seatInternalService.cancelSeat(seatId, userId);
        return ApiResponse.ok(response);
    }
}
