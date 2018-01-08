package com.xsq.common.plugin.database;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xsq.common.core.XsqCommon;
import com.xsq.common.util.LogUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/1/21.
 */
public abstract class AbstractDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final Map<Class<?>, DataDao> daoMap = new ConcurrentHashMap<>();

    protected AbstractDatabaseHelper(String databaseName, int databaseVersion){
        this(databaseName, null, databaseVersion);
    }

    protected AbstractDatabaseHelper(String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion)
    {
        super(XsqCommon.getInstance().getApplicationContext(), databaseName, null, databaseVersion);
    }

    protected abstract Class<?>[] getCreateTableImplClass();
    protected abstract void onTableCreateAfter(Class<?> tableImpl, boolean createSuccess, SQLiteDatabase database, ConnectionSource connectionSource);
    protected abstract Class<?>[] getUpgradeTableImplClass();
    protected abstract void onTableUpgradeAfter(Class<?> tableImpl, boolean upgradeSuccess, SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion);
    protected boolean onTableUpgradeBefore(Class<?> tableImpl, SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion){
        return true;
    }

    @Override
    public final void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource)
    {
        Class<?>[] tableImpls = getCreateTableImplClass();
        if(tableImpls != null){
            for (Class<?> impl : tableImpls){
                if(impl == null)
                    continue;

                boolean createSuccess = false;
                try
                {
                    TableUtils.createTableIfNotExists(connectionSource, impl);
                    createSuccess = true;
                } catch (SQLException e)
                {
                    LogUtil.error("onCreate table error.", e);
                }

                onTableCreateAfter(impl, createSuccess, database, connectionSource);
            }
        }
    }

    @Override
    public final void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        Class<?>[] tableImpls = getUpgradeTableImplClass();
        if(tableImpls != null){
            for (Class<?> impl : tableImpls){
                if(impl == null)
                    continue;

                if(onTableUpgradeBefore(impl, database, connectionSource, oldVersion, newVersion) == false)
                    continue;

                boolean processSuccess = false;
                try
                {
                    TableUtils.dropTable(connectionSource, impl, true);
                    TableUtils.createTableIfNotExists(connectionSource, impl);
                    clearCacheDao(impl);
                    processSuccess = true;
                } catch (SQLException e)
                {
                    LogUtil.error("onUpgrade table error.", e);
                }

                onTableUpgradeAfter(impl, processSuccess, database, connectionSource, oldVersion, newVersion);
            }
        }

    }

    public final <T> DataDao<T> getDataDao(Class<T> tableImpl){
        DataDao<T> dataDao = daoMap.get(tableImpl);
        if(dataDao == null){
            synchronized (this){
                dataDao = daoMap.get(tableImpl);
                if(dataDao == null){
                    dataDao = new DataDao(this, tableImpl);
                    daoMap.put(tableImpl, dataDao);
                }
            }
        }
        return dataDao;
    }

    public final  <T> Dao<T, ?> getDaoNoExcption(Class<T> clazz) {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
        } catch (SQLException e) {
            LogUtil.error("getDaoNoExcption error.", e);
        }
        return dao;
    }

    public final void clearCacheDao(Class<?> clazz){
        daoMap.remove(clazz);
    }

}
