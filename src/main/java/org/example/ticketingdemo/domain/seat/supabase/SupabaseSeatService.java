package org.example.ticketingdemo.domain.seat.supabase;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SupabaseSeatService {

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String supabaseAnonKey;
    private final String supabaseSeatTable;

    public SupabaseSeatService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.anon-key}") String supabaseAnonKey,
            @Value("${supabase.seat-table}") String supabaseSeatTable
    ) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseAnonKey = supabaseAnonKey;
        this.supabaseSeatTable = supabaseSeatTable;

        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/rest/v1/" + supabaseSeatTable)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Authorization", "Bearer " + supabaseAnonKey)
                .build();
    }

    /**
     * 좌석 저장 (Supabase INSERT)
     */
    public Seat saveSeat(Seat seat) {
        return restClient.post()
                .body(seat)
                .retrieve()
                .body(Seat.class);
    }

    /**
     * 좌석 단건 조회
     */
    public Seat getSeatById(Long seatId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("id", "eq." + seatId) // Supabase에서 {컬럼명}=eq.{값}
                        .build())
                .retrieve()
                .body(Seat.class);
    }

    /**
     * 좌석 삭제
     */
    public void deleteSeat(Long seatId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("id", "eq." + seatId)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }
}