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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "FERRAMENTA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ferramenta.findAll", query = "SELECT f FROM Ferramenta f")
    , @NamedQuery(name = "Ferramenta.findByNomeFerramenta", query = "SELECT f FROM Ferramenta f WHERE f.nomeFerramenta = :nomeFerramenta")
    , @NamedQuery(name = "Ferramenta.findByTipoFerramenta", query = "SELECT f FROM Ferramenta f WHERE f.tipoFerramenta = :tipoFerramenta")
    , @NamedQuery(name = "Ferramenta.findById", query = "SELECT f FROM Ferramenta f WHERE f.id = :id")})
public class Ferramenta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "NOME_FERRAMENTA")
    private String nomeFerramenta;
    @Column(name = "TIPO_FERRAMENTA")
    private String tipoFerramenta;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinTable(name = "SESSAO_FERRAMENTA", joinColumns = {
        @JoinColumn(name = "ID_FERRAMENTA", referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "ID_SESSAO", referencedColumnName = "ID")})
    @ManyToMany
    private Collection<Sessao> sessaoCollection;

    public Ferramenta() {
    }

    public Ferramenta(Integer id) {
        this.id = id;
    }

    public String getNomeFerramenta() {
        return nomeFerramenta;
    }

    public void setNomeFerramenta(String nomeFerramenta) {
        this.nomeFerramenta = nomeFerramenta;
    }

    public String getTipoFerramenta() {
        return tipoFerramenta;
    }

    public void setTipoFerramenta(String tipoFerramenta) {
        this.tipoFerramenta = tipoFerramenta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public Collection<Sessao> getSessaoCollection() {
        return sessaoCollection;
    }

    public void setSessaoCollection(Collection<Sessao> sessaoCollection) {
        this.sessaoCollection = sessaoCollection;
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
        if (!(object instanceof Ferramenta)) {
            return false;
        }
        Ferramenta other = (Ferramenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.t2m.devcoach.model.Ferramenta[ id=" + id + " ]";
    }
    
}
