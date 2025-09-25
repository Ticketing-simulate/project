package org.example.ticketingdemo.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.service.PopularService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PopularContoller {

    private final PopularService popularService;

    @PostMapping
    public ResponseEntity<Popular> savepopular(
            @RequestBody Popular popular
    ) {
        return ResponseEntity.ok(popularService.savaPopular(popular));
    }



}
