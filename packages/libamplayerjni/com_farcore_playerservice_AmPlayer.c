#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <android/log.h>

#include "com_farcore_playerservice_AmPlayer.h"

#include "sys_conf.h"
#include "player.h"
//#include "adecproc.h"

#define LOG_TAG "AMPLAYER_JNI"

#if 0
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGI(...)
#define  LOGE(...)
#endif



//ugly code....
static play_control_t _plCtrl;

static JavaVM* gJavaVm = NULL; 
static jmethodID gPostMid = NULL;
static jclass gMplayerClazz = NULL;

//about player info updating interval
#define PLAYER_INFO_POP_INTERVAL 500 // 0.5s


jclass MediaPlayer_getClass(JNIEnv *env) {
    return (*env)->FindClass(env,"com/farcore/playerservice/AmPlayer");
}
/*
jclass PlaybackState_getClass(JNIEnv *env){

    return (*env)->FindClass(env,"com/farcore/playerservice/PlaybackState");

}*/

jclass MediaInfo_getClass(JNIEnv *env){
    return (*env)->FindClass(env,"com/farcore/playerservice/MediaInfo");
}

jclass AudioMediaInfo_getClass(JNIEnv *env){
    return (*env)->FindClass(env,"com/farcore/playerservice/AudioMediaInfo");
}
/*
jclass VideoMediaInfo_getClass(JNIEnv *env){
    return (*env)->FindClass(env,"com/farcore/playerservice/VideoMediaInfo");
}

jclass AudioTagInfo_getClass(JNIEnv *env){
    return (*env)->FindClass(env,"com/farcore/playerservice/AudioTagInfo");
}
*/
jclass Intersub_getClass(JNIEnv *env){
   return (*env)->FindClass(env,"com/farcore/playerservice/InternalSubtitleInfo");
    
}

jclass DivxInfo_getClass(JNIEnv *env){
  return (*env)->FindClass(env, "com/farcore/playerservice/DivxInfo");
}

int onUpdate_player_info_java( JNIEnv *env,int pid,player_info_t * info)
{
    if(gMplayerClazz!=NULL &&gPostMid!=NULL){
        LOGI("call java update method in JNI env 1");  
        (*env)->CallStaticVoidMethod(env, gMplayerClazz, gPostMid, pid,
            info->status, info->full_time, info->current_ms,info->last_time,
            info->error_no, info->drm_rental);
        LOGI("call java update method in JNI env 2");    
        return 0;
    }
    LOGE("never get java media player obj");
    return -1;
    
}
int update_player_info(int pid,player_info_t * info)
{
    JNIEnv *env;
    int isAttached = -1;
    int ret = -1;
    //LOGI("callback handler:current time:%d",time(NULL));
    if(NULL ==info){
        LOGE("info is null,drop it");
        return -1;
    }
    ret = (*gJavaVm)->GetEnv(gJavaVm, (void**) &env, JNI_VERSION_1_4);
    if(ret <0){
        //LOGE("callback handler:failed to get java env by native thread");
        ret = (*gJavaVm)->AttachCurrentThread(gJavaVm,&env,NULL);
        if(ret <0){
            LOGE("callback handler:failed to attach current thread");
            return -2;
        }
        isAttached = 1;
        
    }

    ret = onUpdate_player_info_java(env,pid,info);
        
    if(isAttached >0){
        LOGI("callback handler:detach current thread");
        (*gJavaVm)->DetachCurrentThread(gJavaVm);
    }   
    //LOGI("callback handler:end time:%d",time(NULL));    
    LOGI("pid:%d,status:0x%x,current ms:%d,total:%d,errcode:-0x%x\n",pid,info->status,info->current_ms,info->full_time,-(info->error_no));
    return 0;
}

