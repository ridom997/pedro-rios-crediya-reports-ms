package co.com.pedrorido.api.mapper;

import co.com.pedrorido.api.dto.ReportResponseDTO;
import co.com.pedrorido.model.report.Report;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReportDTOMapper {
    ReportResponseDTO toResponse(Report report);
}
