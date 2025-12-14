package guru.qa.niffler.data.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;

public class ThreadSafeEntityManager implements EntityManager {
    private final ThreadLocal<EntityManager> threadEm = new ThreadLocal<>();
    private final EntityManagerFactory emf;


    public ThreadSafeEntityManager(EntityManager delegate) {
        threadEm.set(delegate);
        emf = delegate.getEntityManagerFactory();
    }

    private EntityManager threadEm() {
        if (threadEm.get() == null || !threadEm.get().isOpen()) {
            threadEm.set(emf.createEntityManager());
        }
        return threadEm.get();
    }

    public void close() {
        if (threadEm.get() == null || !threadEm.get().isOpen()) {
            threadEm.get().close();
            threadEm.remove();
        }
    }

    public void persist(Object o) {
        threadEm().persist(o);
    }

    public <T> Query createNativeQuery(String s, Class<T> aClass) {
        return threadEm().createNativeQuery(s, aClass);
    }

    public void refresh(Object o) {
        threadEm().refresh(o);
    }

    public boolean isJoinedToTransaction() {
        return threadEm().isJoinedToTransaction();
    }

    public EntityGraph<?> getEntityGraph(String s) {
        return threadEm().getEntityGraph(s);
    }

    public boolean contains(Object o) {
        return threadEm().contains(o);
    }

    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> typedQueryReference) {
        return threadEm().createQuery(typedQueryReference);
    }

    public <T> T find(EntityGraph<T> entityGraph, Object o, FindOption... findOptions) {
        return threadEm().find(entityGraph, o, findOptions);
    }

    public void clear() {
        threadEm().clear();
    }

    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return threadEm().createQuery(criteriaQuery);
    }

    public Map<String, Object> getProperties() {
        return threadEm().getProperties();
    }

    public <T> EntityGraph<T> createEntityGraph(Class<T> aClass) {
        return threadEm().createEntityGraph(aClass);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String s, String... strings) {
        return threadEm().createStoredProcedureQuery(s, strings);
    }

    public void lock(Object o, LockModeType lockModeType, Map<String, Object> map) {
        threadEm().lock(o, lockModeType, map);
    }

    public <T> T find(Class<T> aClass, Object o) {
        return threadEm().find(aClass, o);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return threadEm().getCriteriaBuilder();
    }

    public Query createNamedQuery(String s) {
        return threadEm().createNamedQuery(s);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String s) {
        return threadEm().createStoredProcedureQuery(s);
    }

    public EntityTransaction getTransaction() {
        return threadEm().getTransaction();
    }

    public <T> T merge(T t) {
        return threadEm().merge(t);
    }

    public FlushModeType getFlushMode() {
        return threadEm().getFlushMode();
    }

    public void setFlushMode(FlushModeType flushModeType) {
        threadEm().setFlushMode(flushModeType);
    }

    public void refresh(Object o, LockModeType lockModeType, Map<String, Object> map) {
        threadEm().refresh(o, lockModeType, map);
    }

    public CacheStoreMode getCacheStoreMode() {
        return threadEm().getCacheStoreMode();
    }

    public void setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        threadEm().setCacheStoreMode(cacheStoreMode);
    }

    public <C, T> T callWithConnection(ConnectionFunction<C, T> connectionFunction) {
        return threadEm().callWithConnection(connectionFunction);
    }

    public void flush() {
        threadEm().flush();
    }

    public Query createQuery(CriteriaDelete<?> criteriaDelete) {
        return threadEm().createQuery(criteriaDelete);
    }

    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType, Map<String, Object> map) {
        return threadEm().find(aClass, o, lockModeType, map);
    }

    public void refresh(Object o, Map<String, Object> map) {
        threadEm().refresh(o, map);
    }

    public Query createNativeQuery(String s, String s1) {
        return threadEm().createNativeQuery(s, s1);
    }

    public LockModeType getLockMode(Object o) {
        return threadEm().getLockMode(o);
    }

    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> criteriaSelect) {
        return threadEm().createQuery(criteriaSelect);
    }

    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> aClass) {
        return threadEm().getEntityGraphs(aClass);
    }

    public <T> T unwrap(Class<T> aClass) {
        return threadEm().unwrap(aClass);
    }

    public void lock(Object o, LockModeType lockModeType, LockOption... lockOptions) {
        threadEm().lock(o, lockModeType, lockOptions);
    }

    public Query createNativeQuery(String s) {
        return threadEm().createNativeQuery(s);
    }

    public <T> T find(Class<T> aClass, Object o, Map<String, Object> map) {
        return threadEm().find(aClass, o, map);
    }

    public void joinTransaction() {
        threadEm().joinTransaction();
    }

    public <T> T getReference(Class<T> aClass, Object o) {
        return threadEm().getReference(aClass, o);
    }

    public EntityGraph<?> createEntityGraph(String s) {
        return threadEm().createEntityGraph(s);
    }

    public Query createQuery(String s) {
        return threadEm().createQuery(s);
    }

    public void remove(Object o) {
        threadEm().remove(o);
    }

    public void detach(Object o) {
        threadEm().detach(o);
    }

    public <T> TypedQuery<T> createNamedQuery(String s, Class<T> aClass) {
        return threadEm().createNamedQuery(s, aClass);
    }

    public Metamodel getMetamodel() {
        return threadEm().getMetamodel();
    }

    public void setProperty(String s, Object o) {
        threadEm().setProperty(s, o);
    }

    public StoredProcedureQuery createStoredProcedureQuery(String s, Class<?>... classes) {
        return threadEm().createStoredProcedureQuery(s, classes);
    }

    public <T> T find(Class<T> aClass, Object o, FindOption... findOptions) {
        return threadEm().find(aClass, o, findOptions);
    }

    public void lock(Object o, LockModeType lockModeType) {
        threadEm().lock(o, lockModeType);
    }

    public void refresh(Object o, RefreshOption... refreshOptions) {
        threadEm().refresh(o, refreshOptions);
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return threadEm().getEntityManagerFactory();
    }

    public CacheRetrieveMode getCacheRetrieveMode() {
        return threadEm().getCacheRetrieveMode();
    }

    public void setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        threadEm().setCacheRetrieveMode(cacheRetrieveMode);
    }

    public StoredProcedureQuery createNamedStoredProcedureQuery(String s) {
        return threadEm().createNamedStoredProcedureQuery(s);
    }

    public void refresh(Object o, LockModeType lockModeType) {
        threadEm().refresh(o, lockModeType);
    }

    public <T> TypedQuery<T> createQuery(String s, Class<T> aClass) {
        return threadEm().createQuery(s, aClass);
    }

    public boolean isOpen() {
        return threadEm().isOpen();
    }

    public <C> void runWithConnection(ConnectionConsumer<C> connectionConsumer) {
        threadEm().runWithConnection(connectionConsumer);
    }

    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType) {
        return threadEm().find(aClass, o, lockModeType);
    }

    public <T> T getReference(T t) {
        return threadEm().getReference(t);
    }

    public Query createQuery(CriteriaUpdate<?> criteriaUpdate) {
        return threadEm().createQuery(criteriaUpdate);
    }

    public Object getDelegate() {
        return threadEm().getDelegate();
    }
}
