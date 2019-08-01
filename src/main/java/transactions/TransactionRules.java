package transactions;

import entity.dto.TransactionDTO;
import repository.AccountRepository;
import repository.AccountRepositoryImpl;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TransactionRules {

    private static AccountRepository accountRepository;

    public static synchronized void init(EntityManager entityManager){
        if(accountRepository == null){
            synchronized (TransactionRules.class){
                if(accountRepository == null){
                    accountRepository = new AccountRepositoryImpl(entityManager);
                }
            }
        }
    }

    private static final List<Function<TransactionDTO, Boolean>> RULES = new ArrayList<>();

    private static final Function<TransactionDTO, Boolean> SENDING_TO_OWN_ACCOUNT = transactionDTO ->
            transactionDTO.getCrFrom() == transactionDTO.getCrTo() ? false : true;

    private static final Function<TransactionDTO, Boolean> IS_AMOUNT_NOT_ZERO = transactionDTO ->
            transactionDTO.getAmount() == 0.0 ? false : true;

    private static final Function<TransactionDTO, Boolean> IS_SOURCE_DESTINATION_ACCOUNT_PRESENT = transactionDTO ->
            (accountRepository.getAccountByAccountNo(transactionDTO.getCrTo()) != null) &&
                    (accountRepository.getAccountByAccountNo(transactionDTO.getCrFrom()) != null);

    private static final Function<TransactionDTO, Boolean> DOES_SOURCE_ACCOUNT_HAVE_ENOUGH_BALANCE = transactionDTO ->
            accountRepository.getAccountByAccountNo(transactionDTO.getCrFrom()).getBalance() >= transactionDTO.getAmount();

    public static final Function<TransactionDTO, Boolean> CHECK_TRANSACTION_VALID = transactionDTO ->
        applyRules(transactionDTO);

    private static Boolean applyRules(TransactionDTO transactionDTO){
        Boolean isTransactionValid = true;
        if(RULES.isEmpty()) initRules();

        for(Function<TransactionDTO, Boolean> rule : RULES){
            isTransactionValid &= rule.apply(transactionDTO);
            if(isTransactionValid == false) break;
        }

        return isTransactionValid;
    }

    private static void initRules(){
        RULES.add(SENDING_TO_OWN_ACCOUNT);
        RULES.add(IS_AMOUNT_NOT_ZERO);
        RULES.add(IS_SOURCE_DESTINATION_ACCOUNT_PRESENT);
        RULES.add(DOES_SOURCE_ACCOUNT_HAVE_ENOUGH_BALANCE);
    }
}
