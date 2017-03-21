package com.asus.atd.smmitest.base;

/**
 * Created by lizusheng on 2015/12/29.
 */
public interface IModel {

    /**
     * 执行模块测试
     */
    public void startTest();

    /**
     * 重新执行测试
     */
    public void reStartTest();

    /**
     * 获取测试模块名
     */
}
