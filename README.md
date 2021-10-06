# OpenText TeamSite DCR retriever

## About TeamSite
OpenText™ TeamSite™ is an easy-to-use, modern website content management system that helps 
organizations create personalized and visually rich digital customer experiences optimized 
for any device, digital channel or context. It simplifies the entire process of managing 
content across all channels, including websites, mobile platforms, email, social, commerce, 
composite applications, collaboration sites and portals, to make delivering outstanding digital 
experiences easier. From a single interface, users can author, test, target and publish their 
content as well as manage rich media, design websites and create mobile applications.

## Create a Data Content Record (DCR)

## Create a custom component
Follow these steps to create a new component that use the class that we have created in this project:

   - Go to `CC Professional` and browse to `//tsbase/iwadmin/main/livesite/component/WORKAREA/shared`
   - Click on `File > New Folder`
   
   ![Click on 'File > New Folder'](images/teamsite-cc-professional-file-new-folder.png)
   
   - Set a name for your folder, `economy` in our example.
      
   ![Click on 'File > New Folder'](images/teamsite-cc-professional-file-new-folder-name.png)
   
   - Browse to the new folder `//tsbase/iwadmin/main/livesite/component/WORKAREA/shared/economy`
   - Click on `File > New Component`

   ![Click on 'File > New Folder'](images/teamsite-cc-professional-file-new-component.png)
   
   - Select `XSLT 2.0` and click on `Next`
   
   ![Click on 'File > New Folder'](images/teamsite-cc-professional-file-new-component-xslt-2-0.png)   

   - Set a name, e.g. *ECON-Market-DCR-Retrieve*
   - Select `Do not cache` on the **Cache Time** dropdown list
   
   ![Click on 'File > New Folder'](images/teamsite-cc-professional-file-new-component-pop-up.png)      
   
   - Scroll down until the section **Content XML** is visible
   - Copy this code into the **Content XML** text area:
   
```
<Data>
  <External>
    <Parameters>
      <Datum ID="DOCUMENT-QUERY" Name="documentQuery" Type="String">q=TeamSite/Templating/DCR/Type:Economy/Market</Datum>
    </Parameters>
    <Object Scope="local">com.opentext.teamsite.sc.dcr.DCRRetriever</Object>
    <Method>getDCRAssets</Method>
  </External>
</Data>
```

> **NOTE**: `com.opentext.teamsite.sc.dcr.DCRRetriever` is the class created by us in this project

## Environment setup 

### Deploy jar file

The `output` folder of this project contains a jar file with the classes to retrieve DCR using 
TeamSite's LSCS API (Live Site Content Site API).

#### Deploy for development/testing your component
Upload the jar file, `OTTeamSiteEconomy20.4.jar` in our example, to the `lib` folder of your environment:

``` 
	/usr/Interwoven/LiveSiteDisplayServices/runtime/web/WEB-INF/lib 
```

> **IMPORTANT**: This speed up the testing process, but the preview of the process won't work. 
> You'll need to publish the page that contains the component to see the results.

#### Deploy for real live (component tested and ready to use)
Upload the jar file, `OTTeamSiteEconomy20.4.jar` in our example, to the `lib` folder of your environment:

``` 
	/usr/Interwoven/TeamSite/local/config/lib/content_center/livesite_customer_src/lib 
```

```
	cd /usr/Interwoven/TeamSite/local/config/lib/content_center/livesite_customer_src
	./build.sh 	
```

### Enable log messages in log4j

Browse to the following folder:

```
	cd /usr/Interwoven/LiveSiteDisplayServices/runtime/web/WEB-INF/classes
```

Edit the `log4j.xml` file and add the following tag:

```
	<category name="com.opentext.teamsite.sc.dcr.DCRRetriever"><priority value="DEBUG"/></category>
```

> NOTE: `com.opentext.teamsite.sc.economy.DCRRetriever` is the class that we have created

### Restart Tomcat

Once you have deployed the jar, execute the following commands to restart Tomcat:

```
	cd /usr/Interwoven/LiveSiteDisplayServices/runtime/
	ls -l
	./run.linux.sh stop
```
	
Wait 2 minutes and run this command:
	
```	
	./run.linux.sh start
```

### Check you component logs

Execute the following commands in a terminal to see the logs of your component:

```
	cd /usr/Interwoven/LiveSiteDisplayServices/runtime/tomcat/logs/
	cat /dev/null > livesite.runtime.log
	clear
	tail -f livesite.runtime.log	
```

