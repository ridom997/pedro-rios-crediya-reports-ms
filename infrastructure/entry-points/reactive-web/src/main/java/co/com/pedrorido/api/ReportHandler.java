package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.GeneralResponseDTO;
import co.com.pedrorido.api.dto.ReportResponseDTO;
import co.com.pedrorido.api.mapper.ReportDTOMapper;
import co.com.pedrorido.usecase.apis.IReportApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Log4j2
public class ReportHandler {
    private final IReportApi reportUseCase;
    private final ReportDTOMapper mapper;

    public Mono<ResponseEntity<GeneralResponseDTO<ReportResponseDTO>>> listenGETReport(ServerRequest serverRequest) {
        log.info("GET report");
        return reportUseCase.get("approvedLoans")
                .map(mapper::toResponse)
                .map(savedRequestDto -> {
                    HashMap<String, ReportResponseDTO> data = new HashMap<>();
                    data.put("data", savedRequestDto);
                    return new ResponseEntity<>(
                            GeneralResponseDTO.<ReportResponseDTO>builder()
                                    .success(true)
                                    .message("Report obtained successfully")
                                    .data(data)
                                    .build(),
                            HttpStatus.OK);
                })
                .doOnSuccess(log::info)
                .doOnError(log::error);
    }
}
