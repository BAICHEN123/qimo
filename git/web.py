#utf-8
from http.server import BaseHTTPRequestHandler, HTTPServer
import time
import hashlib
import MySmtp
import re
import json
import threading
import os
import news
import ZHhot
import keyer
import mysql.connector

dict_time = {}
str_new_data = "空"
str_zhhot_data = "空"
class thread_clear_dict_time(threading.Thread):
	#def __init__(self):
	def run(self):
		while 1:
			i = 0
			new_news()
			while i < 20:
				i = i + 1
				print("清洗正在执行")
				time.sleep(60)
				clear_dict_time()
				print("执行一轮")


def new_a_thread_clear_dict_time():
	#新建一个下载进程，并开始
	new_thread = thread_clear_dict_time()
	new_thread.start()


def new_news():
	global str_new_data,str_zhhot_data
	str_new_data = news.get_news()
	str_zhhot_data=ZHhot.get_hot()


def clear_dict_time():
	global dict_time
	global str_new_data
	if dict_time == {}:
		print(dict_time,end="")
		return 0
	list_dict = list(dict_time.keys())
	for dict_i in list_dict:
		if (time.time() - dict_time[dict_i][1] / 10000) > 300:
			#将超过5分钟的邮箱删除
			dict_time.pop(dict_i)
	print(dict_time)


