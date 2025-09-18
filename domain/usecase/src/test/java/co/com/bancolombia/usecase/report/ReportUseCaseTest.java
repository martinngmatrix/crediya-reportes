package co.com.bancolombia.usecase.report;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.bancolombia.model.report.Report;
import co.com.bancolombia.model.report.constants.Constants;
import co.com.bancolombia.model.report.gateways.ReportRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportUseCase reportUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getApprovedLoansApplicationsReport_shouldReturnFirstReport() {
        Report report1 = new Report("APPROVED", BigDecimal.valueOf(1000), BigInteger.ONE);
        Report report2 = new Report("APPROVED", BigDecimal.valueOf(2000), BigInteger.TWO);

        when(reportRepository.getEntityBySomeKeys(Constants.APPROVED_STATUS))
                .thenReturn(Mono.just(List.of(report1, report2)));

        StepVerifier.create(reportUseCase.getApprovedLoansApplicationsReport())
                .expectNext(report1)
                .verifyComplete();

        verify(reportRepository).getEntityBySomeKeys(Constants.APPROVED_STATUS);
    }

    @Test
    void getApprovedLoansApplicationsReport_shouldReturnEmptyWhenNoReports() {
        when(reportRepository.getEntityBySomeKeys(Constants.APPROVED_STATUS))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(reportUseCase.getApprovedLoansApplicationsReport())
                .verifyComplete();

        verify(reportRepository).getEntityBySomeKeys(Constants.APPROVED_STATUS);
    }

    @Test
    void updateLoansApplicationReport_shouldCallRepository() {
        BigDecimal amount = BigDecimal.valueOf(3000);

        when(reportRepository.updateApprovedLoansApplicationsReport(Constants.APPROVED_STATUS, amount))
                .thenReturn(Mono.empty());

        StepVerifier.create(reportUseCase.updateLoansApplicationReport(amount))
                .verifyComplete();

        verify(reportRepository).updateApprovedLoansApplicationsReport(Constants.APPROVED_STATUS, amount);
    }
}
