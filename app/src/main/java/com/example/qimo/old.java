//旧get
/*
new Thread()
{
	@Override
	public void run()
	{
		super.run();
		try
		{
			URL url = new URL("http://10.120.52.165:8080/news");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("User-Agent", "HCC's APP");
			int code = conn.getResponseCode();
			if (code == 200)
			{
				InputStream is = conn.getInputStream();//读取内容
				byte[] bytes;
				bytes = new byte[is.available()];
				is.read(bytes);
				is.close();
				Message msg = new Message();
				msg.what = CHANGE_UI;
				msg.obj = new String(bytes);
				handler_news.sendMessage(msg);

			}
			else
			{
				Log.i("TAG", "run: 请求失败");
				Message msg = new Message();
				msg.what = ERROR;
				handler_news.sendMessage(msg);
			}
		}
		catch (Exception e)
		{
			Log.i("TAG", "run: news 图片 请求出错");
			e.printStackTrace();
			Message msg = new Message();
			msg.what = ERROR;
			handler_news.sendMessage(msg);
		}

	}
}.start();
*/

//旧get2
/*new Thread()
{

	public void run()
	{
		Log.i("TAG", "run: " + position1);
		super.run();
		try
		{
			//体现异步加载图片的延时
			try
			{
				Thread.sleep(3000);
				Log.i("TAG", "handleMessage: 开始加载下一个");
			}
			catch (Exception e)
			{
				Log.i("TAG", "handleMessage: 延时失败");
			}


			MyViewData myViewData = new MyViewData();
			URL url = new URL(liat_myviewdata.get(position1).p_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
			int code = conn.getResponseCode();
			if (code == 200)
			{
				InputStream is = conn.getInputStream();//读取内容

				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				myViewData.setBitmapData(bitmap);

				byte[] bytes;
				bytes = new byte[is.available()];
				is.read(bytes);
				is.close();

				Message msg = new Message();
				msg.what = position1;
				msg.obj = bytes.clone();
				handler.sendMessage(msg);

			}
			else
			{
				Log.i("TAG", "run: 请求失败");
				Message msg = new Message();
				msg.what = ERROR;
				msg.obj = position1;
				handler.sendMessage(msg);
			}
		}
		catch (Exception e)
		{
			Log.i("TAG", "run: 请求出错");
			e.printStackTrace();
			Message msg = new Message();
			msg.what = ERROR;
			msg.obj = position1;
			handler.sendMessage(msg);
		}
	}
}.start();*/

//旧get？
/*new Thread()
{

	public void run()
	{
		Log.i("TAG", "run: " + position1);
		super.run();
		try
		{
			//体现异步加载图片的延时
			try
			{
				Thread.sleep(3000);
				Log.i("TAG", "handleMessage: 开始加载下一个");
			}
			catch (Exception e)
			{
				Log.i("TAG", "handleMessage: 延时失败");
			}


			MyViewData myViewData = new MyViewData();
			URL url = new URL(liat_myviewdata.get(position1).p_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
			int code = conn.getResponseCode();
			if (code == 200)
			{
				InputStream is = conn.getInputStream();//读取内容

				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				myViewData.setBitmapData(bitmap);

				byte[] bytes;
				bytes = new byte[is.available()];
				is.read(bytes);
				is.close();

				Message msg = new Message();
				msg.what = position1;
				msg.obj = bytes.clone();
				handler.sendMessage(msg);

			}
			else
			{
				Log.i("TAG", "run: 请求失败");
				Message msg = new Message();
				msg.what = ERROR;
				msg.obj = position1;
				handler.sendMessage(msg);
			}
		}
		catch (Exception e)
		{
			Log.i("TAG", "run: 请求出错");
			e.printStackTrace();
			Message msg = new Message();
			msg.what = ERROR;
			msg.obj = position1;
			handler.sendMessage(msg);
		}
	}
}.start();*/






//原psot请求
/*new Thread()
			{
				public void run()
				{
					thread_post_key = false;//线程锁，
					String post_data = "email=" + userdata.email + "&password1=" + user_password + "&password2=" + app_password;
					OutputStreamWriter out = null;
					BufferedReader in = null;
					String result = "";
					try
					{
						URL realUrl = new URL("http://10.120.52.165:8080/email");
						// 打开和URL之间的连接
						URLConnection conn = realUrl.openConnection();
						// 设置通用的请求属性
						conn.setRequestProperty("Accept", "* /*");
						conn.setRequestProperty("Connection", "Keep-Alive");
						conn.setRequestProperty("user-agent", "HCC's APP");
						// 发送POST请求必须设置如下两行
						conn.setDoOutput(true);
						conn.setDoInput(true);
						// 获取URLConnection对象对应的输出流
						out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
						// 发送请求参数
						out.append(post_data);
						// flush输出流的缓冲
						out.flush();
						// 定义BufferedReader输入流来读取URL的响应
						in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						String line;
						while ((line = in.readLine()) != null)
						{
							result = result + line.trim();
						}
						Message msg = new Message();
						msg.what = CHANGE_UI;
						msg.obj = result;
						handler_log.sendMessage(msg);
					}
					catch (Exception e)
					{
						//System.out.println("发送 POST 请求出现异常！" + e);
						e.printStackTrace();
						Log.i("MyHttp", "http_POST: 请求异常1");

						Message msg = new Message();
						msg.what = ERROR;
						//msg.obj = result;
						handler_log.sendMessage(msg);
					}
					//使用finally块来关闭输出流、输入流
					finally
					{
						try
						{
							if (out != null)
							{
								out.close();
							}
							if (in != null)
							{
								in.close();
							}
						}
						catch (IOException ex)
						{
							Log.i("MyHttp", "http_POST: 关闭异常1");
							ex.printStackTrace();
						}
					}

				}
			}.start();*/
//原get请求
/*final String path = "http://10.120.52.165:8080/email=" + "2275442930@qq.com";
			new Thread()//开启请求发送邮件的线程
			{

				public void run()
				{
					try
					{
						HttpURLConnection conn;
						URL url = new URL(path);
						conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setConnectTimeout(5000);
						conn.setRequestProperty("User-Agent", "HCC's APP");
						int code = conn.getResponseCode();
						if (code == 200)
						{
							InputStream is = conn.getInputStream();
							byte[] bytes;
							bytes = new byte[is.available()];
							is.read(bytes);
							String string_getdata = new String(bytes);
							Log.i("ZhuCe", "run: " + string_getdata.trim());

							Message msg = new Message();
							msg.what = CHANGE_UI;
							msg.obj = string_getdata.trim();
							handler_send.sendMessage(msg);
						}
						else
						{
							time_key = true;
							Log.i("TAG", "run: 请求失败");
							Message msg = new Message();
							msg.what = ERROR;
							handler_send.sendMessage(msg);
						}
					}
					catch (Exception e)
					{
						time_key = true;
						Log.i("TAG", "run: 请求出错");
						e.printStackTrace();
						Message msg = new Message();
						msg.what = ERROR;
						handler_send.sendMessage(msg);
					}
				}
			}.start();*/


