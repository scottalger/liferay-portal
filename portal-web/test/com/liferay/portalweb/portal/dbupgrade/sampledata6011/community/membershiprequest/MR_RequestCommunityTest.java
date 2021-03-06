/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portalweb.portal.dbupgrade.sampledata6011.community.membershiprequest;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class MR_RequestCommunityTest extends BaseTestCase {
	public void testMR_RequestCommunity() throws Exception {
		selenium.open("/user/requestmembersn/home/");

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("link=Available Communities")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Available Communities",
			RuntimeVariables.replace("Available Communities"));
		selenium.waitForPageToLoad("30000");
		selenium.type("_29_name",
			RuntimeVariables.replace("Membership Request Community Name"));
		selenium.clickAt("//form/span/span[2]/span/input",
			RuntimeVariables.replace("Search"));
		selenium.waitForPageToLoad("30000");
		assertEquals(RuntimeVariables.replace("Request Membership"),
			selenium.getText("//span/a/span"));
		selenium.clickAt("//span/a/span",
			RuntimeVariables.replace("Request Membership"));
		selenium.waitForPageToLoad("30000");
		selenium.type("_29_comments",
			RuntimeVariables.replace(
				"Community Description comments request to join"));
		selenium.clickAt("//input[@value='Save']",
			RuntimeVariables.replace("Save"));
		selenium.waitForPageToLoad("30000");
		assertEquals(RuntimeVariables.replace(
				"Your request processed successfully."),
			selenium.getText("xPath=(//div[@class='portlet-msg-success'])[1]"));
		assertEquals(RuntimeVariables.replace(
				"Your request was sent. You will receive a reply by email."),
			selenium.getText("xPath=(//div[@class='portlet-msg-success'])[2]"));
	}
}