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
package org.apache.wicket.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.value.ValueMap;

/**
 * Load properties from properties file. The encoding of the file must be ISO 8859-1.
 * 
 * @see java.util.Properties
 * 
 * @author Juergen Donnerstag
 */
public class IsoPropertiesFilePropertiesLoader implements IPropertiesLoader
{
	private final String extension;

	/**
	 * Construct.
	 * 
	 * @param extension
	 */
	public IsoPropertiesFilePropertiesLoader(final String extension)
	{
		this.extension = extension;
	}

	/**
	 * @see org.apache.wicket.resource.IPropertiesLoader#getFileExtension()
	 */
	public final String getFileExtension()
	{
		return extension;
	}

	/**
	 * @see org.apache.wicket.resource.IPropertiesLoader#loadJavaProperties(java.io.InputStream)
	 */
	public java.util.Properties loadJavaProperties(final InputStream in) throws IOException
	{
		java.util.Properties properties = new java.util.Properties();
		properties.load(in);
		return properties;
	}

	/**
	 * @see org.apache.wicket.resource.IPropertiesLoader#loadWicketProperties(java.io.InputStream)
	 */
	public ValueMap loadWicketProperties(InputStream inputStream)
	{
		return null;
	}
}