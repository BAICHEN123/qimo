package com.example.qimo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SetData extends Activity
{
	private static final int RC_CHOOSE_PHOTO = 1;
	protected static final int CHANGE_UI = 0, ERROR = -1;
	EditText setdata_name;
	TextView setdata_email;
	RadioButton manradio, womanradio;
	ImageView setdata_image;
	UserData userdata;
	Intent intent;
	String path = null;
	boolean thread_key_pic = false, thread_key_userdata = false, key_save = true;
	boolean thread_r_pic = false, thread_r_userdata = false, thread_r_save = false;


	/*
	当前问题
	缓存头像:用户换一次头像就会缓存一个新头像，即使头像相同也会缓存
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{

		Log.i("TAG", "onCreate: 打开编辑用户信息界面");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setdata);
		userdata = new UserData();
		intent = new Intent();
		setdata_name = findViewById(R.id.setdata_name);
		setdata_email = findViewById(R.id.setdata_email);
		manradio = findViewById(R.id.radioButton1);
		womanradio = findViewById(R.id.radioButton2);
		setdata_image = findViewById(R.id.setdata_image);
		Intent intent = getIntent();
		userdata.name = intent.getStringExtra("name");
		userdata.email = intent.getStringExtra("email");
		userdata.sex = intent.getByteExtra("sex", userdata.sex);
		userdata.user_head = intent.getStringArrayExtra("user_head");
		setdata_name.setText(userdata.name);
		setdata_email.setText(userdata.email);
		if (userdata.sex == 1)
		{
			manradio.setChecked(true);
		}
		else if (userdata.sex == 0)
		{
			womanradio.setChecked(true);
		}

		//判断是否有图片，并加载
		if (userdata.user_head[0].compareTo("") != 0)
		{
			try
			{
				FileInputStream is = new FileInputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);
				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(is);
				setdata_image.setImageBitmap(bitmap);
				is.close();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Log.i("TAG", "onActivityResult: 用户头像加载失败");
			}
		}
		Log.i("TAG", "onCreate: 打开编辑用户信息界面成功");
	}

	public void setdata_exit(View v)
	{
		//取消修改信息的操作
		//结束该界面，返回上个页面
		this.finish();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler_key = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if ((!thread_key_pic) || (!thread_key_userdata))
			{
				Toast.makeText(SetData.this, "过段时间再试吧，没有网络服务>_<", Toast.LENGTH_SHORT).show();
			}
			finish();
		}

	};

	@SuppressLint("HandlerLeak")//用户文字信息更新
	private Handler handler_update = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what >= CHANGE_UI)
			{
				String post_data = (String) msg.obj;
				if (post_data.startsWith("update 1"))
				{
					thread_key_userdata = true;
					//Toast.makeText(SetData.this, "同步 用户信息 成功^_^", Toast.LENGTH_SHORT).show();
					if (thread_key_pic )//文字图片信息全部同步成功
					{
						Toast.makeText(SetData.this, "信息同步成功^_^", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				else if (post_data.startsWith("update error 1"))
				{
					Toast.makeText(SetData.this, "服务器处理请求失败。。。", Toast.LENGTH_SHORT).show();
					thread_r_userdata = true;
				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(SetData.this, "失去与服务器连接>_<", Toast.LENGTH_SHORT).show();
				thread_r_userdata = true;
			}
		}
	};
	@SuppressLint("HandlerLeak")//用户头衔更新
	private Handler handler_update_pic = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what >= CHANGE_UI)
			{
				String post_data = (String) msg.obj;
				if (post_data.startsWith("update 1"))
				{
					thread_key_pic = true;
					//Toast.makeText(SetData.this, "同步 头像 成功^_^", Toast.LENGTH_SHORT).show();
					if (thread_key_userdata)//文字图片信息全部同步成功
					{
						Toast.makeText(SetData.this, "信息同步成功^_^", Toast.LENGTH_SHORT).show();
						finish();
					}

				}
				else if (post_data.startsWith("update error 1"))
				{
					Toast.makeText(SetData.this, "服务器处理请求失败。。。", Toast.LENGTH_SHORT).show();
					thread_r_pic = true;
				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(SetData.this, "失去与服务器连接>_<", Toast.LENGTH_SHORT).show();
				thread_r_pic = true;
			}
		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//相册界面返回照片信息的处理函数
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_CHOOSE_PHOTO && RESULT_OK == resultCode)
		{
			Uri uri = data.getData();
			assert uri != null;
			Log.i("TAG", "onActivityResult: " + uri.toString());
			path = FileUtil.getFilePathByUri(this, uri);
			setdata_image.setImageURI(uri);
			try
			{
				FileInputStream fileInputStream = new FileInputStream(path);
				byte[] buffer = new byte[fileInputStream.available()];
				fileInputStream.read(buffer);
				userdata.user_head[0] = MyKeyer.MyMd5(buffer);
				fileInputStream.close();
			}
			catch (Exception e)
			{
				Log.i("TAG", "onActivityResult: 图片文件取MD5出错");
			}
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
		{
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, RC_CHOOSE_PHOTO);
		}
	}

	public void get_image(View v)
	{
		//先择头像头片的处理函数v

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			//未授权，申请授权(从相册选择图片需要读取存储卡的权限)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);

			Log.i("TAG", "get_image: 成功获取权限0");
		}
		else
		{
			//已授权，获取照片
			Log.i("TAG", "get_image: 成功获取权限1");
			//choosePhoto();
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, RC_CHOOSE_PHOTO);
		}
		//收到图片之后将图片放到缓存目录，方便读取
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void setdata_save(View v)
	{
		if (key_save)
		{
			key_save = false;
			if (path != null && !path.equals(""))//如果头像被修改
			{
				//将用户的头像图片缓存到这个目录getApplicationContext().getFilesDir();
				try
				{
					FileInputStream fileInputStream = new FileInputStream(path);
					FileOutputStream fileOutputStream = new FileOutputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);

					byte[] buffer = new byte[fileInputStream.available()];
					fileInputStream.read(buffer);
					//提交图片信息给服务器
					new MyHttp().send_Post_String(MyHttp.IP + "pic=" + userdata.user_head[0] + "." + userdata.user_head[1], buffer, handler_update_pic, CHANGE_UI);
					fileOutputStream.write(buffer);
					fileInputStream.close();
					fileOutputStream.flush();
					fileOutputStream.close();
					Log.i("TAG", "setdata_save: 成功转存用户头像");
				}
				catch (Exception e)
				{
					Log.i("TAG", "setdata_save: 转存用户头像出错");
				}
			}
			else
			{
				//无需同步照片
				//将图片锁设为true
				thread_key_pic = true;
			}

			userdata.name = setdata_name.getText().toString().trim();
			if (manradio.isChecked())
			{
				userdata.sex = 1;
			}
			else if (womanradio.isChecked())
			{
				userdata.sex = 0;
			}

			//校验得到的信息

			//更新本地数据库
			Log.i("TAG", "onCreate: 尝试编辑数据库");
			MySqlLite sql = new MySqlLite(getApplicationContext());
			sql.setSqlUserData(userdata);
			Log.i("TAG", "onCreate: 编辑数据库成功");

			//打包要返回给上个界面的信息
			intent.putExtra("email", userdata.email);
			intent.putExtra("sex", userdata.sex);
			intent.putExtra("name", userdata.name);
			intent.putExtra("user_head", userdata.user_head);
			setResult(1, intent);


			//提交文字信息给服务器
			new MyHttp().send_Post_String(MyHttp.IP + "update", "name=" + userdata.name + "&email=" + userdata.email + "&sex=" + userdata.sex + "&user_head_md5=" + userdata.user_head[0] + "&user_head_end=" + userdata.user_head[1], handler_update, CHANGE_UI);

			new Thread()
			{
				@Override
				public void run()
				{
					super.run();
					int i = 3;
					while (i-- > 0)
					{
						try
						{
							sleep(5000);
							if (thread_key_pic && thread_key_userdata)
							{
								finish();
							}
							else if (thread_r_save && thread_r_pic)
							{
								//图片保存服务异常
								//重建服务
								FileInputStream fileInputStream = new FileInputStream(path);
								byte[] buffer = new byte[fileInputStream.available()];
								new MyHttp().send_Post_String(MyHttp.IP + "pic=" + userdata.user_head[0] + "." + userdata.user_head[1], buffer, handler_update_pic, CHANGE_UI);
								fileInputStream.close();

							}
							else if (thread_r_save && thread_r_userdata)
							{
								//文字保存服务异常
								//重建服务
								new MyHttp().send_Post_String(MyHttp.IP + "update", "name=" + userdata.name + "&email=" + userdata.email + "&sex=" + userdata.sex + "&user_head_md5=" + userdata.user_head[0] + "&user_head_end=" + userdata.user_head[1], handler_update, CHANGE_UI);
							}

						}
						catch (Exception e)
						{
							Log.i("TAG", "run: 检查保存服务延时异常");
						}
					}
					handler_key.sendMessage(new Message());
				}
			}.start();

			//收到服务器修改成功的通知？
			//写在了线程回收函数里

			//退出设置界面
			//finish();
		}
		else
		{
			Toast.makeText(SetData.this, "再存了，在存了，别催我-_-", Toast.LENGTH_SHORT).show();
		}

	}
}
