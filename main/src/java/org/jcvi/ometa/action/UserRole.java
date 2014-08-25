package org.jcvi.ometa.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.jcvi.ometa.bean_interface.ProjectSampleEventWritebackBusiness;
import org.jcvi.ometa.db_interface.ReadBeanPersister;
import org.jcvi.ometa.model.Actor;
import org.jcvi.ometa.model.ActorGroup;
import org.jcvi.ometa.utils.Constants;
import org.jcvi.ometa.utils.UploadActionDelegate;
import org.jtc.common.util.property.PropertyHelper;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.List;
import java.util.Properties;

/**
 * User: movence
 * Date: 4/11/14
 * Time: 3:38 PM
 * org.jcvi.ometa.action
 */
public class UserRole extends ActionSupport {
    private Logger logger = Logger.getLogger(UserRole.class);

    private List<Actor> actors;
    private Long actorId;
    private String groupNames;

    private List<ActorGroup> actorGroups;

    private ReadBeanPersister readPersister;
    ProjectSampleEventWritebackBusiness psewt;

    public UserRole() {
        Properties props = PropertyHelper.getHostnameProperties(Constants.PROPERTIES_FILE_NAME);
        readPersister = new ReadBeanPersister(props);
    }

    public String execute() {
        String rtnVal = INPUT;
        UserTransaction tx = null;
        try {
            if(actorId != null && groupNames != null && !groupNames.isEmpty()) {
                tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                tx.begin();

                UploadActionDelegate udelegate = new UploadActionDelegate();
                psewt = udelegate.initializeBusinessObject(logger, psewt);

                rtnVal = SUCCESS;
                addActionMessage("Actor Roles have been updated.");
            } else {
                actors = readPersister.getAllActor();
            }
        } catch (Exception ex) {
            rtnVal = ERROR;
            try {
                if(tx!=null)
                    tx.rollback();
            } catch (SystemException se) {
                ex = se;
            }
            addActionError(ex.toString());
        } finally {
            try {
                if(tx !=null && tx.getStatus() != Status.STATUS_NO_TRANSACTION)
                    tx.commit();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return rtnVal;
    }

    public String getActorGroup() {
        try {
            actorGroups = readPersister.getActorGroup(actorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "json";
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    public List<ActorGroup> getActorGroups() {
        return actorGroups;
    }

    public void setActorGroups(List<ActorGroup> actorGroups) {
        this.actorGroups = actorGroups;
    }
}
