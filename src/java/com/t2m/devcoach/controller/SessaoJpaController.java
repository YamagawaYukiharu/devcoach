/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t2m.devcoach.controller;

import com.t2m.devcoach.controller.exceptions.NonexistentEntityException;
import com.t2m.devcoach.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.t2m.devcoach.model.Ferramenta;
import java.util.ArrayList;
import java.util.Collection;
import com.t2m.devcoach.model.Programa;
import com.t2m.devcoach.model.Sessao;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author master
 */
public class SessaoJpaController implements Serializable {

    public SessaoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sessao sessao) throws PreexistingEntityException, Exception {
        if (sessao.getFerramentaCollection() == null) {
            sessao.setFerramentaCollection(new ArrayList<Ferramenta>());
        }
        if (sessao.getProgramaCollection() == null) {
            sessao.setProgramaCollection(new ArrayList<Programa>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ferramenta> attachedFerramentaCollection = new ArrayList<Ferramenta>();
            for (Ferramenta ferramentaCollectionFerramentaToAttach : sessao.getFerramentaCollection()) {
                ferramentaCollectionFerramentaToAttach = em.getReference(ferramentaCollectionFerramentaToAttach.getClass(), ferramentaCollectionFerramentaToAttach.getId());
                attachedFerramentaCollection.add(ferramentaCollectionFerramentaToAttach);
            }
            sessao.setFerramentaCollection(attachedFerramentaCollection);
            Collection<Programa> attachedProgramaCollection = new ArrayList<Programa>();
            for (Programa programaCollectionProgramaToAttach : sessao.getProgramaCollection()) {
                programaCollectionProgramaToAttach = em.getReference(programaCollectionProgramaToAttach.getClass(), programaCollectionProgramaToAttach.getId());
                attachedProgramaCollection.add(programaCollectionProgramaToAttach);
            }
            sessao.setProgramaCollection(attachedProgramaCollection);
            em.persist(sessao);
            for (Ferramenta ferramentaCollectionFerramenta : sessao.getFerramentaCollection()) {
                ferramentaCollectionFerramenta.getSessaoCollection().add(sessao);
                ferramentaCollectionFerramenta = em.merge(ferramentaCollectionFerramenta);
            }
            for (Programa programaCollectionPrograma : sessao.getProgramaCollection()) {
                programaCollectionPrograma.getSessaoCollection().add(sessao);
                programaCollectionPrograma = em.merge(programaCollectionPrograma);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSessao(sessao.getId()) != null) {
                throw new PreexistingEntityException("Sessao " + sessao + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sessao sessao) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sessao persistentSessao = em.find(Sessao.class, sessao.getId());
            Collection<Ferramenta> ferramentaCollectionOld = persistentSessao.getFerramentaCollection();
            Collection<Ferramenta> ferramentaCollectionNew = sessao.getFerramentaCollection();
            Collection<Programa> programaCollectionOld = persistentSessao.getProgramaCollection();
            Collection<Programa> programaCollectionNew = sessao.getProgramaCollection();
            Collection<Ferramenta> attachedFerramentaCollectionNew = new ArrayList<Ferramenta>();
            for (Ferramenta ferramentaCollectionNewFerramentaToAttach : ferramentaCollectionNew) {
                ferramentaCollectionNewFerramentaToAttach = em.getReference(ferramentaCollectionNewFerramentaToAttach.getClass(), ferramentaCollectionNewFerramentaToAttach.getId());
                attachedFerramentaCollectionNew.add(ferramentaCollectionNewFerramentaToAttach);
            }
            ferramentaCollectionNew = attachedFerramentaCollectionNew;
            sessao.setFerramentaCollection(ferramentaCollectionNew);
            Collection<Programa> attachedProgramaCollectionNew = new ArrayList<Programa>();
            for (Programa programaCollectionNewProgramaToAttach : programaCollectionNew) {
                programaCollectionNewProgramaToAttach = em.getReference(programaCollectionNewProgramaToAttach.getClass(), programaCollectionNewProgramaToAttach.getId());
                attachedProgramaCollectionNew.add(programaCollectionNewProgramaToAttach);
            }
            programaCollectionNew = attachedProgramaCollectionNew;
            sessao.setProgramaCollection(programaCollectionNew);
            sessao = em.merge(sessao);
            for (Ferramenta ferramentaCollectionOldFerramenta : ferramentaCollectionOld) {
                if (!ferramentaCollectionNew.contains(ferramentaCollectionOldFerramenta)) {
                    ferramentaCollectionOldFerramenta.getSessaoCollection().remove(sessao);
                    ferramentaCollectionOldFerramenta = em.merge(ferramentaCollectionOldFerramenta);
                }
            }
            for (Ferramenta ferramentaCollectionNewFerramenta : ferramentaCollectionNew) {
                if (!ferramentaCollectionOld.contains(ferramentaCollectionNewFerramenta)) {
                    ferramentaCollectionNewFerramenta.getSessaoCollection().add(sessao);
                    ferramentaCollectionNewFerramenta = em.merge(ferramentaCollectionNewFerramenta);
                }
            }
            for (Programa programaCollectionOldPrograma : programaCollectionOld) {
                if (!programaCollectionNew.contains(programaCollectionOldPrograma)) {
                    programaCollectionOldPrograma.getSessaoCollection().remove(sessao);
                    programaCollectionOldPrograma = em.merge(programaCollectionOldPrograma);
                }
            }
            for (Programa programaCollectionNewPrograma : programaCollectionNew) {
                if (!programaCollectionOld.contains(programaCollectionNewPrograma)) {
                    programaCollectionNewPrograma.getSessaoCollection().add(sessao);
                    programaCollectionNewPrograma = em.merge(programaCollectionNewPrograma);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sessao.getId();
                if (findSessao(id) == null) {
                    throw new NonexistentEntityException("The sessao with id " + id + " no longer exists.");
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
            Sessao sessao;
            try {
                sessao = em.getReference(Sessao.class, id);
                sessao.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sessao with id " + id + " no longer exists.", enfe);
            }
            Collection<Ferramenta> ferramentaCollection = sessao.getFerramentaCollection();
            for (Ferramenta ferramentaCollectionFerramenta : ferramentaCollection) {
                ferramentaCollectionFerramenta.getSessaoCollection().remove(sessao);
                ferramentaCollectionFerramenta = em.merge(ferramentaCollectionFerramenta);
            }
            Collection<Programa> programaCollection = sessao.getProgramaCollection();
            for (Programa programaCollectionPrograma : programaCollection) {
                programaCollectionPrograma.getSessaoCollection().remove(sessao);
                programaCollectionPrograma = em.merge(programaCollectionPrograma);
            }
            em.remove(sessao);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sessao> findSessaoEntities() {
        return findSessaoEntities(true, -1, -1);
    }

    public List<Sessao> findSessaoEntities(int maxResults, int firstResult) {
        return findSessaoEntities(false, maxResults, firstResult);
    }

    private List<Sessao> findSessaoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sessao.class));
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

    public Sessao findSessao(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sessao.class, id);
        } finally {
            em.close();
        }
    }

    public int getSessaoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sessao> rt = cq.from(Sessao.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
