package com.example.qimo;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class MyViewData
{
	public ImageView imageview = null;
	private Bitmap bitmap;
	public String p_url;
	public String text;
	public String url;
	private boolean bitmap_key =false;
	public boolean thread_key=false;

	public boolean setImageData(ImageView image)
	{
			this.imageview = image;
			return true;
	}
	public boolean setBitmapData(Bitmap bitmap)
	{
		try
		{
			this.bitmap=bitmap;
			this.bitmap_key = true;
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	public Bitmap getBitmapData()
	{
		if(this.bitmap_key)
		{
			return this.bitmap;
		}
		else
		{
			return null;
		}
	}
	public boolean getbitmap_key()
	{
		return this.bitmap_key;
	}
}
