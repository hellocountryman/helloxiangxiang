package com.feytuo.laoxianghao.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;
import com.feytuo.laoxianghao.domain.Invitation;

public class InvitationDao {
	private SQLiteDatabase db;

	public InvitationDao(Context context) {
		// TODO Auto-generated constructor stub
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		db = dbHelper.getReadableDatabase();
	}

	
	/**
	 * 向“帖子表”中插入一组数据
	 * @param list
	 * @param isLoadMore
	 */
	public void insert2Invitation(List<Invitation> list,boolean isLoadMore){
		String sqlStr;
		if(!isLoadMore){//如果是刷新，需要清空本地表
			sqlStr = "delete from invitation";
			db.execSQL(sqlStr);
		}
		if(list != null && list.size() > 0){
			sqlStr = "insert into "
					+ "invitation(inv_id,uid,home,position,words,voice,voice_duration,time,ishot,praise_num,share_num,comment_num,head_id) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			for(Invitation inv : list){
				db.execSQL(
						sqlStr,
						new Object[] { inv.getObjectId(), inv.getuId(), inv.getHome(),
								inv.getPosition(), inv.getWords(), inv.getVoice(),inv.getVoiceDuration(), inv.getTime(),
								inv.getIsHot(), inv.getPraiseNum(), inv.getShareNum(),
								inv.getCommentNum(),inv.getHeadId()});
			}
		}
	}
	
	/**
	 * 通过id获取主帖子信息
	 * @param invId
	 * @return
	 */
	public Invitation getInvitationById(String invId){
		Invitation inv = null;
		String sqlStr = "select position,words,"
				+ "voice,voice_duration,time,praise_num,"
				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation where inv_id=?";
		Cursor cursor = db.rawQuery(sqlStr,new String[]{invId});
		while(cursor.moveToNext()){
			inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
		}
		cursor.close();
		return inv;
	}
	
	/**
	 * 获取homeid的所有帖子信息
	 * @param homeId 方言地id
	 * @return
	 */
	public List<Invitation> getAllInfo(int homeId){
		String sqlStr = null;
		Cursor cursor = null;
		List<Invitation> list = new ArrayList<>();
		if(homeId == 0){//取出全部
			sqlStr = "select position,words,"
					+ "voice,voice_duration,time,praise_num,"
					+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation";
			cursor = db.rawQuery(sqlStr,null);
		}else{
			sqlStr = "select position,words,"
					+ "voice,voice_duration,time,praise_num,"
					+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation where home=?";
			cursor = db.rawQuery(sqlStr,new String[]{homeId+""});
		}
		while(cursor.moveToNext()){
			Invitation inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
			list.add(inv);
		}
		cursor.close();
		return list;
	}
	/****************************我的发布*******************************/
	/**
	 * 从我的帖子表中获取所有帖子信息
	 * @param homeId 方言地id
	 * @return
	 */
	public List<Invitation> getAllInfoFromMy(){
		List<Invitation> list = new ArrayList<>();
		String sqlStr = "select position,words,voice,voice_duration,time,praise_num,comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_my";
		Cursor cursor = db.rawQuery(sqlStr,null);
		while(cursor.moveToNext()){
			Invitation inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
			list.add(inv);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 通过id获取“我的帖子”信息
	 * @param invId
	 * @return
	 */
	public Invitation getInvitationFromMyById(String invId){
		Invitation inv = null;
		String sqlStr = "select position,words,"
				+ "voice,voice_duration,time,praise_num,"
				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_my where inv_id=?";
		Cursor cursor = db.rawQuery(sqlStr,new String[]{invId});
		while(cursor.moveToNext()){
			inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
		}
		cursor.close();
		return inv;
	}
	
	/**
	 * 向"我的帖子"表插入一组数据
	 * 
	 * @param inv
	 */
	public void insert2Invitation(Invitation inv) {
		String sqlStr = "insert into "
				+ "invitation_my(inv_id,uid,home,position,words,voice,voice_duration,time,ishot,praise_num,share_num,comment_num,head_id) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		db.execSQL(
				sqlStr,
				new Object[] { inv.getObjectId(), inv.getuId(), inv.getHome(),
						inv.getPosition(), inv.getWords(), inv.getVoice(),inv.getVoiceDuration(), inv.getCreatedAt(),
						inv.getIsHot(), inv.getPraiseNum(), inv.getShareNum(),
						inv.getCommentNum(),inv.getHeadId()});
	}
	
	/**
	 * 清空"我的帖子"表中所有数据
	 */
	public void deleteAllDataInMy(){
		String sqlStr = "delete from invitation_my";
		db.execSQL(sqlStr);
	}
	/**
	 * 获取“我的帖子”所有帖子的commentNum
	 */
	public void getAllCommentNum(List<Integer> localCommentNum,List<String> myInvIds){
		String sqlStr = "select inv_id,comment_num from invitation_my";
		Cursor cursor  = db.rawQuery(sqlStr,null);
		while(cursor.moveToNext()){
			myInvIds.add(cursor.getString(0));
			localCommentNum.add(cursor.getInt(1));
		}
		cursor.close();
	}
	/**
	 * 通过帖子id获取帖子评论数
	 * @param invId
	 * @return
	 */
	public int getCommentNumByInvid(String invId){
		int number= 0 ;
		String sqlStr = "select comment_num from invitation_my where inv_id=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{invId});
		while(cursor.moveToNext()){
			number = cursor.getInt(0);
		}
		cursor.close();
		return number;
	}
	
	/**
	 * 从“我的帖子”表中删除invId为inv_id的一条数据
	 * @param inv_id
	 */
	public void deleteInInvitationMy(String inv_id){
		String sqlStr = "delete from invitation_my where inv_id=?";
		db.execSQL(sqlStr,new Object[]{inv_id});
	}
	
	
//	/****************************我的收藏*******************************/
//	/**
//	 * 从我的收藏表中获取homeid的所有帖子信息
//	 * @param homeId 方言地id
//	 * @return
//	 */
//	public List<Invitation> getAllInfoFromCollection(){
//		List<Invitation> list = new ArrayList<>();
//		String sqlStr = "select position,words,voice,voice_duration,time,praise_num,comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_collection";
//		Cursor cursor = db.rawQuery(sqlStr,null);
//		while(cursor.moveToNext()){
//			Invitation inv = new Invitation();
//			inv.setPosition(cursor.getString(0));
//			inv.setWords(cursor.getString(1));
//			inv.setVoice(cursor.getString(2));
//			inv.setVoiceDuration(cursor.getInt(3));
//			inv.setTime(cursor.getString(4));
//			inv.setPraiseNum(cursor.getInt(5));
//			inv.setCommentNum(cursor.getInt(6));
//			inv.setObjectId(cursor.getString(7));
//			inv.setuId(cursor.getString(8));
//			inv.setHome(cursor.getInt(9));
//			inv.setIsHot(cursor.getInt(10));
//			inv.setShareNum(cursor.getInt(11));
//			inv.setHeadId(cursor.getInt(12));
//			list.add(inv);
//		}
//		cursor.close();
//		return list;
//	}
//	
//	/**
//	 * 通过id获取“我的收藏”信息
//	 * @param invId
//	 * @return
//	 */
//	public Invitation getInvitationFromCollectionById(String invId){
//		Invitation inv = null;
//		String sqlStr = "select position,words,"
//				+ "voice,voice_duration,time,praise_num,"
//				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_collection where inv_id=?";
//		Cursor cursor = db.rawQuery(sqlStr,new String[]{invId});
//		while(cursor.moveToNext()){
//			inv = new Invitation();
//			inv.setPosition(cursor.getString(0));
//			inv.setWords(cursor.getString(1));
//			inv.setVoice(cursor.getString(2));
//			inv.setVoiceDuration(cursor.getInt(3));
//			inv.setTime(cursor.getString(4));
//			inv.setPraiseNum(cursor.getInt(5));
//			inv.setCommentNum(cursor.getInt(6));
//			inv.setObjectId(cursor.getString(7));
//			inv.setuId(cursor.getString(8));
//			inv.setHome(cursor.getInt(9));
//			inv.setIsHot(cursor.getInt(10));
//			inv.setShareNum(cursor.getInt(11));
//			inv.setHeadId(cursor.getInt(12));
//		}
//		cursor.close();
//		return inv;
//	}
//	
//	/**
//	 * 从“我的收藏”表找invId和uId的数据
//	 * @param invId
//	 * @param uId
//	 * @return
//	 */
//	public boolean selectCollection(String invId){
//		boolean has = false;
//		String sqlStr = "select inv_id from invitation_collection where inv_id=?";
//		Cursor cursor = db.rawQuery(sqlStr, new String[]{invId});
//		if(cursor.getCount() == 0){
//			has = false;
//		}else{
//			has = true;
//		}
//		cursor.close();
//		return has;
//	}
//	/**
//	 * 向"我的收藏"表插入一组数据
//	 * 
//	 * @param inv
//	 */
//	public void insert2InvitationCollection(Invitation inv) {
//		String sqlStr = "insert into "
//				+ "invitation_collection(inv_id,uid,home,position,words,voice,voice_duration,time,ishot,praise_num,share_num,comment_num,head_id) "
//				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		db.execSQL(
//				sqlStr,
//				new Object[] { inv.getObjectId(), inv.getuId(), inv.getHome(),
//						inv.getPosition(), inv.getWords(), inv.getVoice(),inv.getVoiceDuration(), inv.getTime(),
//						inv.getIsHot(), inv.getPraiseNum(), inv.getShareNum(),
//						inv.getCommentNum(),inv.getHeadId()});
//	}
//	/**
//	 * 从“我的收藏”表中删除invId为inv_id的一条数据
//	 * @param inv_id
//	 */
//	public void deleteInInvitationCollection(String inv_id){
//		String sqlStr = "delete from invitation_collection where inv_id=?";
//		db.execSQL(sqlStr,new Object[]{inv_id});
//	}
//	/**
//	 * 获取“我的收藏”所有帖子的commentNum
//	 */
//	public void getAllCommentNumFromCollection(List<Integer> localCommentNum,List<String> myInvIds){
//		String sqlStr = "select inv_id,comment_num from invitation_collection";
//		Cursor cursor  = db.rawQuery(sqlStr,null);
//		while(cursor.moveToNext()){
//			myInvIds.add(cursor.getString(0));
//			localCommentNum.add(cursor.getInt(1));
//		}
//		cursor.close();
//	}
//	/**
//	 * 获取“我的收藏”所有帖子的invID
//	 */
//	public void getAllIndidFromCollection(List<String> myInvIds){
//		String sqlStr = "select inv_id from invitation_collection";
//		Cursor cursor  = db.rawQuery(sqlStr,null);
//		while(cursor.moveToNext()){
//			myInvIds.add(cursor.getString(0));
//		}
//		cursor.close();
//	}
//	/**
//	 * 清空"我的收藏"表中所有数据
//	 */
//	public void deleteAllDataInCollection(){
//		String sqlStr = "delete from invitation_collection";
//		db.execSQL(sqlStr);
//	}
//	
	/*********************我的帖子分类表************************/
	/**
	 * 获取当前type的所有帖子信息
	 * @param type 分类：0普通，1话题，2段子，3KTV，4秀场
	 * @return
	 */
	public List<Invitation> getInvitationFromClass(int type){
		List<Invitation> list = new ArrayList<>();
		String sqlStr = "select position,words,"
				+ "voice,voice_duration,time,praise_num,"
				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_class where ishot=?";
		Cursor cursor = db.rawQuery(sqlStr,new String[]{type+""});
		while(cursor.moveToNext()){
			Invitation inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
			list.add(inv);
		}
		cursor.close();
		return list;
	}
	/**
	 * 获取话题帖子信息
	 * @param inv 用户主界面更新最新话题
	 * @param type 话题帖
	 * @return
	 */
	public void setTypeInvitationFromClass(Invitation inv ,int type){
		String sqlStr = "select position,words,"
				+ "voice,voice_duration,time,praise_num,"
				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_class where ishot=?";
		Cursor cursor = db.rawQuery(sqlStr,new String[]{type+""});
		if(cursor.moveToNext()){//只取第一组数据
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
		}
		cursor.close();
	}
	/**
	 * 获取分类表中一条帖子信息
	 * @param invId 帖子id
	 * @return
	 */
	public Invitation getTypeInvitationFromClass(String invId){
		Invitation inv = null;
		String sqlStr = "select position,words,"
				+ "voice,voice_duration,time,praise_num,"
				+ "comment_num,inv_id,uid,home,ishot,share_num,head_id from invitation_class where inv_id=?";
		Cursor cursor = db.rawQuery(sqlStr,new String[]{invId});
		if(cursor.moveToNext()){//只取第一组数据
			inv = new Invitation();
			inv.setPosition(cursor.getString(0));
			inv.setWords(cursor.getString(1));
			inv.setVoice(cursor.getString(2));
			inv.setVoiceDuration(cursor.getInt(3));
			inv.setTime(cursor.getString(4));
			inv.setPraiseNum(cursor.getInt(5));
			inv.setCommentNum(cursor.getInt(6));
			inv.setObjectId(cursor.getString(7));
			inv.setuId(cursor.getString(8));
			inv.setHome(cursor.getInt(9));
			inv.setIsHot(cursor.getInt(10));
			inv.setShareNum(cursor.getInt(11));
			inv.setHeadId(cursor.getInt(12));
		}
		cursor.close();
		return inv;
	}
	
	/**
	 * 向“帖子分类表”中插入一组数据
	 * @param list
	 * @param isLoadMore
	 */
	public void insert2InvitationClass(List<Invitation> list,int type,boolean isLoadMore){
		String sqlStr;
		if(!isLoadMore){//如果是刷新，需要清空本地表
			sqlStr = "delete from invitation_class where ishot=?";
			db.execSQL(sqlStr,new Object[]{type});
		}
		if(list != null && list.size() > 0){
			sqlStr = "insert into "
					+ "invitation_class(inv_id,uid,home,position,words,voice,voice_duration,time,ishot,praise_num,share_num,comment_num,head_id) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			for(Invitation inv : list){
				db.execSQL(
						sqlStr,
						new Object[] { inv.getObjectId(), inv.getuId(), inv.getHome(),
								inv.getPosition(), inv.getWords(), inv.getVoice(),inv.getVoiceDuration(), inv.getTime(),
								inv.getIsHot(), inv.getPraiseNum(), inv.getShareNum(),
								inv.getCommentNum(),inv.getHeadId()});
			}
		}
	}
	
}
