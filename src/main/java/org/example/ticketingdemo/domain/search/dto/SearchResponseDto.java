package org.example.ticketingdemo.domain.search.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
public class SearchResponseDto {

    List<ConcertsSearchDto> concerts;

}
