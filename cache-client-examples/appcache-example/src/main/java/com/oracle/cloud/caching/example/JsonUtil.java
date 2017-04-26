package com.oracle.cloud.caching.example;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonUtil {

	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	 public static <T> T fromJson(String json, Class<T> classOfT) {
		return new Gson().fromJson(json, classOfT);
	}

	public static ResponseTransformer json() {
		return JsonUtil::toJson;
	}
}
