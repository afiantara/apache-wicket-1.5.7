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
package org.apache.wicket.markup.html;

import org.apache.wicket.Application;
import org.apache.wicket.WicketTestCase;
import org.junit.Test;

/**
 * @author Juergen Donnerstag
 */
public class SecurePackageResourceGuardTest extends WicketTestCase
{
	/**
	 * 
	 */
	@Test
	public void accept()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.setAllowAccessToWebInfResources(false);
		guard.addPattern("+*.gif");
		assertTrue(guard.accept(Application.class, "test.gif"));
		assertTrue(guard.accept(Application.class, "mydir/test.gif"));
		assertTrue(guard.accept(Application.class, "/root/mydir/test.gif"));
		assertTrue(guard.accept(Application.class, "../test.gif"));
		assertTrue(guard.accept(Application.class, "../../test.gif"));

		// web-inf (root package)
		assertFalse(guard.accept(Application.class, "../../../test.gif"));
		guard.setAllowAccessToWebInfResources(true);
		assertTrue(guard.accept(Application.class, "../../../test.gif"));

		boolean hit = false;
		try
		{
			// you can not go below root
			assertTrue(guard.accept(Application.class, "../../../../test.gif"));
		}
		catch (IllegalArgumentException ex)
		{
			hit = true;
		}
		assertTrue("Expected an IllegalArgumentException", hit);
	}

	/**
	 * 
	 */
	@Test
	public void acceptAbsolutePath()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.addPattern("+*.gif");
		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("/root/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void fileOnly()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.setAllowAccessToWebInfResources(true);
		guard.addPattern("+**.gif");
		guard.addPattern("+*.gif*");
		guard.addPattern("+*.gi*");
		guard.addPattern("+test*.gif");

		assertTrue(guard.acceptAbsolutePath("test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("/root/mydir/test.gif"));

		assertTrue(guard.acceptAbsolutePath("test.giX"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gifABCD"));
		assertTrue(guard.acceptAbsolutePath("mydir/testXXX.gif"));

		guard.addPattern("-**/testA.gif");
		assertFalse(guard.acceptAbsolutePath("mydir/testA.gif"));
	}

	@Test
	public void fileOnly_relative_allowed()
	{
		// ".." is allowed as long as we have parent folder placeholder set in resource settings
		tester.getApplication().getResourceSettings().setParentFolderPlaceholder("::");
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.addPattern("+test*.gif");
		assertTrue(guard.acceptAbsolutePath("../test.gif"));
	}

	@Test
	public void fileOnly_relative_not_allowed()
	{
		// ".." is allowed as long as we have parent folder placeholder set in resource settings
		tester.getApplication().getResourceSettings().setParentFolderPlaceholder(null);
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.addPattern("+test*.gif");
		assertFalse(guard.acceptAbsolutePath("../test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void withDirectory()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+mydir/*/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void one()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+mydir/**/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void two()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+*my*dir*/*/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("mydirXX/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("AAmydirXX/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("myBBdirXX/dir2/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void three()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+mydir**/*X/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("mydirAA/dir2X/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydirAA/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2X/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void four()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+mydir/**/xxx/**/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/xxx/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx/yyy/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir1/xxx/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir1/dir2/xxx/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir1/xxx/dir3/xxx.gif"));

		assertFalse(guard.acceptAbsolutePath("mydir/dir2/aaa/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/aaa/yyy/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir1/aaa/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir1/dir2/aaa/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir1/aaa/dir3/test.gif"));

		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void five()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+/**/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("/mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("/mydir/dir2/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void six()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.setAllowAccessToWebInfResources(true);
		guard.addPattern("+**/*.gif");

		assertTrue(guard.acceptAbsolutePath("test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void seven()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+*/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertTrue(guard.acceptAbsolutePath("mydir/test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/dir2/dir3/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/test.gif"));
	}

	/**
	 * 
	 */
	@Test
	public void eight()
	{
		SecurePackageResourceGuard guard = new SecurePackageResourceGuard();
		guard.getPattern().clear();
		guard.addPattern("+/*/*.gif");

		assertFalse(guard.acceptAbsolutePath("test.gif"));
		assertFalse(guard.acceptAbsolutePath("mydir/test.gif"));
		assertTrue(guard.acceptAbsolutePath("/mydir/test.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/dir2/xxx.gif"));
		assertFalse(guard.acceptAbsolutePath("/mydir/dir2/dir3/xxx.gif"));
	}
}
