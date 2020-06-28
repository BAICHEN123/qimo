import os
import time

"""调用示例
	key="文字测试"
	file_data=keyer_to_key(key,"我来试试中文字符我来试试中文字符")
	keyer_get_data(key,file_data)
"""

def keyer_get_data(key,bytes_data):
	"""
		接受bytes_data
		返回str
	"""
	str_exe_data_file_name=str(time.time()%1)[2:]
	f=open(str_exe_data_file_name,'wb')
	f.write(bytes_data)
	f.close()
	str_keyend_data_file_name=str_exe_data_file_name+"end"
	#print(str_exe_data_file_name)
	if os.system("filekeytofile.exe "+str_exe_data_file_name+" "+key+" "+str_keyend_data_file_name)==1:
		#print("加密成功")
		f=open(str_keyend_data_file_name,'r')
		#print("--------------------------------")
		file_data=f.read()
		try:
			print(file_data)
		except:
			#print("python不认识")
			return 0
		f.close()
		#print("--------------------------------")
		os.system("del "+str_exe_data_file_name+" "+str_keyend_data_file_name)
		return file_data
	else:
		#print("加密失败")
		return 0;
def keyer_to_key(key,str_data):
	"""
		接受str
		返回bytes
	"""
	str_exe_data_file_name=str(time.time()%1)[2:]
	f=open(str_exe_data_file_name,'w')
	f.write(str_data)
	f.close()
	str_keyend_data_file_name=str_exe_data_file_name+"end"
	#print(str_exe_data_file_name)
	if os.system("filekeytofile.exe "+str_exe_data_file_name+" "+key+" "+str_keyend_data_file_name)==1:
		#print("加密成功")#提取问价内容，删除文件
		f=open(str_keyend_data_file_name,'rb')
		file_data=f.read()
		f.close()
		os.system("del "+str_exe_data_file_name+" "+str_keyend_data_file_name)
		return file_data
	else:
		#print("加密失败")
		return 0;


