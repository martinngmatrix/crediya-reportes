package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.constants.Constants;
import co.com.bancolombia.api.constants.messages.ApiResponseMessages;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.usecase.report.ReportUseCase;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@RequiredArgsConstructor
public class Handler {
private final ReportUseCase reportUseCase;

    public Mono<ServerResponse> getApprovedLoansApplicationsReport(ServerRequest serverRequest) {
        log.trace(ApiResponseMessages.REQUEST_RECEIVED_GET_REPORT);
        return reportUseCase.getApprovedLoansApplicationsReport()
                .flatMap(
                    report -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(report)
                )
                .doOnSuccess(res -> log.info(ApiResponseMessages.REPORT_OBTAINED_SUCCESSFULLY))
                .onErrorResume(e -> {
                    ErrorResponse error = new ErrorResponse(
                            e.getMessage() != null ? e.getMessage() : Constants.UNEXPECTED_ERROR
                    );
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(error);
                }); 
    }
}
