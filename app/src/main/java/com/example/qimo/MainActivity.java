package com.example.qimo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Vector;

import static android.view.View.inflate;

public class MainActivity extends AppCompatActivity
{
	UserData userdata;
	protected static final int CHANGE_UI = 0, ERROR = -1;
	private ListView main_list;
	MyList mylist;//list视图和新闻信息列表的桥接-BaseAdapter
	MySqlLite sql;
	boolean f5time = true;
	Vector<MyViewData> liat_myviewdata1 = null;//储存mainActivity的新闻信息列表
	Vector<MyViewData> liat_myviewdata2 = null;//储存mainActivity的订阅信息列表

	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		userdata = new UserData();
		Cursor cursor;
		main_list = findViewById(R.id.main_list);
		liat_myviewdata1 = new Vector<MyViewData>();
		mylist = new MyList(getApplicationContext(), liat_myviewdata1);
		main_list.setAdapter(mylist);


		//向服务器请求listview显示的内容数据包
		new MyHttp().send_Get_String(MyHttp.IP + "news", handler_news, mylist.listview_show_id);

		//Log.i("TAG", "onCreate: mainActivity数据库0");
		try
		{

			sql = new MySqlLite(getApplicationContext());
			SQLiteDatabase db = sql.getWritableDatabase();
			cursor = db.rawQuery("select * from UserData where id=1", null);
			Log.i("TAG", "onCreate: mainActivity数据库1");
			if (cursor.moveToFirst())
			{
				//加载最近登录的用户的邮箱
				Log.i("TAG", "onCreate: 数据库21");
				userdata.email = cursor.getString(cursor.getColumnIndex("name"));
				Log.i("TAG", "onCreate: 数据库22");
			}
			else
			{
				//数据库没有最近登录元素
				Log.i("TAG", "onCreate: 新加1号元素");
				sql.addUserData(userdata, 1);
			}
			Log.i("TAG", "onCreate:mainActivity数据库3");
			db.close();
			db = sql.getWritableDatabase();
			if (userdata.email == null || userdata.email.equals(""))
			{
				//查询数据库，没有用户登录过
				Toast.makeText(MainActivity.this, "没有用户登录", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Log.i("TAG", "onCreate: mainActivity数据库5" + userdata.email);
				//查询到最后一次登录的用户名
				cursor = db.rawQuery("select * from UserData where id>1 and email=?", new String[]{userdata.email});
				if (cursor.moveToFirst())
				{
					userdata.email = cursor.getString(cursor.getColumnIndex("email"));
					userdata.user_head[0] = cursor.getString(cursor.getColumnIndex("head_md5"));
					userdata.user_head[1] = cursor.getString(cursor.getColumnIndex("head_end"));
					userdata.name = cursor.getString(cursor.getColumnIndex("name"));
					userdata.sex = Byte.parseByte(String.valueOf(cursor.getString(cursor.getColumnIndex("sex"))));
					userdata.new_time = Long.parseLong(cursor.getString(cursor.getColumnIndex("new_time")));
					userdata.net_md5 = cursor.getString(cursor.getColumnIndex("net_md5"));
					Toast.makeText(MainActivity.this, "本地信息加载完毕", Toast.LENGTH_SHORT).show();

					//在这里向服务器发起请求，同步用户的信息	//未实现

					sql.addUserData(userdata, 1);
				}
				else
				{
					Toast.makeText(MainActivity.this, "用户身份过期", Toast.LENGTH_LONG).show();
				}
				db.close();
			}
		}
		catch (Exception e)
		{
			Log.i("TAG", "onCreate: 数据库出现问题" + e.getMessage());
			Toast.makeText(MainActivity.this, "本地数据库出现问题" + e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}


	@SuppressLint("HandlerLeak")//接收显示的信息包，并处理
	private Handler handler_news = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == mylist.listview_show_id)
			{
				/*
				设置个变量储存上一次更新的分类
				如果分类发生变化，就切换列表视图
				如果分类无变化，就更新列表视图
				 */
				String str_newsdatas;
				str_newsdatas = (String) msg.obj;
				try
				{
					//先分割后解码
					//str_newsdatas = URLDecoder.decode(str_newsdatas, "UTF-8");
					String[] str_viewdata;
					MyViewData myviewdata;
					for (String str_newsdata : str_newsdatas.split("#{1,2}"))
					{
						//Log.i("TAG", "handleMessage: 数据" + str_newsdata);
						str_viewdata = str_newsdata.split("&");
						if (str_viewdata.length == 3)
						{
							myviewdata = new MyViewData();
							myviewdata.text = URLDecoder.decode(str_viewdata[0], "UTF-8");
							//Log.i("TAG", "handleMessage: " + str_viewdata[0]);
							myviewdata.p_url = URLDecoder.decode(str_viewdata[1], "UTF-8");
							//Log.i("TAG", "handleMessage: " + str_viewdata[1]);
							myviewdata.url = URLDecoder.decode(str_viewdata[2], "UTF-8");
							//Log.i("TAG", "handleMessage: " + str_viewdata[2]);
							mylist.new_BaseAdapter(myviewdata);
						}
					}
				}
				catch (Exception e)
				{
					Log.i("TAG", "handleMessage: 数据解码出现问题" + e.getMessage());
				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(MainActivity.this, "新闻数据请求错误，点击分类刷新", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@SuppressLint("HandlerLeak")//接收显示的信息包，并处理
	private Handler handler_uget = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == mylist.listview_show_id)
			{
				/*
				设置个变量储存上一次更新的分类
				如果分类发生变化，就切换列表视图
				如果分类无变化，就更新列表视图
				 */
				String str_newsdatas;
				str_newsdatas = (String) msg.obj;
				//先分割后解码
				//str_newsdatas = URLDecoder.decode(str_newsdatas,"UTF-8");
				//Log.i("TAG", "handleMessage: 数据" + str_newsdatas);
				String[] str_viewdata;
				MyViewData myviewdata;
				for (String str_newsdata : str_newsdatas.split("#{1,2}"))
				{
					//Log.i("TAG", "handleMessage: 数据" + str_newsdata);
					try
					{
						str_viewdata = str_newsdata.split("&");
						if (str_viewdata.length == 2)
						{
							myviewdata = new MyViewData();
							myviewdata.text = URLDecoder.decode(str_viewdata[0], "UTF-8");
							//Log.i("TAG", "handleMessage: " + URLDecoder.decode(str_viewdata[0], "UTF-8"));
							myviewdata.p_url = URLDecoder.decode(str_viewdata[1], "UTF-8");
							//Log.i("TAG", "handleMessage: " + URLDecoder.decode(str_viewdata[1], "UTF-8"));
							myviewdata.url = null;
							mylist.new_BaseAdapter(myviewdata);
						}
						else if (str_viewdata.length == 1 && !str_viewdata[0].equals(""))
						{
							myviewdata = new MyViewData();
							myviewdata.text = URLDecoder.decode(str_viewdata[0], "UTF-8");
							//Log.i("TAG", "handleMessage: " + URLDecoder.decode(str_viewdata[0], "UTF-8"));
							myviewdata.p_url = null;
							myviewdata.url = null;
							mylist.new_BaseAdapter(myviewdata);
						}
					}
					catch (Exception e)
					{
						Log.i("TAG", "handleMessage: uget数据解码某条出现问题" + str_newsdata + e.getMessage());
					}

				}
			}
			else if (msg.what == ERROR)
			{
				Toast.makeText(MainActivity.this, "订阅数据请求错误，点击分类刷新", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@SuppressLint("HandlerLeak")//listview 新闻网络图片显示
	private Handler handler_1 = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what >= CHANGE_UI)
			{
				liat_myviewdata1.get(msg.what).setBitmapData((Bitmap) msg.obj);
				//Log.i("TAG", "run: ---" + liat_myviewdata1.get(msg.what).text);
				if (mylist.listview_show_id == mylist.news)
				{
					mylist.new_BaseAdapter();
				}
			}
			else if (msg.what == ERROR)
			{
				liat_myviewdata1.get((int) msg.obj).thread_key = false;
				//Toast.makeText(MainActivity.this, "图片请求错误", Toast.LENGTH_SHORT).show();
				//Log.i("TAG", "图片请求错误: " + liat_myviewdata1.get((int) msg.obj).text);
			}
		}
	};
	@SuppressLint("HandlerLeak")//listview uget网络图片显示
	private Handler handler_2 = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what >= CHANGE_UI)
			{
				liat_myviewdata2.get(msg.what).setBitmapData((Bitmap) msg.obj);
				//Log.i("TAG", "run: ---" + liat_myviewdata2.get(msg.what).text);
				if (mylist.listview_show_id == mylist.uget)
				{
					mylist.new_BaseAdapter();
				}
			}
			else if (msg.what == ERROR)
			{

				liat_myviewdata2.get((int) msg.obj).thread_key = false;
				//Toast.makeText(MainActivity.this, "图片请求错误", Toast.LENGTH_SHORT).show();
				//Log.i("TAG", "图片请求错误: " + liat_myviewdata2.get((int) msg.obj).text+liat_myviewdata2.get((int) msg.obj).p_url);
			}
		}
	};

	private class MyList extends BaseAdapter
	{
		Context context;
		final byte news = 1, uget = 2, other = 3, pic = 4;
		byte listview_show_id;
		Vector<MyViewData> liat_myviewdata;//储存listview当前显示内动的数据
		/*listview_show_id
		news=1	新闻
		uget=2	用户订阅
		other=3	其他
		pic=4	图片
		 */

		public MyList(Context context, Vector<MyViewData> liat_myviewdata)
		{
			super();
			this.context = context;
			this.liat_myviewdata = liat_myviewdata;
			listview_show_id = 1;
		}

		@Override
		public int getCount()
		{
			return this.liat_myviewdata.size();
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;
			TextView textView;
			//获取listview_show_id，决定添加界面的格式
			switch (this.listview_show_id)
			{
				case 1:
					//新闻界面加载方式
					view = inflate(MainActivity.this, R.layout.listview_item1, null);
					textView = view.findViewById(R.id.item1_text);
					textView.setText(this.liat_myviewdata.get(position).text);
					this.liat_myviewdata.get(position).setImageData((ImageView) view.findViewById(R.id.item1_image));
					if (this.liat_myviewdata.get(position).getbitmap_key())//对应的item的线程未开启
					{
						this.liat_myviewdata.get(position).imageview.setImageBitmap(this.liat_myviewdata.get(position).getBitmapData());
					}
					else if (!this.liat_myviewdata.get(position).thread_key)
					{
						//在这里添加请求图片的线程
						new MyHttp().send_Get_Bitmap(this.liat_myviewdata.get(position).p_url, handler_1, position);
						this.liat_myviewdata.get(position).thread_key = true;

					}
					break;
				case 2:
					//用户订阅加载方式
					if (this.liat_myviewdata.get(position).p_url != null)
					{
						//Log.i("TAG", "handleMessage: 检查到图片url" + this.liat_myviewdata.get(position).p_url);
						//有图片的知乎热搜
						view = inflate(MainActivity.this, R.layout.listview_item2, null);
						textView = view.findViewById(R.id.item2_text);
						textView.setText(this.liat_myviewdata.get(position).text);
						this.liat_myviewdata.get(position).setImageData((ImageView) view.findViewById(R.id.item2_image));
						if (this.liat_myviewdata.get(position).getbitmap_key())//对应的item的线程未开启
						{
							//this.liat_myviewdata.get(position).setImageData((ImageView) view.findViewById(R.id.item2_image));
							this.liat_myviewdata.get(position).imageview.setImageBitmap(this.liat_myviewdata.get(position).getBitmapData());
						}
						else if (!this.liat_myviewdata.get(position).thread_key)
						{
							//在这里添加请求图片的线程
							new MyHttp().send_Get_Bitmap(this.liat_myviewdata.get(position).p_url, handler_2, position);
							//Log.i("TAG", "handleMessage: 成功创建线程" + this.liat_myviewdata.get(position).p_url);
							this.liat_myviewdata.get(position).thread_key = true;//线程锁，防止，一个链接开多个线程
						}
					}
					else
					{
						//没有图片的知乎热搜
						view = inflate(MainActivity.this, R.layout.listview_item3, null);
						textView = view.findViewById(R.id.item3_text);
						textView.setText(this.liat_myviewdata.get(position).text);
					}
					break;
				case 3:
					//其他分类加载方式

					break;
				case 4:
					//图片加载方式

					break;
			}


			return view;
		}

		public void new_BaseAdapter(MyViewData list_item)
		{//用于添加item
			if (this.liat_myviewdata == null)
			{
				this.liat_myviewdata = new Vector<MyViewData>();
			}
			Log.i("TAG", "new_BaseAdapter: " + list_item.text);
			this.liat_myviewdata.add(list_item);
			this.notifyDataSetChanged();
		}

		public void new_BaseAdapter()
		{//用于添加item
			if (this.liat_myviewdata == null)
			{
				this.liat_myviewdata = new Vector<MyViewData>();
			}
			this.notifyDataSetChanged();
		}

		public void set_Vector(Vector<MyViewData> liat_myviewdata, byte id)
		{
			if (liat_myviewdata != null)
			{
				this.listview_show_id = id;
				this.liat_myviewdata = liat_myviewdata;
				this.notifyDataSetChanged();
			}
		}


	}


	/*
	处理main界面打开的界面返回的参数
	登录界面
	用户信息界面
	 */
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == 1)//用户 登录 界面返回的数据
		{
			//获取到登录界面返回的信息
			assert data != null;
			Log.i("TAG", "onActivityResult: 处理登录界面返回的消息");
			if (!userdata.email.equals(data.getStringExtra("email")))//这个用户和已经登陆的用户不一样
			{
				Log.i("TAG", "onActivityResult: 用户和已经登陆的用户不一样");
				userdata = new UserData();
				userdata.email = data.getStringExtra("email");
				//查询是否曾经登陆过
				//登陆过就加载信息
				Cursor cursor = sql.getWritableDatabase().rawQuery("select * from UserData where id>1 and email=?", new String[]{userdata.email});
				Log.i("TAG", "onActivityResult: 查询用户是否曾经登陆过");
				if (cursor.moveToFirst())
				{//用户曾经在本地登录过
					Log.i("TAG", "onActivityResult: 查询用户曾经登陆过");
					//userdata.email = cursor.getString(cursor.getColumnIndex("email"));
					userdata.user_head[0] = cursor.getString(cursor.getColumnIndex("head_md5"));
					userdata.user_head[1] = cursor.getString(cursor.getColumnIndex("head_end"));
					userdata.name = cursor.getString(cursor.getColumnIndex("name"));
					userdata.sex = Byte.parseByte(String.valueOf(cursor.getString(cursor.getColumnIndex("sex"))));
					Log.i("TAG", "onActivityResult: 查询用户曾经登陆过1");
					String password1 = data.getStringExtra("password");
					Log.i("TAG", "onActivityResult: 查询用户曾经登陆过2");
					if (password1 == null || Objects.equals(password1, ""))
					{//用户没有更新密码
						userdata.password = "";
					}
					else
					{//用户的密码更新
						userdata.password = password1;
					}
					Log.i("TAG", "onActivityResult: 查询用户曾经登陆过3");
					userdata.new_time = data.getLongExtra("new_time", 1);
					userdata.net_md5 = data.getStringExtra("net_md5");
					Log.i("TAG", "onActivityResult: 查询用户曾经登陆过4");
					Toast.makeText(MainActivity.this, "用户信息加载完毕~", Toast.LENGTH_LONG).show();
					sql.addUserData(userdata);
					//userdata.email="1234";
					sql.addUserData(userdata, 1);
				}
				else
				{
					Log.i("TAG", "onActivityResult: 查询用户曾经没有登陆过");
					//没有在本地登陆过
					//入库
					userdata.net_md5 = data.getStringExtra("net_md5");
					userdata.new_time = data.getLongExtra("new_time", 0);
					userdata.password = data.getStringExtra("password");
					if (sql.addUserData(userdata) && sql.addUserData(userdata, 1))
					{
						Toast.makeText(MainActivity.this, "欢迎您^_^", Toast.LENGTH_LONG).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, "用户数据本地存档失败>_<", Toast.LENGTH_LONG).show();
					}

				}

			}
			else//这个用户已经登录过
			{
				Log.i("TAG", "onActivityResult: 登录的用户和当前用户相同");
				userdata.net_md5 = data.getStringExtra("net_md5");
				userdata.new_time = data.getLongExtra("new_time", 0);
				String password1 = data.getStringExtra("password");
				if (password1 == null || Objects.equals(password1, ""))
				{//用户没有更新密码
					//userdata.password = cursor.getString(cursor.getColumnIndex("password"));
					Log.i("TAG", "onActivityResult: 此次活动，没有返回密码");
				}
				else
				{//用户的密码更新
					userdata.password = password1;
				}
				if (sql.addUserData(userdata) && sql.addUserData(userdata, 1))
				{
					Toast.makeText(MainActivity.this, "用户数据更新成功", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(MainActivity.this, "用户数据更新失败", Toast.LENGTH_LONG).show();
				}
			}
			Log.i("TAG", "onActivityResult: 收到登录信息");
		}
		if (requestCode == 0 && resultCode == 2)//用户 登录 界面返回 同步后 的用户数据
		{
			assert data != null;

			Log.i("TAG", "onActivityResult: 处理登录同步的信息1");
			if (!userdata.email.equals(data.getStringExtra("email")))//这个用户和已经登陆的用户不一样
			{
				userdata = new UserData();
				userdata.email = data.getStringExtra("email");
			}
			userdata.new_time = Long.parseLong(Objects.requireNonNull(data.getStringExtra("new_time")));
			userdata.net_md5 = data.getStringExtra("net_md5");
			userdata.name = data.getStringExtra("name");
			userdata.sex = Byte.parseByte(Objects.requireNonNull(data.getStringExtra("sex")));
			userdata.user_head[0] = data.getStringExtra("user_head0");
			userdata.user_head[1] = data.getStringExtra("user_head1");
			Log.i("TAG", "onActivityResult: 处理登录同步的信息2");
			if (sql.addUserData(userdata) && sql.addUserData(userdata, 1))
			{
				Toast.makeText(MainActivity.this, "欢迎您^_^", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(MainActivity.this, "用户数据本地存档失败>_<", Toast.LENGTH_LONG).show();
			}
		}
		else if (requestCode == 1 && resultCode == 1)//用户 信息 界面返回的数据
		{
			//获取到修改的信息
			//判断信息修改情况，然后发送给服务器
			assert data != null;
			userdata.name = data.getStringExtra("name");
			userdata.email = data.getStringExtra("email");
			userdata.sex = data.getByteExtra("sex", userdata.sex);
			userdata.user_head = data.getStringArrayExtra("user_head");

			//旧的用户信息同步服务
			/*String[] head = data.getStringArrayExtra("user_head");
			if (!userdata.user_head[0].equals(head[0]))
			{
				userdata.user_head = head;
				//用户修改了头像
				//打包图片发送给服务器
				try
				{
					FileInputStream is = new FileInputStream(getApplicationContext().getFilesDir() + "/" + userdata.user_head[0] + "." + userdata.user_head[1]);
					byte[] buffer = new byte[is.available()];
					is.read(buffer);
					is.close();
					new MyHttp().send_Post_String(MyHttp.IP + "pic=" + userdata.user_head[0] + "." + userdata.user_head[1], buffer, handler_update_pic, CHANGE_UI);

				}
				catch (Exception e)
				{
					//e.printStackTrace();
					Log.i("TAG", "onActivityResult: 用户上传失败" + e.getMessage());
					Toast.makeText(MainActivity.this, "头像上传失败>_<", Toast.LENGTH_SHORT).show();
				}
			}
			//打包文字信息发送给服务器
			//new MyHttp().send_Post_String(MyHttp.IP + "update", "name=" + userdata.name + "&email=" + userdata.email + "&sex=" + userdata.sex + "&user_head_md5=" + userdata.user_head[0] + "&user_head_end=" + userdata.user_head[1], handler_update, CHANGE_UI);

			//Log.i("TAG", "onActivityResult: 用户修改了信息");
			//Toast.makeText(MainActivity.this, "用户打开信息界面之后修改了信息", Toast.LENGTH_LONG).show();
			//sql.addUserData(userdata);//更新本地数据库//SetData界面更新过了
			*/
		}
	}

	//加载右上角菜单界面
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu1, menu);
		//return super.onCreateOptionsMenu(menu);
		return true;
	}

	/*
	点击菜单栏选项的的处理函数
	*/
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		Log.i("nemu", "onOptionsItemSelected: 菜单");
		Intent intent;
		switch (item.getItemId())
		{
			case R.id.menu1_user:
				Log.i("nemu", "onOptionsItemSelected: 用户");
				//显示登录的用户信息
				if (Objects.equals(userdata.email, "") || userdata.email == null)
				{
					Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
					intent = new Intent(MainActivity.this, Login.class);
					//用户登陆
					startActivityForResult(intent, 0);//打开登陆界面
				}
				else
				{
					intent = new Intent(MainActivity.this, LogData.class);
					intent.putExtra("email", userdata.email);
					intent.putExtra("sex", userdata.sex);
					intent.putExtra("name", userdata.name);
					intent.putExtra("user_head", userdata.user_head);
					startActivityForResult(intent, 1);//打开用户信息界面
				}
				break;
			case R.id.menu1_login:
				Log.i("nemu", "onOptionsItemSelected: 登录");
				intent = new Intent(MainActivity.this, Login.class);
				//用户登陆
				startActivityForResult(intent, 0);//打开登陆界面
				break;
			case R.id.menu1_logout:
				Log.i("nemu", "onOptionsItemSelected: 注销");
				if (Objects.equals(userdata.email, "") || userdata.email == null)
				{
					Toast.makeText(MainActivity.this, "还没有用户登录哦", Toast.LENGTH_SHORT).show();
				}
				else
				{
					userdata = new UserData();
					sql.addUserData(userdata, 1);
					Toast.makeText(MainActivity.this, "拜拜喽。（已注销）", Toast.LENGTH_SHORT).show();
				}
				//释放当前持有的用户信息
				break;

		}
		//return super.onOptionsItemSelected(item);
		return true;
	}


	//线程锁，防止刷新过快程序崩溃
	public void time1(final int i)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				try
				{
					sleep(i);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				f5time = true;
			}
		}.start();
	}

	/*
		news=1	新闻
		uget=2	用户订阅
		other=3	其他
		pic=4	图片
		 */
	public void button_news(View v)
	{
		//listView显示新闻
		//new MyHttp().send_Get_String(MyHttp.IP+"news",handler_news,1);
		//Toast.makeText(MainActivity.this, "功能未实现,先被发现了>_<", Toast.LENGTH_SHORT).show();

		if (mylist.listview_show_id != mylist.news)
		{
			if (liat_myviewdata1 == null)
			{
				liat_myviewdata1 = new Vector<MyViewData>();
				new MyHttp().send_Get_String(MyHttp.IP + "news", handler_news, mylist.news);
				time1(5000);
			}
			mylist.set_Vector(liat_myviewdata1, mylist.news);

		}
		else if (f5time)
		{
			f5time = false;
			liat_myviewdata1 = new Vector<MyViewData>();
			new MyHttp().send_Get_String(MyHttp.IP + "news", handler_news, mylist.news);
			mylist.set_Vector(liat_myviewdata1, mylist.news);
			time1(20000);
		}
		else
		{
			Toast.makeText(MainActivity.this, "休息一下吧", Toast.LENGTH_SHORT).show();
		}
	}

	public void button_uget(View v)
	{
		//listView显示订阅
		//new MyHttp().send_Get_String(MyHttp.IP+"uget",handler_news,2);
		//Toast.makeText(MainActivity.this, "功能未实现,先被发现了>_<", Toast.LENGTH_SHORT).show();
		if (mylist.listview_show_id != mylist.uget)
		{
			if (liat_myviewdata2 == null)
			{
				liat_myviewdata2 = new Vector<MyViewData>();
				new MyHttp().send_Get_String(MyHttp.IP + "uget", handler_uget, mylist.uget);
				time1(5000);
			}
			mylist.set_Vector(liat_myviewdata2, mylist.uget);
		}
		else if (f5time)
		{
			f5time = false;
			liat_myviewdata2 = new Vector<MyViewData>();
			new MyHttp().send_Get_String(MyHttp.IP + "uget", handler_uget, mylist.uget);
			mylist.set_Vector(liat_myviewdata2, mylist.uget);
			time1(20000);
		}
		else
		{
			Toast.makeText(MainActivity.this, "休息一下吧", Toast.LENGTH_SHORT).show();
		}
	}

	public void button_other(View v)
	{
		//listView显示其他
		//new MyHttp().send_Get_String(MyHttp.IP+"other",handler_news,3);
		Toast.makeText(MainActivity.this, "功能未实现,先被发现了>_<", Toast.LENGTH_SHORT).show();
	}

	public void button_pic(View v)
	{
		//listView显示图片界面
		//new MyHttp().send_Get_String(MyHttp.IP+"pic",handler_news,4);
		Toast.makeText(MainActivity.this, "功能未实现,先被发现了>_<", Toast.LENGTH_SHORT).show();
	}


}

