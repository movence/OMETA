var openBtn = "images/dataTables/details_open.png",
    closeBtn = "images/dataTables/details_close.png",
    subrow_html='<div><table cellpadding="6" cellspacing="0">$d$</table></div>';

var sDT, //sample detail table
    eDT; //event detail table

//dataTables functions
$.fn.dataTableExt.afnFiltering.push(
    function( oSettings, aData, iDataIndex ) {
      var fromDate = $('#fromDate').val(), toDate = $('#toDate').val();
      if ( (fromDate == "" && toDate == "" ) || typeof fromDate === "undefined" || typeof toDate === "undefined" ) {
        return true;
      }
      var iMin = parseInt(fromDate===''?'0':fromDate.split("-").join("")),
          iMax = parseInt(toDate===''?'0':toDate.split("-").join("")),
          iDate = parseInt(aData[3].split("-").join(""));
      if ((iMin===0 && iDate<=iMax) || (iMin<=iDate && 0===iMax) || (iMin<=iDate && iDate<=iMax))
        return true;
      else
        return false;
    }
);
$.fn.dataTableExt.oApi.fnFilterOnReturn = function (oSettings) {
  var _settings = this, anControl = $('input', _settings.fnSettings().aanFeatures.f);
  $('img[id^="dosearch_'+oSettings.sTableId+'"]').live('click', function(e) {
    if(anControl.val()!=null && anControl.val().trim()!=='')
      _settings.fnFilter(anControl.val());
  });

  _settings.each(function (i) {
    $.fn.dataTableExt.iApiIndex = i;
    anControl.unbind('keyup').bind('keypress', function (e) {
      if (e.which == 13) {
        $.fn.dataTableExt.iApiIndex = i;
        _settings.fnFilter(anControl.val());
      }
    });
    return this;
  });
  return _settings;
};
$.fn.dataTableExt.oApi.fnNewAjax = function (oSettings, sNewSource) {
  if (sNewSource != null) {
    oSettings.sAjaxSource = sNewSource;
  }
  $('input', this.fnSettings().aanFeatures.f).val('');
  oSettings.oPreviousSearch.sSearch='';
  this.fnDraw();
};

