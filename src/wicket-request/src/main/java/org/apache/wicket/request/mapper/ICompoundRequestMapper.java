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
package org.apache.wicket.request.mapper;

import org.apache.wicket.request.IRequestMapper;

/**
 * Mapper that delegates the mapping to a contained {@link IRequestMapper}s with the highest
 * compatibility score.
 * 
 * @author igor.vaynberg
 */
public interface ICompoundRequestMapper extends IRequestMapper, Iterable<IRequestMapper>
{
	/**
	 * Registers a {@link IRequestMapper}
	 * 
	 * @param mapper
	 * @return {@code this} for chaining
	 */
	ICompoundRequestMapper add(IRequestMapper mapper);

	/**
	 * Unregisters a {@link IRequestMapper}
	 * 
	 * @param mapper
	 * @return {@code this} for chaining
	 */
	ICompoundRequestMapper remove(IRequestMapper mapper);

	/**
	 * Unregisters all {@link IRequestMapper}s which would match on a this path
	 * 
	 * @param path
	 *            the path to unmount
	 */
	void unmount(String path);
}