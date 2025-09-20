package co.com.pedrorido.usecase.apis;

import co.com.pedrorido.model.report.Report;
import reactor.core.publisher.Mono;

public interface IReportApi {
    Mono<Void> increment(Report report);
    Mono<Report> get(String key);
}
