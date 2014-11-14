package com.feytuo.laoxianghao.share_sina;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.global.UserLogin;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

public class AuthListener implements WeiboAuthListener{

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    private Context context;
    private boolean isLogin;
    private String words;
    private int resource;
    public AuthListener(Context context , boolean isLogin,String words,int resource) {
    	this.isLogin = isLogin;
		this.context = context;
		this.words = words;
		this.resource = resource;
	}
	 @Override
     public void onComplete(Bundle values) {
         // 从 Bundle 中解析 Token
         mAccessToken = Oauth2AccessToken.parseAccessToken(values);
         if (mAccessToken.isSessionValid()) {
             
             // 保存 Token 到 SharedPreferences
             AccessTokenKeeper.writeAccessToken(context, mAccessToken);
//             Toast.makeText(context, 
//                     R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
             /**
              * 需要判断，如果是登录，不需要弹出
              */
             if(!isLogin){
            	 new Share_Weibo(context).sendMessage(words,resource);
             }else{
		         /**
		          * 两种方式获取新浪微博登录id
		          */
	             long uid = Long.parseLong(mAccessToken.getUid());
//	             Log.i("WeiboAuthTest", uid+"=="+values.getString("uid"));
	             UserLogin.Login(context, uid+"", "Sina");
             }
             
             //授权成功即可
         } else {
             // 以下几种情况，您会收到 Code：
             // 1. 当您未在平台上注册的应用程序的包名与签名时；
             // 2. 当您注册的应用程序包名与签名不正确时；
             // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
             String code = values.getString("code");
             String message = context.getString(R.string.weibosdk_demo_toast_auth_failed);
             if (!TextUtils.isEmpty(code)) {
                 message = message + "\nObtained the code: " + code;
             }
//             Toast.makeText(context, message, Toast.LENGTH_LONG).show();
         }
     }

     @Override
     public void onCancel() {
//         Toast.makeText(context, 
//                 R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
     }

     @Override
     public void onWeiboException(WeiboException e) {
//         Toast.makeText(context, 
//                 "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
     }
}
