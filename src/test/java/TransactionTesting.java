import constants.Constant;
import database.DbConnection;
import entity.dao.Account;
import entity.dto.AccountDTO;
import entity.dto.TransactionDTO;
import org.junit.Before;
import org.junit.Test;
import repository.*;
import transactions.TransactionRules;
import transactions.TransactionUtility;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TransactionTesting {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private Long one = new Long(1);
    private Long two =  new Long(2);
    private Account xA, yA;
    Map<String, Repository> repositoryMap = new HashMap<>();

    @Before
    public void setup(){
        accountRepository = new AccountRepositoryImpl(DbConnection.getEntityManager());
        transactionRepository = new TransactionRepositoryImpl(DbConnection.getEntityManager());
        repositoryMap.put(Constant.ACCOUNT_REPOSITORY, accountRepository);
        repositoryMap.put(Constant.TRANSACTION_REPOSITORY, transactionRepository);
        TransactionRules.init(DbConnection.getEntityManager());
    }


    public void clearDB(){
        if(xA != null) accountRepository.deleteAccount(xA);
        if(xA != null) accountRepository.deleteAccount(yA);
        AccountDTO x = AccountDTO.builder()
                .accountNo(one)
                .balance(new Double(100))
                .firstName("Joe")
                .lastName("X")
                .build();

         xA = getAccountFromDTO(x);

        AccountDTO y = AccountDTO.builder()
                .accountNo(two)
                .balance(new Double(999))
                .firstName("Alice")
                .lastName("Y")
                .build();

        yA = getAccountFromDTO(y);

        accountRepository.saveAccount(xA);
        accountRepository.saveAccount(yA);

        //transactionRepository.clearAll();
    }

    @Test
    public void transferFromXToYWithValidRules(){
        clearDB();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new Double(50))
                .crFrom(one)
                .crTo(two)
                .build();


        TransactionUtility.PERFORM_TRANSACTION.apply(transaction, repositoryMap);

        Account xN = TransactionUtility.FIND_ACCOUNT.apply(one, accountRepository);
        Account yN = TransactionUtility.FIND_ACCOUNT.apply(two, accountRepository);


        assertEquals(xN.getBalance(), new Double(50));
        assertEquals(yN.getBalance(), new Double(1049));
    }

    @Test
    public void transferFromXToYWithFromAndToAsSameAccount(){
        clearDB();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new Double(0))
                .crFrom(one)
                .crTo(one)
                .build();

        TransactionUtility.PERFORM_TRANSACTION.apply(transaction, repositoryMap);

        Account xN = TransactionUtility.FIND_ACCOUNT.apply(one, accountRepository);
        Account yN = TransactionUtility.FIND_ACCOUNT.apply(two, accountRepository);


        assertEquals(xN.getBalance(), new Double(100));
        assertEquals(yN.getBalance(), new Double(999));
    }

    @Test
    public void transferFromXToYWithToAccountNotPresent(){
        clearDB();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new Double(0))
                .crFrom(one)
                .crTo(new Long(123))
                .build();

        TransactionUtility.PERFORM_TRANSACTION.apply(transaction, repositoryMap);

        Account xN = TransactionUtility.FIND_ACCOUNT.apply(one, accountRepository);
        Account yN = TransactionUtility.FIND_ACCOUNT.apply(two, accountRepository);


        assertEquals(xN.getBalance(), new Double(100));
        assertEquals(yN.getBalance(), new Double(999));
    }

    @Test
    public void transferFromXToYWithZeroAsTransactionBalance(){
        clearDB();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new Double(0))
                .crFrom(one)
                .crTo(two)
                .build();

        TransactionUtility.PERFORM_TRANSACTION.apply(transaction, repositoryMap);

        Account xN = TransactionUtility.FIND_ACCOUNT.apply(one, accountRepository);
        Account yN = TransactionUtility.FIND_ACCOUNT.apply(two, accountRepository);


        assertEquals(xN.getBalance(), new Double(100));
        assertEquals(yN.getBalance(), new Double(999));
    }

    @Test
    public void transferFromXToYWithSourceAccountHavingNotEnoughBalance(){
        clearDB();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new Double(101))
                .crFrom(one)
                .crTo(two)
                .build();

        TransactionUtility.PERFORM_TRANSACTION.apply(transaction, repositoryMap);

        Account xN = TransactionUtility.FIND_ACCOUNT.apply(one, accountRepository);
        Account yN = TransactionUtility.FIND_ACCOUNT.apply(two, accountRepository);


        assertEquals(xN.getBalance(), new Double(100));
        assertEquals(yN.getBalance(), new Double(999));
    }

    private Account getAccountFromDTO(AccountDTO accountDTO){
        Account account = new Account();
        account.setFirstName(accountDTO.getFirstName());
        account.setMiddleName(accountDTO.getMiddleName());
        account.setLastName(accountDTO.getLastName());
        account.setAccountNo(accountDTO.getAccountNo());
        account.setBalance(accountDTO.getBalance());
        return account;
    }
}
