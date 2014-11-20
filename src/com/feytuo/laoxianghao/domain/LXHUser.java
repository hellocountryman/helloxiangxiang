package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class LXHUser extends BmobObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8901536917850459871L;
	//objectId作为聊天服务器的name，openId作为聊天服务器的密码
	private String uName;//openId
	private String uKey;//登录密匙或者记号
	private BmobRelation myInvitation;//用户发的帖子
	private String nickName;//昵称，获取QQ或者新浪昵称，聊天是显示
	private String headUrl;//从QQ上取下来存在自己的服务器上的头像url
	private String personSign;//个性签名
	private String home;//家乡
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
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public String getPersonSign() {
		return personSign;
	}
	public void setPersonSign(String personSign) {
		this.personSign = personSign;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	
	
}
