package com.opentext.teamsite.sc.retriever;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;

import com.interwoven.livesite.content.ContentService;
import com.interwoven.livesite.runtime.RequestContext;
import com.interwoven.wcm.lscs.Client;
import com.opentext.teamsite.sc.api.otmm.OTMMAPIHelper;
import com.opentext.teamsite.sc.api.otmm.beans.OTMMAsset;
import com.opentext.teamsite.sc.api.otmm.util.XMLUtil;

public class OTMMRetriever extends AbstractRetriever {

	/**
	 * Get the assets included in an OTMM collection
	 * 
	 * These are the parameters supported in TeamSite configuration:
	 * 	- collection: (Mandatory) Collection name
	 *  - url: (Optional) OTMM URL
	 *  - user: (Optional) OTMM user
	 *  - password: (Optional) OTMM password
	 * @param context - Request context
	 * @return XML Document that contains the assets included in an OTMM collection
	 */
	@SuppressWarnings("deprecation")
	public Document retrieveAllAssetsOfACollectionByName(RequestContext context) {				
		String url = null; 
		String user = null;
		String password = null;
		String collectionName = null; 		
		Document doc = null;
				
		try {
			Client client = ContentService.getInstance().getContentClient(context);
			String projectName = context.getSite().getBranch();
			client.setProject(projectName);

			logger.info("Client created.  Project name: " + projectName);		
			
			if (context.isPreview() || isEdit(context)) {
				logger.info("Is preview");

				String contextName = context.getSite().getArea();
				client.setContextString(contextName);			
			}	
	
			collectionName = context.getParameterString("collectionName");
			logger.info("collectionName: " + collectionName);
			
			url = context.getParameterString("url");
			user = context.getParameterString("user");
			password = context.getParameterString("password");			
		} catch (Exception ex) {
			logger.error("Recovering component parameters: ", ex);
		}
		
		if(collectionName != null) {
			if(url == null ||  user == null || password == null) {
				//load a properties file from class path
				Properties prop = new Properties();
				try {
					prop.load(OTMMRetriever.class.getClassLoader().getResourceAsStream("otmm-api.properties"));
				} catch (IOException e) {
					logger.error("Get properties file from classpath: ", e);
				}
				
				if(prop.contains("url")) {
					url = prop.getProperty("url");
				}
				if(prop.contains("user")) {
					user = prop.getProperty("user");
				}
				if(prop.contains("password")) {
					password = prop.getProperty("password");
				}				
			}
			
			if(url != null &&  user != null && password != null) {
				OTMMAPIHelper apiHelper = new OTMMAPIHelper(url, user, password);
				
				List<OTMMAsset> assets = apiHelper.retrieveAllAssetsOfACollectionByName(collectionName);
				doc = XMLUtil.assetsToDoc(assets);
			}
			
			if(doc != null) {
				logger.info("doc: " + doc.asXML());
			}
		}
				
		return doc;		
	}
}
