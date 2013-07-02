<%--
  ~ Copyright J. Craig Venter Institute, 2013
  ~
  ~ The creation of this program was supported by J. Craig Venter Institute
  ~ and National Institute for Allergy and Infectious Diseases (NIAID),
  ~ Contract number HHSN272200900007C.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page isELIgnored="false" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <link rel="stylesheet" href="style/dataTables.css" />
        <link rel="stylesheet" href="style/cupertino/jquery-ui-1.8.18.custom.css" />
        <style>
            .loadRadio { margin-left: 10px; margin-right: 3px; }
            #gridBody .ui-autocomplete-input { width: 150px; }
        </style>

    </head>

<body>

    <s:form id="eventLoaderPage" name="eventLoaderPage"
            namespace="/"
            action="eventLoader"
            method="post" theme="simple"
            enctype="multipart/form-data">
        <s:hidden name="jobType" id="jobType"/>
        <s:hidden name="label" id="label"/>
        <s:hidden name="eventName" id="eventName" />
        <s:hidden name="projectName" id="projectName" />
        <s:include value="TopMenu.jsp" />
        <div id="HeaderPane" style="margin:15px 0 0 30px;">
            <table cellpadding="0" cellspacing="0" border="0">
                <tr><td class="panelHeader">Event Loader</td></tr>
                <tr>
                    <td>
                        <div id="errorMessagesPanel" style="margin-top:15px;"></div>
                    </td>
                </tr>
            </table>
        </div>
        <div id="middle_content_template">
            <div id="statusTableDiv">
                <div id="tableTop">
                    <table id="ddTable">
                        <tr>
                            <td><strong>Load Type</strong></td>
                            <td style="padding-left:10px">
                                <input type="radio" name="loadType" class="loadRadio" value="form"><strong>Form</strong></input>
                                <input type="radio" name="loadType" class="loadRadio" value="grid"><strong>Grid</strong></input>
                                <input type="radio" name="loadType" class="loadRadio" value="file"><strong>File</strong></input>
                            </td>
                        </tr>
                        <tr height="10px"/>
                        <tr>
                            <div id="projectDropBox">
                                <td>Project</td>
                                <td style="padding-left:10px" class="ui-combobox">
                                    <s:select id="_projectSelect" list="projectList" name="projectId" headerKey="0" headerValue=""
                                        listValue="projectName" listKey="projectId" required="true"/>
                                </td>
                            </div>
                        </tr>
                        <tr>
                            <div id="eventDropBox">
                                <td>Event</td>
                                <td style="padding-left:10px" class="ui-combobox">
                                    <s:select id="_eventSelect" list="#{'0':''}" name="eventId" required="true" disabled="true"/>
                                </td>
                            </div>
                        </tr>
                        <tr class="sampleSelectTr">
                            <td id="sampleNameLabel">Sample</td>
                            <td style="padding-left:10px" class="ui-combobox">
                                <s:select id="_sampleSelect" list="#{'0':''}" name="sampleName" required="true" disabled="true"/>
                            </td>
                        </tr>
                    </table>
                </div>
                 <div id="projectDetailInputDiv">
                    <div style="margin:25px 10px 0 0;">
                        <h1 class="csc-firstHeader">Project Information</h1>
                    </div>
                    <div id="projectDetailSubDiv">
                        <table>
                            <tr>
                                <td align="right" id="loadProjectNameLabel">Project Name</td>
                                <td class="requiredField"><input type="text" id="_projectName" name="loadingProject.projectName" size="33px"/></td>
                            </tr>
                            <tr class="gappedTr">
                                <td align="right">Public</td>
                                <td class="requiredField">
                                    <s:select id="_isProjectPublic" list="#{0:'No', 1:'Yes'}" name="loadingProject.isPublic" required="true" />
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="sampleDetailInputDiv">
                    <div style="margin:25px 10px 0 0;">
                        <h1 class="csc-firstHeader">Sample Information</h1>
                    </div>
                    <div id="sampleDetailSubDiv">
                        <table>
                            <tr>
                                <td align="right" id="loadSampleNameLabel">Sample Name</td>
                                <td class="requiredField"><input type="text" id="_sampleName" name="loadingSample.sampleName" size="33px"/></td>
                            </tr>
                            <tr id="parentSelectTr" class="gappedTr">
                                <td align="right" id="parentSampleLabel">Parent Sample</td>
                                <td class="ui-combobox">
                                    <s:select id="_parentSampleSelect" list="#{'0':''}" name="loadingSample.parentSampleName" required="true"/>
                                </td>
                            </tr>
                            <tr class="gappedTr">
                                <td align="right">Public</td>
                                <td class="requiredField">
                                    <s:select id="_isSamplePublic" list="#{0:'No', 1:'Yes'}" name="loadingSample.isPublic" required="true" />
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                <div style="margin:25px 10px 0 0;">
                    <div style="float:left;">
                        <h1 class="csc-firstHeader">Event Attributes</h1>
                    </div>
                    <div style="font-size:0.9em;padding-top:5px;margin-left:135px;padding-left:50px">
                        Required Fields= <img style="vertical-align:bottom;" src="images/required.png"/>
                    </div>
                </div>
                <div id="attributeInputDiv" style="margin:10px 0;">
                    <s:if test="beanList != null && beanList.size() > 0">
                        <table>
                            <s:iterator value="beanList" var="attrName" status="stat">
                                <tr class="gappedTr">
                                    <s:hidden name="beanList[%{#stat.index}].projectName" />
                                    <s:hidden name="beanList[%{#stat.index}].sampleName" />
                                    <s:hidden name="beanList[%{#stat.index}].attributeName" />
                                    <td align="right"><s:property value="attributeName"/></td>
                                    <td><s:textfield name="beanList[%{#stat.index}].attributeValue"/></td>
                                </tr>
                            </s:iterator>
                        </table>
                    </s:if>
                </div>
                <div id="gridInputDiv" style="margin:25px 10px 0 0 ;">
                    <table name="eventTable" id="eventTable" class="contenttable">
                        <thead id="gridHeader"></thead>
                        <tbody id="gridBody"></tbody>
                    </table>
                </div>
                <div id="fileInputDiv" style="margin:25px 10px 0 0 ;">
                    <table>
                        <tr>
                            <td>Loader CSV File</td>
                            <td>
                                <s:file name="csvUploadFile" id="csvUploadFile" cssStyle="margin:0 0 0 14px;" size="75px" />
                            </td>
                        </tr>
                    </table>
                </div>

                <div id="submitDiv" style="margin:15px 10px 5px 0;width:100%;">
                    <input type="button" onclick="javascript:button.submit_event();" id="eventLoadButton" value="Submit Event" disabled="true"/>
                    <input type="button" onclick="javascript:button.add_event();" id="gridAddLineBtn" value="Add Event Line" style="display:none;"/>
                    <input type="button" onclick="javascript:button.template();" id="templateButton" value="Download Template"/>
                    <input type="button" onclick="javascript:button.clear_form();" value="Clear" />
                <div>
            </div>
        </div>
    </s:form>

    <script>
        var eventAttributes = [], gridLineCount=0, avDic= {};
        var pBeanHtml='<input type="hidden" value="$pn$" name="$lt$projectName"/>',
            anBeanHtml='<input type="hidden" value="$an$" name="$lt$attributeName"/>',
            avTextHtml='<input type="text" id="$an$" name="$lt$attributeValue" value="$val$"/>',
            avFileHtml='<input type="file" id="$an$" name="$lt$upload"/>',
            avSelectHtml='<select id="$an$" name="$lt$attributeValue">$opts$</select>',
            avSampleSelectHtml='<td><select id="$si$" name="$sn$">$opts$</select></td>',
            avHtml, sample_options;

        var _utils = {
            labeling: function(l) {
                var headerT;
                if(l==='SampleReceipt') {
                    headerT = 'Sample Receipt';
                    l='Sample';
                } else if(l.indexOf("Metadata")>0) {
                    headerT = l;
                    var l_arr = l.split(' ');
                    l=l_arr[0];
                } else {
                    headerT = 'Register ' + l;
                }

                $('.panelHeader').html(headerT);
                $('.csc-firstHeader').html(l+' Information');
                $('#sampleNameLabel').html(l+' ID');
            }, 
            makeAjax: function(u,d,p,cb) {
                $.ajax({
                    url:u,
                    cache: false,
                    async: false,
                    data: d,
                    success: function(data){ cb(data,p); },
                    fail: function() { alert("Ajax Process has failed."); }
                });
            },
            addDD: function(n,p,l) {
                $(
                    '<tr>'+
                    '    <div id="'+n.replace(' ', '')+'Dropbox">'+
                    '        <td>'+n.charAt(0).toUpperCase()+n.substring(1)+' ID</td>'+
                    '        <td style="padding-left:10px" class="ui-combobox" id="td_'+n+'Select"><select id="a_'+n+'Select"></select></td>'+
                    '    </div>'+
                    '</tr>'
                ).insertBefore($('table#ddTable tr:last-child'));
                if(l===1) {
                    this.makeAjax('sharedAjax.action', 'type=Sample&projectId='+p+'&sampleLevel='+l, n, callbacks.added);
                }
                $('#a_'+n+'Select').combobox();
            },
            addGridRows: function(pn,en) {
                //have at least 5 event lines for the grid view
                for(var _rows=$('#gridBody > tr').length;_rows<5;_rows++) {
                    button.add_event(pn,en);
                }   
            },
            showPS:function(et) {
                if(utils.checkSR(et)) { // triggers sample loader
                    $('#sampleDetailInputDiv').show();    
                } else if(utils.checkPR(et)) {
                    $('#projectDetailInputDiv').show();   
                } 
            },
            hidePS: function() {
                $('#sampleDetailInputDiv, #projectDetailInputDiv').hide();
            }
        },
        callbacks = {
            added: function(data,id) {
                var list = vs.empty;
                $.each(data.aaData, function(i1,v1) {
                    if(i1!=null && v1!=null) {
                        list += vs.vnoption.replace('$v$',v1.id).replace('$n$',v1.name);
                    }
                });
                $("#a_"+id+"Select").append(list);
                return;
            },
            sample: function(data) {
                var list = vs.empty;
                $.each(data.aaData, function(i1,v1) {
                    if(i1!=null && v1!=null) {
                        list += vs.vvoption.replace(/\\$v\\$/g, v1.name);
                    }
                });
                $("#_sampleSelect, #_parentSampleSelect").html(list);

                sample_options=list;
                return;
            },
            event: function(data) {
                var list = vs.empty;
                $.each(data, function(i1,v1) {
                    if(v1 != null) {
                        $.each(v1, function(i2,v2) {
                            if(v2 != null && v2.lookupValueId != null && v2.name != null) {
                                list += vs.vnoption.replace('$v$',v2.lookupValueId).replace('$n$',v2.name);
                            }
                        });
                    }
                });

                $("#_eventSelect").html(list);
                return;
            },
            meta: function(data, en) {
                var content = "", count= 0;
                eventAttributes = []; gridLineCount=0; avDic={};
                $.each(data.aaData, function(i1, _ma) { //build attributes input section
                    if(_ma != null) {
                        if(_ma != null && _ma.eventMetaAttributeId != null && _ma.projectId != null) {
                            eventAttributes.push(_ma); //stores event attributes for other uses

                            content += '<tr class="gappedTr">'+
                                    pBeanHtml.replace("$pn$",utils.getProjectName()).replace("$lt$", "beanList["+count+"].")+ //project name for bean
                                    anBeanHtml.replace("$an$",_ma.lookupValue.name).replace("$lt$", "beanList["+count+"].")+ //attribute name
                                    '<td align="right" '+(_ma.desc && _ma.desc!==''?'class="hasTooltip" title="'+_ma.desc+'"':'')+'>'+
                                    (_ma.label!=null&&_ma.label!==''?_ma.label:_ma.lookupValue.name) +  //attribute name or label
                                    '</td>';
                            var subcon='';
                            if(_ma.options!=null&&_ma.options!=='') { 
                                var optcon='';
                                if(_ma.options.indexOf(';')>0) { //select box for option values
                                    var opts='';
                                    $.each(_ma.options.split(';'), function(_opti,_opt) {
                                        opts+=vs.vnoption.replace("$v$",_opt).replace("$n$",(_opt==='1'?'Yes':_opt==='0'?'No':_opt));
                                    });
                                    optcon=avSelectHtml.replace("$an$",_ma.lookupValue.name+"_$id$").replace("$opts$",opts);
                                } else { //set default text input box option values without ";"
                                    optcon=avTextHtml.replace("$an$",_ma.lookupValue.name+" $id$").replace("$val$", _ma.options); 
                                }
                                subcon+=optcon;
                            } else {  
                                if(_ma.lookupValue.dataType==='file') { //file 
                                    subcon+=avFileHtml.replace("$an$",_ma.lookupValue.name+" $id$");
                                } else { //text input
                                    subcon+=avTextHtml.replace("$an$",_ma.lookupValue.name+" $id$");
                                }
                            }
                            avDic[_ma.lookupValue.name]=subcon; //store html contents with its attribute name for later use in adding row
                            subcon='<td'+(_ma.requiredDB?' class="requiredField"':'')+'>'+subcon.replace("$id$", count).replace("$lt$","beanList["+count+"].")+'</td>'; //update ID(count)

                            content+=subcon+'</tr>';
                            count++;
                        }
                    }
                });
                $("div#attributeInputDiv").html("<table>"+content.replace(/\\$val\\$/g, '')+"</table>");
                utils.initDatePicker(); //initialise data fields

                //add table headers for gird view
                content='';
                if(utils.checkNP(en)) {
                    if(!utils.checkSR(en)) {
                        content+='<th class="tableHeaderNoBG requiredField">Sample</th>';
                    } else {
                        content+='<th class="tableHeaderNoBG requiredField">Sample Name</th><th class="tableHeaderNoBG">Parent Sample</th><th class="tableHeaderNoBG requiredField">Public</th>';
                    }
                } else {
                    if(utils.checkPR(en)) {
                        content+='<th class="tableHeaderNoBG requiredField">Project Name</th><th class="tableHeaderNoBG requiredField">Public</th>';    
                    }
                }
                //add headers for event meta attributes
                $.each(eventAttributes, function(i1, v1) {
                    content+='<th class="tableHeaderNoBG' +
                            //(v1.desc&&v1.desc!==''?' hasTooltip':'') +
                            (v1.requiredDB?' requiredField':'') + '" ' +
                            (v1.desc&&v1.desc!==''?'title="'+v1.desc+'"':'') +'>' +
                            v1.lookupValue.name
                        '</th>';
                });
                $('thead#gridHeader').html('<tr>'+content+'</tr>');

                _utils.addGridRows(null, en);
                return;
            }
        },
        changes = {
            project: function(projectId) {
                $("#_sampleSelect").attr("disabled", false);
                _utils.hidePS();

                var label=$('input:hidden#label').val(), level=0;
                switch(label) {
                    case 'SR': label=3; break;
                    case 'HM':case 'FM':case 'PM':level=1; break;
                    default: level=0;
                }
                if(label!=='FM' && label!=='SM') {
                    _utils.makeAjax('sharedAjax.action', 'type=Sample&projectId='+projectId+'&sampleLevel='+level, null, callbacks.sample);
                }
                $("#_eventSelect").attr("disabled", false);
                _utils.makeAjax('sharedAjax.action', 'type=Event&projectId='+projectId+'&eventTypeId=0', null, callbacks.event);
                $('#_sampleSelect+.ui-autocomplete-input, #_eventSelect+.ui-autocomplete-input').val('');
            },
            sample: function(){ /*nothing to do when sample changes*/ },
            event: function(et) {
                _utils.hidePS();

                $("#eventLoadButton").attr("disabled", false);
                if(utils.getLoadType()==='form') {
                    _utils.showPS(et);  
                } 
                _utils.makeAjax('sharedAjax.action', 'type=ea&projectName='+utils.getProjectName()+'&eventName='+et, et, callbacks.meta);
            }
        };

        $(document).ready(function() {
            //customization for labels
            var label=$('input:hidden#label').val();
            switch(label) {
                case 'SR': label="SampleReceipt"; break;
                case 'PM': label="Patient Metadata"; break;
                case 'HM': label="Household Metadata"; break;
                case 'FM': label="Family Metadata"; break;
                case "SM": label="Sample Metadata"; break;
                default: label=null;
            }
            if(label!=null&&label.length>0) {
                $.each($('#_projectSelect').find('option'), function(i1,v1) {
                    if(v1.text===paramP || v1.text.indexOf(paramP)>=0) {
                        $('#_projectSelect').val(parseInt(v1.value));
                        changes.project(v1.value);
                        $.each($('#_eventSelect').find('option'), function(i2,v2) {
                            if(v2.text.indexOf(label)!=-1) {
                                $('#_eventSelect').val(parseInt(v2.value));
                                changes.event(v2.text);
                                $('#_eventSelect').attr('disabled', true);
                            }
                        });
                    }
                });
                if(label==='Family Metadata') {
                    _utils.addDD('patient', $('#_projectSelect').val(), 1);
                } else if(label==='Sample Metadata') {
                    _utils.addDD('patient', $('#_projectSelect').val(), 1);
                    _utils.addDD('family', $('#_projectSelect').val(), 2);
                }

                _utils.labeling(label);
            }

            $('select[id$="Select"]').combobox();

            //retrieve existing values for preload
            var _oldProjectId = '${projectId}',
                _oldSampleName = '${sampleName}',
                _oldEventName = '${eventName}',
                _oldJobType = '${jobType}';

            //load type radio button change event
            $('input[name="loadType"]').change(function() {
                $('div[id$="InputDiv"], #gridAddLineBtn, .sampleSelectTr').hide();
                utils.preSelect('_sampleSelect', '');
                var _selectedType = $(this).val();
                if(_selectedType==='grid') {
                    $('#gridInputDiv, #gridAddLineBtn').show();
                    _utils.addGridRows(utils.getProjectName(), utils.getEventName());
                } else if(_selectedType==='file') {
                    $('#fileInputDiv').show();
                } else {
                    _utils.showPS();
                    $('#attributeInputDiv, .sampleSelectTr').show();
                }
            });

            //preselect load type radio button
            var rtnJobType = (_oldJobType===''||_oldJobType==='insert'||_oldJobType==='template'?'form':_oldJobType);
            $('input[name="loadType"][value='+rtnJobType+']').attr('checked', true);
            $('input[name="loadType"]:checked').change();

            //preload project and event type
            if(_oldProjectId) {
                changes.project(_oldProjectId);
                utils.preSelect("_eventSelect", _oldEventName);
                changes.event(_oldEventName);
                if(_oldSampleName!=='') {
                    utils.preSelect("_sampleSelect", _oldSampleName);
                }
            }

            //preload grid body
            if('${gridList}'!=='') {
                //remove any existing dom elements
                gridLineCount = 0;
                $('#gridBody').html('');
                $('[name^="gridList"]').remove();
                <s:iterator value="gridList" var="gbean" status="gstat">
                    var gridLine={}, beans=[];
                    <s:iterator value="beanList" var="fbean" status="fstat">
                        beans.push(["${fbean.attributeName}", "${attributeValue}"]);
                    </s:iterator>
                    gridLine['pn']="${gbean.projectName}";
                    gridLine['pp']="${gbean.projectPublic}";
                    gridLine['sn']="${gbean.sampleName}";
                    gridLine['psn']="${gbean.parentSampleName}";
                    gridLine['sp']="${gbean.samplePublic}";
                    gridLine['beans']=beans;
                    button.add_event(null,null,gridLine);
                </s:iterator>
                _utils.addGridRows(null,_oldEventName);
            } else if('${beanList}'!=='') {
                //remove any existing dom elements
                $('[name^="beanList"]').remove();
                <s:iterator value="beanList" var="bean" status="bstat">
                    $("[name='beanList[${bstat.count-1}].attributeValue']").val("${bean.attributeValue}");
                </s:iterator>    
            }

            <s:if test="hasActionErrors()">
                utils.error.add('<s:iterator value="actionErrors"><s:property/><br/></s:iterator>');
            </s:if>
        });

        function comboBoxChanged(option, id) {
            if(id==='_projectSelect') {
                button.clear_attr();
                $("#eventLoadButton").attr("disabled", true);
                $("#_eventSelect").html(vs.empty);
                if(option.value!=null && option.value!=0 && option.text!=null && option.text!='') {
                    changes.project(option.value);
                } else {
                    $("#_sampleSelect").html(vs.empty);
                    $("#_eventSelect").html(vs.empty);
                }
            } else if(id==='_eventSelect') {
                button.clear_attr();
                if(option.value && option.value!=0 && option.text && option.text!='') {
                    changes.event(option.text);
                }
            } else if(id==='_sampleSelect') {
                if(option.value && option.value!=0 && option.text && option.text!='' && $('#_eventSelect').val() != 0) {
                    _utils.makeAjax(
                        'sharedAjax.action',
                        'type=ea&projectName='+utils.getProjectName()+'&eventName='+utils.getEventName(),
                        utils.getEventName(),
                        callbacks.meta
                    );
                }
            } else if(id==='a_patientSelect') {
                var label=$('input:hidden#label').val()
                if(label==='FM')
                    _utils.makeAjax(
                        'sharedAjax.action',
                        'type=Sample&projectId='+$('#_projectSelect').val()+'&sampleId='+option.value+'&sampleLevel=2',
                        null,
                        callbacks.sample
                    );
                else if(label==='SM')
                    _utils.makeAjax(
                        'sharedAjax.action',
                        'type=Sample&projectId='+$('#_projectSelect').val()+'&sampleId='+option.value+'&sampleLevel=2',
                        'family',
                        callbacks.added
                    );
            } else if(id==='a_familySelect') {
                _utils.makeAjax(
                    'sharedAjax.action',
                    'type=Sample&projectId='+$('#_projectSelect').val()+'&sampleId='+option.value+'&sampleLevel=3',
                    null,
                    callbacks.sample
                );
            }
        }

        var button = {
            submit_event: function() {
                var loadType = $('input[name="loadType"]:radio:checked').val();
                this.submit_form(loadType==='form'?'insert':loadType);
            },
            template: function() {
                this.submit_form("template");
            },
            submit_form: function(type) {
                var $selectedProject = $("#_projectSelect option:selected"),
                    $selectedSample = $("#_sampleSelect option:selected"),
                    $selectedEvent = $("#_eventSelect option:selected");
                if($selectedProject.val()!==0)
                    $("#projectName").val($selectedProject.text());
                if($selectedSample.val()!==0)
                    $("#sampleName").val($selectedSample.text());
                if($selectedEvent.val()!==0)
                    $("#eventName").val($selectedEvent.text());
                $("#jobType").val(type);
                $('form#eventLoaderPage').submit();
            },
            add_event: function(pn,en,dict) { //add event to grid view
                var _pn=pn?pn:utils.getProjectName(),
                    _en=en?en:utils.getEventName(),
                    content='';
                if(_pn&&_en) {
                    if(utils.checkNP(en)){
                        if(!utils.checkSR(en)) {
                            //add sample select box for other events
                            content+=avSampleSelectHtml.replace("$si$", "_sampleSelect"+gridLineCount).replace("$sn$", "gridList["+gridLineCount+"].sampleName").replace("$opts$",sample_options);
                        } else {
                            //add sample information fields for project registration
                            content=content
                                +'<td><input type="text" name="gridList['+gridLineCount+'].sampleName" id="_sampleName'+gridLineCount+'" /></td>'
                                +avSampleSelectHtml.replace("$si$", "_parentSelect"+gridLineCount)
                                    .replace("$sn$", "gridList["+gridLineCount+"].parentSampleName")
                                    .replace("$opts$",sample_options)
                                +'<td><select name="gridList['+gridLineCount+'].samplePublic">'+vs.ynoption+'</select></td>';
                        }
                    } else {
                        //add project information fields for project registration
                        if(utils.checkPR(en)) {
                            content=content
                                +'<td><input type="text" name="gridList['+gridLineCount+'].projectName" id="_projectName'+gridLineCount+'" /></td>'
                                +'<td><select name="gridList['+gridLineCount+'].projectPublic">'+vs.ynoption+'</select></td>';
                        }
                    }
                    var ltVal, beans=((dict&&dict['beans'])?dict['beans']:null);
                    //add event meta attribute fields
                    $.each(eventAttributes, function(i1, v1) {
                        ltVal="gridList["+gridLineCount+"].beanList["+i1+"].";
                        content+=pBeanHtml.replace("$pn$", _pn).replace("$lt$", ltVal)+
                                anBeanHtml.replace("$an$",(beans?beans[i1][0]:v1.lookupValue.name)).replace("$lt$", ltVal)+
                                '<td>'+avDic[v1.lookupValue.name].replace(/\\$id\\$/g, gridLineCount).replace("$lt$",ltVal).replace('$val$', (beans?beans[i1][1]:''))+'</td>';
                    });
                    //add to grid body
                    $('#gridBody').append('<tr class="borderBottom">'+content+'</tr>');

                    utils.initDatePicker();
                    $('select[id^="_"]').combobox();


                    if(dict) { 
                        //load existing data if any
                        if(utils.checkSR()) {
                            $('input:text#_sampleName'+gridLineCount).val(dict['sn']);
                            utils.preSelect('_parentSelect'+gridLineCount, dict['psn']);  
                            $('select[name="gridList['+gridLineCount+'].samplePublic"]').val(dict['sp']);  
                        } else if(utils.checkPR()) {
                            $('#_projectName'+gridLineCount).val(dict['pn']);
                            $('select[name="gridList['+gridLineCount+'].projectPublic"]').val(dict['pp']);
                        } else {
                            utils.preSelect('_sampleSelect'+gridLineCount, dict['sn']);    
                        }
                    }

                    //set minimum width for date and autocomplete TDs
                    $('#gridBody .hasDatepicker').parent('td').attr('style', 'min-width:163px !important;');
                    $('#gridBody .ui-autocomplete-input').parent('td').attr('style', 'min-width:172px !important;');

                    gridLineCount++;
                }
            },
            clear_form: function() {
                $("#_projectSelect").val(0);
                utils.preSelect("_projectSelect", 0);
                changes.project(0);
                $('#_parentProjectSelect, #_parentSampleSelect').combobox();
                this.clear_attr();
            },
            clear_attr: function() {
                $("#attributeInputDiv, #gridHeader, #gridBody").html('');
                $('[name^="beanList"], [name^="gridList"]').remove();
                $('#_projectName, #_parentProjectSelect~input, #_sampleName, #_parentSampleSelect~input').val('');
            }
        };
    </script>
</body>

</html>