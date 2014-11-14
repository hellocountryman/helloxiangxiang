package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3799056765078804230L;
	private String uId;
	private String invId;
	private String comWords;
	private String comVoice;
	private String comTime;
	private String comPosition;
	private Invitation invitation;
	
	public Invitation getInvitation() {
		return invitation;
	}
	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getInvId() {
		return invId;
	}
	public void setInvId(String invId) {
		this.invId = invId;
	}
	public String getComWords() {
		return comWords;
	}
	public void setComWords(String comWords) {
		this.comWords = comWords;
	}
	public String getComVoice() {
		return comVoice;
	}
	public void setComVoice(String comVoice) {
		this.comVoice = comVoice;
	}
	public String getComTime() {
		return comTime;
	}
	public void setComTime(String comTime) {
		this.comTime = comTime;
	}
	public String getComPosition() {
		return comPosition;
	}
	public void setComPosition(String comPosition) {
		this.comPosition = comPosition;
	}
	
}
