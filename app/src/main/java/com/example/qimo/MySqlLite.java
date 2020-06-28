package com.example.qimo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Objects;

public class MySqlLite extends SQLiteOpenHelper
{

	public MySqlLite(@Nullable Context context)
	{
		super(context, "UserData.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//初次创建sqllsit对象时调用此语句
		db.execSQL("create table UserData(id integer primary key,email text UNIQUE,name text,new_time INTEGER,head_md5 char(32),head_end char(5),net_md5 char(32),sex  INTEGER)");
		//创建用户存档，id=1储存最后一次登录的用户的邮箱。从id=1开始，正常储存用户信息
		/*
		create table UserData(
		 	integer primary key,	//id
		 	email text UNIQUE,		//email
		 	name text,				//name
		 	new_time INTEGER,		//最后一次和服务器互动时间，用于本地强制注销
		 	head_md5 char(32),		//储存用户头像文件的名字，用MD5可以防止重复，但是求MD5需要时间，以后再改，暂时以用户邮箱和用户最后一次修改头像的时间命名
		 	head_end char(5),		//储存用户的头像文件的后缀名
		 	net_md5 char(32),		//储存用户的服务器请求码
		 	sex  INTEGER)			//性别
		 */
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//当super的version变化时会调用这个语句
		db.execSQL("alter table UserData add account varchar(20)");
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public boolean addUserData(UserData userdata)//用户的数据更新和入库，主要给注册调用
	{
		Cursor cursor;
		cursor = this.getWritableDatabase().rawQuery("select * from UserData where id>1 and email=?", new String[]{userdata.email});
		if (cursor.moveToFirst())
		{
			//此用户的账号本地有记录，更新数据即可

			Log.i("TAG", "addUserData: 1");
			this.getWritableDatabase().close();
			Log.i("TAG", "addUserData: f1");
			ContentValues values = new ContentValues();
			Log.i("TAG", "addUserData: f2");
			//values.put("email",userdata.email);
			if (!Objects.equals(userdata.name, ""))
				values.put("name", userdata.name);
			values.put("sex", userdata.sex);
			if (!Objects.equals(userdata.net_md5, ""))
				values.put("net_md5", userdata.net_md5);
			if (userdata.new_time != 0)
				values.put("new_time", userdata.new_time);
			if (!Objects.equals(userdata.user_head[0], ""))
				values.put("head_md5", userdata.user_head[0]);
			if (!Objects.equals(userdata.user_head[1], "head"))
				values.put("head_end", userdata.user_head[1]);
			//values.put("id",1);
			this.getWritableDatabase().update("UserData", values, "id>1 and email=?", new String[]{userdata.email});
			Log.i("TAG", "addUserData: f4");
			this.getWritableDatabase().close();
			Log.i("TAG", "addUserData: f5");
		}
		else
		{
			//此用户是本地新用户，需要入库
			Log.i("TAG", "addUserData: 2");
			this.getWritableDatabase().close();
			ContentValues values = new ContentValues();
			values.put("email", userdata.email);
			//if (!Objects.equals(userdata.name, ""))
				values.put("name", userdata.name);
			values.put("sex", userdata.sex);
			//if (!Objects.equals(userdata.net_md5, ""))
				values.put("net_md5", userdata.net_md5);
			//if (userdata.new_time != 0)
				values.put("new_time", userdata.new_time);
			//if (!Objects.equals(userdata.user_head[0], ""))
				values.put("head_md5", userdata.user_head[0]);
			//if (!Objects.equals(userdata.user_head[1], ".head"))
				values.put("head_end", userdata.user_head[1]);
			//values.put("id",1);
			this.getWritableDatabase().insert("UserData", null, values);
			this.getWritableDatabase().close();
			Log.i("TAG", "addUserData: 3");
		}
		//cursor.close();
		return true;
	}

	public boolean addUserData(UserData userdata, int id)//用于记录最后一次登录的用户邮箱
	{
		Cursor cursor;
		cursor = this.getWritableDatabase().rawQuery("select * from UserData where id=?", new String[]{String.valueOf(id)});
		if (cursor.moveToFirst())
		{
			//此用户的账号本地有记录，更新数据即可

			Log.i("TAG", "addUserData: 1号元素更新");
			this.getWritableDatabase().close();
			ContentValues values = new ContentValues();
			//values.put("email",userdata.email);
			//values.put("id", id);
			values.put("name", userdata.email);
			values.put("email", "texttest");
			//values.put("sex", userdata.sex);
			//values.put("net_md5", userdata.net_md5);
			//values.put("new_time", userdata.new_time);
			//values.put("head_md5", userdata.user_head[0]);
			//values.put("head_end", userdata.user_head[1]);
			//values.put("id",1);
			this.getWritableDatabase().update("UserData", values, "id=?", new String[]{String.valueOf(id)});
			this.getWritableDatabase().close();
		}
		else
		{
			//此用户是本地新用户，需要入库
			Log.i("TAG", "addUserData: 1号元素插入");
			this.getWritableDatabase().close();
			ContentValues values = new ContentValues();
			//values.put("email", userdata.email);
			values.put("name", userdata.email);
			//values.put("sex", userdata.sex);
			//values.put("net_md5", userdata.net_md5);
			//values.put("new_time", userdata.new_time);
			//values.put("head_md5", userdata.user_head[0]);
			//values.put("head_end", userdata.user_head[1]);
			//values.put("id",1);
			this.getWritableDatabase().insert("UserData", null, values);
			this.getWritableDatabase().close();
			Log.i("TAG", "addUserData: 6");
		}
		//cursor.close();
		return true;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public boolean setSqlUserData(UserData userdata)////用户的数据更新和入库，编辑用户信息界面调用
	{
		Cursor cursor;
		cursor = this.getWritableDatabase().rawQuery("select * from UserData where id>1 and email=?", new String[]{userdata.email});
		if (cursor.moveToFirst())
		{
			//此用户的账号本地有记录，更新数据即可

			Log.i("TAG", "addUserData: 用户信息更新开始");
			this.getWritableDatabase().close();
			ContentValues values = new ContentValues();
			if (!Objects.equals(userdata.name, ""))
				values.put("name", userdata.name);
			if (userdata.sex != 2)
				values.put("sex", userdata.sex);
			if (!Objects.equals(userdata.net_md5, ""))
				values.put("net_md5", userdata.net_md5);
			if (userdata.new_time != 0)
				values.put("new_time", userdata.new_time);
			if (!Objects.equals(userdata.user_head[0], ""))
				values.put("head_md5", userdata.user_head[0]);
			if (!Objects.equals(userdata.user_head[1], "head"))
				values.put("head_end", userdata.user_head[1]);
			this.getWritableDatabase().update("UserData", values, "id>1 and email=?", new String[]{userdata.email});
			this.getWritableDatabase().close();
			Log.i("TAG", "addUserData: 用户信息更新结束");
			return true;
		}
		else
		{
			Log.i("TAG", "addUserData: 编辑用户信息界面调用，未查询到用户信息");
			return false;
		}
	}
}
