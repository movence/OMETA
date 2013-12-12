/*
 * Copyright J. Craig Venter Institute, 2013
 *
 * The creation of this program was supported by J. Craig Venter Institute
 * and National Institute for Allergy and Infectious Diseases (NIAID),
 * Contract number HHSN272200900007C.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcvi.ometa.ontology;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hkim
 * Date: 4/11/13
 * Time: 12:02 PM
 */
public class OntologyTerm {
    String termId; //term id
    String shortId;
    String ontologyAbbr; //abbreviated ontology
    String ontologyFull; //full string ontology
    String ontologyId;
    String ontologyVersion;
    String term;
    String preferredName;

    String parentTermId;
    String relationship;
    List<OntologyTerm> children;

    public OntologyTerm(String termId, String term) {
        this("", termId, term);
    }
    public OntologyTerm(String ontologyFull, String termId, String term) {
        this(ontologyFull, null, null, termId, term);
    }
    public OntologyTerm(String ontologyFull, String ontologyAbbr, String ontologyId, String termId, String term) {
        this.ontologyFull = ontologyFull;
        if(ontologyAbbr==null) {
            if(!termId.startsWith("http://") && termId.contains(":")) { //OLS term ID has ontology acronym in it. Bioportal uses URL as term ID
                this.ontologyAbbr = termId.substring(0, termId.indexOf(":"));
            }
        } else {
            this.ontologyAbbr = ontologyAbbr;
        }
        this.ontologyId = ontologyId;
        this.termId = termId;
        this.term = term;
    }
    public OntologyTerm() {}

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getOntologyAbbr() {
        return ontologyAbbr;
    }

    public void setOntologyAbbr(String ontologyAbbr) {
        this.ontologyAbbr = ontologyAbbr;
    }

    public String getOntologyFull() {
        return ontologyFull;
    }

    public void setOntologyFull(String ontologyFull) {
        this.ontologyFull = ontologyFull;
    }

    public String getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getParentTermId() {
        return parentTermId;
    }

    public void setParentTermId(String parentTermId) {
        this.parentTermId = parentTermId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public List<OntologyTerm> getChildren() {
        return children;
    }

    public void setChildren(List<OntologyTerm> children) {
        this.children = children;
    }

    public String getOntologyVersion() {
        return ontologyVersion;
    }

    public void setOntologyVersion(String ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
}
