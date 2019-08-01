package database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Logger;

public class DbConnection {

    private DbConnection() {}
    private static final Logger LOG = Logger.getAnonymousLogger();

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static synchronized EntityManager getEntityManager() {
        if (emf == null && em == null) {
            synchronized (DbConnection.class) {
                if (emf == null && em == null) {
                    emf = Persistence.createEntityManagerFactory("thePersistenceUnit");
                    em = emf.createEntityManager();
                }
            }
        }
        else LOG.info("emf already initialized");
        return em;
    }
}
