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
package org.apache.wicket.extensions.markup.html.repeater.data.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.tester.DiffUtil;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class DataTableTest extends Assert
{
	/** Log for reporting. */
	private static final Logger log = LoggerFactory.getLogger(DataTableTest.class);

	private WicketTester tester;

	/**
	 * 
	 */
	@Before
	public void before()
	{
		tester = new WicketTester(new RepeaterApplication());
	}

	/**
	 * 
	 */
	@After
	public void after()
	{
		tester.destroy();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test_1() throws Exception
	{
		tester.startPage(DataTablePage.class);
		tester.assertRenderedPage(DataTablePage.class);

		String document = tester.getLastResponseAsString();
		int index = document.indexOf("<thead");
		assertTrue("Expected at least on <thead>", index != -1);
		index = document.indexOf("<thead", index + 1);
		assertTrue("There must be only one <thead>", index == -1);

		index = document.indexOf("<tbody");
		assertTrue("Expected at least on <tbody>", index != -1);
		index = document.indexOf("<tbody", index + 1);
		assertTrue("There must be only one <tbody>", index == -1);

		index = document.indexOf("<caption", index + 1);
		assertTrue("There must be not be <caption>", index == -1);

// log.error(document);
// log.error("==============================================");
// log.error("==============================================");
// log.error(removeFillers(document));

		String doc = removeFillers(document);
		DiffUtil.validatePage(doc, getClass(), "DataTablePage_ExpectedResult.html", true);
	}

	/**
	 * Tests that DataTable doesn't produce thead/tfoot if there are no top/bottom toolbars or if
	 * their children components are all invisible
	 */
	@Test
	public void testWicket3603()
	{
		PageParameters parameters = new PageParameters();
		parameters.add("empty", Boolean.TRUE);
		tester.startPage(Wicket3603Page.class, parameters);
// System.err.println(tester.getLastResponseAsString());
		Assert.assertTrue(tester.getLastResponseAsString().contains("thead"));
		Assert.assertTrue(tester.getLastResponseAsString().contains("tfoot"));

		parameters.set("empty", Boolean.FALSE);
		tester.startPage(Wicket3603Page.class);
// System.err.println(tester.getLastResponseAsString());
		Assert.assertFalse(tester.getLastResponseAsString().contains("thead"));
		Assert.assertFalse(tester.getLastResponseAsString().contains("tfoot"));
	}

	/**
	 * Tests that a {@link DataTable} with non-empty {@link DataTable#getCaptionModel()} will render
	 * &lt;caption&gt; element.
	 */
	@Test
	public void testWicket3886()
	{
		DataTablePage page = new DataTablePage()
		{
			@Override
			protected IModel<String> getCaptionModel()
			{
				return Model.of("Caption");
			}
		};

		tester.startPage(page);
		tester.assertRenderedPage(DataTablePage.class);

		String document = tester.getLastResponseAsString();
		int index = document.indexOf("<caption wicket:id=\"caption\">Caption</caption>");
		assertTrue("Caption must be rendered!", index > -1);

	}

	private String removeFillers(String doc)
	{
		doc = doc.replaceAll("(?s)<span .*?>.*?</span>", "<x/>");
		doc = doc.replaceAll("(?s)<div .*?>.*?</div>", "<x/>");
		doc = doc.replaceAll("(?s)<a .*?>.*?</a>", "<x/>");
		doc = doc.replaceAll("(?s)>\\s*?[\\n\\r]+\\s*?</", "><x/></");
		doc = doc.replaceAll("(?s)[\\n\\r]+\\s*?([\\n\\r]+)", "\r\n");
		doc = doc.replaceAll("(<x/>)+", "<x/>");

		return doc;
	}

	/**
	 * A page with a DataTable that either has items (tbody) or header and footer (thead/tfoot)
	 */
	public static class Wicket3603Page extends WebPage implements IMarkupResourceStreamProvider
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param parameters
		 */
		public Wicket3603Page(PageParameters parameters)
		{
			super(parameters);

			IDataProvider<Number> provider = new IDataProvider<Number>()
			{
				private static final long serialVersionUID = 1L;

				private List<Integer> items = Arrays.asList(1, 3, 5);

				public void detach()
				{
				}

				public Iterator<? extends Number> iterator(int first, int count)
				{
					StringValue emptyValue = getPageParameters().get("empty");
					return emptyValue.toBoolean() ? Collections.<Integer> emptyList().iterator()
						: items.iterator();
				}

				public int size()
				{
					StringValue emptyValue = getPageParameters().get("empty");
					return emptyValue.toBoolean() ? 0 : items.size();
				}

				public IModel<Number> model(Number object)
				{
					return Model.of(object);
				}
			};

			List<IColumn<Number>> columns = new ArrayList<IColumn<Number>>();
			columns.add(new PropertyColumn<Number>(Model.of("value"), "value"));

			DataTable<Number> table = new DataTable<Number>("table", columns, provider, 10);
			table.addBottomToolbar(new NoRecordsToolbar(table));
			table.addTopToolbar(new NoRecordsToolbar(table));
			add(table);
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream(
				"<html><body><table wicket:id='table'></table></body></html>");
		}

	}
}
