package co.com.pedrorido.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportResponseDTO {
    private String pk;
    private long approvedLoans;
    private BigDecimal totalAmountLoans;
}
