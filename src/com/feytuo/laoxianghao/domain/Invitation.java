package com.feytuo.laoxianghao.domain;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Invitation extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7481046463594622107L;
	private String uId;// 用户id
	private Integer home;// 方言地
	private String position;// 用户当前所在地
	private String words;// 文字内容
	private String voice;// 语音url
	private Integer voiceDuration;// 录音时长
	private String time;// 发布时间,用于本地数据库操作
	private Integer isHot;// 是否热门
	private Integer praiseNum;// 点赞数
	private Integer shareNum;// 分享数
	private Integer commentNum;// 评论数
	private Integer headId;// 大头贴的id
	private LXHUser user;// 关联到用户
	private BmobRelation comment;//帖子的评论信息
	

	public BmobRelation getComment() {
		return comment;
	}

	public void setComment(BmobRelation comment) {
		this.comment = comment;
	}

	public LXHUser getUser() {
		return user;
	}

	public void setUser(LXHUser user) {
		this.user = user;
	}

	// public String getInvId() {
	// return invId;
	// }
	// public void setInvId(String invId) {
	// this.invId = invId;
	// }
	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public Integer getHome() {
		return home;
	}

	public void setHome(Integer home) {
		this.home = home;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getIsHot() {
		return isHot;
	}

	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}

	public Integer getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(Integer praiseNum) {
		this.praiseNum = praiseNum;
	}

	public Integer getShareNum() {
		return shareNum;
	}

	public void setShareNum(Integer shareNum) {
		this.shareNum = shareNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public Integer getVoiceDuration() {
		return voiceDuration;
	}

	public void setVoiceDuration(Integer voiceDuration) {
		this.voiceDuration = voiceDuration;
	}

	public Integer getHeadId() {
		return headId;
	}

	public void setHeadId(Integer headId) {
		this.headId = headId;
	}

}
