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

package kieker.test.tools.junit.currentTimeEventGeneratorFilter;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.Assert;
import junit.framework.TestCase;
import kieker.analysis.plugin.AbstractAnalysisPlugin;
import kieker.analysis.plugin.AbstractPlugin;
import kieker.analysis.plugin.port.InputPort;
import kieker.common.configuration.Configuration;
import kieker.tools.currentTimeEventGenerator.CurrentTimeEventGenerator;
import kieker.tools.currentTimeEventGenerator.TimestampEvent;

import org.junit.Test;

public class TestCurrentTimeEventGenerator extends TestCase { // NOCS

	@Test
	public void testFirstRecordGeneratesEvent() { // NOPMD (assert in method)
		this.compareInputAndOutput(1000, new long[] { 15 }, new long[] { 15 }); // NOCS (MagicNumberCheck)
	}

	@Test
	public void testSecondWithinBoundNoEvent() { // NOPMD (assert in method)
		final long resolution = 1000;
		final long firstT = 15;
		final long secondT = (firstT + resolution) - 1;
		this.compareInputAndOutput(resolution, new long[] { firstT, secondT }, new long[] { firstT });
	}

	@Test
	public void testSecondOnBoundEvent() { // NOPMD (assert in method)
		final long resolution = 1000;
		final long firstT = 15;
		final long secondT = firstT + resolution;
		this.compareInputAndOutput(resolution, new long[] { firstT, secondT }, new long[] { firstT, secondT });
	}

	@Test
	public void testSecondBeyondBoundEvent() { // NOPMD (assert in method)
		final long resolution = 1000;
		final long firstT = 15;
		final long secondT = firstT + resolution + 1;
		this.compareInputAndOutput(resolution, new long[] { firstT, secondT }, new long[] { firstT, firstT + resolution });
	}

	@Test
	public void testTwoWithinBoundOnlyOneEvent() { // NOPMD (assert in method)
		final long resolution = 10;
		final long firstT = 5;
		final long secondT = firstT + 1;
		final long thirdT = secondT + 4; // NOCS (MagicNumberCheck)
		final long fourthT = firstT + resolution; // triggers next event
		this.compareInputAndOutput(resolution, new long[] { firstT, secondT, thirdT, fourthT }, new long[] { firstT, fourthT });
	}

	@Test
	public void testGapIntermediateEvents() { // NOPMD (assert in method)
		final long resolution = 6;
		final long firstT = 5;
		final long secondT = firstT + (5 * resolution) + 1; // NOCS (MagicNumberCheck)
		this.compareInputAndOutput(resolution, new long[] { firstT, secondT }, new long[] { firstT, firstT + resolution, firstT + (2 * resolution),
			firstT + (3 * resolution), firstT + (4 * resolution), firstT + (5 * resolution), }); // NOCS (MagicNumberCheck)
	}

	/**
	 * Creates a {@link CurrentTimeEventGenerator} with the given time
	 * resolution, input the sequence of input timestamps and compares the
	 * sequence of generated timer events with the given sequence of expected
	 * output timer events.
	 * 
	 * @param timerResolution
	 * @param inputTimestamps
	 * @param expectedOutputTimerEvents
	 */
	private void compareInputAndOutput(final long timerResolution, final long[] inputTimestamps, final long[] expectedOutputTimerEvents) {
		final CurrentTimeEventGenerator filter = new CurrentTimeEventGenerator(timerResolution);

		final DstClass dst = new DstClass();
		AbstractPlugin.connect(filter, CurrentTimeEventGenerator.CURRENT_TIME_OUTPUT_PORT_NAME, dst, DstClass.INPUT_PORT_NAME);

		for (final long timestamp : inputTimestamps) {
			filter.newTimestamp(timestamp);
		}

		final Long[] receivedTimestampsArr = dst.getList().toArray(new Long[dst.getList().size()]);

		if (expectedOutputTimerEvents.length != dst.getList().size()) {
			Assert.fail("Mismatach in sequence length while comparing timer event sequences" + "Expected: " + Arrays.toString(expectedOutputTimerEvents)
					+ " Found: " + Arrays.toString(receivedTimestampsArr));
		}

		int firstMismatchIdx = -1;
		for (int i = 0; i < expectedOutputTimerEvents.length; i++) {
			if (expectedOutputTimerEvents[i] != receivedTimestampsArr[i]) {
				firstMismatchIdx = i;
				break;
			}
		}

		if (firstMismatchIdx >= 0) {
			Assert.fail("Mismatch at index " + firstMismatchIdx + " while comparing timer event sequences" + "Expected: "
					+ Arrays.toString(expectedOutputTimerEvents) + " Found: " + Arrays.toString(receivedTimestampsArr));
		}
	}

	class DstClass extends AbstractAnalysisPlugin {

		public static final String INPUT_PORT_NAME = "doJob";
		private final ConcurrentLinkedQueue<Long> receivedTimestamps = new ConcurrentLinkedQueue<Long>();

		public DstClass() {
			super(new Configuration(null));
		}

		@Override
		public boolean execute() {
			return false;
		}

		@Override
		public void terminate(final boolean error) {}

		@Override
		protected Configuration getDefaultConfiguration() {
			return null;
		}

		@Override
		public Configuration getCurrentConfiguration() {
			return null;
		}

		@InputPort(eventTypes = { TimestampEvent.class })
		public void doJob(final Object data) {
			this.receivedTimestamps.add(((TimestampEvent) data).getTimestamp());
		}

		public ConcurrentLinkedQueue<Long> getList() {
			return this.receivedTimestamps;
		}
	}
}
