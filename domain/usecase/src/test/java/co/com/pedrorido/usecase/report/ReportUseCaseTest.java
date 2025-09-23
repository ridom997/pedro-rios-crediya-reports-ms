package co.com.pedrorido.usecase.report;

import co.com.pedrorido.model.report.Report;
import co.com.pedrorido.model.report.gateways.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportUseCaseTest {
    private ReportRepository reportRepository;

    private ReportUseCase useCase;

    @BeforeEach
    void setUp() {
        reportRepository = Mockito.mock(ReportRepository.class);
        useCase = new ReportUseCase(reportRepository);
    }

    // ===== increment(): con valor actual existente (happy path) =====
    @Test
    void increment_whenCurrentExists_updatesAppropriatelyAndCompletes() {
        // Given
        String pk = "user#123";
        Report incoming = Report.builder()
                .pk(pk)
                .totalAmountLoans(new BigDecimal("50"))
                .approvedLoans(0) // valor del incoming no se usa para el +1, solo totalAmountLoans
                .build();

        Report current = Report.builder()
                .pk(pk)
                .approvedLoans(2)
                .totalAmountLoans(new BigDecimal("100"))
                .build();

        when(reportRepository.get(pk)).thenReturn(Mono.just(current));
        // Mono<Void>: común devolver Mono.empty() para "completado"
        when(reportRepository.increment(any(Report.class))).thenReturn(Mono.empty());

        // When
        StepVerifier.create(useCase.increment(incoming))
                .verifyComplete();

        // Then: capturamos el Report que se envía a increment
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).get(pk);
        verify(reportRepository).increment(captor.capture());

        Report sent = captor.getValue();
        // Debe sumar +1 a approvedLoans y acumular totalAmountLoans
        org.junit.jupiter.api.Assertions.assertEquals(pk, sent.getPk());
        org.junit.jupiter.api.Assertions.assertEquals(3L, sent.getApprovedLoans());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("150"), sent.getTotalAmountLoans());

        verifyNoMoreInteractions(reportRepository);
    }

    // ===== increment(): sin valor actual (switchIfEmpty) =====
    @Test
    void increment_whenNoCurrent_existsUsesZeroBaselineAndCompletes() {
        // Given
        String pk = "user#456";
        Report incoming = Report.builder()
                .pk(pk)
                .totalAmountLoans(new BigDecimal("200.00"))
                .approvedLoans(999) // no debe influir
                .build();

        when(reportRepository.get(pk)).thenReturn(Mono.empty());
        when(reportRepository.increment(any(Report.class))).thenReturn(Mono.empty());

        // When
        StepVerifier.create(useCase.increment(incoming))
                .verifyComplete();

        // Then
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).get(pk);
        verify(reportRepository).increment(captor.capture());

        Report sent = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(pk, sent.getPk());
        // Al no existir previo: approvedLoans parte de 0 y se incrementa a 1
        org.junit.jupiter.api.Assertions.assertEquals(1L, sent.getApprovedLoans());
        // totalAmountLoans debe ser el incoming (0 + incoming)
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("200.00"), sent.getTotalAmountLoans());

        verifyNoMoreInteractions(reportRepository);
    }

    // ===== increment(): error en increment (propaga error y ejecuta doOnError) =====
    @Test
    void increment_whenRepositoryIncrementFails_propagatesError() {
        // Given
        String pk = "user#999";
        Report incoming = Report.builder()
                .pk(pk)
                .totalAmountLoans(new BigDecimal("10"))
                .build();

        Report current = Report.builder()
                .pk(pk)
                .approvedLoans(0)
                .totalAmountLoans(BigDecimal.ZERO)
                .build();

        when(reportRepository.get(pk)).thenReturn(Mono.just(current));
        when(reportRepository.increment(any(Report.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        // When / Then
        StepVerifier.create(useCase.increment(incoming))
                .expectErrorMatches(ex -> ex instanceof RuntimeException && ex.getMessage().equals("boom"))
                .verify();

        verify(reportRepository).get(pk);
        verify(reportRepository).increment(any(Report.class));
        verifyNoMoreInteractions(reportRepository);
    }

    // ===== get(): delega directamente al repositorio =====
    @Test
    void get_delegatesToRepository() {
        // Given
        String pk = "k1";
        Report r = Report.builder()
                .pk(pk)
                .approvedLoans(5)
                .totalAmountLoans(new BigDecimal("123.45"))
                .build();

        when(reportRepository.get(pk)).thenReturn(Mono.just(r));

        // When / Then
        StepVerifier.create(useCase.get(pk))
                .expectNextMatches(out ->
                        out.getPk().equals(pk)
                                && out.getApprovedLoans() == 5L
                                && out.getTotalAmountLoans().compareTo(new BigDecimal("123.45")) == 0
                )
                .verifyComplete();

        verify(reportRepository).get(pk);
        verifyNoMoreInteractions(reportRepository);
    }
}