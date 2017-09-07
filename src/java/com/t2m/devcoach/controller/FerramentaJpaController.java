/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t2m.devcoach.controller;

import com.t2m.devcoach.controller.exceptions.NonexistentEntityException;
import com.t2m.devcoach.controller.exceptions.PreexistingEntityException;
import com.t2m.devcoach.model.Ferramenta;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.t2m.devcoach.model.Sessao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author master
 */
public class FerramentaJpaController implements Serializable {

    public FerramentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ferramenta ferramenta) throws PreexistingEntityException, Exception {
        if (ferramenta.getSessaoCollection() == null) {
            ferramenta.setSessaoCollection(new ArrayList<Sessao>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Sessao> attachedSessaoCollection = new ArrayList<Sessao>();
            for (Sessao sessaoCollectionSessaoToAttach : ferramenta.getSessaoCollection()) {
                sessaoCollectionSessaoToAttach = em.getReference(sessaoCollectionSessaoToAttach.getClass(), sessaoCollectionSessaoToAttach.getId());
                attachedSessaoCollection.add(sessaoCollectionSessaoToAttach);
            }
            ferramenta.setSessaoCollection(attachedSessaoCollection);
            em.persist(ferramenta);
            for (Sessao sessaoCollectionSessao : ferramenta.getSessaoCollection()) {
                sessaoCollectionSessao.getFerramentaCollection().add(ferramenta);
                sessaoCollectionSessao = em.merge(sessaoCollectionSessao);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFerramenta(ferramenta.getId()) != null) {
                throw new PreexistingEntityException("Ferramenta " + ferramenta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ferramenta ferramenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ferramenta persistentFerramenta = em.find(Ferramenta.class, ferramenta.getId());
            Collection<Sessao> sessaoCollectionOld = persistentFerramenta.getSessaoCollection();
            Collection<Sessao> sessaoCollectionNew = ferramenta.getSessaoCollection();
            Collection<Sessao> attachedSessaoCollectionNew = new ArrayList<Sessao>();
            for (Sessao sessaoCollectionNewSessaoToAttach : sessaoCollectionNew) {
                sessaoCollectionNewSessaoToAttach = em.getReference(sessaoCollectionNewSessaoToAttach.getClass(), sessaoCollectionNewSessaoToAttach.getId());
                attachedSessaoCollectionNew.add(sessaoCollectionNewSessaoToAttach);
            }
            sessaoCollectionNew = attachedSessaoCollectionNew;
            ferramenta.setSessaoCollection(sessaoCollectionNew);
            ferramenta = em.merge(ferramenta);
            for (Sessao sessaoCollectionOldSessao : sessaoCollectionOld) {
                if (!sessaoCollectionNew.contains(sessaoCollectionOldSessao)) {
                    sessaoCollectionOldSessao.getFerramentaCollection().remove(ferramenta);
                    sessaoCollectionOldSessao = em.merge(sessaoCollectionOldSessao);
                }
            }
            for (Sessao sessaoCollectionNewSessao : sessaoCollectionNew) {
                if (!sessaoCollectionOld.contains(sessaoCollectionNewSessao)) {
                    sessaoCollectionNewSessao.getFerramentaCollection().add(ferramenta);
                    sessaoCollectionNewSessao = em.merge(sessaoCollectionNewSessao);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ferramenta.getId();
                if (findFerramenta(id) == null) {
                    throw new NonexistentEntityException("The ferramenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ferramenta ferramenta;
            try {
                ferramenta = em.getReference(Ferramenta.class, id);
                ferramenta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ferramenta with id " + id + " no longer exists.", enfe);
            }
            Collection<Sessao> sessaoCollection = ferramenta.getSessaoCollection();
            for (Sessao sessaoCollectionSessao : sessaoCollection) {
                sessaoCollectionSessao.getFerramentaCollection().remove(ferramenta);
                sessaoCollectionSessao = em.merge(sessaoCollectionSessao);
            }
            em.remove(ferramenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ferramenta> findFerramentaEntities() {
        return findFerramentaEntities(true, -1, -1);
    }

    public List<Ferramenta> findFerramentaEntities(int maxResults, int firstResult) {
        return findFerramentaEntities(false, maxResults, firstResult);
    }

    private List<Ferramenta> findFerramentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ferramenta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ferramenta findFerramenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ferramenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getFerramentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ferramenta> rt = cq.from(Ferramenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