class RequestHandler(BaseHTTPRequestHandler):
	Page = '''{data1}'''
	re_email = re.compile("email=[-_0-9a-zA-Z\u4e00-\u9fa5]+@[a-zA-Z0-9]+[\.]{1}com")
	re_md4head = re.compile("/pic=[0-9a-f]{32}\.head")
	global dict_time

	#查询邮件是否注册过
	SQL_SELECT_EMAIL='SELECT COUNT(email) FROM myuserdata WHERE email=%s'
	#同步用户信息
	SQL_SELECT_USERDATA='SELECT id,name,sex,userheadmd5,userheadend  FROM myuserdata WHERE email=%s'
	#插入注册信息
	SQL_INSERT_EMAIL='INSERT INTO myuserdata(email,password,loginnewtime) values(%s,%s,%s)'
	#更新密码
	SQL_UPDATE_PASSWORD='UPDATE myuserdata SET password=%s,loginnewtime=%s WHERE email=%s'
	#更新最后登录时间
	SQL_UPDATE_LOGIN_YX='UPDATE myuserdata SET loginnewtime=%s WHERE email=%s '
	#更新用户信息
	SQL_UPDATE_USERDATA='UPDATE myuserdata SET name=%s,sex=%s,userheadmd5=%s,userheadend=%s where email=%s'
	#{'name': '未闻君名', 'email': '111111111@qq.com', 'sex': '2', 'user_head_md5': 'dcda1eb07de0a72521140853f28b1488', 'user_head_end': 'head'}
	sql1=mysql.connector.connect(host="192.168.74.128",user="qimo1",passwd="123456",database="qimo",auth_plugin="mysql_native_password")
	

	"""
		#加载数据
		sql_send.execute(SQL_INSERT_EMAIL,('email','password',19.5))
		#送入数据库
		self.sql.commit()
	"""



	def send_content(self,content,code=200):
		#get返回数据封装
		self.send_response(code)
		self.send_header("Conrenr-Type","text/html")
		self.send_header("Conrenr-Length",str(len(content)))
		self.end_headers()
		#将数据编码之后发出
		if type(content)==str:
			try:
				self.wfile.write(content.encode("utf-8"))
			except:
				self.wfile.write(content.encode("gb2312"))
		elif type(content)==bytes:
			self.wfile.write(content)

	def Page_adddata(self,send_enddata):
		#Page返回内容添加参数
		values = {
			"data1":send_enddata
			}
		page = self.Page.format(**values)
		return page

	def do_GET(self):
		#收到get请求处理函数
		str_getdata = str(self.path)
		#获取到传来的信息
		user_email = ""
		
		if self.path.startswith("/news") == True:
			try:
				#如果需要可以在这里添加验证
				#验证方法参考/email
				#方便我测试，直接返回需要的内容
				
				self.send_content(str_new_data)

			except:
				self.send_content("error1",400)#处理请求出错

		elif self.path.startswith("/uget") == True:
			try:
				#如果需要可以在这里添加验证
				#验证方法参考/email
				#方便我测试，直接返回需要的内容
	
				self.send_content(str_zhhot_data)

			except:
				self.send_content("error1",400)#处理请求出错

		elif self.path.startswith("/user_head") == True:
			f=open("head/"+self.path[10:],'rb')
			user_head=f.read()
			f.close()
			self.send_content(user_head)
		else:
			self.send_content("error2",404)#不提供以外服务

	def do_POST(self):
		#print(self.headers)
		#print(self.command)
		str_post_path = str(self.path)
		print("文件的长度"+self.headers['content-length'])
		req_datas = self.rfile.read(int(self.headers['content-length'])) #按长度读取post数据
		str_post_data = ""
		print(type(req_datas))
		'''
			字节转换为字符串
			if type(req_datas)==bytes:
				try:
					str_post_data=req_datas.decode()
					print("编码格式为默认")
				except:
					str_post_data=req_datas.decode("gb2312")
					print("编码格式为gb2312")
			elif type(req_datas)==str:
				str_post_data=req_datas
		'''

		except_return_id = 404
		except_return_str = "error 0"
		print(str_post_data)
		if str_post_path.startswith("/email_log_login") == True:
			try:
				userdata_email_md5 = str_post_path[16:]
				print(userdata_email_md5)
				if(len(userdata_email_md5) == 32):
					#用户邮箱的md5剪切成功->合法请求
					##字节转换为字符串
					#except_return_id=404
					#except_return_str="error 0"
					if type(req_datas) == bytes:
						print("key=" + dict_time[userdata_email_md5][2][:4])
						str_post_data = keyer.keyer_get_data(dict_time[userdata_email_md5][2][:4],req_datas)
						
						#except_return_id=404
						#except_return_str="error 0"
						if str_post_data != 0:
							#str_post_data=req_datas.decode()
							print("解析安卓传来数据成功")
							#从post数据里抠出邮箱，校验MD5，检验验证码解析出的数据
							except_return_id = 202
							except_return_str = "jxerror0"
							user_email = self.re_email.findall(str_post_data)[0][6:]
							
							
							if hashlib.md5(user_email.encode('utf-8')).hexdigest() == userdata_email_md5:
								print(str_post_data)
								list_post = str_post_data.split("&")
								dict_post = {}
								for post_data in list_post:
									list_dict = post_data.split("=")
									dict_post[list_dict[0]] = list_dict[1]
								print(dict_post)

								'''post数据：
									接受：
										发送用户输入的验证码和get邮箱返回的验证码加密结果
										用户的邮箱
									发送：
										name
										head_md5
										head_end
										sex
										net_md5		//用户用于向服务器发起请求的请求码
										new_time 	//服务器最后登录时间'''
						
								new_time = str((time.time() * 1000) // 1)
								net_md5 = hashlib.md5(new_time[:-2].encode('utf-8')).hexdigest()
								#更新用户登录时间
								sql_send=self.sql1.cursor()
								sql_send.execute(self.SQL_UPDATE_LOGIN_YX,(new_time,dict_post["email"],))
								self.sql1.commit()
								#将数据库的用户信息发送给用户
								sql_send=self.sql1.cursor()
								sql_send.execute(self.SQL_SELECT_USERDATA,(user_email,))
								sql_data1=sql_send.fetchall()
								if sql_data1[0][0]>0:
									#'SELECT count(name),name,sex,userheadmd5,userheadend  FROM myuserdata WHERE email=%s'
									#					0			1					2				3	name							4	sex								5	userheadmd5				6	userheadend
									print(sql_data1)
									str_post_data = "end=1&" + new_time[:-2] + "&" + net_md5+ "&" + sql_data1[0][1]+ "&" + str(sql_data1[0][2])+ "&" + sql_data1[0][3]+ "&" + sql_data1[0][4]
									print("返回的数据"+str_post_data)
									self.send_content(str_post_data,200)
								else:
									self.send_content("getuserdataerror0",202)#查询数据失败
								#self.wfile.write(str_post_data.encode('utf-8'))
							else:
								self.send_content("jxerror0",202)#验证码错误
						else:
							self.send_content("jxerror0",202)#验证码错误
				else:
					#非法请求
					self.send_content(except_return_str,except_return_id)

			#使用js格式的数据
			#post_data =
			#{"end":"1"}#self.wfile.write(json.dumps(post_data).encode('utf-8'))
			except:
				self.send_content(except_return_str,except_return_id)
		elif str_post_path.startswith("/email_log_zhuce") == True:
			try:
				userdata_email_md5 = str_post_path[16:]
				print(userdata_email_md5)
				if(len(userdata_email_md5) == 32):
					#用户邮箱的md5剪切成功->合法请求
					##字节转换为字符串
					#except_return_id=404
					#except_return_str="error 0"
					if type(req_datas) == bytes:
						print("key=" + dict_time[userdata_email_md5][2][:4])
						str_post_data = keyer.keyer_get_data(dict_time[userdata_email_md5][2][:4],req_datas)
						
						#except_return_id=404
						#except_return_str="error 0"
						if str_post_data != 0:
							#str_post_data=req_datas.decode()
							print("解析安卓传来数据成功")
							#从post数据里抠出邮箱，校验MD5，检验验证码解析出的数据
							except_return_id = 202
							except_return_str = "jxerror0"
							user_email = self.re_email.findall(str_post_data)[0][6:]
							
							
							if hashlib.md5(user_email.encode('utf-8')).hexdigest() == userdata_email_md5:
								print(str_post_data)
								list_post = str_post_data.split("&")
								dict_post = {}
								for post_data in list_post:
									list_dict = post_data.split("=")
									dict_post[list_dict[0]] = list_dict[1]
								print(dict_post)


								'''post数据：
									接受：
										发送用户输入的验证码和get邮箱返回的验证码加密结果
										用户的邮箱
									发送：
										net_md5		//用户用于向服务器发起请求的请求码
										new_time 	//服务器最后登录时间'''

								new_time = str((time.time() * 1000) // 1)
								net_md5 = hashlib.md5(new_time[:-2].encode('utf-8')).hexdigest()
								str_post_data = "end=1&" + new_time[:-2] + "&" + net_md5
								sql_send=self.sql1.cursor()
								sql_send.execute(self.SQL_INSERT_EMAIL,(dict_post["email"],dict_post["password1"],new_time,))
								self.sql1.commit()
								self.send_content(str_post_data,200)
								#self.wfile.write(str_post_data.encode('utf-8'))
							else:
								self.send_content("jxerror0",202)#验证码错误
						else:
							self.send_content("jxerror0",202)#验证码错误
				else:
					#非法请求
					self.send_content(except_return_str,except_return_id)
			except:
				self.send_content(except_return_str,except_return_id)
		elif str_post_path.startswith("/email_send_login") == True:
			#字节转换为字符串
			if type(req_datas) == bytes:
				try:
					str_post_data = req_datas.decode()
					print("编码格式为默认")
				except:
					str_post_data = req_datas.decode("gb2312")
					print("编码格式为gb2312")
			elif type(req_datas) == str:
				str_post_data = req_datas

			print(str_post_data)
			try:
				user_email = self.re_email.findall(str_post_data)[0][6:]
			except:
				self.send_content("post error 2",404)
			#加载数据
			sql_send=self.sql1.cursor()
			sql_send.execute(self.SQL_SELECT_EMAIL,(user_email,))
			#送入数据库
			if sql_send.fetchall()[0][0]!=0:
				#这个邮箱注册过
				print(user_email)
				data_time = time.time() * 10000 // 1
				str_time = str(data_time)
				str_time_md5 = hashlib.md5(str_time.encode('utf-8')).hexdigest()
				#0 邮箱服务正常，邮箱地址有问题 1 发送成功 2邮箱服务有问题

				int_smtp_end = MySmtp.send_email(user_email,str_time_md5[:4])#发送时间的MD5前4位做验证码


				#如果发送邮件成功，将{邮箱：str_time}加入字典
				if int_smtp_end == 1:
					dict_time[hashlib.md5(user_email.encode('utf-8')).hexdigest()] = [user_email,data_time,str_time_md5]
					#比对用 if
					#hashlib.md5(str(self.dict_time[user_email]).encode('utf-8')).hexdigest()[:4]=返回的验证码：
				self.send_content(str_time_md5[-4:])
			else:
				#这个邮箱没有注册
				self.send_content("email found")
		elif str_post_path.startswith("/email_send_zhuce") == True:
			#字节转换为字符串
			if type(req_datas) == bytes:
				try:
					str_post_data = req_datas.decode()
					print("编码格式为默认")
				except:
					str_post_data = req_datas.decode("gb2312")
					print("编码格式为gb2312")
			elif type(req_datas) == str:
				str_post_data = req_datas

			print(str_post_data)
			try:
				user_email = self.re_email.findall(str_post_data)[0][6:]
			except:
				self.send_content("post error 2",404)
			#查询邮箱注册信息
			sql_send=self.sql1.cursor()
			sql_send.execute(self.SQL_SELECT_EMAIL,(user_email,))
			if sql_send.fetchall()[0][0]==0:
				#邮箱没有注册过，发送邮件准备注册
				print(user_email)
				data_time = time.time() * 10000 // 1
				str_time = str(data_time)
				str_time_md5 = hashlib.md5(str_time.encode('utf-8')).hexdigest()
				#0 邮箱服务正常，邮箱地址有问题 1 发送成功 2邮箱服务有问题

				int_smtp_end = MySmtp.send_email(user_email,str_time_md5[:4])#发送时间的MD5前4位做验证码


				#如果发送邮件成功，将{邮箱：str_time}加入字典
				if int_smtp_end == 1:
					try:
						#防止数据库异常
						dict_time[hashlib.md5(user_email.encode('utf-8')).hexdigest()] = [user_email,data_time,str_time_md5]
						#比对用 if
						#hashlib.md5(str(self.dict_time[user_email]).encode('utf-8')).hexdigest()[:4]=返回的验证码：
						self.send_content(str_time_md5[-4:])
					except:
						self.send_content("sql error 2")
			else:
				#邮箱注册过
				self.send_content("hava this email")
		elif str_post_path.startswith("/update") == True:
			if type(req_datas) == bytes:
				try:
					str_post_data = req_datas.decode()
					print("编码格式为默认")
				except:
					str_post_data = req_datas.decode("gb2312")
					print("编码格式为gb2312")
			elif type(req_datas) == str:
				str_post_data = req_datas
			print(str_post_data)
			list_post = str_post_data.split("&")
			dict_post = {}
			for post_data in list_post:
				list_dict = post_data.split("=")
				dict_post[list_dict[0]] = list_dict[1]
			try:
				print(dict_post)
				#{'name': '未闻君名', 'email': '111111111@qq.com', 'sex': '2', 'user_head_md5': 'dcda1eb07de0a72521140853f28b1488', 'user_head_end': 'head'}
				sql_send=self.sql1.cursor()
				sql_send.execute(self.SQL_UPDATE_USERDATA,(dict_post["name"],int(dict_post["sex"]),dict_post["user_head_md5"],dict_post["user_head_end"],dict_post["email"],))
				self.sql1.commit()
				self.send_content("update 1")
			except:
				self.send_content("update error 1",200)
		elif str_post_path.startswith("/pic=") == True:
			try:
				if self.re_md4head.findall(str_post_path)[0]==str_post_path:
					pic_have_id=os.system('dir head | find /i "'+str_post_path[5:]+'" && echo have')
					if pic_have_id==1:
						#文件未找到
						if type(req_datas) == bytes:
							try:
								f=open("head/"+str_post_path[5:],'wb')
								f.write(req_datas)
								f.close
								self.send_content("update 1")
							except:
								self.send_content("update error 1")
						else:
							self.send_content("update error 2")
					elif pic_have_id==0:
						#文件已存在
						self.send_content("update 1")
					else:
						#服务器错误
						self.send_content("update error 2")
			except:
				self.send_content("error",404)
		else:
			self.send_content("post error 2",404)


new_a_thread_clear_dict_time()	
serverAddress = ("",8080)
server = HTTPServer(serverAddress,RequestHandler)
server.serve_forever()



