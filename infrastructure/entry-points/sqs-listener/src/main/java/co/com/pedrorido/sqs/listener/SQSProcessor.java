package co.com.pedrorido.sqs.listener;

import co.com.pedrorido.model.report.Report;
import co.com.pedrorido.usecase.apis.IReportApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final IReportApi myUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Processing message to increment report counter {}", message);
        Report report = null;
        try {
            log.info("Processing message {}", message.body());
            report = objectMapper.readValue(message.body(), Report.class);
        } catch (Exception e) {
            log.error("Error processing message {}", message.body(), e);
            return Mono.error(e);
        }

        return myUseCase.increment(report);
    }
}