int _media_info_dump(media_info_t* minfo)
{
    int i = 0;
    LOGI("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
    LOGI("======||file size:%lld\n",minfo->stream_info.file_size);
    LOGI("======||file type:%d\n",minfo->stream_info.type); 
    LOGI("======||has internal subtitle?:%s\n",minfo->stream_info.has_sub>0?"YES!":"NO!");
    LOGI("======||internal subtile counts:%d\n",minfo->stream_info.total_sub_num);
    LOGI("======||has video track?:%s\n",minfo->stream_info.has_video>0?"YES!":"NO!");
    LOGI("======||has audio track?:%s\n",minfo->stream_info.has_audio>0?"YES!":"NO!");    
    LOGI("======||duration:%d\n",minfo->stream_info.duration);
    LOGI("======||seekable:%d\n",minfo->stream_info.seekable);
    if(minfo->stream_info.has_video && minfo->stream_info.total_video_num>0)
    {        
        LOGI("======||video counts:%d\n",minfo->stream_info.total_video_num);
        LOGI("======||video width:%d\n",minfo->video_info[0]->width);
        LOGI("======||video height:%d\n",minfo->video_info[0]->height);
        LOGI("======||video bitrate:%d\n",minfo->video_info[0]->bit_rate);
        LOGI("======||video format:%d\n",minfo->video_info[0]->format);

    }
    if(minfo->stream_info.has_audio &&minfo->stream_info.total_audio_num> 0)
    {
        LOGI("======||audio counts:%d\n",minfo->stream_info.total_audio_num);
        
        if(NULL !=minfo->audio_info[0]->audio_tag)
        {
            LOGI("======||track title:%s",minfo->audio_info[0]->audio_tag->title!=NULL?minfo->audio_info[0]->audio_tag->title:"unknow");   
            LOGI("\n======||track album:%s",minfo->audio_info[0]->audio_tag->album!=NULL?minfo->audio_info[0]->audio_tag->album:"unknow"); 
            LOGI("\n======||track author:%s\n",minfo->audio_info[0]->audio_tag->author!=NULL?minfo->audio_info[0]->audio_tag->author:"unknow");
            LOGI("\n======||track year:%s\n",minfo->audio_info[0]->audio_tag->year!=NULL?minfo->audio_info[0]->audio_tag->year:"unknow");
            LOGI("\n======||track comment:%s\n",minfo->audio_info[0]->audio_tag->comment!=NULL?minfo->audio_info[0]->audio_tag->comment:"unknow"); 
            LOGI("\n======||track genre:%s\n",minfo->audio_info[0]->audio_tag->genre!=NULL?minfo->audio_info[0]->audio_tag->genre:"unknow");
            LOGI("\n======||track copyright:%s\n",minfo->audio_info[0]->audio_tag->copyright!=NULL?minfo->audio_info[0]->audio_tag->copyright:"unknow");  
            LOGI("\n======||track track:%d\n",minfo->audio_info[0]->audio_tag->track);  
        }
            

        
        for(i = 0;i<minfo->stream_info.total_audio_num;i++)
        {
            LOGI("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
            LOGI("======||%d 'st audio track codec type:%d\n",i,minfo->audio_info[i]->aformat);
            LOGI("======||%d 'st audio track audio_channel:%d\n",i,minfo->audio_info[i]->channel);
            LOGI("======||%d 'st audio track bit_rate:%d\n",i,minfo->audio_info[i]->bit_rate);
            LOGI("======||%d 'st audio track audio_samplerate:%d\n",i,minfo->audio_info[i]->sample_rate);
            LOGI("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
            
        }
        
    }
    if(minfo->stream_info.has_sub &&minfo->stream_info.total_sub_num>0){
        for(i = 0;i<minfo->stream_info.total_sub_num;i++)
        {
            if(0 == minfo->sub_info[i]->internal_external){
                LOGI("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
                LOGI("======||%d 'st internal subtitle pid:%d\n",i,minfo->sub_info[i]->id);   
                LOGI("======||%d 'st internal subtitle language:%s\n",i,minfo->sub_info[i]->sub_language?minfo->sub_info[i]->sub_language:"unknow"); 
                LOGI("======||%d 'st internal subtitle width:%d\n",i,minfo->sub_info[i]->width); 
                LOGI("======||%d 'st internal subtitle height:%d\n",i,minfo->sub_info[i]->height); 
                LOGI("======||%d 'st internal subtitle resolution:%d\n",i,minfo->sub_info[i]->resolution); 
                LOGI("======||%d 'st internal subtitle subtitle size:%lld\n",i,minfo->sub_info[i]->subtitle_size); 
                LOGI("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");       
            }
        }
    }
    
    LOGI("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
    return 0;
}

jobject DivxInfoContext_create(JNIEnv *env, drm_t* info){

  jclass meta_cls = DivxInfo_getClass(env);
  if(NULL == meta_cls){
    LOGE("failed to get DivxInfo class");
    return NULL;
  }
  
  jmethodID constructor = (*env)->GetMethodID(env, meta_cls, "<init>", "()V");
  jobject meta_obj = (*env)->NewObject(env, meta_cls, constructor);

  jstring regCode = (*env)->NewStringUTF(env,info->drm_reg_code);
  (*env)->SetObjectField(env, meta_obj, 
      (*env)->GetFieldID(env, meta_cls, "RegCode","Ljava/lang/String;"), regCode);

  (*env)->SetIntField(env, meta_obj,
      (*env)->GetFieldID(env, meta_cls, "CheckValue", "I"), info->drm_check_value);


  return meta_obj;

}

jobject MediaInfoContext_create(JNIEnv *env,media_info_t *msgt){
    
    int index = 0;
    jclass meta_cls = MediaInfo_getClass(env);
    if(NULL == meta_cls){            
        LOGE("failed to get MediaInfo class");
        return NULL;
    }
    if(NULL==msgt){
        LOGE("set invalid msg info");
        return NULL;
    }
   
    //set file info 
    jmethodID constructor = (*env)->GetMethodID(env, meta_cls, "<init>", "()V");
    
    jobject meta_obj = (*env)->NewObject(env,meta_cls,constructor);
    (*env)->SetIntField(env,meta_obj,\
        (*env)->GetFieldID(env, meta_cls, "seekable", "I"), (int)(msgt->stream_info.seekable));
    jfieldID id_filetype = (*env)->GetFieldID(env, meta_cls, "filetype", "I");
    (*env)->SetIntField(env, meta_obj, id_filetype, (int)(msgt->stream_info.type));

    (*env)->SetLongField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "filesize", "J"), msgt->stream_info.file_size); 
    (*env)->SetIntField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "duration", "I"), msgt->stream_info.duration); 
    (*env)->SetIntField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "drm_check", "I"), msgt->stream_info.drm_check); 
    if (msgt->stream_info.has_video && msgt->stream_info.total_video_num>0) { 
        (*env)->SetIntField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "width", "I"), msgt->video_info[0]->width); 
        (*env)->SetIntField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "height", "I"), msgt->video_info[0]->height); 
        (*env)->SetIntField(env,meta_obj, (*env)->GetFieldID(env, meta_cls, "vformat", "I"), msgt->video_info[0]->format);
        
    }
        
    if(msgt->stream_info.has_audio>0 && msgt->stream_info.total_audio_num>0){
        jclass ainfo_cls = AudioMediaInfo_getClass(env);
        jmethodID amid = (*env)->GetMethodID(env,ainfo_cls, "<init>", "()V");
        if(!amid){                
         LOGE("failed to get audio info constructor");
         return meta_obj;
        }
        jobjectArray ainfoArray = (*env)->NewObjectArray(env,msgt->stream_info.total_audio_num,ainfo_cls, NULL);  
        if(NULL == ainfoArray){                 
        LOGE("failed to get audio info object");              
        return meta_obj;       
        }

        for(index = 0;index<msgt->stream_info.total_audio_num;index++){
        jobject aobj = (*env)->NewObject(env,ainfo_cls, amid);
        if(NULL ==aobj){
            (*env)->DeleteLocalRef(env,ainfoArray);  
            LOGE("failed to get audio info object");                 
            return meta_obj;      
        }
        (*env)->SetIntField(env,aobj,\
            (*env)->GetFieldID(env, ainfo_cls, "audio_format", "I"), (int)(msgt->audio_info[index]->aformat));
        /*(*env)->SetIntField(env,aobj,\
            (*env)->GetFieldID(env, ainfo_cls, "audio_channel", "I"), (int)(msgt->audio_info[index]->channel));
        (*env)->SetIntField(env,aobj,\
            (*env)->GetFieldID(env, ainfo_cls, "audio_samplerate", "I"), (int)(msgt->audio_info[index]->sample_rate));
        (*env)->SetIntField(env,aobj,\
            (*env)->GetFieldID(env, ainfo_cls, "bit_rate", "I"), (int)(msgt->audio_info[index]->bit_rate));*/
        (*env)->SetIntField(env,aobj,\
            (*env)->GetFieldID(env, ainfo_cls, "uid", "I"), (int)(msgt->audio_info[index]->id));


        (*env)->SetObjectArrayElement(env,ainfoArray,index, aobj);  
        }  
        (*env)->SetObjectField(env,meta_obj,(*env)->GetFieldID(env, meta_cls, "ainfo", "[Lcom/farcore/playerservice/AudioMediaInfo;"),ainfoArray);
    }

    //for insub num;
    if(msgt->stream_info.total_sub_num>0)
    {
        LOGI("================== 'in internal subtitle num:%d\n", msgt->stream_info.total_sub_num);
        jclass sub_cls =Intersub_getClass(env);
        (*env)->SetStaticIntField(env,sub_cls,(*env)->GetStaticFieldID(env, sub_cls, "insub_num", "I"),(int)(msgt->stream_info.total_sub_num));
    }
