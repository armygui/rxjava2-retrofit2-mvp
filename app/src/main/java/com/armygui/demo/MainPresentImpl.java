package com.armygui.demo;

import android.app.Activity;
import com.armygui.demo.bean.Token;
import com.armygui.demo.net.Api;
import com.armygui.demo.net.BaseObserver;
import com.lookbi.baselib.base.BasePresenterImpl;
import com.lookbi.baselib.net.RxSchedulers;

import io.reactivex.Observer;


/**
 * Created by armygui on 2017/9/20.
 */

public class MainPresentImpl extends BasePresenterImpl<MainContract.IView>
        implements MainContract.IPresenter {


    public MainPresentImpl(Activity context) {
        super(context);
    }

    @Override
    public void getIntentData(String token) {
        Observer mObserver = new BaseObserver<Token>(mContext, this) {

            @Override
            protected void onSuccess(Token mData) {
                if (!isViewAttached()) {
                    return;
                }
                if (mData == null) {
                    getView().noData(0);
                    return;
                }

                getView().getIntentDataSuccess(mData);
            }
        };
        Api.getService().getToken(token)
                .compose(RxSchedulers.compose(this))
                .subscribe(mObserver);
    }
}
