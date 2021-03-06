package com.shipdream.lib.android.mvc.samples.simple.service.internal;

import android.os.Handler;

import com.shipdream.lib.android.mvc.samples.simple.service.Poster;

public class PosterImpl implements Poster {
    private Handler handler = new Handler();

    @Override
    public void postDelayed(Runnable runnable, long delayMs) {
        handler.postDelayed(runnable, delayMs);
    }
}
