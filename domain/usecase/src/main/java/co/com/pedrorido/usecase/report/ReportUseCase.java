package co.com.pedrorido.usecase.report;

import co.com.pedrorido.model.report.Report;
import co.com.pedrorido.model.report.gateways.ReportRepository;
import co.com.pedrorido.usecase.apis.IReportApi;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ReportUseCase implements IReportApi {
    private final ReportRepository reportRepository;

    @Override
    public Mono<Void> increment(Report report) {
        return reportRepository.get(report.getPk())
                .switchIfEmpty(Mono.just(Report.builder().pk(report.getPk()).totalAmountLoans(BigDecimal.ZERO).approvedLoans(0).build()))
                .doOnSubscribe(s -> System.out.println("Increment start pk={}"))
                .doOnNext(curr -> System.out.println("Current value pk={} = {}"+ curr))
                .flatMap(curr ->
                        reportRepository.increment(
                                Report.builder()
                                        .pk(report.getPk())
                                        .approvedLoans(curr.getApprovedLoans() + 1)
                                        .totalAmountLoans(curr.getTotalAmountLoans().add(report.getTotalAmountLoans()))
                                        .build()
                        )
                )
                .doOnSuccess(v -> System.out.println("Increment done pk={}"+ report.getPk()))
                .doOnError(e -> System.out.println("Increment error pk={}"+ e))
                .then(); // seguimos devolviendo Mono<Void>
    }

    @Override
    public Mono<Report> get(String key) {
        return reportRepository.get(key);
    }
}
