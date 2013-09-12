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

package org.jcvi.ometa.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.jcvi.ometa.bean_interface.ProjectSampleEventPresentationBusiness;
import org.jcvi.ometa.bean_interface.ProjectSampleEventWritebackBusiness;
import org.jcvi.ometa.model.*;
import org.jcvi.ometa.model.web.MetadataSetupReadBean;
import org.jcvi.ometa.stateless_session_bean.ForbiddenResourceException;
import org.jcvi.ometa.utils.Constants;
import org.jcvi.ometa.utils.PresentationActionDelegate;
import org.jcvi.ometa.utils.UploadActionDelegate;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hkim
 * Date: 12/7/11
 * Time: 2:08 PM
 */
public class MetadataSetup extends ActionSupport {
    private Logger logger = Logger.getLogger(MetadataSetup.class);

    private ProjectSampleEventPresentationBusiness psept;
    private ProjectSampleEventWritebackBusiness psewt;

    private List<Project> projectList;
    private String projectNames;

    private String type;
    private List<MetadataSetupReadBean> beanList;
    private Long projectId;

    private String lerror;
    private String emsg;

    public MetadataSetup() {
        PresentationActionDelegate pdeledate = new PresentationActionDelegate();
        psept = pdeledate.initializeEjb(logger, psept);
    }

