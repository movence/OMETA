package org.jcvi.ometa.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.jcvi.ometa.utils.Constants;
import org.jtc.common.util.property.PropertyHelper;

import java.util.Properties;

/**
 * User: hkim
 * Date: 7/9/13
 * Time: 8:30 AM
 * org.jcvi.ometa.action
 * This is a generic action for opening a popup. "t" parameter decides which popup this class should open.
 */
public class Popup extends ActionSupport {
    private Logger logger = Logger.getLogger(Popup.class);

    private String t; //type

    private String projectName;
    private String eventName;
    private String sampleNae;

    private Long projectId;
    private Long eventId;
    private Long sampleId;

    private String ids;

    public Popup() {
    }

    public String run() {
        String rtnVal = SUCCESS;

        if(t.equals("sel_t")) {
            rtnVal = "SELECT_TEMPLATE";
        } else if(t.startsWith("projectDetails")) {
            Properties props = PropertyHelper.getHostnameProperties(Constants.PROPERTIES_FILE_NAME);
            this.ids = props.getProperty(Constants.CONFIG_PROJECT_POPUP_DISPLAY_ATTRS);
            rtnVal = "PROJECT_DETAIL" + (t.endsWith("_pop") ? "_POP" : "");
        }
        return rtnVal;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSampleNae() {
        return sampleNae;
    }

    public void setSampleNae(String sampleNae) {
        this.sampleNae = sampleNae;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
