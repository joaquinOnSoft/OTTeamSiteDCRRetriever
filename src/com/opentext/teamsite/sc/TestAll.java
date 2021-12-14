package com.opentext.teamsite.sc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
	"com.opentext.teamsite.sc.api.otmm",
	"com.opentext.teamsite.sc.api.otmm.util"
	})
@ExcludeClassNamePatterns("^.*Abstract.*$")
public class TestAll {

}
