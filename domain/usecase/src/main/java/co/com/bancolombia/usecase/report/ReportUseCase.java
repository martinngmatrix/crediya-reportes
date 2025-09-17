package co.com.bancolombia.usecase.report;

import java.math.BigDecimal;

import co.com.bancolombia.model.report.Report;
import co.com.bancolombia.model.report.constants.Constants;
import co.com.bancolombia.model.report.gateways.ReportRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportUseCase {
    private final ReportRepository reportRepository;

    public Mono<Report> getApprovedLoansApplicationsReport() {
        return reportRepository.getEntityBySomeKeys(null,Constants.APPROVED_STATUS)
                .flatMap(reports -> Mono.justOrEmpty(reports.stream().findFirst().orElse(null)));
    }

    public Mono<Void> updateLoansApplicationReport(BigDecimal amount) {
        return reportRepository.updateApprovedLoansApplicationsReport(amount);
    }
}
