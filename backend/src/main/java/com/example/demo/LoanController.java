package com.example.demo;

import com.example.demo.dto.LoanResponse;
import org.springframework.web.bind.annotation.*;

@RestController

@CrossOrigin(origins = "http://localhost:5173") // Let Vue access

@RequestMapping("/api/loan")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/check")
    public LoanResponse checkLoan(
            @RequestParam String personalCode,
            @RequestParam int amount,
            @RequestParam int period) {

        return loanService.calculateLoan(personalCode, amount, period);
    }
}