package com.oracle.cloud.caching.example;

import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.Session;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.basic.options.ValueType;

abstract class AbstractUserService {

	private Session cacheSession;
	private Cache<User> users;

	protected AbstractUserService() {
		super();
		cacheSession = initSession();
		users = cacheSession.getCache("users", ValueType.of(User.class));
	}

	abstract Session initSession();

	protected AbstractUserService(Session session) {
		this.cacheSession = session;
	}

	public User getUser(String id) {
		return users.get(id);
	}

	public User createUser(String name, String email) {
		failIfInvalid(name, email);
		User user = new User(name, email);
		users.put(user.getId(),user);
		return user;
	}

	public User updateUser(String id, String name, String email) {
		User user = getUser(id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		failIfInvalid(name, email);
		user.setName(name);
		user.setEmail(email);
		users.replace(id, user);
		return user;
	}

	public User deleteUser(String id) {
		User user = users.remove(id, Return.OLD_VALUE);
		if (null == user) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		} 
		return user;
	}

	private void failIfInvalid(String name, String email) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
	}

}