package test.backend.model;

import java.io.Serializable;

public class Test implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2060602622753495693L;

	private Long id;

	private String name;

	public Test() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
