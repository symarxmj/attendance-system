package com.example.attendancesystem.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    private int successCount = 0;
    private int failCount = 0;
    private List<String> errors = new ArrayList<>();

    public void incrementSuccess() { this.successCount++; }

    public void incrementFail(String reason) {
        this.failCount++;
        if (errors.size() < 20) {
            errors.add(reason);
        }
    }
}