    public String process() {
        String returnValue = ERROR;
        UserTransaction tx = null;

        try {
            List<String> projectNameList = new ArrayList<String>();
            if (projectNames == null || projectNames.equals(""))
                projectNameList.add("ALL");
            else if (projectNames.contains(","))
                projectNameList.addAll(Arrays.asList(projectNames.split(",")));
            else
                projectNameList.add(projectNames);

            projectList = psept.getProjects(projectNameList);

            if(lerror!=null && "true".equals(lerror))
                throw new Exception(emsg);

            if (projectId!=null && projectId!=0 && type!=null && !type.isEmpty() && beanList!=null && beanList.size()>0) {
                tx=(UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                tx.begin();

                UploadActionDelegate udelegate = new UploadActionDelegate();
                psewt = udelegate.initializeBusinessObject(logger, psewt);

                //user selected project
                Project loadingProject = psept.getProject(projectId);

                //existing EMA for current project
                List<EventMetaAttribute> refEmaList = psept.getEventMetaAttributes(loadingProject.getProjectId());

                if("p".equals(type)) {
                    Map<String, ProjectMetaAttribute> existingPmaMap = this.getPmaMap(loadingProject.getProjectId());
                    Map<String, List<EventMetaAttribute>> refEmaMap = this.getEmaMap(refEmaList);

                    List<ProjectMetaAttribute> pmaList = new ArrayList<ProjectMetaAttribute>();
                    List<EventMetaAttribute> emaList = new ArrayList<EventMetaAttribute>();
                    for (MetadataSetupReadBean bean : beanList) {
                        ProjectMetaAttribute pma;
                        if(existingPmaMap.containsKey(bean.getName())) {
                            pma = existingPmaMap.get(bean.getName());
                            if(this.isUnchanged(bean, pma)) {
                                continue; //skip unchanged MA
                            }
                        } else {
                            pma = new ProjectMetaAttribute();
                            pma.setProjectId(loadingProject.getProjectId());
                            pma.setAttributeName(bean.getName());
                            //pma.setDataType(refEmaMap.get(bean.getName()).get(0).getDataType());
                        }
                        this.setMAValues(pma,
                                bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                        pmaList.add(pma);

                        List<EventMetaAttribute> emas = refEmaMap.get(bean.getName());
                        if(emas!=null && emas.size()>0) {
                            for(EventMetaAttribute ema : emas) {
                                this.setMAValues(ema,
                                        bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                        bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                                emaList.add(ema);
                            }
                        }
                    }
                    if(emaList.size()>0)
                        psewt.loadEventMetaAttributes(emaList);
                    if(pmaList.size()>0)
                        psewt.loadProjectMetaAttributes(pmaList);

                } else if ("s".equals(type)) { //sample meta attribute
                    Map<String, SampleMetaAttribute> exsitingSmaMap = this.getSmaMap(loadingProject.getProjectId());
                    Map<String, List<EventMetaAttribute>> refEmaMap = this.getEmaMap(refEmaList);

                    List<SampleMetaAttribute> smaList = new ArrayList<SampleMetaAttribute>();
                    List<EventMetaAttribute> emaList = new ArrayList<EventMetaAttribute>();
                    for (MetadataSetupReadBean bean : beanList) {
                        SampleMetaAttribute sma;
                        if(exsitingSmaMap.containsKey(bean.getName())) {
                            sma = exsitingSmaMap.get(bean.getName());
                            if(this.isUnchanged(bean, sma)) {
                                continue; //skip unchanged MA
                            }
                        } else {
                            sma = new SampleMetaAttribute();
                            sma.setProjectId(loadingProject.getProjectId());
                            sma.setAttributeName(bean.getName());
                            //sma.setDataType(refEmaMap.get(bean.getName()).get(0).getDataType());
                        }
                        this.setMAValues(sma,
                                bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                        smaList.add(sma);

                        List<EventMetaAttribute> emas = refEmaMap.get(bean.getName());
                        if(emas!=null && emas.size()>0) {
                            for(EventMetaAttribute ema : emas) {
                                this.setMAValues(ema,
                                        bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                        bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                                emaList.add(ema);
                            }
                        }
                    }
                    if(emaList.size()>0)
                        psewt.loadEventMetaAttributes(emaList);
                    if(smaList.size()>0)
                        psewt.loadSampleMetaAttributes(smaList);

                } else if ("e".equals(type)) {
                    Map<String, Map<String, EventMetaAttribute>> existingEmaMap = new HashMap<String, Map<String, EventMetaAttribute>>();
                    for(EventMetaAttribute ema : refEmaList) {
                        if(existingEmaMap.containsKey(ema.getEventName())) {
                            existingEmaMap.get(ema.getEventName()).put(ema.getAttributeName(), ema);
                        } else {
                            HashMap<String, EventMetaAttribute> emaMap = new HashMap<String, EventMetaAttribute>();
                            emaMap.put(ema.getAttributeName(), ema);
                            existingEmaMap.put(ema.getEventName(), emaMap);
                        }
                    }

                    //process meta attribute orders
                    Map<String, List<MetadataSetupReadBean>> groupedList = new HashMap<String, List<MetadataSetupReadBean>>();
                    for (MetadataSetupReadBean bean : beanList) {
                        if(groupedList.containsKey(bean.getEt())) {
                            groupedList.get(bean.getEt()).add(bean);
                        } else {
                            List<MetadataSetupReadBean> beanList = new ArrayList<MetadataSetupReadBean>();
                            beanList.add(bean);
                            groupedList.put(bean.getEt(), beanList);
                        }
                    }
                    beanList = new ArrayList<MetadataSetupReadBean>(beanList.size());
                    for(String et : groupedList.keySet()) {
                        List<MetadataSetupReadBean> currentList = groupedList.get(et);
                        Map<String, MetadataSetupReadBean> treeMap = new TreeMap<String, MetadataSetupReadBean>();
                        List<MetadataSetupReadBean> unordered = new ArrayList<MetadataSetupReadBean>();
                        for(MetadataSetupReadBean bean : currentList) {
                            String order = bean.getOrder();
                            if(order==null || order.trim().length()==0) {
                                unordered.add(bean);
                            } else {
                                if(treeMap.containsKey(order)) {
                                    throw new DuplicatedOrderException("Meta Attribute Orders are duplicated!");
                                } else {
                                    treeMap.put(order, bean);
                                }
                            }
                        }

                        int newPosition = 1;
                        for(MetadataSetupReadBean bean : treeMap.values()) {
                            bean.setOrder(String.valueOf(newPosition++));
                            beanList.add(bean);
                        }
                        for(MetadataSetupReadBean bean : unordered) {
                            bean.setOrder(String.valueOf(newPosition++));
                            beanList.add(bean);
                        }
                    }

                    Map<String, SampleMetaAttribute> existingSmaMap = this.getSmaMap(loadingProject.getProjectId());
                    Map<String, ProjectMetaAttribute> existingPmaMap = this.getPmaMap(loadingProject.getProjectId());

                    List<EventMetaAttribute> emaList = new ArrayList<EventMetaAttribute>();
                    List<ProjectMetaAttribute> pmaList = new ArrayList<ProjectMetaAttribute>();
                    List<SampleMetaAttribute> smaList = new ArrayList<SampleMetaAttribute>();
                    for (MetadataSetupReadBean bean : beanList) {
                        EventMetaAttribute ema;
                        boolean isNewOrModified = true;

                        if(existingEmaMap.containsKey(bean.getEt()) && existingEmaMap.get(bean.getEt()).containsKey(bean.getName())) {
                            //updates existing EMA
                            ema = existingEmaMap.get(bean.getEt()).get(bean.getName());
                            //skips unchanged EMA
                            if(this.isUnchanged(bean, ema) && bean.getSampleRequiredDB()==ema.getSampleRequiredDB()
                                    && (ema.getOrder()!=null && ema.getOrder().equals(Integer.parseInt(bean.getOrder())))) {
                                isNewOrModified = false;
                            }
                        } else { //creates new EMA
                            ema = new EventMetaAttribute();
                            ema.setProjectId(loadingProject.getProjectId());
                            ema.setAttributeName(bean.getName());
                        }

                        //only cares for new or modified EMA
                        if(isNewOrModified) {
                            //sets EMA values
                            this.setMAValues(ema,
                                    bean.getActiveDB(), bean.getRequiredDB(),bean.getDesc(),
                                    bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                            ema.setEventName(bean.getEt());
                            ema.setSampleRequiredDB(bean.getSampleRequiredDB());
                            ema.setOrder(Integer.parseInt(bean.getOrder()));
                            emaList.add(ema);
                        }

                        //updates PMA or SMA associated with current EMA
                        if(existingPmaMap.containsKey(bean.getName())) {
                            ProjectMetaAttribute pma = existingPmaMap.get(bean.getName());
                            this.setMAValues(pma,
                                    bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                    bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                            pmaList.add(pma);
                        } else {
                            //handles Project Metadata checkbox by adding new project meta attribute
                            if(bean.getProjectMetaDB()) {
                                ProjectMetaAttribute newPma = new ProjectMetaAttribute();
                                newPma.setProjectName(loadingProject.getProjectName());
                                newPma.setProjectId(loadingProject.getProjectId());
                                newPma.setAttributeName(bean.getName());
                                this.setMAValues(newPma,
                                        bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                        bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                                pmaList.add(newPma);
                            }
                        }
                        if(existingSmaMap.containsKey(bean.getName())) {
                            SampleMetaAttribute sma = existingSmaMap.get(bean.getName());
                            this.setMAValues(sma,
                                    bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                    bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                            smaList.add(sma);
                        } else {
                            //handles Sample Metadata checkbox by adding new sample meta attribute
                            if(bean.getSampleMetaDB()) {
                                SampleMetaAttribute newSma = new SampleMetaAttribute();
                                newSma.setProjectName(loadingProject.getProjectName());
                                newSma.setProjectId(loadingProject.getProjectId());
                                newSma.setAttributeName(bean.getName());
                                this.setMAValues(newSma,
                                        bean.getActiveDB(), bean.getRequiredDB(), bean.getDesc(),
                                        bean.getOptions(), bean.getLabel(), bean.getOntology(), loadingProject.getProjectName());
                                smaList.add(newSma);
                            }
                        }
                    }
                    psewt.loadEventMetaAttributes(emaList);
                    if(pmaList.size()>0)
                        psewt.loadProjectMetaAttributes(pmaList);
                    if(smaList.size()>0)
                        psewt.loadSampleMetaAttributes(smaList);
                }
                projectId = null;
                beanList = null;
            }
            returnValue = SUCCESS;

        } catch(Exception ex) {
            logger.error("Exception in MetadataSetup : " + ex.toString());
            ex.printStackTrace();
            if( ex.getClass() == ForbiddenResourceException.class ) {
                addActionError( Constants.DENIED_USER_EDIT_MESSAGE );
                return Constants.FORBIDDEN_ACTION_RESPONSE;
            } else if( ex.getClass() == ForbiddenResourceException.class ) {
                addActionError( Constants.DENIED_USER_EDIT_MESSAGE );
                return LOGIN;
            } else if( ex.getClass() == ParseException.class ) {
                addActionError( Constants.INVALID_DATE_MESSAGE );
            } else if( ex.getClass() == DuplicatedOrderException.class ) {
                addActionError( "Error while processing meta attribute positions. Check for any duplicated position values." );
            } else {
                addActionError( "Error while adding or updating metadata." );
            }

            try {
                if(tx!=null)
                    tx.rollback();
            } catch (SystemException se) {
                addActionError("Transaction Error! Use Help menu or contact the administrator.");
            }
        } finally {
            try {
                if(tx !=null && tx.getStatus() != Status.STATUS_NO_TRANSACTION)
                    tx.commit();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return returnValue;
    }

    private boolean isUnchanged(MetadataSetupReadBean b1, MetaAttributeModelBean b2) {
        return b1.getActiveDB()==b2.getActiveDB()
                && b1.getRequiredDB()==b2.getRequiredDB()
                && (b1.getDesc()!=null && b1.getDesc().equals(b2.getDesc()))
                && (b1.getOptions()!=null && b1.getOptions().equals(b2.getOptions()))
                && (b1.getLabel()!=null && b1.getLabel().equals(b2.getLabel()))
                && (b1.getOntology()!=null && b1.getOntology().equals(b2.getOntology()));
    }

    private void setMAValues(MetaAttributeModelBean b,
                             Integer active, Integer required,
                             String desc, String options, String label, String ontology, String projectName) {
        b.setActiveDB(active);
        b.setRequiredDB(required);
        b.setDesc(desc);
        b.setOptions(options);
        b.setLabel(label);
        b.setOntology(ontology);
        b.setProjectName(projectName);
    }

    private List<EventMetaAttribute> updateExistingEMA(List<EventMetaAttribute> emas, MetadataSetupReadBean bean, String projectName) {
        List<EventMetaAttribute> emaList = new ArrayList<EventMetaAttribute>();
        if(emas!=null && emas.size()>0) {
            for(EventMetaAttribute ema : emas) {
                if(!this.isUnchanged(bean, ema)) {
                    this.setMAValues(ema, bean.getActiveDB(), bean.getRequiredDB(),
                            bean.getDesc(), bean.getOptions(), bean.getLabel(), bean.getOntology(), projectName);
                    emaList.add(ema);
                }
            }
        }
        return emaList;
    }

    private Map<String, ProjectMetaAttribute> getPmaMap(Long projectId) throws Exception {
        List<ProjectMetaAttribute> existingPmaList = psept.getProjectMetaAttributes(projectId);
        Map<String, ProjectMetaAttribute> exsitingPmaMap = new HashMap<String, ProjectMetaAttribute>();
        for(ProjectMetaAttribute pma : existingPmaList) {
            exsitingPmaMap.put(pma.getAttributeName(), pma);
        }
        return exsitingPmaMap;
    }

    private Map<String, SampleMetaAttribute> getSmaMap(Long projectId) throws Exception {
        List<SampleMetaAttribute> existingSmaList = psept.getSampleMetaAttributes(projectId);
        Map<String, SampleMetaAttribute> exsitingSmaMap = new HashMap<String, SampleMetaAttribute>();
        for(SampleMetaAttribute sma : existingSmaList) {
            exsitingSmaMap.put(sma.getAttributeName(), sma);
        }
        return exsitingSmaMap;
    }

    private Map<String, List<EventMetaAttribute>> getEmaMap(List<EventMetaAttribute> emaList) {
        Map<String, List<EventMetaAttribute>> emas = new HashMap<String, List<EventMetaAttribute>>();
        for(EventMetaAttribute ema : emaList) {
            String attributeName = ema.getLookupValue().getName();
            if(emas.containsKey(attributeName)) {
                emas.get(attributeName).add(ema);
            } else {
                List<EventMetaAttribute> subEmas = new ArrayList<EventMetaAttribute>();
                subEmas.add(ema);
                emas.put(attributeName, subEmas);
            }
        }
        return emas;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public String getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(String projectNames) {
        this.projectNames = projectNames;
    }

    public List<MetadataSetupReadBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<MetadataSetupReadBean> beanList) {
        this.beanList = beanList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLerror() {
        return lerror;
    }

    public void setLerror(String lerror) {
        this.lerror = lerror;
    }

    public String getEmsg() {
        return emsg;
    }

    public void setEmsg(String emsg) {
        this.emsg = emsg;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    private class DuplicatedOrderException extends Exception {
        public DuplicatedOrderException( String message ) {
            super( message ) ;
        }
    }
}
