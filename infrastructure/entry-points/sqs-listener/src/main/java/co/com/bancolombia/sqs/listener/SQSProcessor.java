package co.com.bancolombia.sqs.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancolombia.sqs.listener.config.SQSProperties;
import co.com.bancolombia.sqs.listener.dto.LoanApplicationEvent;
import co.com.bancolombia.sqs.listener.dto.SqsMessageWrapper;
import co.com.bancolombia.sqs.listener.helper.SQSListener;
import co.com.bancolombia.usecase.report.ReportUseCase;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ReportUseCase reportUseCase;
    private final SqsAsyncClient client;
    private final SQSProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        SQSListener.builder()
                .client(client)
                .properties(properties)
                .processor(this)
                .build()
                .start();
    }

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Mensaje recibido {}", message.body());
        return Mono.fromCallable(() -> {
                SqsMessageWrapper<LoanApplicationEvent> wrapper =
                        objectMapper.readValue(
                            message.body(),
                            objectMapper.getTypeFactory().constructParametricType(SqsMessageWrapper.class, LoanApplicationEvent.class));
                return wrapper.getPayload();
            })
                .flatMap(
                        event -> reportUseCase.updateLoansApplicationReport(event.amount())
                                .doOnSuccess(unused -> log.info("Reporte actualizado con exito"))
                )
                .onErrorResume(ex -> {
                    log.error("Error procesando mensaje SQS: {}", ex.getMessage(), ex);
                    return Mono.empty();
                });
    }
}
