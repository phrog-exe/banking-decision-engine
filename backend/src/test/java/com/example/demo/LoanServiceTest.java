package com.example.demo;

import com.example.demo.dto.LoanResponse;
import com.example.demo.dto.DecisionStatus;
import com.example.demo.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private final LoanService loanService = new LoanService();

    @Test
    void shouldApproveSegment3WithMaxLimit() {
        // modifier 1000 * 12 = 12000, cut at 10 000
        LoanResponse response = loanService.calculateLoan("49002010998", 4000, 12);

        assertEquals(DecisionStatus.APPROVED, response.status());
        assertEquals(10000, response.approvedAmount());
        assertEquals(12, response.period()); // requested period works, no need to change it
    }

    @Test
    void shouldRejectIfDebtExists() {
        // This personal code must always be rejected with DEBT status
        LoanResponse response = loanService.calculateLoan("49002010965", 4000, 12);
        assertEquals(DecisionStatus.DEBT, response.status());
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() {
// Checking if the system correctly blocks amounts that are too low.
        assertThrows(InvalidInputException.class, () -> {
            loanService.calculateLoan("49002010976", 500, 12);
        });
    }

    @Test
    void shouldFindAlternativePeriod() {
        // modifier 100, best possible: 100 * 60 = 6000 at period 60
        LoanResponse response = loanService.calculateLoan("49002010976", 4000, 12);

        assertEquals(DecisionStatus.APPROVED, response.status());
        assertEquals(60, response.period());
        assertEquals(6000, response.approvedAmount());
    }
}
