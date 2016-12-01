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

import com.google.inject.Guice;
import com.google.inject.Key;

/**
 * Guice Injector for testing purposes<br>
 * Simple Singleton implementation
 * 
 * @author Andrei Varabyeu
 * 
 */
public class Injector {

	/** Guice Injector */
	private com.google.inject.Injector injector;

	private static Injector instance;

	private Injector() {
		injector = Guice.createInjector(new GuiceTestModule());
	}

	public static synchronized Injector getInstance() {
		return null == instance ? instance = new Injector() : instance;
	}

	public <T> T getBean(Class<T> type) {
		return injector.getInstance(type);
	}

	public <T> T getBean(Key<T> key) {
		return injector.getInstance(key);
	}
}
