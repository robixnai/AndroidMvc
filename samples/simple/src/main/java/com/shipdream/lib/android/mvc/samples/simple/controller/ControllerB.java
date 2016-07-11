package com.shipdream.lib.android.mvc.samples.simple.controller;

import com.shipdream.lib.android.mvc.controller.internal.BaseControllerImpl;
import com.shipdream.lib.android.mvc.event.BaseEventV;
import com.shipdream.lib.android.mvc.manager.NavigationManager;
import com.shipdream.lib.android.mvc.samples.simple.manager.CounterManager;

import javax.inject.Inject;

/**
 * Created by kejun on 11/07/2016.
 */

public class ControllerB extends BaseControllerImpl{
    public interface EventC2V {
        /**
         * Event2C to notify views counter has been updated
         */
        class OnCountUpdated extends BaseEventV {
            private final int count;
            public OnCountUpdated(Object sender, int count) {
                super(sender);
                this.count = count;
            }

            public int getCount() {
                return count;
            }
        }
    }

    @Inject
    private CounterManager counterManager;

    @Inject
    private NavigationManager navigationManager;

    @Override
    public Class modelType() {
        return null;
    }

    private void onEvent(CounterManager.Event.OnCountChanged event) {
        postEvent2V(new EventC2V.OnCountUpdated(event.getSender(), event.getCurrentValue()));
    }

    public void increment(Object sender) {
        counterManager.increment(sender);
    }

    public void decrement(Object sender) {
        counterManager.decrement(sender);
    }

    public int getCount() {
        return counterManager.getCount();
    }

    /**
     * Navigate to LocationB by {@link NavigationManager}to show advance view that can update
     * count continuously by holding buttons.
     * @param sender
     */
    public void navigateBack(Object sender) {
        navigationManager.navigate(sender).back();
    }

}
