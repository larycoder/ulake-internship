package org.usth.ict.ulake.core.backend.impl;

import org.usth.ict.ulake.common.misc.Utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

public class JpaUtils {
    private static final String PERSISTENCE_UNIT_NAME = "org.usth.ict.ulake.core";
    private static EntityManagerFactory factory;

    public static EntityManagerFactory getFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory;
    }

    public static void persist(EntityManager em, Object o) {
        em.getTransaction().begin();
        em.persist(o);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    public static void persist(Object o) {
        persist(JpaUtils.getFactory().createEntityManager(), o);
    }

    public static void merge(EntityManager em, Object o) {
        em.getTransaction().begin();
        em.merge(o);
        em.getTransaction().commit();
        em.close();
    }

    public static void merge(Object o) {
        merge(JpaUtils.getFactory().createEntityManager(), o);
    }

    public static <T> List<T> list(Class<T> clazz) {
        return list(clazz, 0, 0);
    }

    public static <T> List<T> list(Class<T> clazz, int pageSize, int pageNum) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        String queryString = "select a from " + clazz.getSimpleName() + " a";
        Query q = em.createQuery(queryString);
        if (pageSize > 0 && pageNum > 0) {
            q.setFirstResult((pageNum - 1) * pageSize);
            q.setMaxResults(pageSize);
        }
        List<T> ret = q.getResultList();
        em.close();
        return ret;
    }

    public static <T> List<T> query(Class<T> clazz, String queryString) {
        return query(clazz, 15, 1, queryString);
    }

    public static <T> List<T> query(Class<T> clazz, String queryString, Object ... params) {
        return query(clazz, 0, 0, queryString, params);
    }

    public static <T> List<T> query(Class<T> clazz, int pageSize, int pageNum, String queryString, Object ... params) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
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

    /**
     * Convenience for em.find()
     */
    public static <T> T find(Class<T> clazz, int id) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        T ret = em.find(clazz, id);
        em.close();
        return ret;
    }

    public static Object single(String queryString) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        Query q = em.createQuery(queryString);
        Object ret = q.getSingleResult();
        em.close();
        return ret;
    }

    public static Object singleSql(String queryString) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        Query q = em.createNativeQuery(queryString);
        Object ret = q.getSingleResult();
        em.close();
        return ret;
    }

    public static List<Map<String, Object>> listSql(String sql, Object ... params) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        Query q = em.createNativeQuery(sql);
        if (params != null) {
            if (params.length % 2 == 0 && params.length > 0) {
                for (int i = 0; i < params.length; i += 2) {
                    // System.out.println("Adding param " + params[i] + " to " + params[i+1]);
                    String paramName = String.valueOf(params[i]);
                    if (Utils.isNumeric(paramName)) {
                        q.setParameter(Integer.valueOf(paramName), params[i + 1]);
                    }
                    else {
                        q.setParameter(paramName, params[i + 1]);
                    }
                }
            }
        }
        List<Map<String, Object>> ret = q.getResultList();
        em.close();
        return ret;
    }

    public static <T> List<T> listSql(String sql, Class T) {
        EntityManager em = JpaUtils.getFactory().createEntityManager();
        Query q = em.createNativeQuery(sql, T);
        List<T> ret = q.getResultList();
        em.close();
        return ret;
    }
}
