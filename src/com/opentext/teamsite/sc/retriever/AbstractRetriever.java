package com.opentext.teamsite.sc.retriever;

import org.apache.log4j.Logger;

import com.interwoven.livesite.runtime.RequestContext;

public class AbstractRetriever {

	protected static final Logger logger = Logger.getLogger(OTMMRetriever.class);

	public AbstractRetriever() {
		super();
	}

	protected boolean isEdit(RequestContext context) {
		return !context.isPreview() && !context.isRuntime();
	}

}