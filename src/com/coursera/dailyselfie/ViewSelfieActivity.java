package com.coursera.dailyselfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ViewSelfieActivity extends Activity {
	
	private String mCurrentPhotoPath;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_selfie);
		
		mImageView = (ImageView) findViewById(R.id.image);
		
		mCurrentPhotoPath = getIntent().getExtras().getString("filename");
		mCurrentPhotoPath = mCurrentPhotoPath.replace("file:", "");
		setPic();
	}
	
	private void setPic() {

		// Get the dimensions of the View
		DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
		int targetW = metrics.widthPixels;
		int targetH = metrics.heightPixels;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		
		Matrix matrix = new Matrix();
		matrix.postRotate(90);

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		mImageView.setImageBitmap(rotated);	
		bitmap.recycle();
	}
}
