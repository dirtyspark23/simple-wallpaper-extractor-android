/*
 * Copyright (C) 2013 DeAngelo Mannie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nivler.android.wallpaperextractor.helper;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class MediaScannerNotifier implements MediaScannerConnectionClient { 
    private MediaScannerConnection mConnection; 
    private String mPath; 
    private String mMimeType; 
    
    private static final String TAG = "Wallpaper Extractor";
    
    Context mContext;
    
    public MediaScannerNotifier(Context context, String path, String mimeType) {
    	mContext = context;
    	mPath = path;
    	mMimeType = mimeType;
    	mConnection = new MediaScannerConnection(context, this);
    	mConnection.connect();
    }
    
    public void onMediaScannerConnected() { 
        mConnection.scanFile(mPath, mMimeType); 
    } 

    public void onScanCompleted(String path, Uri uri) { 
    	try { 
            if (uri != null) { 
            	Intent intent = new Intent(Intent.ACTION_VIEW); 
                intent.setData(uri); 
                mContext.startActivity(intent); 
            	Log.i(TAG, "File successfully saved!");
            }
    	} finally {
    		 mConnection.disconnect(); 
             mContext = null; 
    	}
    }
}