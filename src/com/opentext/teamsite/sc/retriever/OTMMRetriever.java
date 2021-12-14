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

public class OTMMRetriever extends AbstractRetriever {

	/**
	 * Get the `Content Items` of a given Category/Name or that match the given LSCS query.
	 * These are the parameters supported in TeamSite configuration:
	 * 	- documentQuery: LSCA query string to be use. Some examples:
	 * 			q=type:datasheet 
	 * 			q=category:products&format=json
	 * 	NOTE: If this parameter is specified in TeamSite the other parameters, 
	 * 	`contentCategory` and `contentName`, and will be ignored
	 * 	- contentCategory: Content template category
	 * 	- contentName: Content template name
	 *  - maxResults: Maximum number of result to be returned. 25 by default
	 * @param context - Request context
	 * @return XML Document to contains the content items 
	 * that match with the search criteria
	 */
	@SuppressWarnings("deprecation")
	public Document retrieveAllAssetsOfACollectionByName(RequestContext context) {
		Document doc = null;
		String collectionName = null;
		
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
			
		} catch (Exception ex) {
			logger.error("Recovering component parameters: ", ex);
		}
		
		if(collectionName != null) {
			//load a properties file from class path, inside static method
			Properties prop = new Properties();
			try {
				prop.load(OTMMRetriever.class.getClassLoader().getResourceAsStream("otmm-api.properties"));
			} catch (IOException e) {
				logger.error("Get properties file from classpath: ", e);
			}
						
			OTMMAPIHelper apiHelper = new OTMMAPIHelper(prop.getProperty("url"), 
					prop.getProperty("user"), 
					prop.getProperty("password"));
			
			List<OTMMAsset> assets = apiHelper.retrieveAllAssetsOfACollectionByName("Auckland");
			//TODO  convert a org.dom4j.Document
			//doc = XMLUtil.assetsToDoc(assets);
			
			logger.info("doc: " + doc.asXML());
		}
				
		return doc;		
	}
}
