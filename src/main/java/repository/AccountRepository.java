package repository;

import entity.dao.Account;

public interface AccountRepository extends Repository{
    Account getAccountByAccountNo(Long id);
    Account saveAccount(Account account);
    Account updateAccountBalance(Account account);
    void deleteAccount(Account account);
}
