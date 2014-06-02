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

package kieker.panalysis.framework.concurrent;

import de.chw.util.StopWatch;

import kieker.panalysis.framework.core.AbstractFilter;
import kieker.panalysis.framework.core.IPipeline;
import kieker.panalysis.framework.core.IStage;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class WorkerThread extends Thread {

	private final IPipeline pipeline;
	private IStageScheduler stageScheduler;

	private long durationInNs;

	private volatile StageTerminationPolicy terminationPolicy;
	private volatile boolean shouldTerminate = false;
	private final int accessesDeviceId;
	private int executedUnsuccessfullyCount;
	private final StopWatch stopWatch = new StopWatch();
	private long schedulingOverheadInNs;

	public WorkerThread(final IPipeline pipeline, final int accessesDeviceId) {
		this.pipeline = pipeline;
		for (final IStage stage : pipeline.getStages()) {
			stage.setOwningThread(this);
		}
		this.accessesDeviceId = accessesDeviceId;
	}

	@Override
	public void run() {
		try {
			this.initDatastructures();
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}

		this.stopWatch.start();

		while (this.stageScheduler.isAnyStageActive()) {
			final IStage stage = this.stageScheduler.get();

			this.startStageExecution(stage);
			final boolean executedSuccessfully = stage.execute();
			this.finishStageExecution(stage, executedSuccessfully);

			if (this.shouldTerminate) {
				this.executeTerminationPolicy(stage, executedSuccessfully);
			}
			this.stageScheduler.determineNextStage(stage, executedSuccessfully);
		}

		this.stopWatch.end();

		this.cleanUpDatastructures();

		this.durationInNs = this.stopWatch.getDuration();

		this.schedulingOverheadInNs = this.durationInNs;
		for (final IStage stage : this.pipeline.getStages()) {
			final AbstractFilter<?> af = (AbstractFilter<?>) stage;
			this.schedulingOverheadInNs -= af.getOverallDurationInNs();
		}
	}

	private void executeTerminationPolicy(final IStage executedStage, final boolean executedSuccessfully) {
		// System.out.println("WorkerThread.executeTerminationPolicy(): " + this.terminationPolicy + ", executedSuccessfully=" + executedSuccessfully
		// + ", mayBeDisabled=" + executedStage.mayBeDisabled());

		switch (this.terminationPolicy) {
		case TERMINATE_STAGE_AFTER_NEXT_EXECUTION:
			if (executedStage.mayBeDisabled()) {
				this.stageScheduler.disable(executedStage);
			}
			break;
		case TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION:
			if (!executedSuccessfully) {
				if (executedStage.mayBeDisabled()) {
					this.stageScheduler.disable(executedStage);
				}
			}
			break;
		case TERMINATE_STAGE_NOW:
			for (final IStage stage : this.pipeline.getStages()) {
				this.stageScheduler.disable(stage);
			}
			break;
		default:
			break;
		}
	}

	private void initDatastructures() throws Exception {
		this.pipeline.fireStartNotification();
		this.stageScheduler = new NextStageScheduler(this.pipeline, this.accessesDeviceId);
	}

	private void startStageExecution(final IStage stage) {
		// System.out.println("Executing stage: " + stage);
	}

	private void finishStageExecution(final IStage stage, final boolean executedSuccessfully) {
		// System.out.println("Executed stage " + stage + " successfully: " + executedSuccessfully);
		if (!executedSuccessfully) { // statistics
			this.executedUnsuccessfullyCount++;
		}
	}

	private void cleanUpDatastructures() {
		// System.out.println("Firing stop notification...");
		this.pipeline.fireStopNotification();
		// System.out.println("Thread terminated:" + this);
		// System.out.println(this.getName() + ": executedUnsuccessfullyCount=" + this.executedUnsuccessfullyCount);
	}

	public long getDuration() {
		return this.durationInNs;
	}

	public IPipeline getPipeline() {
		return this.pipeline;
	}

	// BETTER remove this method since it is not intuitive; add a check to onStartPipeline so that a stage automatically disables itself if it has no input ports
	public void terminate(final StageTerminationPolicy terminationPolicyToUse) {
		for (final IStage startStage : this.pipeline.getStartStages()) {
			startStage.fireSignalClosingToAllInputPorts();
		}

		this.setTerminationPolicy(terminationPolicyToUse);
	}

	/**
	 * If not set, this thread will run infinitely.
	 * 
	 * @param terminationPolicyToUse
	 */
	public void setTerminationPolicy(final StageTerminationPolicy terminationPolicyToUse) {
		this.terminationPolicy = terminationPolicyToUse;
		this.shouldTerminate = true;
	}

	public long getSchedulingOverheadInNs() {
		return this.schedulingOverheadInNs;
	}

	public int getExecutedUnsuccessfullyCount() {
		return this.executedUnsuccessfullyCount;
	}

}
