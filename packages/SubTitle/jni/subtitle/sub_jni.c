////////////////////////////////////////////////////////////////////////////////
// JNI Interface
////////////////////////////////////////////////////////////////////////////////

#include <jni.h>
#include <android/log.h>
#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <pthread.h>


#include "sub_set_sys.h"
#include "vob_sub.h"

#define  LOG_TAG    "sub_jni"
#define  ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

#include "sub_api.h"
#include <string.h>
#include "sub_subtitle.h"

JNIEXPORT jobject JNICALL parseSubtitleFile
  (JNIEnv *env, jclass cl, jstring filename, jstring encode)
{
      jclass cls = (*env)->FindClass(env, "com/subtitleparser/SubtitleFile");
      if(!cls){
          ALOGE("parseSubtitleFile: failed to get SubtitleFile class reference");
          return NULL;
      }

      jmethodID constr = (*env)->GetMethodID(env, cls, "<init>", "()V");
      if(!constr){
          ALOGE("parseSubtitleFile: failed to get  constructor method's ID");
          return NULL;
      }

      jobject obj =  (*env)->NewObject(env, cls, constr);
      if(!obj){
          ALOGE("parseSubtitleFile: failed to create an object");
          return NULL;
      }

	  jmethodID mid = (*env)->GetMethodID(env, cls, "appendSubtitle", "(III[BLjava/lang/String;)V");
      if(!mid){
          ALOGE("parseSubtitleFile: failed to get method append's ID");
          return NULL;
      }

      const char *nm = (*env)->GetStringUTFChars(env,filename, NULL);
      const char* charset= (*env)->GetStringUTFChars(env,encode, NULL);
      subdata_t * subdata = NULL;
      subdata = internal_sub_open(nm,0,charset);
      if(subdata == NULL){
          ALOGE("internal_sub_open failed! :%s",nm);
          goto err2;
      }

      jint i=0, j=0;
      list_t *entry;
      jstring jtext;
      
      char * textBuf = NULL;

      list_for_each(entry, &subdata->list)
      {
          i++;
          subtitle_t *subt = list_entry(entry, subtitle_t, list);
	
		  textBuf = (char *)malloc(subt->text.lines *512);
		  if(textBuf == NULL){
			ALOGE("malloc text buffer failed!");
			goto err;
		  }

		memset( textBuf, 0,subt->text.lines *512);

		for (j=0; j< subt->text.lines; j++) {
			strcat(textBuf, subt->text.text[j]);
			strcat(textBuf, "\n");
		}
		
	  jbyteArray array= (*env)->NewByteArray(env,strlen(textBuf));
	  
	  (*env)->SetByteArrayRegion(env,array,0,strlen(textBuf), textBuf);	  
		//jtext = (*env)->NewStringUTF(env, textBuf); //may cause err.
              

		(*env)->CallVoidMethod(env, obj, mid, i, subt->start/90, subt->end/90, array,encode);
		(*env)->DeleteLocalRef (env,array );
		free(textBuf);
	}
	internal_sub_close(subdata);
	(*env)->ReleaseStringUTFChars(env,filename, nm);
	return obj;

err:


      internal_sub_close(subdata);

err2:

      (*env)->ReleaseStringUTFChars(env,filename, nm);


      return NULL;
  }
  
JNIEXPORT void JNICALL playfileChanged
  (JNIEnv *env, jclass cl,jstring filename )  
{
	close_subtitle();
    ALOGE("playfileChanged!");
	
}
  
  
  
JNIEXPORT jint JNICALL getInSubtitleTotal
  (JNIEnv *env, jclass cl)  
{
	int subtitle_num = get_subtitle_num();
    ALOGE("jni getInSubtitleTotal!");
    if(subtitle_num > 0)
    	return subtitle_num;
	return 0;
}

static 
JNIEXPORT jint JNICALL setInSubtitleNumber
  (JNIEnv *env, jclass cl, jint index,jstring name )  
{	
	if(index == 0xff){
		set_subtitle_enable(0);
//		close_subtitle();
		//should clear subtitle buffer and don't send data in libplayer
	}
	else if(index < get_subtitle_num()){
//		close_subtitle();
		set_subtitle_enable(1);
		set_subtitle_curr(index);
	}
    ALOGE("jni setInSubtitleNumber!");
	return 0;
}

JNIEXPORT jint JNICALL getCurrentInSubtitleIndex
  (JNIEnv *env, jclass cl )  
{
	int subtitle_curr = get_subtitle_curr();
    ALOGE("jni getCurrentInSubtitleIndex!");
	if(subtitle_curr >= 0)
    	return subtitle_curr;
	return -1;
}



