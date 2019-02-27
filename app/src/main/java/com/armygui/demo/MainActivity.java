package com.armygui.demo;


import com.armygui.demo.bean.Token;
import com.lookbi.baselib.base.BaseActivity;

public class MainActivity extends BaseActivity<MainContract.IView,
        MainPresentImpl> implements MainContract.IView {

    @Override
    protected MainPresentImpl createPresenter() {
        return new MainPresentImpl(this);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFristTopViewId() {
        return 0;
    }

    @Override
    protected void initView() {
        //调接口
        mPresenter.getIntentData("token值");
    }

    @Override
    protected void initData() {

    }

    /*
    * 请求网络成功
    * */
    @Override
    public void getIntentDataSuccess(Token mdata) {

    }
    /*
     * 请求网络失败
     * */
    @Override
    public void httpError(String e) {

    }
    /*
     * 请求网络无数据
     * */
    @Override
    public void noData(int code) {

    }
    /*
     * 请求网络结束
     * */
    @Override
    public void requestEnd() {

    }
}
