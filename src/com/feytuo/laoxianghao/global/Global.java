package com.feytuo.laoxianghao.global;

public interface Global {

	/**
	 * preference string
	 */
	public static final String PREFERENCE_NAME = "laoxianghao";//preference名
	public static final String IS_FIRST_USE = "isFirstUse";//保存在pre中是否首次打开
	public static final String CURRENT_NATIVE = "currentNative";//保存在pre中当前方言地序号
	public static final String USER_HOME = "userHome";//保存在pre中当前用户家乡
	public static final String USER_ID = "userId";//保存在pre中当前用户id（String）
	public static final String NO_LOGIN = "noLogin";//保存在pre中表示没有登录的userid标记
	public static final String IS_MAIN_LIST_NEED_REFRESH = "isNeedRefresh";//主界面列表是否需要刷新
	
	/**
	 * appid
	 * 友盟和百度定位的appid在主文件中设置
	 */
	public static final String BMOB_APPID = "ed1ba04dd59cfa26d2dd3d7565fb4f94";//bmob
	public static final String WEIXIN_APPID = "wx5f6483b251ab08e7";//微信
	public static final String QQ_APPID = "1102486695";//qq
	public static final String WEIBO_APPID = "3750147705";//新浪微博
}
