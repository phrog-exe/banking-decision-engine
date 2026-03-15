package com.example.demo;

import com.example.demo.dto.LoanResponse;
import com.example.demo.dto.DecisionStatus;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.UnknownApplicantException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoanService {

    private static final Map<String, Integer> CREDIT_MODIFIERS = Map.of(
            "49002010976", 100,
            "49002010987", 300,
            "49002010998", 1000
    );
    private static final String DEBT_PERSONAL_CODE = "49002010965";

    private static final int MIN_AMOUNT = 2000;
    private static final int MAX_AMOUNT = 10000;
    private static final int MIN_PERIOD = 12;
    private static final int MAX_PERIOD = 60;

    public LoanResponse calculateLoan(String personalCode, int requestedAmount, int requestedPeriod) {
        validateInput(requestedAmount, requestedPeriod);

        // Reject applicants with existing debt
        if (DEBT_PERSONAL_CODE.equals(personalCode)) {
            return new LoanResponse(DecisionStatus.DEBT, 0, requestedPeriod);
        }

        // Reject unknown applicants with a clear status
        Integer modifier = CREDIT_MODIFIERS.get(personalCode);
        if (modifier == null) {
            throw new UnknownApplicantException("Personal code not found: " + personalCode);
        }

        // Try to find the maximum approvable amount at the requested period
        int approvedAmount = maxApprovedAmount(modifier, requestedPeriod);
        if (meetsMinimum(approvedAmount)) {
            return new LoanResponse(DecisionStatus.APPROVED, approvedAmount, requestedPeriod);
        }

        // Search all valid periods
        // Try shorter periods first (closer to requested), then longer
        for (int period = MIN_PERIOD; period <= MAX_PERIOD; period++) {
            if (period == requestedPeriod) continue; // already tried above
            int amount = maxApprovedAmount(modifier, period);
            if (meetsMinimum(amount)) {
                return new LoanResponse(DecisionStatus.APPROVED, amount, period);
            }
        }

        //No valid combination found
        return new LoanResponse(DecisionStatus.REJECTED, 0, requestedPeriod);
    }




    private int maxApprovedAmount(int modifier, int period) {
        return Math.min(modifier * period, MAX_AMOUNT);
    }

    private boolean meetsMinimum(int amount) {
        return amount >= MIN_AMOUNT;
    }

    private void validateInput(int amount, int period) {
        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            throw new InvalidInputException(
                    String.format("Loan amount must be between %d and %d", MIN_AMOUNT, MAX_AMOUNT)
            );
        }
        if (period < MIN_PERIOD || period > MAX_PERIOD) {
            throw new InvalidInputException(
                    String.format("Loan period must be between %d and %d months", MIN_PERIOD, MAX_PERIOD)
            );
        }
    }
}
