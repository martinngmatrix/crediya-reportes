package co.com.bancolombia.model.report.gateways;

import java.math.BigDecimal;
import java.util.List;

import co.com.bancolombia.model.report.Report;
import reactor.core.publisher.Mono;

public interface ReportRepository {
    Mono<List<Report>> getEntityBySomeKeys(String partitionKey, String sortKey);
    Mono<Void> updateApprovedLoansApplicationsReport(BigDecimal amount);
}
