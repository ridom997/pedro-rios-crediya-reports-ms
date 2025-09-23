package co.com.pedrorido.api;

import co.com.pedrorido.api.dto.GeneralResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reportes",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = ReportHandler.class,
                    beanMethod = "listenGETReport",
                    operation = @Operation(
                            summary = "Obtiene el reporte de préstamos aprobados - ADMIN",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            description = "Retorna un reporte que contiene los detalles de los préstamos aprobados.",
                            operationId = "getApprovedLoansReport",
                            tags = { "Reportes" },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Reporte obtenido exitosamente.",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = GeneralResponseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error en el servidor. No se pudo obtener el reporte.",
                                            content = @Content
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(ReportHandler reportHandler) {
        return route(
                GET("/api/v1/reportes"),
                req -> reportHandler.listenGETReport(req)
                        .flatMap(re -> ServerResponse
                                .status(re.getStatusCode())
                                .headers(h -> h.addAll(re.getHeaders()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(re.getBody())));
    }
}
