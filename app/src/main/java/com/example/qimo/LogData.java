package com.example.qimo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LogData extends AppCompatActivity
{
	TextView logdata_name, logdata_email, logdata_sex;
	ImageView logdata_image;
	UserData userdata;
	protected static final int CHANGE_UI = 0, ERROR = -1;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.i("login", "onCreate: 尝试打开用户界面");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logdata);
		Log.i("login", "onCreate: 尝试打开用户界面1");
		userdata = new UserData();
		logdata_name = findViewById(R.id.logdata_name);
		logdata_sex = findViewById(R.id.logdata_sex);
		logdata_email = findViewById(R.id.logdata_email);
		logdata_image = findViewById(R.id.logdata_image);
		Log.i("login", "onCreate: 尝试打开用户界面1");
		//在这里获取userdata
		Intent intent = getIntent();
		userdata.name = intent.getStringExtra("name");
		userdata.email = intent.getStringExtra("email");
		userdata.sex = intent.getByteExtra("sex", userdata.sex);
		userdata.user_head = intent.getStringArrayExtra("user_head");
		//在这里加载用户信息
		logdata_name.setText(userdata.name);
		switch (userdata.sex)
		{
			case 0:
				logdata_sex.setText("女");
				break;
			case 1:
				logdata_sex.setText("男");
				break;
			case 2:
				logdata_sex.setText("未知");
				break;
		}
		logdata_email.setText(userdata.email);
		//判断是否有图片，并加载
		if (!userdata.user_head[0].equals(""))
		{
			try
			{
				FileInputStream is = new FileInputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);

				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(is);
				logdata_image.setImageBitmap(bitmap);
				is.close();

			}
			catch (Exception e)
			{
				//e.printStackTrace();
				Log.i("TAG", "onActivityResult: 用户头像本地加载失败" + e.getMessage());
				new MyHttp().send_Get_Bitmap(MyHttp.IP + "user_head" + userdata.user_head[0] + "." + userdata.user_head[1], handler_head, CHANGE_UI);

			}
		}
		//Log.i("login", "onCreate: 成功打开用户界面" + userdata.user_head[0] + "收到图片内容");
		//super.onCreate(savedInstanceState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu2, menu);
		//return super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == 1)//从信息修改界面返回，且信息被修改
		{
			//信息被修改
			assert data != null;
			userdata.name = data.getStringExtra("name");
			userdata.email = data.getStringExtra("email");
			userdata.sex = data.getByteExtra("sex", userdata.sex);
			userdata.user_head = data.getStringArrayExtra("user_head");
			//在这里加载用户信息
			logdata_name.setText(userdata.name);
			switch (userdata.sex)
			{
				case 0:
					logdata_sex.setText("女");
					break;
				case 1:
					logdata_sex.setText("男");
					break;
				case 2:
					logdata_sex.setText("未知");
					break;
			}
			logdata_email.setText(userdata.email);
			//判断是否有图片，并加载
			try
			{

				FileInputStream is = new FileInputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);
				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(is);
				logdata_image.setImageBitmap(bitmap);
				is.close();

			}
			catch (Exception e)
			{
				//e.printStackTrace();
				Log.i("TAG", "onActivityResult: 用户头像加载失败" + e.getMessage());
			}
			//加载返回信息
			Intent intent = new Intent();
			intent.putExtra("email", userdata.email);
			intent.putExtra("sex", userdata.sex);
			intent.putExtra("name", userdata.name);
			intent.putExtra("user_head", userdata.user_head);
			setResult(1, intent);
		}
	}

	@SuppressLint("HandlerLeak")//listview 新闻网络图片显示
	private Handler handler_head = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what >= CHANGE_UI)
			{
				Bitmap bitmap = (Bitmap) msg.obj;
				logdata_image.setImageBitmap(bitmap);
				try
				{
					FileOutputStream fileOutputStream = new FileOutputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);

					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(LogData.this, "头像请求失败>_<", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		Log.i("nemu", "onOptionsItemSelected: 菜单");
		switch (item.getItemId())
		{
			case R.id.menu2_set:
				//用户尝试编辑个人信息
				Log.i("nemu", "onOptionsItemSelected: 编辑");
				//在此添加安全功能

				//打开编辑页面
				Intent intent = new Intent(LogData.this, SetData.class);

				intent.putExtra("email", userdata.email);
				intent.putExtra("sex", userdata.sex);
				intent.putExtra("name", userdata.name);
				intent.putExtra("user_head", userdata.user_head);

				startActivityForResult(intent, 2);//打开修改信息页面
				break;

		}
		//return super.onOptionsItemSelected(item);
		return true;
	}
}
