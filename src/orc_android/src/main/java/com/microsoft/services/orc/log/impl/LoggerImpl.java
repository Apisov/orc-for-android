package com.microsoft.services.orc.log.impl;

import android.util.Log;

import com.microsoft.services.orc.log.LogLevel;
import com.microsoft.services.orc.log.LoggerBase;

/**
 * The type Logger impl.
 */
public class LoggerImpl extends LoggerBase {
    private static final String TAG = "Office365-SDK";

    @Override
    public void print(String content, LogLevel logLevel) {
        if (content != null) {
            switch (logLevel) {
                case ERROR:
                    Log.e(TAG, content);
                    break;
                case INFO:
                    Log.i(TAG, content);
                    break;
                case VERBOSE:
                    Log.v(TAG, content);
                    break;
                case WARNING:
                    Log.w(TAG, content);
                    break;
            }
        }
    }
}
