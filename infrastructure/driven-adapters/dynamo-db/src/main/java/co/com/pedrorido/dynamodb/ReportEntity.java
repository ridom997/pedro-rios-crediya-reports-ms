package co.com.pedrorido.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class ReportEntity {

    private String pk;
    private long approvedLoans;
    private BigDecimal totalAmountLoans;

    public ReportEntity() {
    }


    public ReportEntity(String pk, long approvedLoans, BigDecimal totalAmountLoans) {
        this.pk = pk;
        this.approvedLoans = approvedLoans;
        this.totalAmountLoans = totalAmountLoans;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbAttribute("totalAmountLoans")
    public BigDecimal getTotalAmountLoans() {
        return totalAmountLoans;
    }

    public void setTotalAmountLoans(BigDecimal totalAmountLoans) {
        this.totalAmountLoans = totalAmountLoans;
    }

    @DynamoDbAttribute("approvedLoans")
    public long getApprovedLoans() {
        return approvedLoans;
    }

    public void setApprovedLoans(long approvedLoans) {
        this.approvedLoans = approvedLoans;
    }
}
