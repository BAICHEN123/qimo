#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <Windows.h>
/*
argc 储存argv的大小
argv 第0个 储存自己的名字
		1	储存文件名字
		2	储存加密钥匙
		3	储存输出文件的名字
		其他是输入的参数
*/
int main(int argc,char * argv[])
{
	FILE* fp1 = NULL, *fp2 = NULL;
	printf("%d\n",argc);
	for(int i=0;i<argc;i=i+1)
	 printf("%s\n",*(argv+i));

	do
	{
		Sleep(10);
		fp1 = fopen(argv[1], "rb");
		fp2 = fopen(argv[3], "wb");
		printf("文件打开...\n");
	} while (fp1 == NULL||fp2==NULL);
	printf("文件打开成功\n");
	char read_char[32];//读取文件用的指针
	char write_char[32];//写文件用
	int length = strlen(argv[2]);//取得钥匙的长度
	int length2 = length;//取得钥匙的长度
	int length3;//取得钥匙的长度
	if (length > 32)
	{
		return 0;
	}
	int i;
	char* read_data;
	char* write_data;
	read_data = read_char;
	write_data = write_char;
	do
	{
		length = fread(read_data, sizeof(char), length, fp1);
		for (i = 0; i < length; i++)
		{
			printf("%c", *(read_data+i));
			write_data[i] = (~read_data[i] & argv[2][i]) | (read_data[i] & ~argv[2][i]);

		}
		printf("%d	", i);
		length3=fwrite(write_data, sizeof(char), length, fp2);
		for (i = 0; i < length; i++)
		{
			*(read_data+i) = 0;
		}

		//length=fread(read_data, sizeof(char), length, fp1);
		printf("%d	%d	", length,length3);
	}while (!feof(fp1));

	fclose(fp2);
	fclose(fp1);
	return 1;
}

