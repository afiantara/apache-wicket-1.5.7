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
package org.apache.wicket.util.crypt;

import java.util.UUID;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

/**
 * Crypt factory that produces {@link SunJceCrypt} instances based on http session-specific
 * encryption key. This allows each user to have their own encryption key, hardening against CSRF
 * attacks.
 * 
 * Note that the use of this crypt factory will result in an immediate creation of a http session
 * 
 * @author igor.vaynberg
 */
public class KeyInSessionSunJceCryptFactory implements ICryptFactory
{
	/** metadata-key used to store crypto-key in session metadata */
	private static MetaDataKey<String> KEY = new MetaDataKey<String>()
	{
		private static final long serialVersionUID = 1L;
	};


	public ICrypt newCrypt()
	{
		Session session = Session.get();
		session.bind();

		// retrieve or generate encryption key from session
		String key = session.getMetaData(KEY);
		if (key == null)
		{
			// generate new key
			key = session.getId() + "." + UUID.randomUUID().toString();
			session.setMetaData(KEY, key);
		}

		// build the crypt based on session key
		ICrypt crypt = new SunJceCrypt();
		crypt.setKey(key);
		return crypt;
	}
}
