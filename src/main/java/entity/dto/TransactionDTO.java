package entity.dto;

import entity.dao.Transactions;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionDTO {
    String transactionMessage;
    Long crFrom;
    Long crTo;
    Double amount;
}