#if 0               
        if(msgt->audio_info[0]->audio_tag!=NULL){

            jclass tag_cls = AudioTagInfo_getClass(env);
            jmethodID tagmid = (*env)->GetMethodID(env,tag_cls, "<init>", "()V");
            if(NULL == tagmid){                
                LOGE("failed to get tag info constructor"); 
                return meta_obj;
            }
            jobject tagobj = (*env)->NewObject(env,tag_cls, tagmid);
            if(NULL ==tagobj){
                LOGE("failed to get tag info object");   
                return meta_obj;      
            }        
            jstring title = (*env)->NewStringUTF(env,msgt->audio_info[0]->audio_tag->title);                                
            (*env)->SetObjectField(env,tagobj,
                (*env)->GetFieldID(env, tag_cls, "title", "Ljava/lang/String;"), title);

            jstring album = (*env)->NewStringUTF(env,msgt->audio_info[0]->audio_tag->album);

            (*env)->SetObjectField(env,tagobj,
                (*env)->GetFieldID(env, tag_cls, "album", "Ljava/lang/String;"),album);

            jstring author = (*env)->NewStringUTF(env,msgt->audio_info[0]->audio_tag->author);

            (*env)->SetObjectField(env,tagobj,
                (*env)->GetFieldID(env, tag_cls, "author", "Ljava/lang/String;"), author);                                     

            (*env)->SetObjectField(env,meta_obj,
                (*env)->GetFieldID(env, meta_cls, "taginfo", "Lcom/farcore/playerservice/AudioTagInfo;"),tagobj);                     


    }
    if(msgt->stream_info.total_sub_num>0) {
        jclass sub_cls =Intersub_getClass(env);
        jmethodID submid = (*env)->GetMethodID(env,sub_cls, "<init>", "()V");
        if(NULL == submid){
            LOGE("failed to get sub info constructor");  
            return meta_obj;
        }
        jobjectArray subArray = (*env)->NewObjectArray(env,msgt->stream_info.total_sub_num,sub_cls, NULL);  
        if(NULL == subArray){                   
            LOGI("failed to get audio info object");
            return meta_obj;       
        }

        for(index = 0;index<msgt->stream_info.total_sub_num;index++) {
         
            jobject subobj = (*env)->NewObject(env,sub_cls, submid);
            if(NULL ==subobj){
                LOGE("failed to get sub info object");
                return meta_obj;      
            }                        
            (*env)->SetIntField(env,subobj,
                (*env)->GetFieldID(env, sub_cls, "sub_uid", "I"),(int) (msgt->sub_info[index]->id));
           (*env)->SetObjectArrayElement(env,subArray,index, subobj);   

        }  
        
        (*env)->SetObjectField(env,meta_obj,(*env)->GetFieldID(env, meta_cls, "sinfo", "[Lcom/farcore/playerservice/InternalSubtitleInfo;"),subArray);  
    }
