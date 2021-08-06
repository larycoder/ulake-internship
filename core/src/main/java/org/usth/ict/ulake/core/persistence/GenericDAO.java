package org.usth.ict.ulake.core.persistence;

import org.usth.ict.ulake.common.misc.Utils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Dependent
public class GenericDAO<T> {
    @Inject
    EntityManager em;

    // CRUD methods
    @Transactional
    public void save(T o) {
        em.persist(o);
    }

    @Transactional
    public void update(T o) {
        em.merge(o);
    }

    @Transactional
    public void delete( T o) {
        em.remove(o);
    }

    public T findById(Class<T> clazz, long id) {
        return em.find(clazz, id);
    }

    // query methods
    public List<T> query(String queryString) {
        return query(15, 1, queryString);
    }

    public List<T> query(String queryString, Object ... params) {
        return query(0, 0, queryString, params);
    }

    public <T> List<T> query(int pageSize, int pageNum, String queryString, Object ... params) {
        Query q = em.createQuery(queryString);
        if (params != null) {
            if (params.length % 2 == 0 && params.length > 0) {
                for (int i = 0; i < params.length; i += 2) {
                    // System.out.println("Adding param " + params[i] + " to " + params[i+1]);
                    if (Utils.isNumeric(String.valueOf(params[i]))) {
                        q.setParameter(Integer.valueOf(String.valueOf(params[i])), params[i + 1]);
                    }
                    else {
                        q.setParameter(String.valueOf(params[i]), params[i + 1]);
                    }
                }
            }
        }
        if (pageNum > 0 && pageSize > 0) {
            q.setFirstResult((pageNum - 1) * pageSize);
            q.setMaxResults(pageSize);
        }
        List<T> ret = q.getResultList();
        em.close();
        return ret;
    }

    public Object querySingle(String queryString) {
        Query q = em.createQuery(queryString);
        Object ret = q.getSingleResult();
        return ret;
    }

    // listing methods
    public List<T> list(Class<T> clazz) {
        return list(clazz, 0, 0);
    }

    public List<T> list(Class<T> clazz, int pageSize, int pageNum) {
        String queryString = "select a from " + clazz.getSimpleName() + " a";
        Query q = em.createQuery(queryString);
        if (pageSize > 0 && pageNum > 0) {
            q.setFirstResult((pageNum - 1) * pageSize);
            q.setMaxResults(pageSize);
        }
        List<T> ret = q.getResultList();
        return ret;
    }

    public List<Object> listSql(String sql, Class T) {
        Query q = em.createNativeQuery(sql, T);
        List<Object> ret = q.getResultList();
        return ret;
    }

    public T findBy(Class<T> clazz, String field, String value) {
        List<T> ret = query("select o from " + clazz.getSimpleName() + " o where o." + field + "=:value", 1, value);
        if (ret.isEmpty()) return null;
        return ret.get(0);
    }
}
