package com.armygui.demo;


import com.armygui.demo.bean.Token;
import com.lookbi.baselib.base.IBasePresenter;
import com.lookbi.baselib.base.IBaseView;

/**
 * Created by armygui on 2017/10/16.
 */

public class MainContract {
    interface IView extends IBaseView {
        void getIntentDataSuccess(Token mdata);
    }

    interface IPresenter extends IBasePresenter {
        void getIntentData(String token);
    }
}
