import requests

headers={
	'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0'
	}


data={
	"email":"**********@qq.com",
	"password1":"passworddata",
	"password2":"passworddata"
	}
def my_post():
	global headers,data
	x=requests.session()
	req=x.get("http://10.120.52.165:8080/email=**********@qq.com",headers=headers)
	print("get------------------------------")
	print(req.content)
	req=x.post("http://10.120.52.165:8080/email",headers=headers,data=data)
	print("post-----------------------------")
	print(req.content)
	print("-----------------------------")

while 1:
	input("执行my_post()")
	my_post()
