package org.example.ticketingdemo.domain.seat.supabase;

import org.example.ticketingdemo.domain.payment.temporary.Ticket;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


import java.util.List;
import java.util.Optional;

@Service
public class SupabaseSeatService implements SeatExternalService {

    private final RestClient restClient;
    private final String supabaseTicketTable;

    public SupabaseSeatService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.anon-key}") String supabaseAnonKey,
            @Value("${supabase.ticket-table}") String supabaseTicketTable) {

        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/rest/v1/") // Supabase REST API 기본 경로
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Authorization", "Bearer " + supabaseAnonKey)
                .build();
        this.supabaseTicketTable = supabaseTicketTable;
    }

    @Override
    public Optional<Seat> findTicketById(Long id) {
        // Supabase PostgREST API 쿼리 문자열 구성
        // 예: /rest/v1/tickets?id=eq.1&select=id,concertName,price
        String uri = String.format("%s?id=eq.%d&select=id,concertName,price",
                supabaseTicketTable, id);

        try {
            // RestClient를 사용하여 동기적으로 요청을 수행
            List<Seat> tickets = restClient.get()
                    .uri(uri)
                    .retrieve()
                    // 응답을 List<Ticket>으로 받음. List로 받는 이유는 Supabase가 JSON 배열로 응답하기 때문
                    .body(new ParameterizedTypeReference<List<Seat>>() {});

            // 첫 번째 요소만 반환하거나, 리스트가 비어있으면 Optional.empty() 반환
            if (tickets != null && !tickets.isEmpty()) {
                return Optional.of(tickets.get(0));
            }

        } catch (RestClientException e) {
            // HTTP 4xx, 5xx 오류 및 연결 오류 처리
            System.err.println("Supabase RestClient 오류: " + e.getMessage());
            // 오류 발생 시 티켓을 찾지 못한 것으로 처리
        }

        return Optional.empty();
    }
}