JNIEXPORT void JNICALL closeInSubView (JNIEnv *env, jclass cl )  
{
	ALOGE("jni closeInSubView!");
	set_subtitle_enable(0);
	close_subtitle();
}


JNIEXPORT jobject JNICALL getrawdata
	  (JNIEnv *env, jclass cl, jint msec )  
{
    ALOGE("jni getdata! return a java object:RawData         begin....");
	jclass cls = (*env)->FindClass(env, "com/subtitleparser/subtypes/RawData");
	if(!cls){
		ALOGE("com/subtitleparser/subtypes/RawData: failed to get RawData class reference");
		return NULL;
	}
	
	jmethodID constr = (*env)->GetMethodID(env, cls, "<init>", "([IIIIILjava/lang/String;)V");
	if(!constr){
		ALOGE("com/subtitleparser/subtypes/RawData: failed to get  constructor method's ID");
	  return NULL;
	}
	jmethodID constrforstr = (*env)->GetMethodID(env, cls, "<init>", "([BILjava/lang/String;)V");
	if(!constrforstr){
		ALOGE("com/subtitleparser/subtypes/RawData: failed to get  constructor method2's ID");
	  return NULL;
	}	
	
	ALOGE("start get packet\n\n");
	int sub_pkt = get_inter_spu_packet(msec*90+get_subtitle_startpts());
	ALOGI("subtitle get start pts is %x\n\n",get_subtitle_startpts());
	if(sub_pkt < 0){
		ALOGE("sub pkt fail\n\n");
		return NULL;
	}
	int size = get_subtitle_buffer_size();
	ALOGI("when get sub packet buffer size %d\n", size);
	
	if(get_inter_spu_type()==SUBTITLE_SSA)
	{
		int sub_size = get_inter_spu_size();
		ALOGE("getrawdata: get_inter_spu_type()=SUBTITLE_SSA size  %d ",sub_size);
		if(sub_size <= 0){
			return NULL;
		}	
		jbyteArray array= (*env)->NewByteArray(env,sub_size);
		(*env)->SetByteArrayRegion(env,array,0, sub_size, get_inter_spu_data() );	 
		
		ALOGE("getrawdata: SetByteArrayRegion finish");
		
		jobject obj =  (*env)->NewObject(env, cls, constrforstr,array,get_inter_spu_delay()/90,0);
		ALOGE("getrawdata: NewObject  finish");

		add_read_position();
		if(!obj){
		  ALOGE("parseSubtitleFile: failed to create an object");
		  return NULL;
		}
		return obj;
	}
	else if(get_inter_spu_type()==SUBTITLE_PGS)
	{
		int sub_size = get_inter_spu_size();
		ALOGE("getrawdata: get_inter_spu_type()=SUBTITLE_PGS size  %d ",sub_size);
		if(sub_size <= 0){
			return NULL;
		}	
		jintArray array= (*env)->NewIntArray(env,sub_size);
		(*env)->SetIntArrayRegion(env,array,0, sub_size, get_inter_spu_data() );	 
		
		ALOGE("getrawdata: SetByteArrayRegion finish");
		int delay_pts = get_inter_spu_delay();
		if(delay_pts <= 0)
			delay_pts = 0;
		else
			delay_pts = (get_inter_spu_delay()-get_subtitle_startpts())/90;
		jobject obj =  (*env)->NewObject(env, cls, constr,array,1,get_inter_spu_width(),
			get_inter_spu_height(),delay_pts,0);
		ALOGE("getrawdata: NewObject  finish");

		add_read_position();
		if(!obj){
		  ALOGE("parseSubtitleFile: failed to create an object");
		  return NULL;
		}
		return obj;
	}
	else //if(get_inter_spu_type()==SUBTITLE_VOB) //SUBTITLE_VOB
	{
		ALOGE("getrawdata: get_inter_spu_type()=SUBTITLE_VOB");

		int sub_size = get_inter_spu_size();
		if(sub_size <= 0){
			ALOGE("sub_size invalid \n\n");
			return NULL;
		}	
		ALOGE("sub_size is %d\n\n",sub_size);
		int *inter_sub_data = NULL;
		inter_sub_data = malloc(sub_size*4);
		if(inter_sub_data == NULL){
			ALOGE("malloc sub_size fail \n\n");
			return NULL;
		}
		memset(inter_sub_data, 0x0, sub_size*4);
		ALOGE("start get new array\n\n");
		jintArray array= (*env)->NewIntArray(env,sub_size);
		if(!array){
			ALOGE("new int array fail \n\n");
			return NULL;
		}
	 
		parser_inter_spu(inter_sub_data);
		int *resize_data = malloc(get_inter_spu_resize_size()*4);
		if(resize_data == NULL){
			free(inter_sub_data);
			return NULL;
		}
		fill_resize_data(resize_data, inter_sub_data);
		ALOGE("end parser_inter_spu\n\n");
		(*env)->SetIntArrayRegion(env,array,0,get_inter_spu_resize_size(), resize_data);	 
		ALOGE("start get new object\n\n");
		free(inter_sub_data);
		free(resize_data);
		jobject obj =  (*env)->NewObject(env, cls, constr,array,1,get_inter_spu_width(),
			get_inter_spu_height(),(get_inter_spu_delay()-get_subtitle_startpts())/90,0);
		add_read_position();
		if(!obj){
		  ALOGE("parseSubtitleFile: failed to create an object");
		  return NULL;
		}
		return obj;

	}
	ALOGE("getrawdata: get_inter_spu_type()== other type");
	return NULL;
	

}
JNIEXPORT void JNICALL setidxsubfile
	  (JNIEnv *env, jclass cl, jstring name,jint index )
{
    ALOGE("jni setidxsubfile  \n" );
	const char *file = (*env)->GetStringUTFChars(env,name, NULL);
	idxsub_init_subtitle(file,index);
	(*env)->ReleaseStringUTFChars(env,name, file);

}  

