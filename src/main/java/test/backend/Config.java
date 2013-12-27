package test.backend;

public class Config {

	public Integer getBacklog() {
		return 128;
	}

	public Integer getPort() {
		return 9999;
	}

	public Integer getClientMaxBodySize() {
		return 1048576;
	}

	public Integer getTaskThreadPoolSize() {
		return 30;
	}
}
