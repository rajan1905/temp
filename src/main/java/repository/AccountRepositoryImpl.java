package repository;

import entity.dao.Account;

import javax.persistence.EntityManager;

public class AccountRepositoryImpl implements AccountRepository {
    private EntityManager entityManager;

    public AccountRepositoryImpl(EntityManager em){
        entityManager = em;
    }

    @Override
    public Account getAccountByAccountNo(Long id) {
        return entityManager.find(Account.class, id);
    }

    @Override
    public Account saveAccount(Account account) {
        if(getAccountByAccountNo(account.getAccountNo()) == null){
            entityManager.persist(account);
        }
        else{
            account = entityManager.merge(account);
        }
        return account;
    }

    @Override
    public Account updateAccountBalance(Account account) {
        return saveAccount(account);
    }

    @Override
    public void deleteAccount(Account account) {
        if(getAccountByAccountNo(account.getAccountNo()) == null)
            entityManager.remove(account);
    }
}
