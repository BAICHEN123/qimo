#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <Windows.h>
/*
argc ����argv�Ĵ�С
argv ��0�� �����Լ�������
		1	�����ļ�����
		2	�������Կ��
		3	��������ļ�������
		����������Ĳ���
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
		printf("�ļ���...\n");
	} while (fp1 == NULL||fp2==NULL);
	printf("�ļ��򿪳ɹ�\n");
	char read_char[32];//��ȡ�ļ��õ�ָ��
	char write_char[32];//д�ļ���
	int length = strlen(argv[2]);//ȡ��Կ�׵ĳ���
	int length2 = length;//ȡ��Կ�׵ĳ���
	int length3;//ȡ��Կ�׵ĳ���
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

