package com.oracle.cloud.caching.example;

public class LocalMain {
	
	public static void main(String[] args) {
		new UserController(new LocalUserService());
	}
	
}
