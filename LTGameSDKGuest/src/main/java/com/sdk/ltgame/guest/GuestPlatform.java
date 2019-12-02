package com.sdk.ltgame.guest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gentop.ltgame.ltgamesdkcore.common.LTGameOptions;
import com.gentop.ltgame.ltgamesdkcore.common.LTGameSdk;
import com.gentop.ltgame.ltgamesdkcore.common.Target;
import com.gentop.ltgame.ltgamesdkcore.impl.OnLoginStateListener;
import com.gentop.ltgame.ltgamesdkcore.impl.OnRechargeListener;
import com.gentop.ltgame.ltgamesdkcore.model.LoginObject;
import com.gentop.ltgame.ltgamesdkcore.model.RechargeObject;
import com.gentop.ltgame.ltgamesdkcore.platform.AbsPlatform;
import com.gentop.ltgame.ltgamesdkcore.platform.IPlatform;
import com.gentop.ltgame.ltgamesdkcore.platform.PlatformFactory;
import com.gentop.ltgame.ltgamesdkcore.uikit.BaseActionActivity;
import com.gentop.ltgame.ltgamesdkcore.util.LTGameUtil;
import com.sdk.ltgame.guest.uikit.GuestActivity;

public class GuestPlatform extends AbsPlatform {

    private GuestHelper mGuestHelper;


    public GuestPlatform(Context context, String appId, String appName, String appKey, int target) {
        super(context, appId, appName, appKey, target);
    }

    /**
     * 工厂
     */
    public static class Factory implements PlatformFactory {


        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            LTGameOptions options = LTGameSdk.options();
            if (!LTGameUtil.isAnyEmpty(options.getAppName(), options.getLtAppKey())) {
                platform = new GuestPlatform(context, options.getLtAppId(),
                        options.getAppName(), options.getLtAppKey(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_GUEST;
        }

        @Override
        public boolean checkLoginPlatformTarget(int target) {
            return target == Target.LOGIN_GUEST;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return false;
        }
    }

    @Override
    public Class getUIKitClazz() {
        return GuestActivity.class;
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        mGuestHelper.onGoogleResult(requestCode, data);
        mGuestHelper.onFBResult(requestCode, resultCode, data);
    }


    @Override
    public void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        mGuestHelper = new GuestHelper(activity, object.getmGoogleClient(), object.getmAdID(),
                object.getSelfRequestCode(), listener);
        mGuestHelper.guestLogin(object.getGuestType(), object.getFacebookAppID());
    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeListener listener) {

    }
}
