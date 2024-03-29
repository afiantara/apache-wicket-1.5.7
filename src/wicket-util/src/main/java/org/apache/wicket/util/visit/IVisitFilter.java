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
package org.apache.wicket.util.visit;

/**
 * A filter that can be used to restrict the types of objects visited by the visitor
 * 
 * @author igor.vaynberg
 */
public interface IVisitFilter
{
	/**
	 * Controls whether or not the {@code object} will be visited
	 * 
	 * @param object
	 * @return {@code true} if the object should be visited
	 */
	boolean visitObject(Object object);

	/**
	 * Controls whether or not the {@code object}'s children will be visited
	 * 
	 * @param object
	 * @return {@code true} if the object's children should be visited
	 */
	boolean visitChildren(Object object);

	/**
	 * A visitor filter that allows all objects and their children to be visited
	 */
	public static IVisitFilter ANY = new IVisitFilter()
	{
		/** {@inheritDoc} */
		public boolean visitObject(final Object object)
		{
			return true;
		}

		/** {@inheritDoc} */
		public boolean visitChildren(final Object object)
		{
			return true;
		}
	};
}
