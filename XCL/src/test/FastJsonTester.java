package test;

import com.alibaba.fastjson.JSONObject;

public class FastJsonTester {

	public static void main(String[] args) {
		
		System.out.println(JSONObject.parse("{\"abc\":123}").toString());

	}

}
