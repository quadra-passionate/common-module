package com.quadra.common.contract.controller;

import com.quadra.common.contract.dto.ValidationTestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ValidationTestController {
    @GetMapping("/validation1")
    public String validation1(@Valid @RequestBody List<ValidationTestDTO> request) {
        System.out.println(request.toString());
        return "success";
    }

    @GetMapping("/validation2")
    public String validation2(@Valid @ModelAttribute ValidationTestDTO request) {
        System.out.println(request.toString());
        return "success";
    }

    @GetMapping("/validation3/{id}")
    public String validation3(@Size(min = 5) String my, @Size(min = 5) @PathVariable String id, @CookieValue String test, @RequestBody List<@NotBlank String> dto) {
        return String.format("%s + %s + %s + %s\n", my, id, test, dto);
    }
}
