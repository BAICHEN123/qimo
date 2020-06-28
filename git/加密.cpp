#include <stdio.h>
int main()
{
	unsigned int a=170,key;
	unsigned int b,c,d,e,f;
	printf("请输入秘钥");
	scanf("%d",&key);
	while(1)
	{
		printf("请输入数据");
		scanf("%d",&a);
		b=(~a)&key;//保存数据对应的key为1的位取反
		printf("%d\n",b);
		c=(a&~key)|b;//合并取反的和没取反的
		printf("%d\n",c);
		printf("开始复原过程\n");
		d=(~c)&key;//保存 取反加密数据对应的key为1的位
		e=(c&~key)|d;

		printf("%d\n",e);
	}
	return 1;
}