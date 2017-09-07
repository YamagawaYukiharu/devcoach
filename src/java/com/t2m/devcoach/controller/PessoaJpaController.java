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
import com.t2m.devcoach.model.Endereco;
import com.t2m.devcoach.model.Pessoa;
import com.t2m.devcoach.model.Telefone;
import java.util.ArrayList;
import java.util.Collection;
import com.t2m.devcoach.model.Programa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author master
 */
public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) throws PreexistingEntityException, Exception {
        if (pessoa.getTelefoneCollection() == null) {
            pessoa.setTelefoneCollection(new ArrayList<Telefone>());
        }
        if (pessoa.getProgramaCollection() == null) {
            pessoa.setProgramaCollection(new ArrayList<Programa>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Endereco idEndereco = pessoa.getIdEndereco();
            if (idEndereco != null) {
                idEndereco = em.getReference(idEndereco.getClass(), idEndereco.getIdEndereco());
                pessoa.setIdEndereco(idEndereco);
            }
            Collection<Telefone> attachedTelefoneCollection = new ArrayList<Telefone>();
            for (Telefone telefoneCollectionTelefoneToAttach : pessoa.getTelefoneCollection()) {
                telefoneCollectionTelefoneToAttach = em.getReference(telefoneCollectionTelefoneToAttach.getClass(), telefoneCollectionTelefoneToAttach.getId());
                attachedTelefoneCollection.add(telefoneCollectionTelefoneToAttach);
            }
            pessoa.setTelefoneCollection(attachedTelefoneCollection);
            Collection<Programa> attachedProgramaCollection = new ArrayList<Programa>();
            for (Programa programaCollectionProgramaToAttach : pessoa.getProgramaCollection()) {
                programaCollectionProgramaToAttach = em.getReference(programaCollectionProgramaToAttach.getClass(), programaCollectionProgramaToAttach.getId());
                attachedProgramaCollection.add(programaCollectionProgramaToAttach);
            }
            pessoa.setProgramaCollection(attachedProgramaCollection);
            em.persist(pessoa);
            if (idEndereco != null) {
                idEndereco.getPessoaCollection().add(pessoa);
                idEndereco = em.merge(idEndereco);
            }
            for (Telefone telefoneCollectionTelefone : pessoa.getTelefoneCollection()) {
                telefoneCollectionTelefone.getPessoaCollection().add(pessoa);
                telefoneCollectionTelefone = em.merge(telefoneCollectionTelefone);
            }
            for (Programa programaCollectionPrograma : pessoa.getProgramaCollection()) {
                Pessoa oldIdPessoaOfProgramaCollectionPrograma = programaCollectionPrograma.getIdPessoa();
                programaCollectionPrograma.setIdPessoa(pessoa);
                programaCollectionPrograma = em.merge(programaCollectionPrograma);
                if (oldIdPessoaOfProgramaCollectionPrograma != null) {
                    oldIdPessoaOfProgramaCollectionPrograma.getProgramaCollection().remove(programaCollectionPrograma);
                    oldIdPessoaOfProgramaCollectionPrograma = em.merge(oldIdPessoaOfProgramaCollectionPrograma);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoa(pessoa.getId()) != null) {
                throw new PreexistingEntityException("Pessoa " + pessoa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getId());
            Endereco idEnderecoOld = persistentPessoa.getIdEndereco();
            Endereco idEnderecoNew = pessoa.getIdEndereco();
            Collection<Telefone> telefoneCollectionOld = persistentPessoa.getTelefoneCollection();
            Collection<Telefone> telefoneCollectionNew = pessoa.getTelefoneCollection();
            Collection<Programa> programaCollectionOld = persistentPessoa.getProgramaCollection();
            Collection<Programa> programaCollectionNew = pessoa.getProgramaCollection();
            if (idEnderecoNew != null) {
                idEnderecoNew = em.getReference(idEnderecoNew.getClass(), idEnderecoNew.getIdEndereco());
                pessoa.setIdEndereco(idEnderecoNew);
            }
            Collection<Telefone> attachedTelefoneCollectionNew = new ArrayList<Telefone>();
            for (Telefone telefoneCollectionNewTelefoneToAttach : telefoneCollectionNew) {
                telefoneCollectionNewTelefoneToAttach = em.getReference(telefoneCollectionNewTelefoneToAttach.getClass(), telefoneCollectionNewTelefoneToAttach.getId());
                attachedTelefoneCollectionNew.add(telefoneCollectionNewTelefoneToAttach);
            }
            telefoneCollectionNew = attachedTelefoneCollectionNew;
            pessoa.setTelefoneCollection(telefoneCollectionNew);
            Collection<Programa> attachedProgramaCollectionNew = new ArrayList<Programa>();
            for (Programa programaCollectionNewProgramaToAttach : programaCollectionNew) {
                programaCollectionNewProgramaToAttach = em.getReference(programaCollectionNewProgramaToAttach.getClass(), programaCollectionNewProgramaToAttach.getId());
                attachedProgramaCollectionNew.add(programaCollectionNewProgramaToAttach);
            }
            programaCollectionNew = attachedProgramaCollectionNew;
            pessoa.setProgramaCollection(programaCollectionNew);
            pessoa = em.merge(pessoa);
            if (idEnderecoOld != null && !idEnderecoOld.equals(idEnderecoNew)) {
                idEnderecoOld.getPessoaCollection().remove(pessoa);
                idEnderecoOld = em.merge(idEnderecoOld);
            }
            if (idEnderecoNew != null && !idEnderecoNew.equals(idEnderecoOld)) {
                idEnderecoNew.getPessoaCollection().add(pessoa);
                idEnderecoNew = em.merge(idEnderecoNew);
            }
            for (Telefone telefoneCollectionOldTelefone : telefoneCollectionOld) {
                if (!telefoneCollectionNew.contains(telefoneCollectionOldTelefone)) {
                    telefoneCollectionOldTelefone.getPessoaCollection().remove(pessoa);
                    telefoneCollectionOldTelefone = em.merge(telefoneCollectionOldTelefone);
                }
            }
            for (Telefone telefoneCollectionNewTelefone : telefoneCollectionNew) {
                if (!telefoneCollectionOld.contains(telefoneCollectionNewTelefone)) {
                    telefoneCollectionNewTelefone.getPessoaCollection().add(pessoa);
                    telefoneCollectionNewTelefone = em.merge(telefoneCollectionNewTelefone);
                }
            }
            for (Programa programaCollectionOldPrograma : programaCollectionOld) {
                if (!programaCollectionNew.contains(programaCollectionOldPrograma)) {
                    programaCollectionOldPrograma.setIdPessoa(null);
                    programaCollectionOldPrograma = em.merge(programaCollectionOldPrograma);
                }
            }
            for (Programa programaCollectionNewPrograma : programaCollectionNew) {
                if (!programaCollectionOld.contains(programaCollectionNewPrograma)) {
                    Pessoa oldIdPessoaOfProgramaCollectionNewPrograma = programaCollectionNewPrograma.getIdPessoa();
                    programaCollectionNewPrograma.setIdPessoa(pessoa);
                    programaCollectionNewPrograma = em.merge(programaCollectionNewPrograma);
                    if (oldIdPessoaOfProgramaCollectionNewPrograma != null && !oldIdPessoaOfProgramaCollectionNewPrograma.equals(pessoa)) {
                        oldIdPessoaOfProgramaCollectionNewPrograma.getProgramaCollection().remove(programaCollectionNewPrograma);
                        oldIdPessoaOfProgramaCollectionNewPrograma = em.merge(oldIdPessoaOfProgramaCollectionNewPrograma);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoa.getId();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
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
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            Endereco idEndereco = pessoa.getIdEndereco();
            if (idEndereco != null) {
                idEndereco.getPessoaCollection().remove(pessoa);
                idEndereco = em.merge(idEndereco);
            }
            Collection<Telefone> telefoneCollection = pessoa.getTelefoneCollection();
            for (Telefone telefoneCollectionTelefone : telefoneCollection) {
                telefoneCollectionTelefone.getPessoaCollection().remove(pessoa);
                telefoneCollectionTelefone = em.merge(telefoneCollectionTelefone);
            }
            Collection<Programa> programaCollection = pessoa.getProgramaCollection();
            for (Programa programaCollectionPrograma : programaCollection) {
                programaCollectionPrograma.setIdPessoa(null);
                programaCollectionPrograma = em.merge(programaCollectionPrograma);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
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

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