JNIEXPORT void JNICALL  closeIdxSubFile(JNIEnv *env, jclass cl )
{
	ALOGE("jni closeIdxSubFile");
	idxsub_close_subtitle();
}


JNIEXPORT jobject JNICALL getidxsubrawdata
	  (JNIEnv *env, jclass cl, jint msec )  
{
    ALOGE("jni getidxsubrawdata! need return a java object:RawData         begin....");
	jclass cls = (*env)->FindClass(env, "com/subtitleparser/subtypes/RawData");
	if(!cls){
		ALOGE("com/subtitleparser/subtypes/RawData: failed to get RawData class reference");
		return NULL;
	}
	
	jmethodID constr = (*env)->GetMethodID(env, cls, "<init>", "([IIIIILjava/lang/String;)V");
	if(!constr){
		ALOGE("com/subtitleparser/subtypes/RawData: failed to get  constructor method's ID");
	  return NULL;
	}
	
	subtitlevobsub_t* vobsub = getIdxSubData(msec);
	
	if(vobsub==NULL)
	{
		ALOGE("jni vobsub==NULL");
		return NULL;
	}
	int raw_byte = (vobsub->vob_subtitle_config.width) *(vobsub->vob_subtitle_config.height)/4;   //byte
	int pixnumber = (vobsub->vob_subtitle_config.width) *(vobsub->vob_subtitle_config.height);
	int photosize = pixnumber*4;
	ALOGE("w%d h%d s%d\n", vobsub->vob_subtitle_config.width ,vobsub->vob_subtitle_config.height,raw_byte );
	int *idxsubdata = NULL;
	idxsubdata = malloc(photosize);
	if(idxsubdata == NULL){
		ALOGE("malloc sub_size fail \n\n");
		return NULL;
	}
	memset(idxsubdata, 0x0, photosize);
	jintArray array= (*env)->NewIntArray(env,pixnumber);
	if(!array){
		ALOGE("new int array fail \n");
		return NULL;
	}
	ALOGE("start parser_data  spu_alpha=0x%x \n",vobsub->vob_subtitle_config.contrast);
	idxsub_parser_data(vobsub->vob_subtitle_config.prtData,raw_byte,(vobsub->vob_subtitle_config.width)/4 ,idxsubdata,vobsub->vob_subtitle_config.contrast );
	ALOGE("parser_data over\n\n");

	(*env)->SetIntArrayRegion(env,array,0,pixnumber,idxsubdata);	 
	free(idxsubdata);

	jobject obj =  (*env)->NewObject(env, cls, constr,array,1,vobsub->vob_subtitle_config.width,
		vobsub->vob_subtitle_config.height,vobsub->cur_endpts100/90,0);
	if(!obj){
	  ALOGE("parseSubtitleFile: failed to create an object");
	  return NULL;
	}
	//(*env)->CallVoidMethod(env, obj, constr, array, 1, get_inter_spu_width(),
		//get_inter_spu_height(),0);

    ALOGE("jni getdata! return a java object:RawData         finished");
	return obj;

}

//JNIEXPORT jobject JNICALL getdata
//	  (JNIEnv *env, jclass cl, jint msec )  
//{
//    ALOGE("jni getdata!,eed return a java object:SubData");
//	return NULL;
//
//}
#if 0
void inter_subtitle_parser()
{
	if(get_subtitle_num())
		get_inter_spu();
}
#else
void *inter_subtitle_parser()
{
	while(1){
		if(get_subtitle_num())
			get_inter_spu();
		usleep(500000);
	}
	return NULL;
}
#endif


