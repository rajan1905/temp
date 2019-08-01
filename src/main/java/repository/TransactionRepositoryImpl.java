package repository;

import entity.dao.Transactions;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TransactionRepositoryImpl implements TransactionRepository {
    private EntityManager entityManager;
    private static final String TRUNCATE = "delete Transactions";

    public TransactionRepositoryImpl(EntityManager em){
        entityManager = em;
    }

    @Override
    public void saveTransaction(Transactions transaction) {
        entityManager.persist(transaction);
    }

    @Override
    public void clearAll() {
        Query truncateQuery = entityManager.createQuery(TRUNCATE);
        truncateQuery.executeUpdate();
    }
}
