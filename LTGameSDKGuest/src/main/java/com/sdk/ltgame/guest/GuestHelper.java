package com.sdk.ltgame.guest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.gentop.ltgame.ltgamesdkcore.base.BaseEntry;
import com.gentop.ltgame.ltgamesdkcore.common.Target;
import com.gentop.ltgame.ltgamesdkcore.impl.OnLoginStateListener;
import com.sdk.ltgame.ltfacebook.FacebookLoginHelper;
import com.sdk.ltgame.ltgoogle.GoogleLoginHelper;
import com.sdk.ltgame.ltnet.impl.OnLoginSuccessListener;
import com.sdk.ltgame.ltnet.manager.LoginRealizeManager;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class GuestHelper {

    private int mLoginTarget;
    private static WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    private static String clientID;
    public static int selfRequestCode;
    private String adID;


    GuestHelper(Activity activity, String clientID, String adID,
                int selfRequestCode, OnLoginStateListener listener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.clientID = clientID;
        this.adID = adID;
        this.selfRequestCode = selfRequestCode;
        this.mListener = listener;
        this.mLoginTarget = Target.LOGIN_GUEST;
    }


    /**
     * 绑定账户
     */
    void onGoogleResult(int requestCode, Intent data) {
        String token = GoogleLoginHelper.getGuestToken(requestCode, data, selfRequestCode);
        LoginRealizeManager.bingAccount(mActivityRef.get(), token, "google", mListener);
    }


    /**
     * 结果
     */
    void onFBResult(int requestCode, int resultCode, Intent data) {
        FacebookLoginHelper.getTokenResult(requestCode, resultCode, data);
    }


    /**
     * 游客登录
     */
    private void guestLogin() {
        LoginRealizeManager.guestLogin(mActivityRef.get(), mListener);
    }

    /**
     * 绑定账户
     */
    private void bindFB(String appID) {
        FacebookLoginHelper.getToken(mActivityRef.get(), appID, new OnLoginSuccessListener<String>() {
            @Override
            public void onSuccess(BaseEntry<String> result) {
                LoginRealizeManager.bingAccount(mActivityRef.get(), result.getResult(), "facebook",
                        mListener);
            }

            @Override
            public void onFailed(BaseEntry<String> failed) {

            }
        });
    }

    /**
     * 绑定账户
     */
    private void bindGoogle() {
        GoogleLoginHelper.getToken(clientID, selfRequestCode, mActivityRef.get());
    }

    /**
     * 登录或者绑定
     */
    void guestLogin(String result, String appID) {
        if (!TextUtils.isEmpty(result)) {
            if (TextUtils.equals(result, "1")) {//游客
                guestLogin();
            } else if (TextUtils.equals(result, "2")) {//绑定FB
                bindFB(appID);
            } else if (TextUtils.equals(result, "3")) {//绑定Google
                bindGoogle();
            }
        }
    }


}
