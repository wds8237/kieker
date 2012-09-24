/***************************************************************************
 * Copyright 2012 Kieker Project (http://kieker-monitoring.net)
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

package kieker.tools.traceAnalysis.repository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kieker.analysis.plugin.annotation.Property;
import kieker.analysis.repository.AbstractRepository;
import kieker.analysis.repository.annotation.Repository;
import kieker.common.configuration.Configuration;
import kieker.tools.traceAnalysis.filter.visualization.graph.Color;

/**
 * Implementation of a trace color repository, which associates colors to traces. These colors can, for instance,
 * be used to highlight traces in graph renderings.
 * 
 * @author Holger Knoche
 * 
 */
@Repository(name = "Trace color repository",
		description = "Provides color information for trace coloring",
		configuration = {
			@Property(name = TraceColorRepositoryConfiguration.CONFIG_PROPERTY_NAME_TRACE_COLOR_FILE_NAME, defaultValue = "")
		})
public class TraceColorRepository extends AbstractRepository {

	private static final String DEFAULT_KEYWORD = "default";
	private static final String COLLISION_KEYWORD = "collision";

	private static final String COLOR_REGEX = "0x([0-9|a-f]{6})";
	private static final Pattern COLOR_PATTERN = Pattern.compile(COLOR_REGEX);

	private static final String DELIMITER_REGEX = "=";

	private static final String ENCODING = "UTF-8";

	private final ConcurrentMap<Long, Color> colorMap;
	private final Color defaultColor;
	private final Color collisionColor;

	/**
	 * Creates a new trace color repository using the given configuration.
	 * 
	 * @param configuration
	 *            The configuration to use
	 * @throws IOException
	 *             If an I/O error occurs during initialization
	 */
	public TraceColorRepository(final Configuration configuration) throws IOException {
		this(configuration, TraceColorRepository.readDataFromFile(new TraceColorRepositoryConfiguration(configuration).getTraceColorFileName()));
	}

	/**
	 * Creates a new color repository with the given data.
	 * 
	 * @param configuration
	 *            The configuration to use
	 * @param colorData
	 *            The color data to use for this repository
	 */
	public TraceColorRepository(final Configuration configuration, final TraceColorRepositoryData colorData) {
		super(configuration);
		this.colorMap = colorData.getColorMap();
		this.defaultColor = colorData.getDefaultColor();
		this.collisionColor = colorData.getCollisionColor();
	}

	public Configuration getCurrentConfiguration() {
		return this.configuration;
	}

	/**
	 * Returns the color map stored in this repository.
	 * 
	 * @return See above
	 */
	public Map<Long, Color> getColorMap() {
		return Collections.unmodifiableMap(this.colorMap);
	}

	/**
	 * Returns the color to use for elements which are not defined in the color map.
	 * 
	 * @return See above
	 */
	public Color getDefaultColor() {
		return this.defaultColor;
	}

	/**
	 * Returns the color to use for elements for which no unique color can be determined.
	 * 
	 * @return See above
	 */
	public Color getCollisionColor() {
		return this.collisionColor;
	}

	private static Long parseTraceId(final String input) {
		try {
			return Long.parseLong(input);
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	private static Color parseColor(final String input) {
		final Matcher matcher = COLOR_PATTERN.matcher(input);
		if (!matcher.matches()) {
			return null;
		}

		final int rgbValue = Integer.parseInt(matcher.group(1), 16);
		return new Color(rgbValue);
	}

	/**
	 * Initializes a trace color repository from a given file.
	 * 
	 * @param fileName
	 *            The name of the file to read from
	 * @return The initialized trace color repository
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static TraceColorRepository createFromFile(final String fileName) throws IOException {
		final TraceColorRepositoryConfiguration configuration = new TraceColorRepositoryConfiguration(new Configuration());
		configuration.setTraceColorFileName(fileName);

		return new TraceColorRepository(configuration.getWrappedConfiguration(), TraceColorRepository.readDataFromFile(fileName));
	}

	private static TraceColorRepositoryData readDataFromFile(final String fileName) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), ENCODING));
			final ConcurrentMap<Long, Color> colorMap = new ConcurrentHashMap<Long, Color>();
			Color defaultColor = Color.BLACK;
			Color collisionColor = Color.GRAY;

			while (true) {
				final String currentLine = reader.readLine();
				if (currentLine == null) {
					break;
				}

				final String[] parts = currentLine.split(DELIMITER_REGEX);
				if (parts.length != 2) {
					continue;
				}

				final String traceName = parts[0];
				final String colorSpecification = parts[1];

				final Color traceColor = TraceColorRepository.parseColor(colorSpecification);

				if (DEFAULT_KEYWORD.equals(traceName)) {
					if (traceColor != null) {
						defaultColor = traceColor;
					}
				} else if (COLLISION_KEYWORD.equals(traceName)) {
					if (traceColor != null) {
						collisionColor = traceColor;
					}
				} else {
					final Long traceId = TraceColorRepository.parseTraceId(traceName);

					if ((traceId != null) && (traceColor != null)) {
						colorMap.put(traceId, traceColor);
					}
				}
			}

			return new TraceColorRepositoryData(colorMap, defaultColor, collisionColor);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * @author Holger Knoche
	 */
	public static class TraceColorRepositoryData {
		private final ConcurrentMap<Long, Color> colorMap;
		private final Color defaultColor;
		private final Color collisionColor;

		public TraceColorRepositoryData(final ConcurrentMap<Long, Color> colorMap, final Color defaultColor, final Color collisionColor) {
			this.colorMap = colorMap;
			this.defaultColor = defaultColor;
			this.collisionColor = collisionColor;
		}

		public ConcurrentMap<Long, Color> getColorMap() {
			return this.colorMap;
		}

		public Color getDefaultColor() {
			return this.defaultColor;
		}

		public Color getCollisionColor() {
			return this.collisionColor;
		}

	}

}
