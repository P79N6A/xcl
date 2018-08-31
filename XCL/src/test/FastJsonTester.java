package test;

import van.xcl.util.json.JsonObject;

public class FastJsonTester {

	public static void main(String[] args) {
		
		System.out.println(JsonObject.parse("{\"abc\":123}").toString());

	}

}
