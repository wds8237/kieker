/***************************************************************************
 * Copyright 2013 Kieker Project (http://kieker-monitoring.net)
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

package kieker.tools.traceAnalysis.filter.visualization.dependencyGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kieker.analysis.IProjectContext;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.annotation.RepositoryPort;
import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.tools.traceAnalysis.filter.AbstractGraphProducingFilter;
import kieker.tools.traceAnalysis.filter.AbstractTraceAnalysisFilter;
import kieker.tools.traceAnalysis.systemModel.AbstractMessage;
import kieker.tools.traceAnalysis.systemModel.ISystemModelElement;
import kieker.tools.traceAnalysis.systemModel.repository.SystemModelRepository;

/**
 * Refactored copy from LogAnalysis-legacy tool.
 * 
 * @param <T>
 * 
 * @author Andre van Hoorn, Lena Stoever, Matthias Rohr,
 */
@Plugin(repositoryPorts = @RepositoryPort(name = AbstractTraceAnalysisFilter.REPOSITORY_PORT_NAME_SYSTEM_MODEL, repositoryType = SystemModelRepository.class))
public abstract class AbstractDependencyGraphFilter<T extends ISystemModelElement> extends AbstractGraphProducingFilter<AbstractDependencyGraph<T>> {
	private static final Log LOG = LogFactory.getLog(AbstractDependencyGraphFilter.class);

	private final TimeUnit timeunit;

	private final List<AbstractNodeDecorator> decorators = new ArrayList<AbstractNodeDecorator>();

	/**
	 * Creates a new abstract dependency graph filter using the given data.
	 * 
	 * @param configuration
	 *            The configuration to use for this filter.
	 * @param projectContext
	 *            The project context to use for this filter.
	 * @param graph
	 *            The graph to produce / extend
	 */
	public AbstractDependencyGraphFilter(final Configuration configuration, final IProjectContext projectContext, final AbstractDependencyGraph<T> graph) {
		super(configuration, projectContext, graph);
		if (null != projectContext) { // TODO #819 remove non-null check and else case in Kieker 1.8)
			final String recordTimeunitProperty = projectContext.getProperty(IProjectContext.CONFIG_PROPERTY_NAME_RECORDS_TIME_UNIT);
			TimeUnit recordTimeunit;
			try {
				recordTimeunit = TimeUnit.valueOf(recordTimeunitProperty);
			} catch (final IllegalArgumentException ex) { // already caught in AnalysisController, should never happen
				LOG.warn(recordTimeunitProperty + " is no valid TimeUnit! Using NANOSECONDS instead.");
				recordTimeunit = TimeUnit.NANOSECONDS;
			}
			this.timeunit = recordTimeunit;
		} else {
			this.timeunit = TimeUnit.NANOSECONDS;
		}
	}

	/**
	 * Creates a new abstract dependency graph filter using the given data.
	 * 
	 * @param configuration
	 *            The configuration to use
	 * @param graph
	 *            The graph to produce / extend
	 * 
	 * @deprecated To be removed in Kieker 1.8.
	 */
	@Deprecated
	public AbstractDependencyGraphFilter(final Configuration configuration, final AbstractDependencyGraph<T> graph) {
		this(configuration, null, graph);
	}

	/**
	 * Adds a node decorator to this graph.
	 * 
	 * @param decorator
	 *            The decorator to add
	 */
	public void addDecorator(final AbstractNodeDecorator decorator) {
		this.decorators.add(decorator);
	}

	protected void invokeDecorators(final AbstractMessage message, final DependencyGraphNode<?> sourceNode, final DependencyGraphNode<?> targetNode) {
		for (final AbstractNodeDecorator currentDecorator : this.decorators) {
			currentDecorator.processMessage(message, sourceNode, targetNode, this.timeunit);
		}
	}

	protected boolean isDependencyAssumed(final DependencyGraphNode<?> source, final DependencyGraphNode<?> target) {
		return source.isAssumed() || target.isAssumed();
	}
}
