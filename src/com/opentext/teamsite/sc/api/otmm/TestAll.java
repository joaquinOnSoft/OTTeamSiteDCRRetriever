package com.opentext.teamsite.sc.api.otmm;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("com.opentext.teamsite.sc.api.otmm")
@ExcludeClassNamePatterns("^.*Abstract.*$")
public class TestAll {

}