var _page = {
      change: {
        project: function(projectId, sampleId) {
          if(_page.get.project(projectId) == true) {
            _page.get.sample(projectId);
            createSampleDataTable();
            _page.change.sample(projectId, sampleId?sampleId:0)
            $("#editProjectBtn, #editSampleBtn").attr("disabled", ($("#editable").val()!=1));
          } else {
            //reset
            $("#_sampleSelect").html(vs.empty);
            $("tbody#projectTableBody").empty();
            if(sDT) { //} && eDT) {
              sDT.fnNewAjax("eventDetailAjax.action?type=sdt&projectId=0");
              // eDT.fnNewAjax("eventDetailAjax.action?type=sdt&projectId=0");
            }
            $("#editProjectBtn, #editSampleBtn").attr("disabled", "true");
            utils.error.add(utils.error.message.permission);
          }
        },
        sample: function(projectId, sampleId) {
          _page.get.sdt(projectId, sampleId);
          // _page.get.edt(projectId, sampleId, 0);
        },
        eventStatus: function(eventId) {
          gethtmlByType("ces", 0, 0, eventId);
        }
      },
      get: {
        project: function(projectId) {
          return gethtmlByType("project", projectId, 0, 0);
        },
        sample: function(projectId) {
          return gethtmlByType("sample", projectId, 0, 0);
        },
        sdt: function(projectId, sampleId) {
          $('#fromDate, #toDate').val('');
          sDT.fnNewAjax("eventDetailAjax.action?type=sdt&projectId="+projectId+"&sampleId="+sampleId+"&eventId=0");
        },
        edt: function(projectId, sampleId, eventId) {
          var dates = "&fd="+$('input[name="fromDate"]').val()+"&td="+$('input[name="toDate"]').val();
        }
      },
      edit: {
        project: function() {
          $('#eventDetailPage').append($('<input/>').attr({type: 'hidden', name: 'projectName', value: $('#_projectSelect:selected').text()}));
          $('#eventDetailPage').append($('<input/>').attr({type: 'hidden', name: 'eventName', value: 'ProjectUpdate'}));
          $('#eventDetailPage').append($('<input/>').attr({type: 'hidden', name: 'jobType', value: 'projectedit'}));
          this.submit('project');
        },
        sampleEvent: function() {
          var sampleIds = '';
          $('#sampleTableBody input[id^=sampleCB]:checked').each(function(i,v) {
            sampleIds += v.id.substr(v.id.indexOf('_') + 1) + ',';
          });
          if(sampleIds.length === 0) {
            utils.error.baloon("Please select sample to load or edit event.");
          } else {
            if (!$('input[name="ids"]').length) {
              $('#eventDetailPage').append($('<input/>').attr({type: 'hidden', name: 'ids'}).val(sampleIds));
            } else {
              $('input[name="ids"]').val(sampleIds);
            }
            this.submit('sample');
          }
        },
        submit: function(type) {
          if (!$('input[name="label"]').length){
            $('#eventDetailPage').append($('<input/>').attr({type: 'hidden', name: 'label', value: type}));
          }else{
            $('input[name="label"]').val(type);
          }
          $('#eventDetailPage').attr('target', '_blank');
          $('#eventDetailPage').attr('action', 'eventLoader.action?filter=su').submit();
        }
      },
      button: {
        toggleSample: function() {
          $('#sampleTableDiv').toggle(400);
          buttonSwitch(null, 'sampleToggleImage');
        },
        showSample: function() {
          if(!$('#sampleTableDiv').is(":visible")) {
            this.toggleSample();
          }
        }
      }
    },
    buttonSwitch = function(node, name) {
      if(node==null) { node = document.getElementById(name); }
      if(node.src.match('details_close')){ node.src = openBtn; } else { node.src = closeBtn; }
    };


function comboBoxChanged(option, id) {
  if(id==='_projectSelect') {
    //document.getElementById('sampleTable').getElementsByTagName('img')[0].src = openBtn;
    if(option.value!=null && option.value!=0 && option.text!=null && option.text!='') {
      _page.change.project(option.value, 0);
      $('.ui-autocomplete-input').val('');
    } else {
      $("#_sampleSelect").html(vs.empty);
    }
  } else if(id==='_sampleSelect') {
    if(option.value!=null && option.text!=null && option.text!='') {
      _page.change.sample($('#_projectSelect').val(), option.value);
    }
  }
}

var headerList = [];

<!-- Generate html content using Ajax by type -->
function gethtmlByType(ajaxType, projectId, sampleId, eventId) {
  $("#loadingImg").show();
  var content = '', rtnVal = false;
  $.ajax({
    url: ajaxType === 'project' ? 'getproject.action' : 'sharedAjax.action',
    cache: false,
    async: ajaxType === 'project'?false:true,
    data: "type="+ajaxType+"&projectId="+projectId+"&sampleId="+sampleId+"&eventId="+eventId,
    success: function(html){
      if(html.aaData) {
        if(ajaxType == "project") {
          $(html.aaData).each(function(i1,v1) {
            if(v1 && i1 == 0) {
              $.each(v1, function(i2,v2) {
                if(v2) {
                  if(i2==="editable") { $("#editable").val(v2); }
                  else {
                    if(typeof v2 === "string" && v2.indexOf("http://") > -1) {
                      v2 = '<a href="#" onclick="window.open(&quot;' + v2 + '&quot;);">' + v2 + '</a>';
                    }
                    content += '<tr class="even"><td width="25%">'+i2+'</td><td>'+v2+'</td></tr>';
                  }
                }
              });
              rtnVal = true;
            } else if(v1 && i1 == 1){  //create dynamic table header based on sample meta attributes
              var $header = $("#sampleTableHeader tr");
              $header.empty();
              headerList = [];

              $header.append("<th class='tableHeaderStyle'>Sample Name</th>")
                  .append("<th class='tableHeaderStyle'>Parent</th>")
                  .append("<th class='tableHeaderStyle'>User</th>")
                  .append("<th class='tableHeaderStyle'>Date</th>");

              $.each(v1, function(i2,v2) {
                if(v2)
                  $header.append("<th class='tableHeaderStyle'>" + v2 + "</th>");
                headerList.push(v2);
              });
            }
          });
          $("tbody#projectTableBody").html(content);
        } else if(ajaxType === "sample") {
          var list = vs.alloption;
          $(html.aaData).each(function(i1,v1) {
            if(v1) { list += vs.vnoption.replace("$v$",v1.id).replace("$n$",v1.name); }
          });
          if(sampleId == null || sampleId == 0) { $("#_sampleSelect").html(list); }

        } else if(ajaxType == "ces") { //change event status
          if(html.aaData && html.aaData[0]==='success') {
            _page.get.edt($('#_projectSelect').val(), $('#_sampleSelect').val(), 0);
            rtnVal = true;
          }
        }
      }
    }
  });
  return rtnVal;
}

