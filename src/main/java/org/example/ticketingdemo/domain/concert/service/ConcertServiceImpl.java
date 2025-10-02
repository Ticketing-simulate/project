package org.example.ticketingdemo.domain.concert.service;

import jakarta.transaction.Transactional;
import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.enums.Category;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.service.SeatExternalService;
import org.springframework.stereotype.Service;
import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ConcertServiceImpl implements ConcertService {

    // 의존성 주입
    private final ConcertRepository concertRepository;
    private final SeatExternalService seatExternalService;

    // 의존성 주입을 위한 생성자
    public ConcertServiceImpl(ConcertRepository concertRepository, SeatExternalService seatExternalService) {
        this.concertRepository = concertRepository;
        this.seatExternalService = seatExternalService;
    }

    // 콘서트 생성 하는 로직
    @Override
    @Transactional
    public ConcertDTO createConcert(ConcertDTO concertDTO) {
        // 1. String -> Enum 변환
        Category category = parseCategory(concertDTO.getCategory());

        Concert concert = Concert.builder()
                .title(concertDTO.getTitle())
                .category(category)
                .description(concertDTO.getDescription())
                .price(concertDTO.getPrice())
                .seat(concertDTO.getSeatCount())
                .ticket(concertDTO.getTicketCount())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        // 2. 콘서트 저장
        Concert savedConcert = concertRepository.save(concert);

        // 3. 좌석 생성 및 저장
        IntStream.rangeClosed(1, savedConcert.getSeat())
                // 1부터 콘서트 좌석 수까지의 연속된 정수 시퀀스
                // 예: 좌석 수가 5면 1,2,3,4,5
                .mapToObj(i -> Seat.create(
                        // mapToObj: 이 숫자의 흐름을 받아서 각 숫자를 새로운 '객체'로 변환해주는 역할의 스트림 메서드
                        // i -> Seat.create(...)는 람다식: 각각의 숫자 i를 받아서 Seat.create() 메서드를 호출하라'는 뜻
                        savedConcert,
                        // 이 좌석이 속한 콘서트 엔티티 지정 (DB에 저장된 콘서트 엔티티를 가리키는 변수)
                        SeatStatus.AVAILABLE,// 좌석 상태를 '사용 가능'으로 설정
                        "S-" + i //  "S-1", "S-2"와 같은 고유한 좌석 이름을 붙임
                ))
                .forEach(seatExternalService::createSeat);
        // forEach는 변환된 객체들의 흐름을 받아서 각각의 객체에 대해 어떤 행동을 실행
        //seatExternalService::createSeat는 메서드 레퍼런스.
        // seatExternalService의 createSeat 메서드를 호출하겠다는 의미.

        //결과적으로, mapToObj를 통해 만들어진 모든 좌석(Seat) 객체가 seatExternalService로 전달되어 하나하나씩 저장됨.

        // 저장된 엔티티를 DTO로 변환하여 반환
        return toDTO(savedConcert);
    }

    // ID로 콘서트 조회
    @Override
    public ConcertDTO getConcertById(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다." + id));
        return toDTO(concert);
    }

    // 모든 콘서트 목록 조회
    @Override
    public List<ConcertDTO> getAllConcerts() {
        return concertRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 콘서트 정보 수정
    @Override
    @Transactional
    public ConcertDTO updateConcert(Long id, ConcertDTO concertDTO) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다."+ id));

        // DTO의 정보로 엔티티의 필드를 업데이트
        concert.setTitle(concertDTO.getTitle());
        concert.setCategory(parseCategory(concertDTO.getCategory()));
        concert.setDescription(concertDTO.getDescription());
        concert.setPrice(concertDTO.getPrice());
        concert.setModifiedAt(LocalDateTime.now());

        // 수정된 엔티티를 저장하고 DTO로 변환하여 반환
        return toDTO(concertRepository.save(concert));
    }

    // 콘서트 삭제
    @Override
    @Transactional
    public void deleteConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다." + id));

        // 콘서트 삭제 cascade 옵션 덕분에 좌석 삭제 로직을 명시적으로 호출할 필요 없음
        // 즉, 콘서트 삭제 시 연결된 좌석도 함께 삭제됨
        concertRepository.delete(concert);
    }


    // ---------------- Helper Methods ----------------

    // Entity -> DTO 변환
    private ConcertDTO toDTO(Concert entity) {
        ConcertDTO dto = new ConcertDTO();
        dto.setConcertId(entity.getConcertId());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory().name());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setSeatCount(entity.getSeat());
        dto.setTicketCount(entity.getTicket());
        return dto;
    }

    // String -> Category enum 변환
    private Category parseCategory(String categoryStr) {
        try {
            return Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("알 수 없는 카테고리입니다: " + categoryStr);
        }
    }

    // 콘서트 조회 헬퍼
    private Concert findConcertById(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다. id=" + id));
    }
}
