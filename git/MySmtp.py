#!/usr/bin/python
# -*- coding: utf-8 -*-
import smtplib
import email

from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart

HOST ='smtp.qq.com'
FROM =发信者邮箱

def send_email(str_to_email,str_data,SUBJECT ='我是标题'):
	message=MIMEText("[内容]["+str_data+"][内容]")
	message['From']=FROM
	message['To']=str_to_email
	message['Subject']=SUBJECT

	email_client=smtplib.SMTP_SSL(HOST,465)
	email_client.connect(HOST,465)

	result1,result2=email_client.login(FROM,字符串类型邮箱登录码)
	if result2==b'Authentication successful':
		try:
			end1=email_client.sendmail(FROM,str_to_email,message.as_string())
			print ("邮件发送成功",end1)
			email_client.close()
			return 1
		except smtplib.SMTPException:
			print ("Error: 无法发送邮件")
			email_client.close()
			return 0
	else:
		print("发送邮件身份验证出现问题")
		return 2

#示例
#send_email("*********@qq.com","123456")