package com.oracle.cloud.caching.example;

import java.util.Optional;

import com.oracle.cloud.cache.basic.RemoteSessionProvider;
import com.oracle.cloud.cache.basic.Session;
import com.oracle.cloud.cache.basic.SessionProvider;
import com.oracle.cloud.cache.basic.options.Transport;


public class UserService extends AbstractUserService {
	
	private static final String CACHE_HOST = System.getenv("CACHING_INTERNAL_CACHE_URL");
	private static final Optional<String> CACHE_PROTOCOL = Optional.ofNullable(System.getenv("CACHE_PROTOCOL"));

	public UserService() {
		super();
	}

	protected Session initSession() {
		String protocolName = CACHE_PROTOCOL.orElse("REST").toUpperCase();
		String port = null;
		String cacheUrlSuffix = "";
		Transport transport = null;
		switch (protocolName) {
		case "GRPC":
			port = "1444";
			transport = Transport.grpc();
			break;
		default: // REST
			port = "8080";
			transport = Transport.rest();
			cacheUrlSuffix = "ccs";
			break;
		}
		String cacheUrl = "http://" + CACHE_HOST + ":" + port + "/" + cacheUrlSuffix;
		SessionProvider sessionProvider = new RemoteSessionProvider(cacheUrl);
		return sessionProvider.createSession(transport);
	}
}
