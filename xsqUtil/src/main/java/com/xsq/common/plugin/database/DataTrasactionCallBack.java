package com.xsq.common.plugin.database;

import com.j256.ormlite.dao.Dao;

/**
 * Created by Administrator on 2016/1/21.
 */
public interface DataTrasactionCallBack {

    public <T> void onDataProcess(Dao<T, ?> dao);

}
