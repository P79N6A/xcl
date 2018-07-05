package van.util.http;

public class HttpResult {
	public static final int RESULT_OK = 200;
	private Integer code;
	private String body;
	private String response;
	public HttpResult() {
	}
	public HttpResult(Integer code, String body, String response) {
		this.code = code;
		this.body = body;
		this.response = response;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String toString() {
		return "[code: " + code + "] [body: " + body + "] [response: " + response + "]";
	}

}