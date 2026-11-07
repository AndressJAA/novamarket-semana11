package pe.edu.upsjb.novamarket.application;

public record EventHistorySummary(
        long eventCount,
        long processingCount,
        long successfulProcessingCount,
        long failedProcessingCount) {
}
