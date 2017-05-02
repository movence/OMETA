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

package org.jcvi.ometa.utils;

import org.jcvi.ometa.PSTTestAbstract;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: hkim
 * Date: 10/4/12
 * Time: 3:51 PM
 */
public class JsonProducerTest extends PSTTestAbstract {
    JsonProducer producer = null;

    @Test
    public void doJson() throws Exception{
        producer = new JsonProducer(this.getReadEjb("int"));
        this.whole();
        this.helper();
    }

    private void helper() {
        String projectNames = "GCID Acinetobacter,DEMO_WildFly";
        //String attributes = "Project Name,Collaborator,Collaborator Institute,Study Objective,Planned Samples,Received Samples,Discarded Samples,In Progress Samples,Published Samples,End Date,Failed QC";
        String attributes = "Organism,Aim,Category,External Identifier,Project Code,Sample Status,Sample Name,Project Leader,BioProject ID,BioSample ID,BioSample_Accession,Taxonomy ID,Project ID,sra accession,sra date,sra status,trace count,trace date,wgs accession,wgs date,wgs status,annotation accession,annotation date,annotation status,dbSNP accession,dbSNP date,dbSNP status,Project Group, Isolate Repository Accession,Isolate Repository Type,Isolate Repository Status,Anticipated final destination";
        //String screen = "Project Name,Collaborator,Collaborator Institute,Study Objective,Planned Samples,Received Samples,Discarded Samples,In Progress Samples,Published Samples,End Date,Failed QC";
        String screen = "Organism,Aim,Category,External Identifier,Project Code,Sample Status,Sample Name,Project Leader,BioProject ID,BioSample ID,Anticipated final destination";
        String sorting = "13,1,0";
        String fileName = "gscmsc";
        String filePath = "H:/test";

        producer.jsonHelper(projectNames, attributes, screen, sorting, fileName, filePath, "some");
    }

    private void whole() throws Exception{
        producer.generateJson();
    }
}
