package entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AccountDTO {
    String firstName;
    String middleName;
    String lastName;
    Long accountNo;
    Double balance;
}
