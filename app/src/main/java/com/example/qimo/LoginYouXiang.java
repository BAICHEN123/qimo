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


public class LoginYouXiang extends Activity
{
	protected static final int CHANGE_UI = 1, ERROR = 0;
	EditText log_edit1, log_edit2;
	String email_data = null;//储存发送邮件的返回的数据
	String post_data = null;//储存post返回的数据

	String app_password = null;//储存APP验证码
	String user_password = null;//储存用户输入的验证码
	UserData userdata;
	private int send_time_num;
	private boolean send_time_key = true;
	private int post_time_num;
	private boolean thread_post_key = true;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.i("login", "onCreate: 尝试打开邮箱登录界面");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginyouxiang);
		log_edit1 = findViewById(R.id.log_edit1);
		log_edit2 = findViewById(R.id.log_edit2);
		userdata = new UserData();
		Log.i("login", "onCreate: 成功打开邮箱登录界面");
	}

	@SuppressLint("HandlerLeak")//处理发送验证码
	private Handler handler_send = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == CHANGE_UI)
			{
				email_data = msg.obj.toString();//容器储存返回数据
				if (email_data.startsWith("email found"))
				{
					email_data = "";
					app_password = "";
					Toast.makeText(LoginYouXiang.this, "邮箱还未注册", Toast.LENGTH_SHORT).show();
				}
				else if (JaoYan.jy_password(email_data))
				{
					app_password = email_data;//储存返回的验证码
					Toast.makeText(LoginYouXiang.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(LoginYouXiang.this, "服务器无法对" + userdata.email + "发送邮件", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@SuppressLint("HandlerLeak")//处理登录post数据
	private Handler handler_log = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == CHANGE_UI)
			{
				post_data = msg.obj.toString();//容器储存post返回数据，可用于登录
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
					//用户注册之后从未修改过个人信息
					userdata.net_md5 = post_data_item[2];
					userdata.new_time = Long.parseLong(post_data_item[1]);
					userdata.password = "";
					Log.i("TAG", "handleMessage: " + userdata.net_md5 + userdata.new_time);
					Toast.makeText(LoginYouXiang.this, "登录成功", Toast.LENGTH_SHORT).show();
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
				else if(post_data_item.length == 7)
				{
					//用户注册之后修改过注册信息
					//将服务器传回的数据加载到本地
					//Log.i("TAG", "handleMessage: " + userdata.net_md5 + userdata.new_time);
					Toast.makeText(LoginYouXiang.this, "登录成功", Toast.LENGTH_SHORT).show();
					//返回数据，用户登录
					Intent intent = new Intent();
					intent.putExtra("email", userdata.email);
					intent.putExtra("net_md5", post_data_item[2]);//这里返回的服务器请求码
					intent.putExtra("new_time", post_data_item[1]);//服务器最后登录时间
					intent.putExtra("name", post_data_item[3]);
					intent.putExtra("sex", post_data_item[4]);
					intent.putExtra("user_head0", post_data_item[5]);
					intent.putExtra("user_head1", post_data_item[6]);
					setResult(2, intent);
					//结束这个页面活动
					finish();
				}
				else if (post_data.equals("jxerror0"))
				{
					Toast.makeText(LoginYouXiang.this, "再检查一下验证码O_O,服务器君不认识", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(LoginYouXiang.this, "没听懂服务器说啥emmmmm", Toast.LENGTH_SHORT).show();
					Log.i("TAG", "handleMessage: post返回数据切割错误");
				}

			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(LoginYouXiang.this, "服务器不要我了>_<", Toast.LENGTH_SHORT).show();
				Log.i("TAG", "handleMessage: 服务器响应登录信息出现问题");
			}

			new Thread()//延时使能登录按键
			{
				public void run()
				{
					post_time_num = 5;
					try
					{
						while (post_time_num-- > 0)
						{
							Thread.sleep(1000);
						}
						thread_post_key = true;
					}
					catch (Exception e)
					{
						Log.i("TAG", "run: 延时失败");//2280057905@qq.com
					}
				}
			}.start();

		}
	};

	//发送邮件的按钮
	public void yx_send(View v)
	{
		//发送验证码
		//判断时间间隔，防止请求过量
		//成功发送邮件之后time_key设置为true
		userdata.email = log_edit1.getText().toString().trim();
		if (userdata.email.equals(""))
		{
			Toast.makeText(LoginYouXiang.this, "邮箱不见了O_O", Toast.LENGTH_SHORT).show();
		}
		else if (!send_time_key)
		{
			Toast.makeText(LoginYouXiang.this, send_time_num + "S后再试^_^,不然服务器君会罢工的", Toast.LENGTH_SHORT).show();
		}
		else if (!JaoYan.jy_email(userdata.email))
		{

			Toast.makeText(LoginYouXiang.this, "这个邮箱我看不懂啊&#@%&*", Toast.LENGTH_SHORT).show();
		}
		else
		{
			//拼接url，发送请求

			new MyHttp().send_Post_String(MyHttp.IP + "email_send_login", "email=" + userdata.email, handler_send, CHANGE_UI);

			new Thread()//延时使能按键
			{
				public void run()
				{
					send_time_num = 60;
					try
					{
						while (send_time_num-- > 0)
						{
							Thread.sleep(1000);
						}
						send_time_key = true;
					}
					catch (Exception e)
					{
						Log.i("TAG", "run: 延时失败");//2280057905@qq.com
					}
				}
			}.start();
			send_time_key = false;
		}
	}

	//登录按钮
	public void yx_login(View v)
	{
		user_password = log_edit2.getText().toString().trim();
		//处理用户点击登录的事件
		//判定在发送邮件之后是否修改过邮箱
		/*
		0	邮箱为空
		1	邮箱是否修改
		2	APP验证码是否到达
		3	APP验证码是否合法
		4	用户验证码是否为空
		5	用户输入验证码是否合法
		 */
		if (userdata.email == null || userdata.email.equals(""))//0用户邮箱空
		{
			Log.i("TAG", "run: 邮箱为空" + userdata.email);
			Log.i("TAG", "run: 邮箱为空");
			Toast.makeText(LoginYouXiang.this, "没有邮箱不可以哦O_O", Toast.LENGTH_SHORT).show();
		}
		else if (!userdata.email.equals(log_edit1.getText().toString().trim()))//1邮箱被修改
		{
			Log.i("TAG", "yx_login: 还未发送该邮箱的验证码");
			Toast.makeText(LoginYouXiang.this, "还未发送该邮箱的验证码", Toast.LENGTH_SHORT).show();

		}
		else if (user_password == null || user_password.equals(""))//4用户验证码为空
		{
			Log.i("TAG", "yx_login: 用户未输入的验证码");
			Toast.makeText(LoginYouXiang.this, "验证码不见了O_O", Toast.LENGTH_SHORT).show();
		}
		else if (!JaoYan.jy_password(user_password))//5用户输入的验证码非法
		{
			Log.i("TAG", "yx_login: 用户输入的验证码非法");
			Toast.makeText(LoginYouXiang.this, "在检查一下验证码哦^_^", Toast.LENGTH_SHORT).show();
		}
		else if (app_password == null || app_password.equals(""))//2APP验证码未收到
		{
			Log.i("TAG", "yx_login: 验证码APP未收到");
			Toast.makeText(LoginYouXiang.this, "验证码APP未收到", Toast.LENGTH_SHORT).show();
		}
		else if (!JaoYan.jy_password(app_password))//3APP验证码非法
		{
			Log.i("TAG", "yx_login: APP验证码非法");
			Toast.makeText(LoginYouXiang.this, "APP收到的验证码非法-_-", Toast.LENGTH_SHORT).show();
		}
		else if (thread_post_key)
		{
			/*post数据：
				发送：
					发送用户输入的验证码和get邮箱返回的验证码加密结果
					用户的邮箱
				接受：
					name
					head_md5
					head_end
					sex
					net_md5		//用户用于向服务器发起请求的请求码
					new_time 	//服务器最后登录时间
			 */
			//发送post请求
			new MyHttp().send_Post_String(MyHttp.IP + "email_log_login" + MyKeyer.MyMd5(userdata.email), MyKeyer.keyer_get_bytes("email=" + userdata.email + "&password1=" + user_password + "&password2=" + app_password, user_password), handler_log, CHANGE_UI);
			thread_post_key = false;
		}
		else
		{
			//Toast.makeText(LoginYouXiang.this, "正在与服务器叽咕叽咕￥%@##*&……", Toast.LENGTH_SHORT).show();
			Toast.makeText(LoginYouXiang.this, "改了吗，就来按按按……" + post_time_num + "S", Toast.LENGTH_SHORT).show();
		}
	}

}
