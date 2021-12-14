package com.opentext.teamsite.sc.api.otmm.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.opentext.teamsite.sc.api.otmm.beans.OTMMAsset;
import com.opentext.teamsite.sc.api.otmm.beans.OTMMCollection;

public class XMLUtilOld {
	static final Logger logger = Logger.getLogger(XMLUtilOld.class);

	/**
	 * Create a XML document (org.w3c.dom.Document object)
	 * 
	 * @return
	 */
	private static Document createDocument() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			logger.error("createDocument: ", e);
		}

		return doc;
	}

	/**
	 * Utility method to create text node
	 * 
	 * @param doc     - XML document
	 * @param element - XML element
	 * @param name    - element name
	 * @param value-  element value
	 * @return
	 */
	private static Node createNode(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	public static String docToXML(Document doc) {
		String xmlString = null;
		try {
			// A character stream that collects its output in a string buffer,
			// which can then be used to construct a string.
			StringWriter writer = new StringWriter();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			// for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// transform document to string
			transformer.transform(new DOMSource(doc), new StreamResult(writer));

			xmlString = writer.getBuffer().toString();
		} catch (IllegalArgumentException | TransformerFactoryConfigurationError | TransformerException e) {
			logger.error("docToXML: ", e);
		}
		return xmlString;
	}

	/**
	 * 
	 * @param otmmCollection
	 * @return
	 * @see https://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser
	 * @see https://howtodoinjava.com/java/xml/xml-to-string-write-xml-file/
	 */
	public static Document assetsToDoc(List<OTMMAsset> assets) {
		Document doc = createDocument();
		if (doc != null) {
			try {
				// add elements to Document
				Element rootElement = doc.createElement("collection");
				// append root element to document
				doc.appendChild(rootElement);

				for (OTMMAsset asset : assets) {

					Element assetElement = doc.createElement("asset");
					// append first child element to root element
					assetElement.appendChild(createNode(doc, assetElement, "id", asset.getId()));
					assetElement.appendChild(createNode(doc, assetElement, "name", asset.getName()));
					assetElement.appendChild(createNode(doc, assetElement, "mimeType", asset.getMimeType()));
					assetElement.appendChild(
							createNode(doc, assetElement, "deliveryServiceURL", asset.getDeliveryServiceURL()));

					rootElement.appendChild(assetElement);
				}
			} catch (Exception e) {
				logger.error("assetsToXML: ", e);
			}
		}

		return doc;
	}

	/**
	 * Convert a list of OTMM assets to a XML string
	 * 
	 * @param assets - OTMM assets
	 * @return XML string
	 */
	public static String assetsToXML(List<OTMMAsset> assets) {
		String xmlString = null;

		Document doc = assetsToDoc(assets);
		if (doc != null) {
			xmlString = docToXML(doc);
		}

		return xmlString;
	}

	/**
	 * 
	 * @param otmmCollections
	 * @return
	 * @see https://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser
	 * @see https://howtodoinjava.com/java/xml/xml-to-string-write-xml-file/
	 */
	public static Document otmmCollectionsToDoc(Map<String, OTMMCollection> otmmCollections) {
		Document doc = createDocument();
		if (doc != null) {
			try {
				// add elements to Document
				Element rootElement = doc.createElement("Collections");
				// append root element to document
				doc.appendChild(rootElement);

				OTMMCollection collection = null;
				for (String collectionId : otmmCollections.keySet()) {
					collection = otmmCollections.get(collectionId);

					Element collectionElement = doc.createElement("collection");
					// append first child element to root element
					collectionElement.appendChild(createNode(doc, collectionElement, "id", collection.getId()));
					collectionElement.appendChild(createNode(doc, collectionElement, "name", collection.getName()));
					collectionElement
							.appendChild(createNode(doc, collectionElement, "ownerName", collection.getOwnerName()));

					rootElement.appendChild(collectionElement);
				}

			} catch (Exception e) {
				logger.error("otmmCollectionsToXML: ", e);
			}
		}

		return doc;
	}

	/**
	 * 
	 * @param otmmCollections
	 * @return
	 */
	public static String otmmCollectionsToXML(Map<String, OTMMCollection> otmmCollections) {
		String xmlString = null;

		Document doc = otmmCollectionsToDoc(otmmCollections);
		if (doc != null) {
			xmlString = docToXML(doc);
		}

		return xmlString;
	}

	public static Document xmlToDocument(String xml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			builder = dbf.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(xml)));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error(e);
		}

		return doc;
	}
}
