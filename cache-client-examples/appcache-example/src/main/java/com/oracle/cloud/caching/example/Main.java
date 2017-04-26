package com.oracle.cloud.caching.example;

public class Main {
	
	public static void main(String[] args) {
		new UserController(new UserService());
	}
	
}
