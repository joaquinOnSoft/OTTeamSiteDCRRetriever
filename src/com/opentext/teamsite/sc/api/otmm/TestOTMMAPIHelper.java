package com.opentext.teamsite.sc.api.otmm;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.opentext.teamsite.sc.api.otmm.beans.OTMMAsset;

class TestOTMMAPIHelper extends TestAbstractOTMMAPI {
	
	@Test
	void retrieveAllAssetsOfACollectionByName() {
		OTMMAPIHelper apiHelper = new OTMMAPIHelper(prop.getProperty("url"), 
				prop.getProperty("user"), 
				prop.getProperty("password"));
		
		List<OTMMAsset> assets = apiHelper.retrieveAllAssetsOfACollectionByName("Auckland");
		
		assertNotNull(assets);
		assertTrue(assets.size() > 0);
	}
	
}
