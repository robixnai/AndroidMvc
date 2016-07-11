package com.shipdream.lib.android.mvc.samples.simple.controller;

import com.shipdream.lib.android.mvc.controller.internal.BaseControllerImpl;
import com.shipdream.lib.android.mvc.event.BaseEventV;
import com.shipdream.lib.android.mvc.manager.NavigationManager;
import com.shipdream.lib.android.mvc.samples.simple.manager.CounterManager;

import javax.inject.Inject;

/**
 * Created by kejun on 11/07/2016.
 */

public class ControllerASubView extends BaseControllerImpl{
    public interface EventC2V {
        /**
         * Event2C to notify views counter has been updated
         */
        class OnCountUpdated extends BaseEventV {
            private final String countInEnglish;
            public OnCountUpdated(Object sender, String countInEnglish) {
                super(sender);
                this.countInEnglish = countInEnglish;
            }

            public String getCountInEnglish() {
                return countInEnglish;
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
        postEvent2V(new EventC2V.OnCountUpdated(event.getSender(),
                convertNumberToEnglish(event.getCurrentValue())));
    }

    public String getCountInEnglish() {
        return convertNumberToEnglish(counterManager.getCount());
    }

    private String convertNumberToEnglish(int number) {
        if (number < -3) {
            return "Less than negative three";
        } else  if (number == -3) {
            return "Negative three";
        } else  if (number == -2) {
            return "Negative two";
        } else  if (number == -1) {
            return "Negative one";
        } else if (number == 0) {
            return "Zero";
        } else if (number == 1) {
            return "One";
        } else if (number == 2) {
            return "Two";
        } else if (number == 3) {
            return "Three";
        } else {
            return "Greater than three";
        }
    }

}
