package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;

public class FeedBack extends BmobObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7904296773303495482L;
	private String content;
	private String version;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
