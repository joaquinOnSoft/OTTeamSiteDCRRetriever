package com.opentext.teamsite.sc.api.otmm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opentext.teamsite.sc.api.otmm.beans.OTMMCollection;

public class TestXMLUtil {
	private static final String XML_COLLECTION = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"+
			"<Collections>\r\n" + 
			"    <collection>\r\n" +
			"        <id>37565d42b090cae4774605bd1c9dd85c75063ad5</id>\r\n" +
			"        <name>Auckland</name>\r\n" +
			"        <ownerName>admin, otmm</ownerName>\r\n" +
			"    </collection>\r\n" +
			"</Collections>\r\n";
	
	@Test
	void otmmCollectionToXML() {
		OTMMCollection col = new OTMMCollection("37565d42b090cae4774605bd1c9dd85c75063ad5", "Auckland");
		col.setOwnerName("admin, otmm");
		
		Map<String, OTMMCollection> otmmCollections = new HashMap<String, OTMMCollection>();
		otmmCollections.put("37565d42b090cae4774605bd1c9dd85c75063ad5", col);
		
		String xml = XMLUtil.otmmCollectionToXML(otmmCollections);
		assertNotNull(xml);
		assertEquals(XML_COLLECTION, xml);
	}
}
