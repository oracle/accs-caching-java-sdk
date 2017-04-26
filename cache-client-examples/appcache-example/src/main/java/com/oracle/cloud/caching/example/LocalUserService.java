package com.oracle.cloud.caching.example;

import com.oracle.cloud.cache.basic.LocalSessionProvider;
import com.oracle.cloud.cache.basic.Session;

public class LocalUserService extends AbstractUserService {

	public LocalUserService() {
		super();
	}

	protected Session initSession() {
		return new LocalSessionProvider().createSession();
	}
}
