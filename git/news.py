
from bs4 import BeautifulSoup
import requests
import re
from urllib import parse


""" 
import sys
import time
import threading
#import threaddown
import mysql.connector
#coding=utf-8
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.keys import Keys#模拟按键
import time


def chrome_help2(netadd,gdid):
	kk=mysql.connector.connect(
		host="192.168.198.128"
		,user="sa",passwd="123456"
		,database="wyygd"
		,auth_plugin='mysql_native_password')
	ms=kk.cursor()
	sql = "INSERT INTO gequ(gname,zname,gdid) VALUES (%s,%s,%s)"
	options=webdriver.ChromeOptions()
	#https://blog.csdn.net/weixin_43968923/article/details/87899762
	#无头
	options.add_argument('--disable-gpu')#禁用gpu加速
	options.add_argument('--start-maximized')#最大化运行
	prefs={'profile.default_content_setting_values':{'notifications':2}}
	options.add_experimental_option('prefs',prefs)#2行禁止弹窗
	options.add_argument('user-agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18363"')
	#options.add_argument('--proxy-server='+ipwork)
	#启动浏览器#https://blog.csdn.net/xc_zhou/article/details/82415870
	#删掉(chrome_options=options)参数，前台显示
	browser=webdriver.Chrome(chrome_options=options)
	for i1 in range(0,len(netadd)):
		browser.get(netadd[i1])
		time.sleep(2)
		browser.switch_to.frame("contentFrame")
		#dat=browser.find_element_by_id("play2")
		#input("开始获取元素")
		dat_name1=browser.find_elements_by_css_selector("tbody>tr>td>div.f-cb>div.tt>div.ttc>span.txt>a>b")#音乐名
		dat_name2=browser.find_elements_by_css_selector("tbody>tr>td>div.text>span")#作者名
		#dat2=ActionChains(browser).move_to_element(dat[1]).click(on_element=None)
		datlistf_name=list()
		datlistnet_add=list()
		for i2 in range(0,len(dat_name1)):
			name1=dat_name1[i2].get_attribute('title')
			name2=dat_name2[i2].get_attribute('title')
			print(name1+name2)
			val=(name1.strip(),name2.strip(),gdid[i1])
			ms.execute(sql,val)
			kk.commit()
		#input()
	ms.close()
	kk.close()
	browser.quit()


def chrome_help(netadd):
	kk=mysql.connector.connect(
		host="192.168.198.128"
		,user="sa",passwd="123456"
		,database="wyygd"
		,auth_plugin='mysql_native_password')
	ms=kk.cursor()
	sql = "INSERT INTO gedan (id,gdname) VALUES (%s,%s)"
	#val=('1',"测试歌单名0")
	options=webdriver.ChromeOptions()
	#https://blog.csdn.net/weixin_43968923/article/details/87899762
	#无头
	'''options选项
		#options.add_argument('--headless')#添加无界面选项
		#options.add_argument('--incognito')#启动进入无痕模式
		#options.add_argument('--start-maximized')#最大化运行
		#options.add_argument('--windows-size=800,600')#设置分辨率
		#options.add_argument('user-agent="Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"')#模拟ipone6
		#prefs={'profile.default_content_setting_values':{'notifications':2}}
		#options.add_experimental_option('prefs',prefs)#2行禁止弹窗
		#options.add_argument('--disable-gpu')#禁用gpu加速
		#options.add_argument('--proxy-server=http://202.20.16.82:10152')
	'''
	options.add_argument('--disable-gpu')#禁用gpu加速
	options.add_argument('--start-maximized')#最大化运行
	prefs={'profile.default_content_setting_values':{'notifications':2}}
	options.add_experimental_option('prefs',prefs)#2行禁止弹窗
	options.add_argument('user-agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18363"')
	#options.add_argument('--proxy-server='+ipwork)
	#启动浏览器#https://blog.csdn.net/xc_zhou/article/details/82415870
	#删掉(chrome_options=options)参数，前台显示
	browser=webdriver.Chrome(chrome_options=options)
	browser.get(netadd)
	time.sleep(2)
	browser.switch_to.frame("contentFrame")
	#dat=browser.find_element_by_id("play2")
	#input("开始获取元素")
	dat=browser.find_elements_by_css_selector("ul#cBox.m-cvrlst>li>p.dec>a")
	#print(dat)
	#dat2=ActionChains(browser).move_to_element(dat[1]).click(on_element=None)
	datlistf_name=list()
	datlistnet_add=list()
	gdid=list()
	for i in range(0,len(dat)):
		f_name=dat[i].get_attribute('title')
		net=dat[i].get_attribute('href')
		datlistf_name.append(f_name.strip())
		datlistnet_add.append(net.strip())
		val=(str(i+1),datlistf_name[i])
		print(val)
		ms.execute(sql,val)
		kk.commit()
		gdid.append(str(ms.lastrowid))
	ms.close()
	kk.close()
	browser.quit()
	chrome_help2(datlistnet_add,gdid)
	input("程序结束，回车推出") """




def get_news():
	headers={
		'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 Edg/81.0.416.77'
		}
	req=requests.get("http://www.people.com.cn/",headers=headers)
	soup=BeautifulSoup(req.content,"html.parser")
	list_title=list()
	list_pic=list()
	list_net=list()
	
	"""f=open("people.html",'wb')
	f.write(req.content)
	f.close() """
	data1=soup.select("div#focus_list>ul>li>a>img")
	for data1_i in data1:
		list_pic.append("http://www.people.com.cn"+str(data1_i.get('src').encode('raw_unicode_escape').decode('gbk')))

	data2=soup.select("div#focus_list>ul>li>div.show")
	for item in data2:
		#list_title.append(item.get_text().encode('raw_unicode_escape').decode('utf-8'))
		list_title.append(item.get_text().encode().decode('utf-8'))
	data3=soup.select("div#focus_list>ul>li>a")
	for item in data3:
		list_net.append(item.get("href").encode().decode('gbk'))
	#print(list_title)
	#print(list_pic)
	#print(list_net)
	str_data=""
	for i in range(0,len(list_title)):
		str_data=str_data+"#"+parse.quote(list_title[i])+"&"+parse.quote(list_pic[i])+"&"+parse.quote(list_net[i])+"#"
	#print(str_data)
	#  #标题&图片网址&内容地址#
	#parse.quote()#返回数据前编码
	return str_data
