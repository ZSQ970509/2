package com.xsq.czy.net;


import com.xsq.czy.beans.Apply;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class GetApplyListPackage extends BasePackage {

    /**请求列表*/
    private List<Apply> applyList;

    public void setApplyList(List<Apply> applyList) {
        this.applyList = applyList;
    }

    public List<Apply> getApplyList() {
        return applyList;
    }
}
