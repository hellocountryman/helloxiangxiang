package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;

public class Praise extends BmobObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7341967951602391272L;
	private String uId;
	private String invId;
	public String getInvId() {
		return invId;
	}
	public void setInvId(String invId) {
		this.invId = invId;
	}
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	
}
