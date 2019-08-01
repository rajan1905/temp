package repository;

import entity.dao.Transactions;

public interface TransactionRepository extends Repository{
    void saveTransaction(Transactions transaction);
    void clearAll();
}
