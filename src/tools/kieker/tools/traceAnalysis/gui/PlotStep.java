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

package kieker.tools.traceAnalysis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kieker.tools.traceAnalysis.Constants;
import kieker.tools.traceAnalysis.gui.util.AllSelectionBindingItemListener;
import kieker.tools.traceAnalysis.gui.util.SelectionBindingItemListener;

/**
 * In this step of the trace analysis wizard, the user can choose the plots to draw.
 * 
 * @author Nils Christian Ehmke
 * 
 * @since 1.9
 */
public class PlotStep extends AbstractStep { // NOPMD (number of fields)

	private static final long serialVersionUID = 1L;

	private static final String PROPERTY_KEY_IDENTIFIER = PlotStep.class.getSimpleName();
	private static final String PROPERTY_KEY_DEPLOYMENT_SEQUENCE_DIAGRAMS = PROPERTY_KEY_IDENTIFIER + ".deploymentSequenceDiagrams";
	private static final String PROPERTY_KEY_ASSEMBLY_SEQUENCE_DIAGRAMS = PROPERTY_KEY_IDENTIFIER + ".assemblySequenceDiagrams";
	private static final String PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH = PROPERTY_KEY_IDENTIFIER + ".deploymentComponentDependencyGraph";
	private static final String PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT = PROPERTY_KEY_IDENTIFIER + ".deploymentComponentDependencyGraphResponseTime";
	private static final String PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT_CB = PROPERTY_KEY_IDENTIFIER
			+ ".deploymentComponentDependencyGraphResponseTimeComboBox";
	private static final String PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH = PROPERTY_KEY_IDENTIFIER + ".assemblyComponentDependencyGraph";
	private static final String PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT = PROPERTY_KEY_IDENTIFIER + ".assemblyComponentDependencyGraphResponseTime";
	private static final String PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT_CB = PROPERTY_KEY_IDENTIFIER
			+ ".assemblyComponentDependencyGraphResponseTimeComboBox";
	private static final String PROPERTY_KEY_CONTAINER_DEPENDENCY_GRAPH = PROPERTY_KEY_IDENTIFIER + ".containerDependencyGraph";
	private static final String PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH = PROPERTY_KEY_IDENTIFIER + ".deploymentOperationDependencyGraph";
	private static final String PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT = PROPERTY_KEY_IDENTIFIER + ".deploymentOperationDependencyGraphResponseTime";
	private static final String PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT_CB = PROPERTY_KEY_IDENTIFIER
			+ ".deploymentOperationDependencyGraphResponseTimeComboBox";
	private static final String PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH = PROPERTY_KEY_IDENTIFIER + ".assemblyOperationDependencyGraph";
	private static final String PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT = PROPERTY_KEY_IDENTIFIER + ".assemblyOperationDependencyGraphResponseTime";
	private static final String PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT_CB = PROPERTY_KEY_IDENTIFIER
			+ ".assemblyOperationDependencyGraphResponseTimeCOmboBox";
	private static final String PROPERTY_KEY_AGGREGATED_DEPLOYMENT_CALL_TREE = PROPERTY_KEY_IDENTIFIER + ".aggregatedDeploymentCallTree";
	private static final String PROPERTY_KEY_AGGREGATED_ASSEMBLY_CALL_TREE = PROPERTY_KEY_IDENTIFIER + ".aggregatedAssemblyCallTree";
	private static final String PROPERTY_KEY_CALL_TREES = PROPERTY_KEY_IDENTIFIER + ".callTrees";

	private final JLabel infoLabel = new JLabel("In this step you choose plots to be generated by the trace analysis.");

