/*
 * Copyright 2016 Kejun Xia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shipdream.lib.android.mvp;

import android.os.Bundle;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MvpStateKeeper implements StateKeeper {
    private static Gson gson;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AndroidModelKeeper navigationModelKeeper = new NavigationModelKeeperModelKeeper();
    Bundle bundle;

    MvpStateKeeper() {
        gson = new GsonBuilder().create();
    }

    //TODO: first param should be Bean and bean's model should be saved recursively
    @Override
    public void saveState(String key, Object value) {
        long ts = System.currentTimeMillis();
        if (value instanceof Parcelable) {
            Parcelable parcelable = (Parcelable) value;
            bundle.putParcelable(key, parcelable);
            logger.trace("Save model by parcel model keeper - {}, {}ms used.",
                    key, System.currentTimeMillis() - ts);
        } else {
            //Use Gson to restore model
//            String json = gson.toJson(value);
//            bundle.putString(key, json);
//
//            logger.trace("Save model by JSON - {}, {}ms used. Content: {}",
//                    key, System.currentTimeMillis() - ts, json);
        }
    }

    @Override
    public <T> T restoreState(String key, Class<T> type) {
//        long ts = System.currentTimeMillis();
//        String json = bundle.getString(key);
//        if (json == null) {
//            throw new IllegalStateException("Can't find restore model for " + key);
//        } else {
//            T state = gson.fromJson(json, type);
//            logger.trace("Save model by JSON - {}, {}ms used. Content: {}",
//                    key, System.currentTimeMillis() - ts, json);
//            return state;
//        }
        return null;
    }

}