#endif
    return meta_obj;  
     
}



#define FILENAME_LENGTH_MAX 2048  // 2k
/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    addMediaSource
 * Signature: (Ljava/lang/String;III)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setMedia
  (JNIEnv *env, jobject obj, jstring url,jint isloop, jint pMode,jint st){    

    int pid = -1;   
    jclass clazz = (*env)->GetObjectClass(env, obj);

    gMplayerClazz =(*env)->NewGlobalRef(env,clazz);
    if(gMplayerClazz){
        LOGI("get mediaplayer class");
    }else{
        LOGE("can't get mediaplayer class");
        return -100;
    }
    

    
    gPostMid = (*env)->GetStaticMethodID(env, gMplayerClazz, "onUpdateState", "(IIIIIII)V");
    if(gPostMid){
        LOGI("get update state object id");
    }else{
        LOGE("failed to get update object id");
        return -101;
    }        
    const char * pname = (*env)->GetStringUTFChars(env,url, NULL);     
    if(NULL == pname)
    {
        LOGE("failed to change jstring to standard string");    
        return -1;
    }

    if(_plCtrl.file_name != NULL){
        free(_plCtrl.file_name);        
    }
    
    memset((void*)&_plCtrl,0,sizeof(play_control_t)); 
    
    player_register_update_callback(&_plCtrl.callback_fn,&update_player_info,PLAYER_INFO_POP_INTERVAL);

    _plCtrl.file_name = strndup(pname,FILENAME_LENGTH_MAX);
    _plCtrl.video_index = -1;//MUST
    _plCtrl.audio_index = -1;//MUST
    _plCtrl.hassub = 1;  //enable subtitle
    if(pMode == 1){
        _plCtrl.nosound = 1;
        SYS_set_tsync_enable(0);//if no sound,can set to be 0
        LOGI("disable sound");
    }else if(pMode ==2){
        _plCtrl.novideo = 1;
        LOGI("disable video");
    }
    if(st>0){
        LOGI("play start position:%d",st);
        _plCtrl.t_pos = st;
    }
    
    SYS_set_tsync_enable(1);//if no sound,can set to be 0

    if(isloop>0){
        _plCtrl.loop_mode =1;
        LOGI("set loop mode");
    }
    _plCtrl.need_start = 1;
    LOGI("set a media file to play,but need start it using start interface");
    pid=player_start(&_plCtrl,0);
    if(pid<0)
    {
        LOGI("player start failed!error=%d\n",pid);
        return -1;
    }
    
    
    (*env)->ReleaseStringUTFChars(env,url, pname);
    return pid;
            
}
  


