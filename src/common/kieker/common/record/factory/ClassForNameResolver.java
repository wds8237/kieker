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

package kieker.common.record.factory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import kieker.common.exception.MonitoringRecordException;
import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class ClassForNameResolver<T> {

	private final ConcurrentMap<String, Class<? extends T>> cachedClasses = new ConcurrentHashMap<String, Class<? extends T>>(); // NOCS
	private final Class<T> classToCast;

	public ClassForNameResolver(final Class<T> classToCast) {
		this.classToCast = classToCast;
	}

	/**
	 * This method tries to find a monitoring record class with the given name.
	 *
	 * @param classname
	 *            The name of the class.
	 *
	 * @return A {@link Class} instance corresponding to the given name, if it exists.
	 *
	 * @throws MonitoringRecordException
	 *             If either a class with the given name could not be found or if the class doesn't implement {@link IMonitoringRecord}.
	 */
	public final Class<? extends T> classForName(final String classname) throws MonitoringRecordException {
		Class<? extends T> clazz = this.cachedClasses.get(classname);
		if (clazz == null) {
			try {
				clazz = Class.forName(classname).asSubclass(this.classToCast);
				this.cachedClasses.putIfAbsent(classname, clazz);
			} catch (final ClassNotFoundException ex) {
				throw new MonitoringRecordException("Failed to get record type of name " + classname, ex);
			} catch (final ClassCastException ex) {
				throw new MonitoringRecordException("Failed to get record type of name " + classname, ex);
			}
		}
		return clazz;
	}
}
