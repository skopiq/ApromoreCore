/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Expression generated by hbm2java
 */
@Entity
@Table(name = "expression")
@Configurable("expression")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Expression implements java.io.Serializable {

    private Integer id;
    private String description;
    private String language;
    private String expression;
    private String returnType;

    private Node inputNode;
    private Node outputNode;
    private Set<Edge> edges = new HashSet<>();


    public Expression() { }



    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }


    @Column(name = "description", length = 40)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Column(name = "language", length = 40)
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    @Column(name = "expression", length = 40)
    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    @Column(name = "returnType", length = 40)
    public String getReturnType() {
        return this.returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


    @OneToMany(mappedBy = "conditionExpression", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Edge> getEdges() {
        return this.edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }


    @ManyToOne
    @JoinColumn(name = "inputNodeId")
    public Node getInputNode() {
        return this.inputNode;
    }

    public void setInputNode(Node newInputNode) {
        this.inputNode = newInputNode;
    }

    @ManyToOne
    @JoinColumn(name = "outputNodeId")
    public Node getOutputNode() {
        return this.outputNode;
    }

    public void setOutputNode(Node newOutputNode) {
        this.outputNode = newOutputNode;
    }
}
