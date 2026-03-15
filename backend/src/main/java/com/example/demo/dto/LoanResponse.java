package com.example.demo.dto;

public record LoanResponse(
        DecisionStatus status,
        int approvedAmount,
        int period
) {
    public boolean isApproved() {
        return status == DecisionStatus.APPROVED;
    }
}
