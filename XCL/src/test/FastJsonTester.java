package test;

import van.util.json.JsonObject;

public class FastJsonTester {

	public static void main(String[] args) {
		
		System.out.println(JsonObject.parse("{\"abc\":123}").toString());

	}

}
