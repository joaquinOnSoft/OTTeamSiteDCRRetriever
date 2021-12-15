package com.opentext.teamsite.sc.retriever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interwoven.livesite.content.ContentService;
import com.interwoven.livesite.model.EndUserSite;
import com.interwoven.livesite.runtime.RequestContext;
import com.interwoven.livesite.spring.web.WebApplicationContextUtils;
import com.interwoven.wcm.lscs.Client;


@ExtendWith(MockitoExtension.class)
public class TestOTMMRetriever {
	@Mock 
	EndUserSite endUserSite;	
	@Mock
	com.interwoven.wcm.rules.engine.base.RuleExecDef ruleExecDef;
	@Mock
	HttpServletResponse response;
	@Mock
	RequestContext context;
	@Mock
	ContentService contentService;
	@Mock
	Client client;
	@Mock 
	WebApplicationContextUtils webApplicationContextUtils; 	
	
	/**
	 * @see https://www.vogella.com/tutorials/Mockito/article.html
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void retrieveAllAssetsOfACollectionByName() {
		assertNotNull(context);
		when(ContentService.getInstance().getContentClient(context)).thenReturn(client);
		when(context.getSite()).thenReturn(endUserSite);
		when(context.getSite().getBranch()).thenReturn("main");
        when(context.isPreview()).thenReturn(false);
        when(context.isRuntime()).thenReturn(false);
		when(context.getParameterString("collectionName")).thenReturn("Auckland");
        
        
        OTMMRetriever retriever = new OTMMRetriever();
        Document doc = retriever.retrieveAllAssetsOfACollectionByName(context);
        assertNotNull(doc);
        assertEquals("", doc.asXML());
	}
}
