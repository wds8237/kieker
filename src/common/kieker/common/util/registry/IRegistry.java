/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.common.util.registry;

/**
 * @author Christian Wulf
 *
 * @since 1.11
 */
// TODO remove "extends ILookup<T>" if migration has been completed
public interface IRegistry<T> extends ILookup<T> {

	/**
	 * Registers the passed <code>element</code> if it has not yet been registered
	 *
	 * @return the registered unique identifier for the passed <code>element</code>
	 *
	 * @since 1.11
	 */
	int addIfAbsent(T element);

	/**
	 * @return the registered unique identifier for the passed <code>element</code>
	 *
	 * @since 1.11
	 */
	@Override
	int get(T element);

	/**
	 * @return the current number of registered elements
	 *
	 * @since 1.11
	 */
	@Override
	int getSize();
}
