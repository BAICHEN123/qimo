import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Hello
{
	final int CHANGE_UI = 1, ERROR = 0;
	
	public static void main(String args[])
	{

		JaoYan jy=new JaoYan();
		System.out.print(jy.jy_email("2275442830@qq.com"));
		System.out.print("\n");
		System.out.print(jy.jy_email("2275442830"));
		System.out.print("\n");
		System.out.print(jy.jy_email("2275442830com"));
		System.out.print("\n");
		System.out.print(jy.jy_email("227544-2830qqcom"));
		System.out.print("\n");
		System.out.print(jy.jy_email("22754_42830@q.om"));
		System.out.print("\n");
		System.out.print(jy.jy_email(""));
		System.out.print("\n");


	/*
		ClassMyMd5 mymd5=new ClassMyMd5();
		//System.out.print(mymd5.MyMd5("abb")+"\n");
		try
		{
			byte[] key="ab".getBytes();
			//byte[] data="让俺试试中文".getBytes();
			
			byte[] end;
			end=mymd5.keyer_get_bytes("让俺试试中文","addd");
			System.out.print(new String(end));
			//end=mymd5.keyer_get_string1(end,key);
			System.out.print(mymd5.keyer_get_string(end,"addd"));
			//System.out.print(String.valueOf(mymd5.keyer_get("abbccc".getBytes(),"kc".getBytes())));
			//System.out.print(mymd5.MyMd5("abb".getBytes())+"\n");
		}
		catch(Exception e)
		{
			System.out.print("转换出错");
		}
		*/
		{
			/*
			Hello hello=new Hello();
			try
			{
				String str2=URLDecoder.decode(hello.http_GET(),"UTF-8");
				System.out.print(str2+"\n\n");
		
				String str1=URLDecoder.decode(hello.http_GET());
				System.out.print(str1+"\n\n");
		
				String str1=URLEncoder.encode("https://cn.bing.com/search?q=pythonurl%E7%BC%96%E7%A0%81&qs=n&form=QBRE&sp=-1&pq=pythonurl%E7%BC%96%E7%A0%81&sc=0-11&sk=&cvid=25435F58981B4A5F94F787F59621A84D","UTF-8");
				System.out.print(str1+"\n\n");
				String str2=URLDecoder.decode("https%3A%2F%2Fcn.bing.com%2Fsearch%3Fq%3Dpythonurl%25E7%25BC%2596%25E7%25A0%2581%26qs%3Dn%26form%3DQBRE%26sp%3D-1%26pq%3Dpythonurl%25E7%25BC%2596%25E7%25A0%2581%26sc%3D0-11%26sk%3D%26cvid%3D25435F58981B4A5F94F787F59621A84D","UTF-8");
				System.out.print(str2+"\n\n");
				System.out.print(str2.compareTo("https://cn.bing.com/search?q=pythonurl%E7%BC%96%E7%A0%81&qs=n&form=QBRE&sp=-1&pq=pythonurl%E7%BC%96%E7%A0%81&sc=0-11&sk=&cvid=25435F58981B4A5F94F787F59621A84D"));
		
			}
			catch(Exception e)
			{
				System.out.print("失败");
			}
			//System.out.print(System.currentTimeMillis());//打印时间戳

			String post_data;
			Hello hello=new Hello();
			try
			{
				post_data=hello.http_POST("email=2275442930%40qq.com&password1=passworddata&password2=passworddata");
				System.out.print(post_data);
			}
			catch(Exception e)
			{
				System.out.print("转换或者post出错");
			}
			*/
		}
	}
	
	public String http_POST(String post_data)
	{
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL("http://10.120.52.165:8080/email");
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
			// 发送请求参数
			out.append(post_data);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += line;
			}
		}
		catch (Exception e)
		{
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
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
				ex.printStackTrace();
			}
		}
		return result;
	}

	public String  http_GET()
	{
		HttpURLConnection conn;
		//String path = "http://10.120.52.165:8080/email=" + "2275442930@qq.com";
		String path = "http://10.120.52.165:8080/news";
		try
		{
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
				//System.out.print(string_getdata);
				System.out.print("正常1");
				return string_getdata;
			}
			else
			{
				System.out.print("错误1");
				return "";
			}
		}
		catch (Exception e)
		{

			System.out.print("错误2");
			return "";
		}
	}
}
class ClassMyMd5
{
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static char[] md5_end=new char[32];
	public String MyMd5(String md5_data)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(md5_data.getBytes("UTF-8"));
			byte[] digest = md.digest();
			//System.out.print(digest.length);
			//System.out.print(String.valueOf(digest)+"\n");
			for(int i=0;i<16;i=i+1)
			{
				md5_end[2*i+1]=HEX_DIGITS[digest[i]&15];
				md5_end[2*i]=HEX_DIGITS[(digest[i]&240)>>4];
				//System.out.print(i);
			}
			return String.valueOf(md5_end);
		}
		catch(Exception e)
		{
			return "";
		}
	}
	public String MyMd5(byte[] md5_data)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(md5_data);
			byte[] digest = md.digest();
			//System.out.print(digest.length);
			//System.out.print(String.valueOf(digest)+"\n");
			for(int i=0;i<16;i=i+1)
			{
				md5_end[2*i+1]=HEX_DIGITS[digest[i]&15];
				md5_end[2*i]=HEX_DIGITS[(digest[i]&240)>>4];
				//System.out.print(i);
			}
			return String.valueOf(md5_end);
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public byte[] keyer_get_bytes(byte[] data,byte[] key)
	{
		byte[] data_end=new byte[data.length];
		byte a=0,b=0;
		for(int i=0;i<data.length;i++)
		{
			data_end[i]=(byte)((byte)(data[i]&~key[i%key.length])|(byte)(~data[i]&key[i%key.length]));
		}
		return data_end;
	}
	public byte[] keyer_get_bytes(String data1,String key1)
	{
		byte[] data;
		byte[] key;
		try
		{
			data=data1.getBytes();
			key=key1.getBytes();
	
		}
		catch(Exception e)
		{
			return null;
		}
		byte[] data_end=new byte[data.length];
		byte a=0,b=0;
		for(int i=0;i<data.length;i++)
		{
			data_end[i]=(byte)((byte)(data[i]&~key[i%key.length])|(byte)(~data[i]&key[i%key.length]));
		}
		return data_end;
	}
	public String keyer_get_string(byte[] data,byte[] key)
	{
		byte[] data_end=new byte[data.length];
		byte a=0,b=0;
		for(int i=0;i<data.length;i++)
		{
			data_end[i]=(byte)((byte)(data[i]&~key[i%key.length])|(byte)(~data[i]&key[i%key.length]));
		}
		return new String(data_end);
	}
	public String keyer_get_string(byte[] data,String key1)
	{
		byte[] key;
		try
		{
			key=key1.getBytes();
		}
		catch(Exception e)
		{
			return null;
		}
		byte[] data_end=new byte[data.length];
		byte a=0,b=0;
		for(int i=0;i<data.length;i++)
		{
			data_end[i]=(byte)((byte)(data[i]&~key[i%key.length])|(byte)(~data[i]&key[i%key.length]));
		}
		return new String(data_end);
	}
}
class JaoYan
{
	private static Pattern pattern=Pattern.compile("[_0-9a-zA-Z]+@[a-zA-Z0-9]+\\.com");

	public boolean jy_email(String email)
	{
		Matcher re_email = pattern.matcher(email);
		return re_email.matches();
	}
}