	private final JCheckBox deploymentSequenceDiagrams = new JCheckBox("Deployment Sequence Diagrams");
	private final JCheckBox assemblySequenceDiagrams = new JCheckBox("Assembly Sequence Diagrams");
	private final JCheckBox deploymentComponentDependencyGraph = new JCheckBox("Deployment Component Dependency Graph");
	private final JCheckBox deploymentComponentDependencyGraphResponseTime = new JCheckBox("Response Times");
	private final JCheckBox assemblyComponentDependencyGraph = new JCheckBox("Assembly Component Dependency Graph");
	private final JCheckBox assemblyComponentDependencyGraphResponseTime = new JCheckBox("Response Times");
	private final JCheckBox containerDependencyGraph = new JCheckBox("Plot Container Dependency Graph");
	private final JCheckBox deploymentOperationDependencyGraph = new JCheckBox("Deployment Operation Dependency Graph");
	private final JCheckBox deploymentOperationDependencyGraphResponseTime = new JCheckBox("Response Times");
	private final JCheckBox assemblyOperationDependencyGraph = new JCheckBox("Assembly Operation Dependency Graph");
	private final JCheckBox assemblyOperationDependencyGraphResponseTime = new JCheckBox("Response Times");
	private final JCheckBox aggregatedDeploymentCallTree = new JCheckBox("Aggregated Deployment Call Tree");
	private final JCheckBox aggregatedAssemblyCallTree = new JCheckBox("Aggregated Assembly Call Tree");
	private final JCheckBox callTrees = new JCheckBox("Call Trees");
	private final JCheckBox allGraphs = new JCheckBox("Select All");
	private final JPanel expandingPanel = new JPanel();

	private final String[] responseTimeStrings = { "ns", "us", "ms", "s" };
	private final JComboBox deploymentComponentDependencyGraphResponseTimeComboBox = new JComboBox(this.responseTimeStrings);
	private final JComboBox assemblyComponentDependencyGraphResponseTimeComboBox = new JComboBox(this.responseTimeStrings);
	private final JComboBox deploymentOperationDependencyGraphResponseTimeComboBox = new JComboBox(this.responseTimeStrings);
	private final JComboBox assemblyOperationDependencyGraphResponseTimeComboBox = new JComboBox(this.responseTimeStrings);

	public PlotStep() {
		this.addAndLayoutComponents();
		this.addLogicToComponents();
	}

	private void addAndLayoutComponents() {
		this.setLayout(new GridBagLayout());

		final GridBagConstraints infoLabelConstraints = new GridBagConstraints();
		infoLabelConstraints.gridwidth = GridBagConstraints.REMAINDER;
		infoLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
		infoLabelConstraints.insets.set(5, 5, 5, 5);
		infoLabelConstraints.weightx = 1.0;
		infoLabelConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.infoLabel, infoLabelConstraints);

