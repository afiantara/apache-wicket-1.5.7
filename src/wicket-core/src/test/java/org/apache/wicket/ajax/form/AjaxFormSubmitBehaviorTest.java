/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.ajax.form;

import org.apache.wicket.WicketTestCase;
import org.junit.Test;

/**
 * Test case for WICKET-1743
 * 
 * @see <a href="https://issues.apache.org/jira/browse/WICKET-1743">WICKET-1743</a>
 */
public class AjaxFormSubmitBehaviorTest extends WicketTestCase
{
	/**	 */
	@Test
	public void ajaxFormSubmitBehavior()
	{
		// start and render the test page
		tester.startPage(AjaxFormSubmitBehaviorTestPage.class);
		// assert rendered page class
		tester.assertRenderedPage(AjaxFormSubmitBehaviorTestPage.class);
		// assert rendered page class
		AjaxFormSubmitBehaviorTestPage homePage = (AjaxFormSubmitBehaviorTestPage)tester.getLastRenderedPage();
		TestForm testForm = homePage.getForm();
		tester.executeAjaxEvent(testForm.getTextField(), "onchange");
		assertTrue(testForm.isSubmitedByAjaxBehavior());
	}
}