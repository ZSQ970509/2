package com.xsq.czy.state;


/**
 * Created by lan on 2016/4/14.
 */
public interface BaseState {

    void registerEvent(Object recevier);

    void unregisterEvent(Object recevier);

    void notifyBleCallback(Object item) ;

    class BaseArgumentEvent<T> {
        public final T item;

        public BaseArgumentEvent(T item) {
            this.item = item;
        }
    }

    class BleCallbackListener<T> {
        public final T item;

        public BleCallbackListener(T item) {
            this.item = item;
        }
    }

}
