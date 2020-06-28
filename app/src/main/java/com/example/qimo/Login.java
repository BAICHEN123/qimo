package com.example.qimo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Login extends Activity
{
	//向主界面返回数据时要包含
	/*
	email
	password
	new_time
	net_md5
	 */
	protected static final int CHANGE_UI = 1, ERROR = 0;
	UserData userdata;
	EditText log_edit1, log_edit2;
	boolean thread_post_key = true;

	//boolean thread_time_key=true;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.i("login", "onCreate: 尝试打开登陆界面");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		log_edit1 = findViewById(R.id.log_edit1);
		log_edit2 = findViewById(R.id.log_edit2);
		userdata = new UserData();
		Toast.makeText(Login.this, "该功能正在完善安全。请用邮箱登录，或者注册账号", Toast.LENGTH_SHORT).show();
		Log.i("login", "onCreate: 成功打开登陆界面");
	}

	/*接受打开界面的返回值*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("TAG", "onActivityResult: 登录界面收到信息");
		;
		if (requestCode == 0 && resultCode == 2)
		{
			Log.i("TAG", "onActivityResult: 接收到注册界面的信息");
			/*data包含的数据
				new_time	//服务器最后登录时间
				net_md5		//服务器请求码
				email		//邮箱
			 */
			setResult(1, data);
			Log.i("TAG", "onActivityResult: 转发用户注册信息");
			finish();
		}
		else if (requestCode == 1)
		{
			Log.i("TAG", "onActivityResult: 尝试返还注册同步的消息1");
			setResult(resultCode, data);
			Log.i("TAG", "onActivityResult: 尝试返还注册同步的消息2");
			finish();
		}
	}

	public void log_youxiang(View v)
	{
		//点击按键  用邮箱登录
		//切换到邮箱登录界面
		Intent intent = new Intent(Login.this, LoginYouXiang.class);
		startActivityForResult(intent, 1);//打开邮箱登陆界面
	}

	public void zhuce(View v)
	{
		//点击注册按键，切换都注册界面
		Log.i("TAG", "zhuce: 尝试打开注册界面1");

		Intent intent = new Intent(Login.this, ZhuCe.class);
		startActivityForResult(intent, 0);//打开注册界面

		Log.i("TAG", "zhuce: 打开注册界面3");
	}
	@SuppressLint("HandlerLeak")//处理登录post数据
	private Handler handler_log = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == CHANGE_UI)
			{
				String post_data = msg.obj.toString();//容器储存post返回数据，可用于登录
				//处理post返回数据email_data
				/*post数据：
					发送：
						发送用户输入的验证码和get邮箱返回的验证码加密结果
						用户的邮箱
					接受：
						net_md5		//用户用于向服务器发起请求的请求码
						new_time 	//服务器最后登录时间

				 */
				String[] post_data_item = post_data.split("&");
				Log.i("TAG", "handleMessage: " + post_data);
				if (post_data_item.length == 3)
				{
					userdata.net_md5 = post_data_item[2];
					userdata.new_time = Long.parseLong(post_data_item[1]);
					userdata.password="";
					Log.i("TAG", "handleMessage: " + userdata.net_md5 + userdata.new_time);
					Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
					//返回数据，用户登录
					Intent intent = new Intent();
					intent.putExtra("email", userdata.email);
					intent.putExtra("net_md5", userdata.net_md5);//这里返回的服务器请求码
					intent.putExtra("new_time", userdata.new_time);//服务器最后登录时间
					intent.putExtra("password", userdata.password);//用户的密码
					setResult(1, intent);
					//结束这个页面活动
					finish();
				}
				else
				{
					Toast.makeText(Login.this, "没听懂服务器说啥emmmmm", Toast.LENGTH_SHORT).show();
					Log.i("TAG", "handleMessage: post返回数据切割错误");
					thread_post_key = true;
				}


			}
			else if (msg.what == ERROR)
			{
				thread_post_key=true;//重新使能注册按键
				Toast.makeText(Login.this, "服务器不要我了>_<", Toast.LENGTH_SHORT).show();
				Log.i("TAG", "handleMessage: 服务器响应登录信息出现问题");
			}
		}
	};

	public void login_password(View v)
	{
		//点击登录按键的处理函数
		userdata.email = log_edit1.getText().toString().trim();
		userdata.password = log_edit2.getText().toString().trim();
		/*
		0	邮箱是否为空
		1	邮箱是否合法
		2	密码是否为空

		 */
		if (userdata.email.equals(""))//0用户邮箱空
		{
			Log.i("TAG", "run: 邮箱为空" + userdata.email);
			Log.i("TAG", "run: 邮箱为空");
			Toast.makeText(Login.this, "没有邮箱不可以哦O_O", Toast.LENGTH_SHORT).show();
		}
		else if (!JaoYan.jy_email(userdata.email))//1邮箱非法
		{
			Log.i("TAG", "yx_login: 邮箱非法");
			Toast.makeText(Login.this, "这个邮箱我不认识呃>_<", Toast.LENGTH_SHORT).show();

		}
		else if (userdata.password.equals(""))//3密码为空
		{
			Log.i("TAG", "yx_login: 密码为空");
			Toast.makeText(Login.this, "没有密码，才不给你进去‘_‘", Toast.LENGTH_SHORT).show();

		}
		else if (thread_post_key)
		{
			//向服务器发送请求
			//或者查询本地数据库登录
			Log.i("TAG", "yx_login: 进行登录操作");
			//new MyHttp().send_Post_String("email_log",MyKeyer.keyer_get_bytes("email="+userdata.email+"&password="+userdata.password,"pass"),handler_log,CHANGE_UI);
			Toast.makeText(Login.this, "该功能正在完善安全。请用邮箱登录，或者注册账号", Toast.LENGTH_SHORT).show();
			//thread_post_key=false;
		}
		else
		{
			Toast.makeText(Login.this, "稍等下，正在与服务器叽咕叽咕￥%@##*&……", Toast.LENGTH_SHORT).show();

		}
	}
}
