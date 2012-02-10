/***************************************************************************
 * Copyright 2011 by
 *  + Christian-Albrechts-University of Kiel
 *    + Department of Computer Science
 *      + Software Engineering Group 
 *  and others.
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

package kieker.tools.traceAnalysis.systemModel;

/**
 * 
 * @author Andre van Hoorn
 */
public class Signature {
	private final String name;

	private final String[] modifierList;
	private final String returnType;
	private final String[] paramTypeList;

	public Signature(final String name, final String[] modifierList, final String returnType, final String[] paramTypeList) {
		this.name = name;
		this.modifierList = modifierList;
		this.returnType = returnType;
		this.paramTypeList = paramTypeList.clone();
	}

	public final String getName() {
		return this.name;
	}

	public String[] getModifier() {
		return this.modifierList;
	}

	public final String[] getParamTypeList() {
		return this.paramTypeList.clone();
	}

	public final String getReturnType() {
		return this.returnType;
	}

	@Override
	public String toString() {
		final StringBuilder strBuild = new StringBuilder();
		boolean first = true;
		for (final String t : this.modifierList) {
			if (!first) {
				strBuild.append(" ");
			} else {
				first = false;
			}
			strBuild.append(t);
		}
		strBuild.append(this.name).append("(");
		first = true;
		for (final String t : this.paramTypeList) {
			if (!first) {
				strBuild.append(",");
			} else {
				first = false;
			}
			strBuild.append(t);
		}
		strBuild.append(")").append(":").append(this.returnType);
		return strBuild.toString();
	}

}
