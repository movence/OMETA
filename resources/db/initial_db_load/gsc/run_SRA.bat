set DBG=-Xnoagent -Djava.compiler=NONE -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
REM set DBG=

set CP=..\..\..\dist\ProjectWebsitesApp.jar
set LOC=Y_pestis

REM java %DBG% -classpath %CP% org.jcvi.project_websites.engine.LoadingEngine -inputfile "%LOC%\Short Read Archive_EventMetaAttributes.tsv"
java %DBG% -classpath %CP% org.jcvi.project_websites.engine.LoadingEngine -inputfile "%LOC%\Short Read Archive_EventAttributes.tsv"