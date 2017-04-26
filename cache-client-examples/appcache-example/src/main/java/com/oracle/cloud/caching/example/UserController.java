package com.oracle.cloud.caching.example;

import static com.oracle.cloud.caching.example.JsonUtil.json;
import static com.oracle.cloud.caching.example.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;
import static spark.Spark.put;
import static spark.Spark.port;

import java.util.Optional;


public class UserController {

	public static final Optional<String> PORT = Optional.ofNullable(System.getenv("PORT"));

	public UserController(final AbstractUserService userService) {

		Integer port = Integer.valueOf(PORT.orElse("8080"));
		port(port);
		System.err.println("[info] Listening on " + port);

		get("/users/:id", (req, res) -> {
			String id = req.params(":id");
			User user = userService.getUser(id);
			if (user != null) {
				return user;
			}
			res.status(404);
			return new ResponseError("No user with id '%s' found", id);
		}, json());

		post("/users", (req, res) -> userService.createUser(
				req.queryParams("name"),
				req.queryParams("email")
		), json());

		put("/users/:id", (req, res) -> userService.updateUser(
				req.params(":id"),
				req.queryParams("name"),
				req.queryParams("email")
		), json());

		delete("/users/:id", (req, res) -> userService.deleteUser(
				req.params(":id")
		), json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});

	}
}
