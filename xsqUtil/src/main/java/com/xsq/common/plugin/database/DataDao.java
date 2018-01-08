package com.xsq.common.plugin.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;
import com.xsq.common.util.LogUtil;

import java.sql.SQLException;

/**
 * Created by Administrator on 2016/1/21.
 */
public class DataDao<T> {

    private AbstractDatabaseHelper databaseHelper;
    private Class<T> tableImpl;

    public final Dao<T, ?> getDao(){
        Dao<T, ?> dao = null;
        try {
            dao = databaseHelper.getDao(tableImpl);
        } catch (SQLException e) {
            LogUtil.error("DataDao getDao error.", e);
        }
        return dao;
    }

    public DataDao(AbstractDatabaseHelper databaseHelper, Class<T> tableImpl){
        this.databaseHelper = databaseHelper;
        this.tableImpl = tableImpl;
    }


    public void executeForTrasaction(DataTrasactionCallBack callBack){
        if (callBack != null){
            Dao<T, ?> dao = getDao();

            if(dao == null)
                return;

            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                callBack.onDataProcess(dao);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                try {
                    dao.rollBack(databaseConnection);
                } catch (SQLException e1) {
                    LogUtil.warn("executeForTrasaction error.", e1);
                }
                LogUtil.warn("executeForTrasaction error.", e);
            } finally {
                try {
                    dao.endThreadConnection(databaseConnection);
                } catch (SQLException e) {
                    LogUtil.warn("executeForTrasaction error.", e);
                }
            }
        }
    }

}
