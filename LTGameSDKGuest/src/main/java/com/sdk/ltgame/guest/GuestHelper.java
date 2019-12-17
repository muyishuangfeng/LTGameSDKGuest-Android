package com.sdk.ltgame.guest;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.login.LoginManager;
import com.gentop.ltgame.ltgamesdkcore.common.Target;
import com.gentop.ltgame.ltgamesdkcore.impl.OnLoginStateListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.sdk.ltgame.ltnet.manager.LoginRealizeManager;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class GuestHelper {

    private int mLoginTarget;
    private static WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    private static String clientID;
    public static int selfRequestCode;
    private String adID;
    private CallbackManager mFaceBookCallBack;


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
        onActivityResult(requestCode, data, selfRequestCode);

    }


    /**
     * 结果
     */
    void onFBResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
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
    private void bindGoogle() {
        initGoogle(mActivityRef.get(), clientID, selfRequestCode);
    }

    /**
     * 登录或者绑定
     */
    void guestLogin( String result, String appID) {
        if (!TextUtils.isEmpty(result)) {
            if (TextUtils.equals(result, "1")) {//游客
                guestLogin();
            } else if (TextUtils.equals(result, "2")) {//绑定FB
                initFaceBook(appID);
            } else if (TextUtils.equals(result, "3")) {//绑定Google
                bindGoogle();
            }
        }
    }

    private void initGoogle(Activity context, String clientID, int selfRequestCode) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, selfRequestCode);
    }


    private void onActivityResult(int requestCode, Intent data, int selfRequestCode) {
        if (requestCode == selfRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (!TextUtils.isEmpty(adID)) {
                handleSignInResult(task);
            }
        }
    }


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            LoginRealizeManager.bingAccount(mActivityRef.get(), idToken, "google", mListener);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化
     */
    private void initFaceBook(String mFacebookID) {
        FacebookSdk.setApplicationId(mFacebookID);
        FacebookSdk.sdkInitialize(mActivityRef.get());
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions(mActivityRef.get(),
                            Arrays.asList("public_profile"));
            LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                    new FacebookCallback<com.facebook.login.LoginResult>() {
                        @Override
                        public void onSuccess(com.facebook.login.LoginResult loginResult) {
                            if (loginResult != null) {
                                LoginRealizeManager.bingAccount(mActivityRef.get(), loginResult.getAccessToken().getToken(), "facebook",
                                        mListener);

                            }

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

        } catch (FacebookSdkNotInitializedException ex) {
            ex.printStackTrace();
        }

    }





}
