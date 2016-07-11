package com.shipdream.lib.android.mvc.samples.simple.manager;

import com.shipdream.lib.android.mvc.event.ValueChangeEventC;
import com.shipdream.lib.android.mvc.manager.internal.BaseManagerImpl;

/**
 * Created by kejun on 11/07/2016.
 */

public class CounterManager extends BaseManagerImpl<CounterManager.Model>{
    public interface Event {
        class OnCountChanged extends ValueChangeEventC<Integer> {
            public OnCountChanged(Object sender, int lastValue, int currentValue) {
                super(sender, lastValue, currentValue);
            }
        }
    }

    public static class Model {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Override
    public Class<Model> modelType() {
        return Model.class;
    }

    public void setCount(Object sender, int count) {
        int oldCount = getCount();
        getModel().setCount(count);
        postEvent2C(new Event.OnCountChanged(sender, oldCount, count));
    }

    public int getCount() {
        return getModel().getCount();
    }

    public void increment(Object sender) {
        setCount(sender, getCount() + 1);
    }

    public void decrement(Object sender) {
        setCount(sender, getCount() - 1);
    }
}
