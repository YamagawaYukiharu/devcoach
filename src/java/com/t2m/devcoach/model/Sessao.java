/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t2m.devcoach.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author master
 */
@Entity
@Table(name = "SESSAO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sessao.findAll", query = "SELECT s FROM Sessao s")
    , @NamedQuery(name = "Sessao.findByNumeroDaSessao", query = "SELECT s FROM Sessao s WHERE s.numeroDaSessao = :numeroDaSessao")
    , @NamedQuery(name = "Sessao.findByData", query = "SELECT s FROM Sessao s WHERE s.data = :data")
    , @NamedQuery(name = "Sessao.findByHora", query = "SELECT s FROM Sessao s WHERE s.hora = :hora")
    , @NamedQuery(name = "Sessao.findByFeedback", query = "SELECT s FROM Sessao s WHERE s.feedback = :feedback")
    , @NamedQuery(name = "Sessao.findByLicoesAprendidas", query = "SELECT s FROM Sessao s WHERE s.licoesAprendidas = :licoesAprendidas")
    , @NamedQuery(name = "Sessao.findById", query = "SELECT s FROM Sessao s WHERE s.id = :id")})
public class Sessao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "NUMERO_DA_SESSAO")
    private String numeroDaSessao;
    @Column(name = "DATA")
    private String data;
    @Column(name = "HORA")
    private String hora;
    @Column(name = "FEEDBACK")
    private String feedback;
    @Column(name = "LICOES_APRENDIDAS")
    private String licoesAprendidas;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @ManyToMany(mappedBy = "sessaoCollection")
    private Collection<Ferramenta> ferramentaCollection;
    @ManyToMany(mappedBy = "sessaoCollection")
    private Collection<Programa> programaCollection;

    public Sessao() {
    }

    public Sessao(Integer id) {
        this.id = id;
    }

    public String getNumeroDaSessao() {
        return numeroDaSessao;
    }

    public void setNumeroDaSessao(String numeroDaSessao) {
        this.numeroDaSessao = numeroDaSessao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getLicoesAprendidas() {
        return licoesAprendidas;
    }

    public void setLicoesAprendidas(String licoesAprendidas) {
        this.licoesAprendidas = licoesAprendidas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public Collection<Ferramenta> getFerramentaCollection() {
        return ferramentaCollection;
    }

    public void setFerramentaCollection(Collection<Ferramenta> ferramentaCollection) {
        this.ferramentaCollection = ferramentaCollection;
    }

    @XmlTransient
    public Collection<Programa> getProgramaCollection() {
        return programaCollection;
    }

    public void setProgramaCollection(Collection<Programa> programaCollection) {
        this.programaCollection = programaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sessao)) {
            return false;
        }
        Sessao other = (Sessao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.t2m.devcoach.model.Sessao[ id=" + id + " ]";
    }
    
}