function createSampleDataTable(){
  //Destroy current data table to create new one
  if(typeof sDT !== 'undefined') {
    sDT.fnDestroy();
    $("#sampleTableBody").empty();
  }

  <!-- SAMPLE TABLE -->
  sDT = $("#sampleTable").dataTable({
    "sDom": '<"datatable_top"lf><"datatable_table"rt><"datatable_bottom"ip>',
    "bProcessing": true,
    "bServerSide": true,
    "bDestroy": true,
    "sPaginationType": "full_numbers",
    "sAjaxSource": "",
    "iDeferLoading": 0, // no initial loading flag
    "fnServerData": function (sSource, aoData, fnCallback) {
      if(sSource!=='') {
        $.ajax({
          dataType: 'json',
          type: "POST",
          url: sSource,
          data: aoData,
          success: function(json) {
            if(json && json.aaData) {
              var rows = [];
              $.each(json.aaData, function(ri,rowData) {
                var row = [], attributes;
                row.push(
                    rowData.sampleName + "<input type='checkbox' style='margin-left:6px;' id='sampleCB_" + rowData.sample.sampleId + "'/>",
                    rowData.parentSampleName,
                    rowData.actor,
                    rowData.createdOn
                );
                if(rowData.attributes) {
                  for(var i=0; i < headerList.length; i++){
                    row.push(rowData.attributes[headerList[i]]);
                  }
                } else {
                  attributes = '<tr class="odd"><td colspan="6">No Data</td></tr>';
                  row.push(attributes);
                }

                rows.push(row);
              })
            }
            json.aaData = rows;
            fnCallback(json);

            $('#projectTableDivHeader').show();
            $('#sampleTableDivHeader').show();
            $('#sampleTableDiv').show();
            $("#loadingImg").hide();
          }
        });
      }
    },
    "aaSorting": [[3,'asc']],
    "bAutoWidth" : false,
    "aoColumnDefs": aoColumns()
  }).fnFilterOnReturn();
}

function aoColumns() {
  var ao = [];
  ao.push({"sWidth": "100px", "aTargets":[0]},
      {"sWidth": "100px", "aTargets":[1]},
      {"sWidth": "100px", "aTargets":[2]},
      {"sWidth": "100px", "aTargets":[3]}
  );
  for(var i=0; i<headerList.length; i++){
    var index = i+4;

    if(headerList[i].length < 15) {
      ao.push({"sWidth": "100px", "aTargets":[index]});
    } else if(headerList[i].length < 30){
      ao.push({"sWidth": "150px", "aTargets":[index]});
    } else{
      ao.push({"sWidth": "200px", "aTargets":[index]});
    }
  }

  return ao;
}