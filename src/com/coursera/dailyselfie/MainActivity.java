package com.coursera.dailyselfie;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private static final String TAG = "DAILY_SELFIE";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final long ALARM_DELAY = 2 * 60 * 1000L; // 2 Minutes
	private static final String FILE_PREFIX = "SELFIE_";
	private String mCurrentPhotoPath;
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
	private SelfieViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new SelfieViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);
		
		initFileList();
		
		mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		mNotificationReceiverIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mNotificationReceiverIntent, 0);
		
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 
				SystemClock.elapsedRealtime() + ALARM_DELAY, 
				ALARM_DELAY, 
				mNotificationReceiverPendingIntent);
	}
	
	private void initFileList() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File files[] = path.listFiles(new SelfieFileFilter());
		
		for (int i = 0; i < files.length; ++i) {
			String filename = "file:" + files[i];
			mAdapter.add(filename);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			
			File photoFile = null;
			
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				Log.e(TAG, "Failed to create image file");
			}
			
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			mAdapter.add(mCurrentPhotoPath);
			galleryAddPic(mCurrentPhotoPath);
		}
	}
	
	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
		String imageFileName = FILE_PREFIX + timeStamp + ".jpg";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		if (!storageDir.exists())
			storageDir.mkdirs();

		//File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		File image = new File(storageDir, imageFileName);
		
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	private void galleryAddPic(String filePath) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(filePath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}
	
	public class SelfieFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			if (!pathname.isDirectory() && pathname.isFile() && pathname.getName().toString().startsWith(FILE_PREFIX)) {
				return true;
			}
			
			return false;
		}
	}
}
