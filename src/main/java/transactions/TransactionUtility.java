package transactions;

import constants.Constant;
import entity.dao.Account;
import entity.dao.Transactions;
import entity.dto.TransactionDTO;
import repository.AccountRepository;
import repository.Repository;
import repository.TransactionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class TransactionUtility {
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    /*private static Function<Account, AccountDTO> buildAccountDTO = account -> AccountDTO.builder()
            .firstName(account.getFirstName())
            .middleName(account.getMiddleName())
            .lastName(account.getLastName())
            .accountNo(account.getAccountNo())
            .balance(account.getBalance())
            .build();*/

    public static final BiFunction<Long, AccountRepository, Account> FIND_ACCOUNT = ((accountNo, repository) -> {
        Account account = null;
        try{
            account = repository.getAccountByAccountNo(accountNo);
        }catch (Exception e) { e.printStackTrace(); }

        return account;
    });

    private static final BiConsumer<TransactionDTO, TransactionRepository> SAVE_TRANSACTION = (transactionDTO, transactionRepository) ->{
        Transactions transactions = new Transactions();
        transactions.setTransactionMessage("Amount "+transactionDTO.getAmount() +" transferred from :" +
                transactionDTO.getCrFrom() + " to account : "+transactionDTO.getCrTo());
        transactions.setAmount(transactionDTO.getAmount());
        transactions.setCrFrom(transactionDTO.getCrFrom());
        transactions.setCrTo(transactionDTO.getCrTo());
        transactionRepository.saveTransaction(transactions );
    };


    public static final BiFunction<Map<Account, Double>, AccountRepository, Boolean> UPDATE_ACCOUNT = ((accounts, repository) -> {
        AtomicBoolean result = new AtomicBoolean(true);

        accounts.keySet()
                .stream()
                .forEach(account -> {
                    account.setBalance(accounts.get(account));
                    repository.updateAccountBalance(account);
                    LOGGER.info("Updated account = "+ account.getAccountNo() + " with balance : "+accounts.get(account));
                });


        return result.get();
    });

    public static final BiFunction<TransactionDTO, Map<String, Repository>, Boolean> PERFORM_TRANSACTION =
            ((transactionDTO, repositories) -> {
                AccountRepository aRepository = (AccountRepository) repositories.get(Constant.ACCOUNT_REPOSITORY);
                TransactionRepository tRepository = (TransactionRepository) repositories.get(Constant.TRANSACTION_REPOSITORY);
                Boolean result = false;
                boolean isTransactionValid = TransactionRules.CHECK_TRANSACTION_VALID.apply(transactionDTO);
                if(isTransactionValid) {
                    Account accountTo = FIND_ACCOUNT.apply(transactionDTO.getCrTo(), aRepository);
                    Account accountFrom = FIND_ACCOUNT.apply(transactionDTO.getCrFrom(), aRepository);
                    Map<Account, Double> updates = new HashMap<>();
                    if (accountFrom != null && accountTo != null) {
                        updates.put(accountFrom, accountFrom.getBalance() - transactionDTO.getAmount());
                            updates.put(accountTo, accountTo.getBalance() + transactionDTO.getAmount());

                        result = UPDATE_ACCOUNT.apply(updates, aRepository);
                        if (result) {
                            SAVE_TRANSACTION.accept(transactionDTO, tRepository);
                            LOGGER.info("Transaction completed");
                        }
                        else LOGGER.info("Transaction not complete");
                    } else {
                        LOGGER.info("No account found with account number : " + transactionDTO.getCrTo());
                    }
                }
                else{
                    // To-Do : Log Messages could be more refined
                    LOGGER.info("Transaction not valid");
                }
                return result;
    });


}
