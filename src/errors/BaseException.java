package errors;

@SuppressWarnings("serial")
public class BaseException extends Exception {
	private String info;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