int subtitle_thread_create()
{
#if 1
	pthread_t thread;
    int rc;
    ALOGI("[subtitle_thread:%d]starting controler thread!!\n", __LINE__);
    rc = pthread_create(&thread, NULL, inter_subtitle_parser, NULL);
    if (rc) {
        ALOGE("[subtitle_thread:%d]ERROR; start failed rc=%d\n", __LINE__,rc);
    }
	return rc;
#if 0
	struct sigaction act; 
    union sigval tsval; 

    act.sa_handler = inter_subtitle_parser; 
    act.sa_flags = 0; 
    sigemptyset(&act.sa_mask); 
    sigaction(50, &act, NULL); 

    while(1)
    { 
        usleep(300); 
        sigqueue(getpid(), 50, tsval); 
    } 
    return 0; 
#endif
#else

    struct sigaction tact; 
    
    tact.sa_handler = inter_subtitle_parser; 
    tact.sa_flags = 0; 
    sigemptyset(&tact.sa_mask); 
    sigaction(SIGALRM, &tact, NULL); 

	struct itimerval value; 
    
    value.it_value.tv_sec = 1; 
    value.it_value.tv_usec = 0;//500000; 
    value.it_interval = value.it_value; 
    setitimer(ITIMER_REAL, &value, NULL);
    //while(1);
	return 0;
#endif

}

static JNINativeMethod gMethods[] = {
    /* name, signature, funcPtr */
    { "parseSubtitleFileByJni", "(Ljava/lang/String;Ljava/lang/String;)Lcom/subtitleparser/SubtitleFile;",
            (void*) parseSubtitleFile},
    };

static JNINativeMethod insubMethods[] = {
    /* name, signature, funcPtr */
    	{ "getInSubtitleTotalByJni", "()I",                                 (void*)getInSubtitleTotal},
		{ "getCurrentInSubtitleIndexByJni", "()I",                          (void*)getCurrentInSubtitleIndex },
//		{ "FileChangedByJni", "(Ljava/lang/String;)V",                          (void*)playfileChanged },
		
    };
    
static JNINativeMethod insubdataMethods[] = {
    /* name, signature, funcPtr */
    	{ "getrawdata", "(I)Lcom/subtitleparser/subtypes/RawData;", (void*)getrawdata},
		{ "setInSubtitleNumberByJni", "(ILjava/lang/String;)I",                               (void*)setInSubtitleNumber},
		{ "closeInSub", "()V", (void*)closeInSubView},

    };    

static JNINativeMethod idxsubdataMethods[] = {
    /* name, signature, funcPtr */
    	{ "setIdxFile", "(Ljava/lang/String;I)V", (void*)setidxsubfile},
    	{ "getIdxsubRawdata", "(I)Lcom/subtitleparser/subtypes/RawData;", (void*)getidxsubrawdata},
    	{ "closeIdxSub", "()V", (void*)closeIdxSubFile},

    };  

static int registerNativeMethods(JNIEnv* env, const char* className,
                                 const JNINativeMethod* methods, int numMethods)
{
    int rc;
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'\n", className);
        return -1;
    }
    if (rc = ((*env)->RegisterNatives(env, clazz, methods, numMethods)) < 0) {
        ALOGE("RegisterNatives failed for '%s' %d\n", className, rc);
        return -1;
    }

    return 0;
}

JNIEXPORT jint
JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jclass * localClass;
    ALOGE("================= JNI_OnLoad ================\n");

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("GetEnv failed!");
        return -1;
    }

    if (registerNativeMethods(env, "com/subtitleparser/Subtitle",gMethods, NELEM(gMethods)) < 0){
        ALOGE("registerNativeMethods failed!");
        return -1;
    }
	if (registerNativeMethods(env, "com/subtitleparser/SubtitleUtils",insubMethods, NELEM(insubMethods)) < 0){
		ALOGE("registerNativeMethods failed!");
		return -1;
    }
	if (registerNativeMethods(env, "com/subtitleparser/subtypes/InSubApi",insubdataMethods, NELEM(insubdataMethods)) < 0){
		ALOGE("registerNativeMethods failed!");
		return -1;
    }    
    if (registerNativeMethods(env, "com/subtitleparser/subtypes/IdxSubApi",idxsubdataMethods, NELEM(idxsubdataMethods)) < 0){
		ALOGE("registerNativeMethods failed!");
		return -1;
    } 
	subtitle_thread_create();
	init_subtitle_file();
    return JNI_VERSION_1_4;
}
