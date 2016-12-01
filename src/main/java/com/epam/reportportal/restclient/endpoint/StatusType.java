/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/client-java-rest-core
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epam.reportportal.restclient.endpoint;

/**
 * HTTP Response Status Type
 * 
 * @author Andrei Varabyeu
 * 
 */
public enum StatusType {

	/** Informational Response */
	INFORMATIONAL(1),
	/** Successful response */
	SUCCESSFUL(2),
	/** Redirection response */
	REDIRECTION(3),
	/** Client Error Response */
	CLIENT_ERROR(4),
	/** Server Error Response */
	SERVER_ERROR(5);

	/** First Symbol of HTTP response code */
	private final int value;

	StatusType(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	/**
	 * Obrains {@link StatusType} from HTTP status code. If there are no status
	 * defined throws {@link java.lang.IllegalArgumentException}
	 * 
	 * @param status
	 * @return
	 */
	public static StatusType valueOf(int status) {
		int seriesCode = status / 100;
		for (StatusType series : values()) {
			if (series.value == seriesCode) {
				return series;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + status + "]");
	}

}
