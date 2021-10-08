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

public class DCRRetriever {
	static final Logger logger = Logger.getLogger(DCRRetriever.class);
	
	private static final int MAX_RESULTS = 25;

	public Document getDCRAssets(RequestContext context) {
		Document doc = Dom4jUtils.newDocument();
		Element rootElement = doc.addElement("root");
		Element resultsElement = rootElement.addElement("results");		
		
		@SuppressWarnings("deprecation")
		String queryString = context.getParameterString("documentQuery");
		logger.info("QUERY STRING: " + queryString);

		try {
			Client client = ContentService.getInstance().getContentClient(context);
			String projectName = context.getSite().getBranch();
			client.setProject(projectName);

			logger.info("Client created.  Project name: " + projectName);			
			
			if (context.isPreview()) {
				logger.info("Is preview");

				String contextName = context.getSite().getArea();
				client.setContextString(contextName);			
			}			
			
			LSCSIterator<com.interwoven.wcm.lscs.Document> iter = client.getDocuments(queryString, 0, MAX_RESULTS);

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
	
	
	// Constructing document from String Object
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
