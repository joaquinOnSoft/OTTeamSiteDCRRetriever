package com.opentext.teamsite.sc.api.otmm.util;


import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.opentext.teamsite.sc.api.otmm.beans.OTMMCollection;

public class XMLUtil {
	static final Logger logger = Logger.getLogger(XMLUtil.class);

	/**
	 * 
	 * @param otmmCollection
	 * @return
	 * @see https://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser
	 * @see https://howtodoinjava.com/java/xml/xml-to-string-write-xml-file/
	 */
	public static String otmmCollectionToXML(Map<String, OTMMCollection> otmmCollections) {
		String xmlString = null;
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			//add elements to Document
            Element rootElement = doc.createElement("Collections");
            //append root element to document
            doc.appendChild(rootElement);

            OTMMCollection collection = null;
            for (String collectionId : otmmCollections.keySet()) {
            	collection = otmmCollections.get(collectionId);
            	
            	Element collectionElement = doc.createElement("collection");
            	//append first child element to root element
            	collectionElement.appendChild(createNode(doc, collectionElement, "id", collection.getId()));            
            	collectionElement.appendChild(createNode(doc, collectionElement, "name", collection.getName()));
                collectionElement.appendChild(createNode(doc, collectionElement, "ownerName", collection.getOwnerName()));
                
                rootElement.appendChild(collectionElement);                
			}
            
            //A character stream that collects its output in a string buffer, 
            //which can then be used to construct a string.
            StringWriter writer = new StringWriter();
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //transform document to string 
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            xmlString = writer.getBuffer().toString();  
		} catch (Exception e) {
			logger.error("", e);
		}

		return xmlString;
	}
	
    //utility method to create text node
    private static Node createNode(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }	
}
