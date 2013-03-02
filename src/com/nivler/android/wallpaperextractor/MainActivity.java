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

package com.nivler.android.wallpaperextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nivler.android.wallpaperextractor.helper.MediaScannerNotifier;
import com.nivler.android.wallpaperextractor.util.Util;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private String SAVE_LOCATION = Environment.getExternalStorageDirectory().toString();
	private String basePath = SAVE_LOCATION + "/Pictures/Wallpaper Extractor";
	
	private Button getWallpaper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWallpaper = (Button)findViewById(R.id.wallpaper_selector);
		
		getWallpaper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				grabWallpaper();
			}
		});
	}
	
	private void grabWallpaper() {
		WallpaperManager manager = WallpaperManager.getInstance(this);
		
		if (Util.isExternalStorageWritable() && Util.isExternalStorageReadable() && !Util.isLiveWallpaper(manager)) {
			FileOutputStream out;
			
			File dir = new File(basePath);
			File output = new File(dir, "Image-" + UUID.randomUUID() + ".jpg");
			
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			try {
				out = new FileOutputStream(output);
				
				Drawable drawable = manager.getFastDrawable();
				Bitmap saveBitmap = null;
				
				saveBitmap = drawableToBitmap(drawable);
				saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
				
				new MediaScannerNotifier(this, output.toString(), "image/jpeg"); 
				Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		} else if (!Util.isExternalStorageWritable()) {
			Toast.makeText(this, "SD Card Mounted, Unable to save image!", Toast.LENGTH_SHORT).show();
		} else if (Util.isLiveWallpaper(manager)) {
			Toast.makeText(this, "Cannot save static images from live wallpaper!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
}