package com.example.qimo;

public class UserData
{
	//邮箱 密码 名称
	String email,password,name,net_md5;
	//毫秒时间戳

	//存储时间
	//System.currentTimeMillis()
	long new_time;

	//储存用户的头像信息
	//储存方法，【0】图片的MD5，【1】图片格式（后缀名）
	String[] user_head=new String[2];

	//储存性别 11 00
	byte sex;
	public UserData()
	{
		email="";
		password="123456";
		name="未闻君名";
		new_time=System.currentTimeMillis();
		user_head[0]="";
		user_head[1]="head";
		sex=2;
	}
}
