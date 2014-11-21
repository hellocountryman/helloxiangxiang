package com.feytuo.laoxianghao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;
import com.feytuo.laoxianghao.domain.LXHUser;

public class LXHUserDao {

private SQLiteDatabase db;
private DatabaseHelper dbHelper;
	
	public LXHUserDao(Context context) {
		// TODO Auto-generated constructor stub
		dbHelper = DatabaseHelper.getInstance(context);
	}
	
	/**
	 * 从帖子用户表中根据id获取用户昵称和头像
	 * @param uId
	 * @return
	 */
	public LXHUser getNickAndHeadByUid(String uId){
		db = dbHelper.getReadableDatabase();
		LXHUser user = null;
		String sqlStr = "select u_nickname,u_headurl from invitation_user where uid=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{uId});
		while(cursor.moveToNext()){
			user = new LXHUser();
			user.setNickName(cursor.getString(cursor.getColumnIndex("u_nickname")));
			user.setHeadUrl(cursor.getString(cursor.getColumnIndex("u_headurl")));
		}
		cursor.close();
		return user;
	}
	
	/**
	 * 插入一个用户
	 * @param user
	 */
	public void insertUser(LXHUser user){
		db = dbHelper.getWritableDatabase();
		String sql = "insert into invitation_user(uid,u_nickname,u_headurl,u_home,u_personsign) values(?,?,?,?,?)";
		db.execSQL(sql, new Object[]{user.getObjectId(),user.getNickName(),user.getHeadUrl(),
				user.getHome(),user.getPersonSign()});
	}
}
