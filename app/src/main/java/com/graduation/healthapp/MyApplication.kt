package com.graduation.healthapp

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import cn.bmob.v3.Bmob
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.graduation.healthapp.gen.DaoMaster
import com.graduation.healthapp.gen.DaoSession
import com.hjq.permissions.XXPermissions

private lateinit var context: MyApplication

class MyApplication : Application() {
    private var mHelper: DaoMaster.DevOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var mDaoMaster: DaoMaster? = null
    private var mDaoSession: DaoSession? = null

    override fun onCreate() {
        super.onCreate()
        context = this
        setDatabase()
        XXPermissions.setScopedStorage(true)
        Bmob.initialize(this,"931aa07205e9cddf2cd85458d029af79")
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用使用IjkPlayer解码
            .setPlayerFactory(IjkPlayerFactory.create())
//                //使用ExoPlayer解码
//                .setPlayerFactory(ExoMediaPlayerFactory.create())
//                //使用MediaPlayer解码
//                .setPlayerFactory(AndroidMediaPlayerFactory.create())
            .build())
    }

    companion object {
        fun getApplication(): MyApplication = context
    }

    fun getDaoSession(): DaoSession? {
        return mDaoSession
    }

    fun getDb(): SQLiteDatabase? {
        return db
    }

    private fun setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = DaoMaster.DevOpenHelper(this, "health-db", null)
        db = mHelper!!.writableDatabase
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = DaoMaster(db)
        mDaoSession = mDaoMaster!!.newSession()
    }

}