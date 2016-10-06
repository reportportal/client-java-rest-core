/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/epam/ReportPortal
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
package com.epam.reportportal.restclient.endpoint.exception;

/**
 * Base HTTP error representation
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndpointException extends RuntimeException {

	private static final long serialVersionUID = 728718628763519460L;

	/** HTTP Status Code */
	protected int statusCode;

	/** HTTP Status Message */
	protected String statusMessage;

	/** HTTP Response Body */
	protected byte[] content;

	public RestEndpointException(int statusCode, String statusMessage, byte[] content) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "Some REST error occured\n" + "Status code: " + statusCode + "\n" + "Status message: " +
				statusMessage;
	}

}
