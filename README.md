# 2020年安卓期末设计


最后测试运行时间2020年6月26日（爬虫有时效性）

	整个库是安卓studio里上传的，（git用的不熟练，文件有些乱）

	git文件夹是服务器端的代码

		1.服务器端主要是用python写的，字节加密调用的exe。

		2.exe源码就是同名的cpp（略微区别，可能打印的东西不一样，主要的字节处理时一样的）。

		3.hello.java没啥用		//hello.java是模拟手机端测试程序用的(hello.java有的安卓工程里都有)
		
	数据库是在虚拟机里建的，只有一个表
	'''
		Create table myuserdata(
			id int unsigned AUTO_INCREMENT PRIMARY KEY,
			email nvarchar(100) not null unique KEY,
			name Nvarchar(100) ,
			sex TINYINT DEFAULT 2 ,
			password Char(32) not null,
			loginNewtime DOUBLE Unsigned not null,
			userHeadmd5 Char(32),
			userHeadend char(5) DEFAULT '.head',
			life TINYINT unsigned,
				)ENGINE=InnoDB DEFAULT CHARSET=utf8;
		'''
	
