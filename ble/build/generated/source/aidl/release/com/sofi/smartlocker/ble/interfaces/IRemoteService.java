/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\AndroidStudioCode\\2\\ble\\src\\main\\aidl\\com\\sofi\\smartlocker\\ble\\interfaces\\IRemoteService.aidl
 */
package com.sofi.smartlocker.ble.interfaces;
// Declare any non-default types here with import statements

public interface IRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.sofi.smartlocker.ble.interfaces.IRemoteService
{
private static final java.lang.String DESCRIPTOR = "com.sofi.smartlocker.ble.interfaces.IRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.sofi.smartlocker.ble.interfaces.IRemoteService interface,
 * generating a proxy if needed.
 */
public static com.sofi.smartlocker.ble.interfaces.IRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.sofi.smartlocker.ble.interfaces.IRemoteService))) {
return ((com.sofi.smartlocker.ble.interfaces.IRemoteService)iin);
}
return new com.sofi.smartlocker.ble.interfaces.IRemoteService.Stub.Proxy(obj);
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
case TRANSACTION_isBleEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBleEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_enableBle:
{
data.enforceInterface(DESCRIPTOR);
this.enableBle();
reply.writeNoException();
return true;
}
case TRANSACTION_setHighMode:
{
data.enforceInterface(DESCRIPTOR);
this.setHighMode();
reply.writeNoException();
return true;
}
case TRANSACTION_setLowMode:
{
data.enforceInterface(DESCRIPTOR);
this.setLowMode();
reply.writeNoException();
return true;
}
case TRANSACTION_startBleScan:
{
data.enforceInterface(DESCRIPTOR);
this.startBleScan();
reply.writeNoException();
return true;
}
case TRANSACTION_stopBleScan:
{
data.enforceInterface(DESCRIPTOR);
this.stopBleScan();
reply.writeNoException();
return true;
}
case TRANSACTION_isBleScaning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBleScaning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connectLock:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connectLock(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_disconnectLock:
{
data.enforceInterface(DESCRIPTOR);
this.disconnectLock();
reply.writeNoException();
return true;
}
case TRANSACTION_getLockStatus:
{
data.enforceInterface(DESCRIPTOR);
this.getLockStatus();
reply.writeNoException();
return true;
}
case TRANSACTION_openLock:
{
data.enforceInterface(DESCRIPTOR);
this.openLock();
reply.writeNoException();
return true;
}
case TRANSACTION_closeLock:
{
data.enforceInterface(DESCRIPTOR);
this.closeLock();
reply.writeNoException();
return true;
}
case TRANSACTION_getTamper:
{
data.enforceInterface(DESCRIPTOR);
this.getTamper();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.sofi.smartlocker.ble.interfaces.IRemoteCallback _arg0;
_arg0 = com.sofi.smartlocker.ble.interfaces.IRemoteCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.sofi.smartlocker.ble.interfaces.IRemoteCallback _arg0;
_arg0 = com.sofi.smartlocker.ble.interfaces.IRemoteCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.sofi.smartlocker.ble.interfaces.IRemoteService
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
@Override public boolean isBleEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBleEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void enableBle() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableBle, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setHighMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setHighMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setLowMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setLowMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startBleScan() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startBleScan, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopBleScan() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopBleScan, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isBleScaning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBleScaning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void connectLock(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_connectLock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void disconnectLock() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnectLock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void getLockStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLockStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void openLock() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_openLock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void closeLock() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeLock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void getTamper() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTamper, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.sofi.smartlocker.ble.interfaces.IRemoteCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.sofi.smartlocker.ble.interfaces.IRemoteCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isBleEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_enableBle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setHighMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setLowMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startBleScan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopBleScan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isBleScaning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_connectLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_disconnectLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getLockStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_openLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_closeLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getTamper = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
}
public boolean isBleEnable() throws android.os.RemoteException;
public void enableBle() throws android.os.RemoteException;
public void setHighMode() throws android.os.RemoteException;
public void setLowMode() throws android.os.RemoteException;
public void startBleScan() throws android.os.RemoteException;
public void stopBleScan() throws android.os.RemoteException;
public boolean isBleScaning() throws android.os.RemoteException;
public void connectLock(java.lang.String address) throws android.os.RemoteException;
public void disconnectLock() throws android.os.RemoteException;
public void getLockStatus() throws android.os.RemoteException;
public void openLock() throws android.os.RemoteException;
public void closeLock() throws android.os.RemoteException;
public void getTamper() throws android.os.RemoteException;
public void registerCallback(com.sofi.smartlocker.ble.interfaces.IRemoteCallback callback) throws android.os.RemoteException;
public void unregisterCallback(com.sofi.smartlocker.ble.interfaces.IRemoteCallback callback) throws android.os.RemoteException;
}
