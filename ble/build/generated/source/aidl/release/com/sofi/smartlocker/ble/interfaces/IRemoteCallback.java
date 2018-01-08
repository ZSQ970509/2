/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\AndroidStudioCode\\2\\ble\\src\\main\\aidl\\com\\sofi\\smartlocker\\ble\\interfaces\\IRemoteCallback.aidl
 */
package com.sofi.smartlocker.ble.interfaces;
// Declare any non-default types here with import statements

public interface IRemoteCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.sofi.smartlocker.ble.interfaces.IRemoteCallback
{
private static final java.lang.String DESCRIPTOR = "com.sofi.smartlocker.ble.interfaces.IRemoteCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.sofi.smartlocker.ble.interfaces.IRemoteCallback interface,
 * generating a proxy if needed.
 */
public static com.sofi.smartlocker.ble.interfaces.IRemoteCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.sofi.smartlocker.ble.interfaces.IRemoteCallback))) {
return ((com.sofi.smartlocker.ble.interfaces.IRemoteCallback)iin);
}
return new com.sofi.smartlocker.ble.interfaces.IRemoteCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
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
case TRANSACTION_bleSupportFeature:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.bleSupportFeature(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_bleScanResult:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
int _arg3;
_arg3 = data.readInt();
this.bleScanResult(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_bleStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
java.lang.String _arg1;
_arg1 = data.readString();
this.bleStatus(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_bleCmdError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.bleCmdError(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getLockStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.getLockStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLockTamper:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.getLockTamper(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_bleCmdReply:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.bleCmdReply(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.sofi.smartlocker.ble.interfaces.IRemoteCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void bleSupportFeature(boolean isFeature) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isFeature)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_bleSupportFeature, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//是否支持蓝牙4.0：true支持,false不支持

@Override public void bleScanResult(java.lang.String name, java.lang.String address, java.lang.String vol, int rssi) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(address);
_data.writeString(vol);
_data.writeInt(rssi);
mRemote.transact(Stub.TRANSACTION_bleScanResult, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//扫描到的蓝牙名字、地址、电压、信号强度

@Override public void bleStatus(boolean status, java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((status)?(1):(0)));
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_bleStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//蓝牙连接状态：false关闭，true连接

@Override public void bleCmdError(int cmd, java.lang.String msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
_data.writeString(msg);
mRemote.transact(Stub.TRANSACTION_bleCmdError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//读取锁状态、开锁、关锁命令失败回调，cmd参考CmdUtil, msg错误信息

@Override public void getLockStatus(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_getLockStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//读取锁状态成功的回调

@Override public void getLockTamper(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_getLockTamper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//读取锁防拆成功的回调

@Override public void bleCmdReply(int cmd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
mRemote.transact(Stub.TRANSACTION_bleCmdReply, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_bleSupportFeature = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_bleScanResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_bleStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_bleCmdError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getLockStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getLockTamper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_bleCmdReply = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void bleSupportFeature(boolean isFeature) throws android.os.RemoteException;
//是否支持蓝牙4.0：true支持,false不支持

public void bleScanResult(java.lang.String name, java.lang.String address, java.lang.String vol, int rssi) throws android.os.RemoteException;
//扫描到的蓝牙名字、地址、电压、信号强度

public void bleStatus(boolean status, java.lang.String address) throws android.os.RemoteException;
//蓝牙连接状态：false关闭，true连接

public void bleCmdError(int cmd, java.lang.String msg) throws android.os.RemoteException;
//读取锁状态、开锁、关锁命令失败回调，cmd参考CmdUtil, msg错误信息

public void getLockStatus(int state) throws android.os.RemoteException;
//读取锁状态成功的回调

public void getLockTamper(int state) throws android.os.RemoteException;
//读取锁防拆成功的回调

public void bleCmdReply(int cmd) throws android.os.RemoteException;
}
