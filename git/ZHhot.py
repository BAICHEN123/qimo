import requests
from bs4 import BeautifulSoup
from urllib import parse

headers={
	'User-Agent':r'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36',
	'Cookie':'在这里写知乎的cookies'}

url='https://www.zhihu.com/hot'

def get_hot():
	req=requests.get(url,headers=headers)
	"""
		f=open("知乎.html",'wb')
		f.write(req.content)
		f.close()
	"""
	soup=BeautifulSoup(req.text,"lxml")
	list_tit=list()
	list_img=list()
	list_tit2=list()
	data1=soup.select('section[tabindex="0"]>a.HotItem-img')
	for item in data1:
		list_tit.append(item.get('title').strip())
	data2=soup.select('section[tabindex="0"]>a.HotItem-img>img')
	for item in data2:
		list_img.append(item.get('src').strip())
	data4=soup.select('div.HotList-list>section.HotItem>div.HotItem-content>a')
	for item in data4:
		list_tit2.append(item.get('title').strip())


	#print(len(list_tit))#带图片的标题
	#print(len(list_img))#全部的图片的连接
	#print(len(list_tit2))#全部的标题
	c=0#没有图片的标题数量
	i=0#计数器
	ZH_data=""
	while i<len(list_tit):
		try:
			if list_tit2[i+c][:8]==list_tit[i][:8]:
				#print(list_tit2[i+c]+"	\n"+"		"+list_img[i])
				ZH_data=ZH_data+"#"+parse.quote(list_tit2[i+c])+"&"+parse.quote(list_img[i])+"#"
				i=i+1
			else:
				#print(list_tit2[i+c]+"	\n"+"		")
				ZH_data=ZH_data+"#"+parse.quote(list_tit2[i+c])+"#"
				c=c+1
		except:
			#防止标题过短，程序崩溃
			len_min=mymin(len(list_tit2[i+c]),len(list_tit[i]))
			if list_tit2[i+c][:len_min]==list_tit[i][:len_min]:
				#print(list_tit2[i+c]+"	\n"+"		"+list_img[i])
				ZH_data=ZH_data+"#"+parse.quote(list_tit2[i+c])+"&"+parse.quote(list_img[i])+"#"
				i=i+1
			else:
				#print(list_tit2[i+c]+"	\n"+"		")
				ZH_data=ZH_data+"#"+parse.quote(list_tit2[i+c])+"#"
				c=c+1
	#print(ZH_data)
	return ZH_data

	
def mymin(q,w):
	if q==w:
		return q
	elif q>w:
		return w
	else:
		return q


#get_hot()

