package com.example.qimo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaoYan
{
	private static Pattern pattern=Pattern.compile("[-_0-9a-zA-Z]+@[a-zA-Z0-9]+\\.com");
	private static Pattern pattern2=Pattern.compile("[0-9a-fA-F]+");
	public static boolean jy_email(String email)
	{
		Matcher re_email = pattern.matcher(email);
		return re_email.matches();
	}
	public static boolean jy_password(String email)
	{
		Matcher re_email = pattern2.matcher(email);
		return re_email.matches();
	}
}
