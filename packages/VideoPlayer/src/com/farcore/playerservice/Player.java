/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Player.aidl
 */
package com.farcore.playerservice;
public interface Player extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.farcore.playerservice.Player
{
private static final java.lang.String DESCRIPTOR = "com.farcore.playerservice.Player";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.farcore.playerservice.Player interface,
 * generating a proxy if needed.
 */
public static com.farcore.playerservice.Player asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.farcore.playerservice.Player))) {
return ((com.farcore.playerservice.Player)iin);
}
return new com.farcore.playerservice.Player.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_Init:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Init();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Open:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _result = this.Open(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Play:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Play();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Pause:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Pause();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Resume:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Resume();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Stop:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Stop();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Close:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.Close();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_GetMediaInfo:
{
data.enforceInterface(DESCRIPTOR);
com.farcore.playerservice.MediaInfo _result = this.GetMediaInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_GetDivxInfo:
{
data.enforceInterface(DESCRIPTOR);
com.farcore.playerservice.DivxInfo _result = this.GetDivxInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_SwitchAID:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.SwitchAID(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_SwitchAudioChannel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.SwitchAudioChannel(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_SetColorKey:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.SetColorKey(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_DisableColorKey:
{
data.enforceInterface(DESCRIPTOR);
this.DisableColorKey();
reply.writeNoException();
return true;
}
case TRANSACTION_GetOsdBpp:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.GetOsdBpp();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Seek:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.Seek(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_Set3Dmode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.Set3Dmode(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_FastForward:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.FastForward(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_BackForward:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.BackForward(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_RegisterClientMessager:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _arg0;
_arg0 = data.readStrongBinder();
int _result = this.RegisterClientMessager(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.farcore.playerservice.Player
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int Init() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Init, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Open(java.lang.String filepath, int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filepath);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_Open, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Play() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Play, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Pause, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Resume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Resume, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Stop, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Close() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Close, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.farcore.playerservice.MediaInfo GetMediaInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.farcore.playerservice.MediaInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_GetMediaInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.farcore.playerservice.MediaInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.farcore.playerservice.DivxInfo GetDivxInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.farcore.playerservice.DivxInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_GetDivxInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.farcore.playerservice.DivxInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int SwitchAID(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_SwitchAID, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int SwitchAudioChannel(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_SwitchAudioChannel, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int SetColorKey(int color) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(color);
mRemote.transact(Stub.TRANSACTION_SetColorKey, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void DisableColorKey() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_DisableColorKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int GetOsdBpp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_GetOsdBpp, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Seek(int time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(time);
mRemote.transact(Stub.TRANSACTION_Seek, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Set3Dmode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_Set3Dmode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int Set3Dviewmode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_Set3Dviewmode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}

public int Set3Daspectfull(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_Set3Daspectfull, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}

public int Set3Dswitch(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_Set3Dswitch, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}

public int Set3Dgrating(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_Set3Dgrating, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}

public int FastForward(int speed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(speed);
mRemote.transact(Stub.TRANSACTION_FastForward, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int BackForward(int speed) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(speed);
mRemote.transact(Stub.TRANSACTION_BackForward, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int RegisterClientMessager(android.os.IBinder hbinder) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder(hbinder);
mRemote.transact(Stub.TRANSACTION_RegisterClientMessager, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_Init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_Open = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_Play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_Pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_Resume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_Stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_Close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_GetMediaInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_GetDivxInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_SwitchAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_SwitchAudioChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_SetColorKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_DisableColorKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_GetOsdBpp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_Seek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_Set3Dmode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_FastForward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_BackForward = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_RegisterClientMessager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_Set3Dviewmode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_Set3Daspectfull = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_Set3Dswitch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_Set3Dgrating = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
}
public int Init() throws android.os.RemoteException;
public int Open(java.lang.String filepath, int position) throws android.os.RemoteException;
public int Play() throws android.os.RemoteException;
public int Pause() throws android.os.RemoteException;
public int Resume() throws android.os.RemoteException;
public int Stop() throws android.os.RemoteException;
public int Close() throws android.os.RemoteException;
public com.farcore.playerservice.MediaInfo GetMediaInfo() throws android.os.RemoteException;
public com.farcore.playerservice.DivxInfo GetDivxInfo() throws android.os.RemoteException;
public int SwitchAID(int id) throws android.os.RemoteException;
public int SwitchAudioChannel(int id) throws android.os.RemoteException;
public int SetColorKey(int color) throws android.os.RemoteException;
public void DisableColorKey() throws android.os.RemoteException;
public int GetOsdBpp() throws android.os.RemoteException;
public int Seek(int time) throws android.os.RemoteException;
public int Set3Dmode(int mode) throws android.os.RemoteException;
public int Set3Dviewmode(int mode) throws android.os.RemoteException;
public int Set3Daspectfull(int aspect) throws android.os.RemoteException;
public int Set3Dswitch(int isOn) throws android.os.RemoteException;
public int Set3Dgrating(int isOn) throws android.os.RemoteException;
public int FastForward(int speed) throws android.os.RemoteException;
public int BackForward(int speed) throws android.os.RemoteException;
public int RegisterClientMessager(android.os.IBinder hbinder) throws android.os.RemoteException;
}