		final GridBagConstraints deploymentSequenceDiagramsConstraints = new GridBagConstraints();
		deploymentSequenceDiagramsConstraints.gridwidth = GridBagConstraints.REMAINDER;
		deploymentSequenceDiagramsConstraints.insets.set(5, 5, 0, 0);
		deploymentSequenceDiagramsConstraints.weightx = 0.0;
		deploymentSequenceDiagramsConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.deploymentSequenceDiagrams, deploymentSequenceDiagramsConstraints);

		final GridBagConstraints assemblySequenceDiagramsConstraints = new GridBagConstraints();
		assemblySequenceDiagramsConstraints.gridwidth = GridBagConstraints.REMAINDER;
		assemblySequenceDiagramsConstraints.insets.set(0, 5, 0, 0);
		assemblySequenceDiagramsConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.assemblySequenceDiagrams, assemblySequenceDiagramsConstraints);

		final GridBagConstraints deploymentComponentDependencyGraphConstraints = new GridBagConstraints();
		deploymentComponentDependencyGraphConstraints.insets.set(0, 5, 0, 0);
		deploymentComponentDependencyGraphConstraints.fill = GridBagConstraints.BOTH;
		deploymentComponentDependencyGraphConstraints.gridx = 1;
		deploymentComponentDependencyGraphConstraints.gridy = 4;
		this.add(this.deploymentComponentDependencyGraph, deploymentComponentDependencyGraphConstraints);

		final GridBagConstraints deploymentComponentDependencyGraphResponseTimeConstraints = new GridBagConstraints();
		deploymentComponentDependencyGraphResponseTimeConstraints.insets.set(0, 5, 0, 0);
		deploymentComponentDependencyGraphResponseTimeConstraints.fill = GridBagConstraints.BOTH;
		deploymentComponentDependencyGraphResponseTimeConstraints.gridx = 2;
		deploymentComponentDependencyGraphResponseTimeConstraints.gridy = 4;
		this.add(this.deploymentComponentDependencyGraphResponseTime, deploymentComponentDependencyGraphResponseTimeConstraints);

		final GridBagConstraints responseTimeComboBoxConstraints = new GridBagConstraints();
		this.deploymentComponentDependencyGraphResponseTimeComboBox.setSelectedIndex(2);
		responseTimeComboBoxConstraints.gridwidth = GridBagConstraints.REMAINDER;
		responseTimeComboBoxConstraints.anchor = GridBagConstraints.LINE_START;
		responseTimeComboBoxConstraints.insets.set(0, 5, 0, 0);
		responseTimeComboBoxConstraints.fill = GridBagConstraints.NONE;
		responseTimeComboBoxConstraints.gridx = 3;
		responseTimeComboBoxConstraints.gridy = 4;
		this.add(this.deploymentComponentDependencyGraphResponseTimeComboBox, responseTimeComboBoxConstraints);

		final GridBagConstraints assemblyComponentDependencyGraphConstraints = new GridBagConstraints();
		assemblyComponentDependencyGraphConstraints.insets.set(0, 5, 0, 0);
		assemblyComponentDependencyGraphConstraints.fill = GridBagConstraints.BOTH;
		assemblyComponentDependencyGraphConstraints.gridx = 1;
		assemblyComponentDependencyGraphConstraints.gridy = 5;
		this.add(this.assemblyComponentDependencyGraph, assemblyComponentDependencyGraphConstraints);

		final GridBagConstraints assemblyComponentDependencyGraphResponseTimeConstraints = new GridBagConstraints();
		assemblyComponentDependencyGraphResponseTimeConstraints.insets.set(0, 5, 0, 0);
		assemblyComponentDependencyGraphResponseTimeConstraints.fill = GridBagConstraints.BOTH;
		assemblyComponentDependencyGraphResponseTimeConstraints.gridx = 2;
		assemblyComponentDependencyGraphResponseTimeConstraints.gridy = 5;
		this.add(this.assemblyComponentDependencyGraphResponseTime, assemblyComponentDependencyGraphResponseTimeConstraints);

		this.assemblyComponentDependencyGraphResponseTimeComboBox.setSelectedIndex(2);
		responseTimeComboBoxConstraints.gridy = 5;
		this.add(this.assemblyComponentDependencyGraphResponseTimeComboBox, responseTimeComboBoxConstraints);

		final GridBagConstraints containerDependencyGraphConstraints = new GridBagConstraints();
		containerDependencyGraphConstraints.gridwidth = GridBagConstraints.REMAINDER;
		containerDependencyGraphConstraints.insets.set(0, 5, 0, 0);
		containerDependencyGraphConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.containerDependencyGraph, containerDependencyGraphConstraints);

		final GridBagConstraints deploymentOperationDependencyGraphConstraints = new GridBagConstraints();
		deploymentOperationDependencyGraphConstraints.insets.set(0, 5, 0, 0);
		deploymentOperationDependencyGraphConstraints.fill = GridBagConstraints.BOTH;
		deploymentOperationDependencyGraphConstraints.gridx = 1;
		deploymentOperationDependencyGraphConstraints.gridy = 7;
		this.add(this.deploymentOperationDependencyGraph, deploymentOperationDependencyGraphConstraints);

		final GridBagConstraints deploymentOperationDependencyGraphResponseTimeConstraints = new GridBagConstraints();
		deploymentOperationDependencyGraphResponseTimeConstraints.insets.set(0, 5, 0, 0);
		deploymentOperationDependencyGraphResponseTimeConstraints.fill = GridBagConstraints.BOTH;
		deploymentOperationDependencyGraphResponseTimeConstraints.gridx = 2;
		deploymentOperationDependencyGraphResponseTimeConstraints.gridy = 7;
		this.add(this.deploymentOperationDependencyGraphResponseTime, deploymentOperationDependencyGraphResponseTimeConstraints);

		this.deploymentOperationDependencyGraphResponseTimeComboBox.setSelectedIndex(2);
		responseTimeComboBoxConstraints.gridy = 7;
		this.add(this.deploymentOperationDependencyGraphResponseTimeComboBox, responseTimeComboBoxConstraints);

		final GridBagConstraints assemblyOperationDependencyGraphConstraints = new GridBagConstraints();
		assemblyOperationDependencyGraphConstraints.insets.set(0, 5, 0, 0);
		assemblyOperationDependencyGraphConstraints.fill = GridBagConstraints.BOTH;
		assemblyOperationDependencyGraphConstraints.gridx = 1;
		assemblyOperationDependencyGraphConstraints.gridy = 8;
		this.add(this.assemblyOperationDependencyGraph, assemblyOperationDependencyGraphConstraints);

		final GridBagConstraints assemblyOperationDependencyGraphResponseTimeConstraints = new GridBagConstraints();
		assemblyOperationDependencyGraphResponseTimeConstraints.insets.set(0, 5, 0, 0);
		assemblyOperationDependencyGraphResponseTimeConstraints.fill = GridBagConstraints.BOTH;
		assemblyOperationDependencyGraphResponseTimeConstraints.gridx = 2;
		assemblyOperationDependencyGraphResponseTimeConstraints.gridy = 8;
		this.add(this.assemblyOperationDependencyGraphResponseTime, assemblyOperationDependencyGraphResponseTimeConstraints);

		this.assemblyOperationDependencyGraphResponseTimeComboBox.setSelectedIndex(2);
		responseTimeComboBoxConstraints.gridy = 8;
		this.add(this.assemblyOperationDependencyGraphResponseTimeComboBox, responseTimeComboBoxConstraints);

		final GridBagConstraints aggregatedDeploymentCallTreeConstraints = new GridBagConstraints();
		aggregatedDeploymentCallTreeConstraints.gridwidth = GridBagConstraints.REMAINDER;
		aggregatedDeploymentCallTreeConstraints.insets.set(0, 5, 0, 0);
		aggregatedDeploymentCallTreeConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.aggregatedDeploymentCallTree, aggregatedDeploymentCallTreeConstraints);

		final GridBagConstraints aggregatedAssemblyCallTreeConstraints = new GridBagConstraints();
		aggregatedAssemblyCallTreeConstraints.gridwidth = GridBagConstraints.REMAINDER;
		aggregatedAssemblyCallTreeConstraints.insets.set(0, 5, 0, 0);
		aggregatedAssemblyCallTreeConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.aggregatedAssemblyCallTree, aggregatedAssemblyCallTreeConstraints);

		final GridBagConstraints callTreesConstraints = new GridBagConstraints();
		callTreesConstraints.gridwidth = GridBagConstraints.REMAINDER;
		callTreesConstraints.insets.set(0, 5, 0, 0);
		callTreesConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.callTrees, callTreesConstraints);

		final GridBagConstraints allGraphsConstraints = new GridBagConstraints();
		allGraphsConstraints.gridwidth = GridBagConstraints.REMAINDER;
		allGraphsConstraints.insets.set(10, 5, 5, 5);
		allGraphsConstraints.fill = GridBagConstraints.BOTH;
		this.add(this.allGraphs, allGraphsConstraints);

		final GridBagConstraints expandingPanelConstraints = new GridBagConstraints();
		expandingPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
		expandingPanelConstraints.weighty = 1.0;
		expandingPanelConstraints.fill = GridBagConstraints.VERTICAL;
		this.add(this.expandingPanel, expandingPanelConstraints);
	}

	private void addLogicToComponents() {
		this.allGraphs.addItemListener(new AllSelectionBindingItemListener(this.deploymentSequenceDiagrams, this.assemblySequenceDiagrams,
				this.deploymentComponentDependencyGraph, this.deploymentComponentDependencyGraphResponseTime, this.assemblyComponentDependencyGraph,
				this.assemblyComponentDependencyGraphResponseTime, this.assemblyComponentDependencyGraphResponseTime, this.containerDependencyGraph,
				this.deploymentOperationDependencyGraph, this.deploymentOperationDependencyGraphResponseTime, this.assemblyOperationDependencyGraph,
				this.assemblyOperationDependencyGraph, this.assemblyOperationDependencyGraphResponseTime, this.aggregatedDeploymentCallTree,
				this.aggregatedAssemblyCallTree, this.callTrees));

		this.deploymentComponentDependencyGraph.addItemListener(new SelectionBindingItemListener(this.deploymentComponentDependencyGraphResponseTime, true));
		this.deploymentComponentDependencyGraphResponseTime.addItemListener(new SelectionBindingItemListener(this.deploymentComponentDependencyGraph, false));

		this.assemblyComponentDependencyGraph.addItemListener(new SelectionBindingItemListener(this.assemblyComponentDependencyGraphResponseTime, true));
		this.assemblyComponentDependencyGraphResponseTime.addItemListener(new SelectionBindingItemListener(this.assemblyComponentDependencyGraph, false));

		this.deploymentOperationDependencyGraph.addItemListener(new SelectionBindingItemListener(this.deploymentOperationDependencyGraphResponseTime, true));
		this.deploymentOperationDependencyGraphResponseTime.addItemListener(new SelectionBindingItemListener(this.deploymentOperationDependencyGraph, false));

		this.assemblyOperationDependencyGraph.addItemListener(new SelectionBindingItemListener(this.assemblyOperationDependencyGraphResponseTime, true));
		this.assemblyOperationDependencyGraphResponseTime.addItemListener(new SelectionBindingItemListener(this.assemblyOperationDependencyGraph, false));
	}

	@Override
	public boolean isNextStepAllowed() {
		final boolean nothingSelected = !(this.deploymentSequenceDiagrams.isSelected() || this.assemblySequenceDiagrams.isSelected()
				|| this.deploymentComponentDependencyGraph.isSelected() || this.assemblyComponentDependencyGraph.isSelected()
				|| this.containerDependencyGraph.isSelected() || this.deploymentOperationDependencyGraph.isSelected()
				|| this.assemblyOperationDependencyGraph.isSelected() || this.aggregatedDeploymentCallTree.isSelected()
				|| this.aggregatedAssemblyCallTree.isSelected() || this.callTrees.isSelected());
		if (nothingSelected) {
			final int result = JOptionPane.showConfirmDialog(this, "No plots have been selected. Continue?", "No Plots Selected", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return (JOptionPane.YES_OPTION == result);
		}
		return true;
	}

	@Override
	public void addSelectedTraceAnalysisParameters(final Collection<String> parameters) {
		if (this.deploymentSequenceDiagrams.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTALLOCATIONSEQDS);
		}

		if (this.assemblySequenceDiagrams.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTASSEMBLYSEQDS);
		}

		if (this.deploymentComponentDependencyGraph.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTALLOCATIONCOMPONENTDEPG);
			if (this.deploymentComponentDependencyGraphResponseTime.isSelected()) {
				switch (this.deploymentComponentDependencyGraphResponseTimeComboBox.getSelectedIndex()) {
				case 0: // ns
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_NS);
					break;
				case 1: // us
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_US);
					break;
				case 2: // ms
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_MS);
					break;
				case 3: // s
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_S);
					break;
				}
			}
		}

		if (this.assemblyComponentDependencyGraph.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTASSEMBLYCOMPONENTDEPG);
			if (this.assemblyComponentDependencyGraphResponseTime.isSelected()) {
				switch (this.assemblyComponentDependencyGraphResponseTimeComboBox.getSelectedIndex()) {
				case 0: // ns
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_NS);
					break;
				case 1: // us
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_US);
					break;
				case 2: // ms
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_MS);
					break;
				case 3: // s
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_S);
					break;
				}
			}
		}

		if (this.containerDependencyGraph.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTCONTAINERDEPG);
		}

		if (this.deploymentOperationDependencyGraph.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTALLOCATIONOPERATIONDEPG);
			if (this.deploymentOperationDependencyGraphResponseTime.isSelected()) {
				switch (this.deploymentOperationDependencyGraphResponseTimeComboBox.getSelectedIndex()) {
				case 0: // ns
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_NS);
					break;
				case 1: // us
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_US);
					break;
				case 2: // ms
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_MS);
					break;
				case 3: // s
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_S);
					break;
				}
			}
		}

		if (this.assemblyOperationDependencyGraph.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTASSEMBLYOPERATIONDEPG);
			if (this.assemblyOperationDependencyGraphResponseTime.isSelected()) {
				switch (this.assemblyOperationDependencyGraphResponseTimeComboBox.getSelectedIndex()) {
				case 0: // ns
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_NS);
					break;
				case 1: // us
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_US);
					break;
				case 2: // ms
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_MS);
					break;
				case 3: // s
					parameters.add(Constants.RESPONSE_TIME_DECORATOR_FLAG_S);
					break;
				}
			}
		}
		if (this.aggregatedDeploymentCallTree.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTAGGREGATEDALLOCATIONCALLTREE);
		}

		if (this.aggregatedAssemblyCallTree.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTAGGREGATEDASSEMBLYCALLTREE);
		}

		if (this.callTrees.isSelected()) {
			parameters.add("--" + Constants.CMD_OPT_NAME_TASK_PLOTCALLTREES);
		}

	}

	@Override
	public void saveCurrentConfiguration(final Properties properties) {
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_SEQUENCE_DIAGRAMS, Boolean.toString(this.deploymentSequenceDiagrams.isSelected()));
		properties.setProperty(PROPERTY_KEY_ASSEMBLY_SEQUENCE_DIAGRAMS, Boolean.toString(this.assemblySequenceDiagrams.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH, Boolean.toString(this.deploymentComponentDependencyGraph.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT,
				Boolean.toString(this.deploymentComponentDependencyGraphResponseTime.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT_CB,
				Integer.toString(this.deploymentComponentDependencyGraphResponseTimeComboBox.getSelectedIndex()));
		properties.setProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH, Boolean.toString(this.assemblyComponentDependencyGraph.isSelected()));
		properties
				.setProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT, Boolean.toString(this.assemblyComponentDependencyGraphResponseTime.isSelected()));
		properties.setProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT_CB,
				Integer.toString(this.assemblyComponentDependencyGraphResponseTimeComboBox.getSelectedIndex()));
		properties.setProperty(PROPERTY_KEY_CONTAINER_DEPENDENCY_GRAPH, Boolean.toString(this.containerDependencyGraph.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH, Boolean.toString(this.deploymentOperationDependencyGraph.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT,
				Boolean.toString(this.deploymentOperationDependencyGraphResponseTime.isSelected()));
		properties.setProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT_CB,
				Integer.toString(this.deploymentOperationDependencyGraphResponseTimeComboBox.getSelectedIndex()));
		properties.setProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH, Boolean.toString(this.assemblyOperationDependencyGraph.isSelected()));
		properties
				.setProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT, Boolean.toString(this.assemblyOperationDependencyGraphResponseTime.isSelected()));
		properties.setProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT_CB,
				Integer.toString(this.assemblyOperationDependencyGraphResponseTimeComboBox.getSelectedIndex()));
		properties.setProperty(PROPERTY_KEY_AGGREGATED_DEPLOYMENT_CALL_TREE, Boolean.toString(this.aggregatedDeploymentCallTree.isSelected()));
		properties.setProperty(PROPERTY_KEY_AGGREGATED_ASSEMBLY_CALL_TREE, Boolean.toString(this.aggregatedAssemblyCallTree.isSelected()));
		properties.setProperty(PROPERTY_KEY_CALL_TREES, Boolean.toString(this.callTrees.isSelected()));
	}

	@Override
	public void loadCurrentConfiguration(final Properties properties) {
		this.deploymentSequenceDiagrams.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_DEPLOYMENT_SEQUENCE_DIAGRAMS)));
		this.assemblySequenceDiagrams.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_ASSEMBLY_SEQUENCE_DIAGRAMS)));
		this.deploymentComponentDependencyGraph.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH)));
		this.deploymentComponentDependencyGraphResponseTime.setSelected(Boolean.parseBoolean(properties
				.getProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT)));
		this.deploymentComponentDependencyGraphResponseTimeComboBox.setSelectedIndex((Integer.parseInt(properties
				.getProperty(PROPERTY_KEY_DEPLOYMENT_COMPONENT_DEPENDENCY_GRAPH_RT_CB))));
		this.assemblyComponentDependencyGraph.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH)));
		this.assemblyComponentDependencyGraphResponseTime.setSelected(Boolean.parseBoolean(properties
				.getProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT)));
		this.assemblyComponentDependencyGraphResponseTimeComboBox.setSelectedIndex(Integer.parseInt(properties
				.getProperty(PROPERTY_KEY_ASSEMBLY_COMPONENT_DEPENDENCY_GRAPH_RT_CB)));
		this.containerDependencyGraph.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_CONTAINER_DEPENDENCY_GRAPH)));
		this.deploymentOperationDependencyGraph.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH)));
		this.deploymentOperationDependencyGraphResponseTime.setSelected(Boolean.parseBoolean(properties
				.getProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT)));
		this.deploymentOperationDependencyGraphResponseTimeComboBox.setSelectedIndex(Integer.parseInt(properties
				.getProperty(PROPERTY_KEY_DEPLOYMENT_OPERATION_DEPENDENCY_GRAPH_RT_CB)));
		this.assemblyOperationDependencyGraph.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH)));
		this.assemblyOperationDependencyGraphResponseTime.setSelected(Boolean.parseBoolean(properties
				.getProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT)));
		this.assemblyOperationDependencyGraphResponseTimeComboBox.setSelectedIndex(Integer.parseInt(properties
				.getProperty(PROPERTY_KEY_ASSEMBLY_OPERATION_DEPENDENCY_GRAPH_RT_CB)));
		this.aggregatedDeploymentCallTree.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_AGGREGATED_DEPLOYMENT_CALL_TREE)));
		this.aggregatedAssemblyCallTree.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_AGGREGATED_ASSEMBLY_CALL_TREE)));
		this.callTrees.setSelected(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_CALL_TREES)));
	}

	@Override
	public void loadDefaultConfiguration() {
		this.assemblyComponentDependencyGraph.setSelected(true);
		this.assemblyComponentDependencyGraphResponseTime.setSelected(true);

		this.assemblyOperationDependencyGraph.setSelected(true);
		this.assemblyOperationDependencyGraphResponseTime.setSelected(true);

		this.deploymentComponentDependencyGraph.setSelected(true);
		this.deploymentComponentDependencyGraphResponseTime.setSelected(true);

		this.deploymentOperationDependencyGraph.setSelected(true);
		this.deploymentOperationDependencyGraphResponseTime.setSelected(true);
	}

}
