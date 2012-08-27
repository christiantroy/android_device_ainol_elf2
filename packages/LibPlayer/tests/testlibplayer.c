
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <syslog.h>
#include <unistd.h>
#include <errno.h>
#include <amconfigutils.h>

int am_config_test()
{
    char value[32];
    int testresult;
    int kkk;
    float ff;
    printf("start am_config_test test \n");


#define testset(path,val)   \
    testresult=am_setconfig(path,val);\
    printf("set %s=%s, ret=%d\n",path,val,testresult);

    testset("hello.aboutyou", "yes");
    testset("hello.aboutyou2", "no");
    testset("buffer.level", "20000000");
    testset("buffer.level222", "600");
    testset("buffer.level222.123123333333333333333333333", "8000");
    testset("hello.666", "yes");
    testset("hello.555", "nnnnnnnn");
    testset("buffer.333", "pppppppp");
    testset("buffer.666", "ooooooo");
    testset("hello.kkkh", "yes");
    testset("hello.mmm", "nnnnnnnn");
    testset("buffer.rrrrrrrr", "pppppppp");
    testset("buffer.777777", "ooooooo");

#define printtestset(path,val)\
    value[0]='\0';\
    testresult =am_getconfig(path,value,"nosetting");\
    printf("get %s=%s,result=%d\n",path,value,testresult);

    printtestset("hello.aboutyou", "yes");
    printtestset("hello.aboutyou2", "no");
    printtestset("buffer.level", "20000000");
    printtestset("buffer.level222", "600");
    printtestset("buffer.level222.123123333333333333333333333", "8000");
    printtestset("hello.232", "yes");
    printtestset("hello.aboutyou244", "no");
    printtestset("buffer.level55", "20000000");
    printtestset("buffer.level22211", "600");


#define testsetint(path,val)    \
    testresult=am_setconfig_float(path,(float)val);\
    printf("set %s=%f, ret=%d\n",path,(float)val,testresult);


    testsetint("hello.aboutyou", -10000);
    testsetint("hello.aboutyou2", 0);
    testsetint("buffer.level", 888);
    testsetint("buffer.level22", 80999.999);
    testsetint("buffer.level", 666);
    testsetint("buffer.5555", 0.0000001);
    testsetint("buffer.56565", 0.00091119);

    printf("finished am_setconfig_int test \n");

#define printtestsetint(path,val)\
    ff=-1000;\
    testresult =am_getconfig_float(path,&ff);\
    printf("get %s=%f,result=%d\n",path,ff,testresult);

    printtestsetint("hello.aboutyou", -10000);
    printtestsetint("hello.aboutyou2", 0);
    printtestsetint("buffer.level", 12312312321);
    printtestsetint("buffer.level22", 12312312321);
    printtestsetint("buffer.5555", 0.0000001);
    printtestsetint("buffer.56565", 0.00091119);

    printf("finished printtestsetint test \n");
    testset("hello.mmm", "");
    testset("buffer.rrrrrrrr", "");
    am_dumpallconfigs();
    printf("finished am_config_test test \n");

    return 0;
}



int main(int argc, char **argv)
{

    printf("libplayer test start\n");
    am_config_test();

    printf("libplayer test end\n\n");
    return 0;
}
