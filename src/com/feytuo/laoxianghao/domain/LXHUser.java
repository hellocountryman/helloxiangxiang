package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class LXHUser extends BmobObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8901536917850459871L;
	private String uName;//openId
	private String uKey;//登录密匙或者记号
	private BmobRelation myInvitation;//用户发的帖子
	public BmobRelation getMyInvitation() {
		return myInvitation;
	}
	public void setMyInvitation(BmobRelation myInvitation) {
		this.myInvitation = myInvitation;
	}
	public String getuName() {
		return uName;
	}
	public void setuName(String uName) {
		this.uName = uName;
	}
	public String getuKey() {
		return uKey;
	}
	public void setuKey(String uKey) {
		this.uKey = uKey;
	}
	
	
}
