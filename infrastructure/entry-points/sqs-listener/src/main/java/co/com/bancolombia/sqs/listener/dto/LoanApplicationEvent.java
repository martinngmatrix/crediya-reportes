package co.com.bancolombia.sqs.listener.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record LoanApplicationEvent (
    BigInteger loanApplicationId,
    String status,
    BigDecimal amount
){
    
}