/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    playMediaSource
 * Signature: Ljava/lang/String;II)I
 */
 

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_playMedia
  (JNIEnv *env, jobject obj,jstring url, jint isloop, jint pMode,jint st){
    
    int pid = -1;    
    jclass clazz = (*env)->GetObjectClass(env, obj);

    gMplayerClazz =(*env)->NewGlobalRef(env,clazz);
    if(gMplayerClazz){
        LOGI("get mediaplayer class");
    }else{
        LOGE("can't get mediaplayer class");
        return -100;
    }
    

    
    gPostMid = (*env)->GetStaticMethodID(env, gMplayerClazz, "onUpdateState", "(IIIIIII)V");
    if(gPostMid){
        LOGI("get update state object id");
    }else{
        LOGE("failed to get update object id");
        return -101;
    }

    const char * pname = (*env)->GetStringUTFChars(env,url, NULL);     
    if(NULL == pname)
    {
        LOGE("failed to change jstring to standard string");    
        return -1;
    }

    if(_plCtrl.file_name != NULL){
        free(_plCtrl.file_name);        
    }
    
    memset((void*)&_plCtrl,0,sizeof(play_control_t));     
    
    player_register_update_callback(&_plCtrl.callback_fn,&update_player_info,PLAYER_INFO_POP_INTERVAL);

    _plCtrl.file_name = strndup(pname,FILENAME_LENGTH_MAX);
    _plCtrl.video_index = -1;//MUST
    _plCtrl.audio_index = -1;//MUST
    _plCtrl.hassub = 1;
    if(pMode == 1){
        _plCtrl.nosound = 1;
        SYS_set_tsync_enable(0);//if no sound,can set to be 0
        LOGI("disable sound");
    }else if(pMode ==2){
        _plCtrl.novideo = 1;
        LOGI("disable video");
    }
    
    SYS_set_tsync_enable(1);//if no sound,can set to be 0

    if(isloop>0){
        _plCtrl.loop_mode =1;
        LOGI("set loop mode");
    }
    if(st>0){
        LOGI("play start position:%d",st);
        _plCtrl.t_pos = st;
    }    
    LOGI("add a media file to play");
    
    pid=player_start(&_plCtrl,0);
    if(pid<0)
    {
        LOGI("player start failed!error=%d\n",pid);
        return -1;
    }
    
    (*env)->ReleaseStringUTFChars(env,url, pname);

    return pid;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    closeMediaId
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_close
  (JNIEnv *env, jobject obj, jint pid){  
    if(pid>=0)  
        player_exit(pid);
    //if(gMplayerClazz != NULL)
    //    (*env)->DeleteGlobalRef(env,gMplayerClazz);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    start
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_start
  (JNIEnv *env, jobject obj, jint pid){
#if 0
    player_cmd_t cmd;
    int ret = -1;
    memset((void*)&cmd,0,sizeof(player_cmd_t));

    cmd.ctrl_cmd = CMD_START;

    ret = player_send_message(pid,&cmd);
    
    return ret;
#else
    LOGI("player start play");
    player_start_play(pid);
    return 0;
#endif
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    pause
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_pause
  (JNIEnv *env, jobject obj, jint pid){
    LOGI("player pause");
    player_pause(pid);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    resume
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_resume
  (JNIEnv *env, jobject obj, jint pid){
    LOGI("player resume");
    player_resume(pid);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    seek
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_seek
  (JNIEnv *env, jobject obj, jint pid, jint pos){
    player_timesearch(pid,pos);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    set3Dmode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_set3Dmode
  (JNIEnv *env, jobject obj, jint pid, jint mode){
    LOGI("JNI Set 3D mode:%d,player pid:%d\n",mode,pid);
    int ret = SYS_set_3D_mode((SYS_3D_MODE_SET)mode);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_set3Dviewmode
  (JNIEnv *env, jobject obj, jint vmode){
    int ret = -1; 
		ret = SYS_set_3D_view_mode((SYS_3D_VIEW_MODE_SET)vmode);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_set3Daspectfull
  (JNIEnv *env, jobject obj, jint aspect){
    int ret = -1; 
    LOGI("set 3d aspect full,value:%d\n",aspect);
		ret = SYS_set_3D_aspect_full(aspect);
    return ret;
}
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_set3Dswitch
  (JNIEnv *env, jobject obj, jint isOn){
    int ret = -1; 
    LOGI("set 3d switch:%d\n",isOn);
    ret = SYS_set_3D_switch(isOn);
		
    return ret;
}
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_set3Dgrating
  (JNIEnv *env, jobject obj, jint isOn){
	int ret = -1;
	LOGI("set 3d grating,%s\n",isOn>0?"enable":"disable");
	ret = SYS_set_3D_grating(isOn);
	return ret;
  
}
/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    stop
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_stop
  (JNIEnv *env, jobject obj, jint pid){
    int ret = -1; 
    LOGI("player stop");
    ret =player_stop(pid);
    return ret;
}



/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    getMetaInfo
 * Signature: (I)Ljava/lang/Object;
 */

JNIEXPORT jobject JNICALL Java_com_farcore_playerservice_AmPlayer_getMetaInfo
  (JNIEnv *env, jobject obj, jint pid){
    media_info_t minfo;
    int ret = -1;
    jobject meta_obj =NULL;  
    LOGI("Get media info");
    ret = player_get_media_info(pid,&minfo);
    if(ret<0){
        LOGE("can't get media info");
        return NULL;
    }
        
     _media_info_dump(&minfo);
    meta_obj = MediaInfoContext_create(env,&minfo); 
    return meta_obj;
}

/**
 * Class : com_farcode_playerservice_MediaPlayer
 * Method: getDivxInfo
 * Signature: (I)Ljava/lang/Object
 */
JNIEXPORT jobject JNICALL Java_com_farcore_playerservice_AmPlayer_getDivxInfo
(JNIEnv* env, jobject obj, jint pid){
  drm_t *info;
  jobject meta_obj = NULL;
  LOGI("Get Divx Info");
  info = drm_get_info();
  
  meta_obj = DivxInfoContext_create(env, info);

  return meta_obj;

}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    fastforward
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_fastforward
  (JNIEnv *env, jobject obj, jint pid, jint speed){
    int ret = -1;
    ret = player_forward(pid, speed);
    return ret;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    fastrewind
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_fastrewind
  (JNIEnv *env, jobject obj, jint pid, jint speed){
    int ret = -1;
    ret =player_backward(pid,speed);
    return ret;
}


/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setAudioTrack
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setAudioTrack
  (JNIEnv *env, jobject obj, jint pid, jint atrack_uid){
    
    player_aid(pid,atrack_uid);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setAudioTrack
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setAudioChannel
  (JNIEnv *env, jobject obj, jint pid, jint aud_channel){
	if(aud_channel == 0)
		audio_stereo(pid);
	else if(aud_channel == 1)
		audio_left_mono(pid) ;
	else if(aud_channel == 2)
		audio_right_mono(pid);
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setVolume
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setIVolume
  (JNIEnv *env , jclass clazz, jint vol){
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    mute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_mute
  (JNIEnv *env,jclass clazz){
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    unmute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_unmute
  (JNIEnv *env, jclass clazz){
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setVideoBlackOut
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setVideoBlackOut
  (JNIEnv *env, jclass clazz, jint isBlackout){
    return 0;
    
}
/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setRepeat
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setRepeat
  (JNIEnv *env, jobject obj, jint pid, jint isRepeat){
    jint ret = -1;
    if(isRepeat>0){
        LOGI("set loop play");
        ret = player_loop(pid);
    }else{
        LOGI("stop to play looply");
        ret = player_noloop(pid);

    }     
    return ret;
}
/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setSubtitleOut
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setSubtitleOut
  (JNIEnv *env, jobject obj, jint pid, jint sub_uid){   
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    setTone
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setTone
  (JNIEnv *env, jclass clazz, jint pid, jint tone){     
    return 0;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    init
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_init
  (JNIEnv *env, jclass clazz){
    int ret =-1;
    ret = player_init();
    
    //ret = amadec_thread_init();
    memset((void*)&_plCtrl,0,sizeof(play_control_t)); 
    _plCtrl.file_name = NULL;    
   
    LOGI("player init ok"); 
    return ret;
}

/*
 * Class:     com_farcore_playerservice_MediaPlayer
 * Method:    uninit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_uninit
  (JNIEnv *env, jclass clazz){
    //int ret = -1;
    //ret = amadec_thread_exit();
    pid_info_t alive_pids;
    int i =-1;
    player_list_allpid(&alive_pids);
    for(i=0;i<alive_pids.num;i++){
        if(check_pid_valid(alive_pids.pid[i]))
            player_exit(alive_pids.pid[i]);
    }
    
    if(_plCtrl.file_name !=NULL){
        LOGI("collect memory for player para\n");
        free(_plCtrl.file_name);
        _plCtrl.file_name = NULL;
    }
    LOGI("player uninit ok"); 
    return 0;

}

jint
Java_com_farcore_playerservice_AmPlayer_enablecolorkey(JNIEnv *env, jclass clazz, jshort key_rgb565) 
{
    int ret = -1;
    short key = key_rgb565;
    ret =SYS_enable_colorkey(key);   
    return ret;
}

jint
Java_com_farcore_playerservice_AmPlayer_disablecolorkey(JNIEnv *env, jclass clazz) 
{
    int ret = -1;
    ret = SYS_disable_colorkey();
    return ret;
}
JNIEXPORT jint Java_com_farcore_playerservice_AmPlayer_setglobalalpha(JNIEnv *env, jclass clazz, jint alpha){
    int ret = -1;
    ret = SYS_set_global_alpha(alpha);
    LOGI("set global alpha is %d",alpha);
    return ret;
}
JNIEXPORT jint Java_com_farcore_playerservice_AmPlayer_getosdbpp(JNIEnv *env, jclass clazz){
    jint ret = -1;
    ret = SYS_get_osdbpp();
    LOGI("get osd bpp:%d",ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_enable_1freescale(JNIEnv *env, jclass class, jint cfg){
    jint ret = -1;
    ret = enable_freescale(cfg);
    LOGI("enable freeacale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_disable_1freescale(JNIEnv *env, jclass class, jint cfg){
    jint ret = -1;
    ret = disable_freescale(cfg);
    LOGI("disable freeacale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_getProductType(JNIEnv *env, jclass class)
{
    int ret;
#ifdef ENABLE_FREE_SCALE
    LOGI("ENABLE_FREE_SCALE defined!\n");
    ret = 1;
#else
    LOGI("ENABLE_FREE_SCALE not define!\n");
    ret = 0;
#endif
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_disableFreescaleMBX(JNIEnv *env, jclass class){
    jint ret = -1;
    ret = disable_freescale_MBX();
    LOGI("disable freeacale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_enable2XScale(JNIEnv *env, jclass class){
    jint ret = -1;
    ret = enable_2Xscale();
    LOGI("enable2XScale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_enable2XYScale(JNIEnv *env, jclass class){
    jint ret = -1;
    ret = enable_2XYscale();
    LOGI("enable2XYScale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_enableFreescaleMBX(JNIEnv *env, jclass class){
    jint ret = -1;
    ret = enable_freescale_MBX();
    LOGI("enableFreescaleMBX:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_disable2X2XYScale(JNIEnv *env, jclass class){
    jint ret = -1;
    ret = disable_2X_2XYscale();
    LOGI("disable2X2XYScale:%d\n", ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_GL2XScale(JNIEnv *env, jclass class, jint mSwitch){
    jint ret = -1;
    ret = GL_2X_scale(mSwitch);
    LOGI("GL2XScale:%d\n", ret);
    return ret;
}

//
static JNINativeMethod gMethods[] = {
    {"setMedia",            "(Ljava/lang/String;III)I",         (void*)Java_com_farcore_playerservice_AmPlayer_setMedia},   
    {"playMedia",           "(Ljava/lang/String;III)I",         (void*)Java_com_farcore_playerservice_AmPlayer_playMedia}, 
    {"close",       "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_close},
    {"start",           "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_start}, 
    {"stop",            "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_stop},
    {"pause",           "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_pause},
    {"resume",          "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_resume},
    {"seek",            "(II)I",                        (void*)Java_com_farcore_playerservice_AmPlayer_seek},
    {"set3Dmode",        "(II)I",                       (void*)Java_com_farcore_playerservice_AmPlayer_set3Dmode},
    {"set3Dviewmode",           "(I)I",                 (void*)Java_com_farcore_playerservice_AmPlayer_set3Dviewmode},
    {"set3Daspectfull",           "(I)I",               (void*)Java_com_farcore_playerservice_AmPlayer_set3Daspectfull},
    {"set3Dswitch",           "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_set3Dswitch},    
	{"set3Dgrating",           "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_set3Dgrating},
    {"fastforward",             "(II)I",                (void*)Java_com_farcore_playerservice_AmPlayer_fastforward},
    {"fastrewind",              "(II)I",                (void*)Java_com_farcore_playerservice_AmPlayer_fastrewind},      
    {"setAudioTrack",           "(II)I",                (void*)Java_com_farcore_playerservice_AmPlayer_setAudioTrack},
    {"setSubtitleOut",              "(II)I",            (void*)Java_com_farcore_playerservice_AmPlayer_setSubtitleOut},
    {"setVideoBlackOut",        "(I)I",                 (void*)Java_com_farcore_playerservice_AmPlayer_setVideoBlackOut},
    {"setTone",                 "(II)I",                    (void*)Java_com_farcore_playerservice_AmPlayer_setTone},
    {"setRepeat",               "(II)I",                    (void*)Java_com_farcore_playerservice_AmPlayer_setRepeat},
    {"setIVolume",                  "(I)I",                         (void*)Java_com_farcore_playerservice_AmPlayer_setIVolume},
    {"getMetaInfo",                 "(I)Ljava/lang/Object;",                (void*)Java_com_farcore_playerservice_AmPlayer_getMetaInfo},
    {"getDivxInfo",        "(I)Ljava/lang/Object;",         (void*)Java_com_farcore_playerservice_AmPlayer_getDivxInfo},
    {"mute",                    "()I",                          (void*)Java_com_farcore_playerservice_AmPlayer_mute},
    {"unmute",                  "()I",                          (void*)Java_com_farcore_playerservice_AmPlayer_unmute},
    {"native_init",         "()I",                      (void*)Java_com_farcore_playerservice_AmPlayer_native_init},
    {"native_uninit",           "()I",                  (void*)Java_com_farcore_playerservice_AmPlayer_native_uninit},  
    { "native_enablecolorkey", "(S)I",                  (void*) Java_com_farcore_playerservice_AmPlayer_enablecolorkey },
    { "native_disablecolorkey", "()I",                  (void*) Java_com_farcore_playerservice_AmPlayer_disablecolorkey },
    { "native_setglobalalpha",              "(I)I",                                 (void*) Java_com_farcore_playerservice_AmPlayer_setglobalalpha },   
    { "native_getosdbpp",                   "()I",                                          (void*) Java_com_farcore_playerservice_AmPlayer_getosdbpp },   
        
    
};


int jniRegisterNativeMethods(JNIEnv* env,
                             const char* className,
                             const JNINativeMethod* gMethods,
                             int numMethods)
{
    jclass clazz;

    LOGI("Registering %s natives\n", className);
    clazz = (*env)->FindClass(env,className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", className);
    return -1;
    }
    if ((*env)->RegisterNatives(env,clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'\n", className);
    return -1;
    }
    return 0;
}

int register_com_farcore_playerservice_mediaplayer(JNIEnv *env) {
    const char* const kClassPathName = "com/farcore/playerservice/AmPlayer";

    return jniRegisterNativeMethods(env,kClassPathName , gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}



jint JNI_OnLoad(JavaVM* vm, void* reserved){   
    jint result = -1;      
    JNIEnv* env = NULL;
       
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) { 
        LOGE("GetEnv failed!");        
        return -1;  
    }
    gJavaVm = vm;//    
    
    LOGI("GetEnv ok");    /* success -- return valid version number */   
    result = JNI_VERSION_1_4;   
    register_com_farcore_playerservice_mediaplayer(env); 
    return result;
}

void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("GetEnv failed!");
        return;
    }
    if(gMplayerClazz != NULL)
        (*env)->DeleteGlobalRef(env, gMplayerClazz);
    
    return;
}
