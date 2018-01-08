package com.xsq.czy.state;


import com.squareup.otto.Bus;
import com.xsq.czy.util.ObjectUtil;

/**
 * Created by lan on 2016/4/14.
 */
public final class ApplicationState implements BaseState {
    private final Bus mEventBus;

    public ApplicationState(Bus mEventBus) {
        this.mEventBus = mEventBus;
    }

    @Override
    public void notifyBleCallback(Object item) {
        if(ObjectUtil.isNotNull(item)) {
            mEventBus.post(new BleCallbackListener<>(item));
        }
    }

    @Override
    public void registerEvent(Object recevier) {
        mEventBus.register(recevier);
    }

    @Override
    public void unregisterEvent(Object recevier) {
        mEventBus.unregister(recevier);
    }

}
