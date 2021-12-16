package com.opentext.teamsite.sc.dcr;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.interwoven.livesite.common.io.StreamUtil;
import com.interwoven.livesite.content.ContentService;
import com.interwoven.livesite.dom4j.Dom4jUtils;
import com.interwoven.livesite.runtime.RequestContext;
import com.interwoven.wcm.lscs.Client;
import com.interwoven.wcm.lscs.LSCSException;
import com.interwoven.wcm.lscs.LSCSIterator;

/**
 * @deprecated Since 20.4.5. Moved to com.opentext.teamsite.sc.retriever package
 * @see com.opentext.teamsite.sc.retriever.DCRRetriever 
 */
public class DCRRetriever {
	static final Logger logger = Logger.getLogger(DCRRetriever.class);

	private static final int MAX_RESULTS = 25;

	private String getQueryString(RequestContext context) {
		logger.info("Init getQueryString");

		/*
				@SuppressWarnings("deprecation")
				String queryString = context.getParameterString("documentQuery");
				logger.info("QueryString parameter: " + queryString);
				*/
		String queryString = "";
		try {
			//if(queryString == null || queryString.compareTo("") == 0) {								
				String contentCategory = context.getParameterString("contentCategory");
				logger.info("Content category: " + contentCategory);	

				String contentName = context.getParameterString("contentName");
				logger.info("Content Name: " + contentName);	

				//if(contentCategory != null && contentName != null) {
					StringBuilder query = new StringBuilder();
					query.append("q=TeamSite/Templating/DCR/Type:")
					.append(contentCategory)
					.append("/")
					.append(contentName);
					queryString = query.toString();
				//}
			//}
		} catch (Exception e) {
			logger.error("getQueryString: ", e);
		}

		logger.info("QUERY STRING: " + queryString);

		return queryString;
	}

	private int getMaxResultsParam(RequestContext context) {	
		int intMaxResults = MAX_RESULTS;
		
		String strMaxResults = context.getParameterString("maxResults");
		logger.info("Max Result: " + strMaxResults);	

		if(strMaxResults != null && strMaxResults.compareTo("") != 0) {
			try {
				intMaxResults = Integer.parseInt(strMaxResults);
			}
			catch (NumberFormatException e) {
				logger.error("Max Result is not a valid number. Using default max. value", e);	
			}
		}
		else {
			logger.info("Using default max. value");
		}
		
		return intMaxResults;
	}

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
	public Document getDCRAssets(RequestContext context) {
		Document doc = Dom4jUtils.newDocument();
		Element rootElement = doc.addElement("root");
		Element resultsElement = rootElement.addElement("results");		

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
	
			String queryString = getQueryString(context);
			logger.info("QUERY STRING: " + queryString);
			int maxResults = getMaxResultsParam(context);
			
			LSCSIterator<com.interwoven.wcm.lscs.Document> iter = client.getDocuments(queryString, 0, maxResults);
			logger.info("# results: " + iter.getTotalSize());

			while (iter.hasNext()) {				
				com.interwoven.wcm.lscs.Document iterDoc = iter.next();
				logger.info("Result document path: " + iterDoc.getPath());
				resultsElement.add(lscsDocumentToXml(iterDoc, true).getRootElement());
			}			
		} catch (Exception ex) {
			logger.error("Get DRC assets: ", ex);
		}

		logger.info("doc: " + doc.asXML());

		return doc;			
	}

	private boolean isEdit(RequestContext context) {
		return !context.isPreview() && !context.isRuntime();
	}


	/**
	 * Get the `Content Item` that match the given Id.
	 * External component parameters:
	 * 	- id: Content Item identifier
	 * @param context - Request context
	 * @return XML Document to contains the content items 
	 * that match with the search criteria
	 */
	public Document getDCRAssetById(RequestContext context) {
		//TODO Refactor getDCRAssetById and getDCRAssets
		Document doc = Dom4jUtils.newDocument();
		Element rootElement = doc.addElement("root");
		Element resultsElement = rootElement.addElement("results");		

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
			
			@SuppressWarnings("deprecation")
			String id = context.getParameterString("id");
			logger.info("ID: " + id);
			
			com.interwoven.wcm.lscs.Document docFound = client.getDocumentById(id);

			resultsElement.add(lscsDocumentToXml(docFound, true).getRootElement());
		} catch (Exception ex) {
			logger.error("Get DRC asset by Id: ", ex);
		}

		logger.info("doc: " + doc.asXML());

		return doc;			
	}	

	/**
	 * Constructing document from String Object
	 * @param rs
	 * @return
	 * @throws FactoryConfigurationError
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private org.w3c.dom.Document toDocument(String rs)
			throws FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(rs);
		InputSource source = new InputSource(reader);
		return builder.parse(source);
	}	

	private org.dom4j.Document lscsDocumentToXml(com.interwoven.wcm.lscs.Document lscsDocument, boolean includeContent)
			throws LSCSException, IOException {
		Element xmlElement = DocumentHelper.createElement("document");
		org.dom4j.Document xmlDocument = DocumentHelper.createDocument(xmlElement);
		String content;
		xmlElement.addAttribute("id", lscsDocument.getId());
		xmlElement.addAttribute("path", lscsDocument.getPath());
		xmlElement.addAttribute("uri", lscsDocument.getContentURL());

		Element metadata = xmlElement.addElement("metadata");
		String[] metadataNames = lscsDocument.getAttributeNames();
		for (String metadataName : metadataNames) {
			metadata.addElement("field").addAttribute("name", metadataName).addText(lscsDocument.getAttribute(metadataName));
		}

		org.w3c.dom.Document w3DocumentContainer;
		org.dom4j.Document document;

		// This is needed until client.setIncludeContent(true); is fixed
		if (includeContent) {
			content = StreamUtil.read(lscsDocument.getContentStream());
			Element contentXmlElement = xmlElement.addElement("content");

			try {
				w3DocumentContainer = toDocument(content);
				org.dom4j.io.DOMReader reader = new DOMReader();
				document = reader.read(w3DocumentContainer);
				contentXmlElement.add(document.getRootElement());
			} catch (FactoryConfigurationError | ParserConfigurationException | SAXException e) {
				logger.info("Error parsing LSCS document: " + lscsDocument.getPath(), e);
			}
		}

		return xmlDocument;
	}	
}