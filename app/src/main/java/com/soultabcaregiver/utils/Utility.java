package com.soultabcaregiver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.soultabcaregiver.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Utility {
	
	private static final String PREFERENCE = "Soultab Caregiver";
	
	private static final int MAX_IMAGE_DIMENSION = 720;
	
	public static final String SinchServiceConnected = "SinchServiceConnected";
	
	public final static SimpleDateFormat dd_MM_yyyy =
			new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	
	public final static SimpleDateFormat MM_dd_yyyy =
			new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
	
	public final static SimpleDateFormat yyyy_MM_dd =
			new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	
	public final static SimpleDateFormat hh_mm_aa =
			new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
	
	public final static SimpleDateFormat EEE_dd_MMM_yyyy =
			new SimpleDateFormat("EEE, MMM dd yyyy", Locale.ENGLISH);
	
	public final static SimpleDateFormat EEE_dd_MMM =
			new SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH);
	
	public final static SimpleDateFormat dd_MMM = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
	
	public final static SimpleDateFormat MMM_dd_yyyy =
			new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
	
	public final static SimpleDateFormat dd = new SimpleDateFormat("dd", Locale.ENGLISH);
	
	public final static SimpleDateFormat MMM = new SimpleDateFormat("MMM", Locale.ENGLISH);
	
	public final static SimpleDateFormat EEE_dd_MMM_yyyy_hh_mm_aa =
			new SimpleDateFormat("EEE, MMM dd yyyy HH:mm aa", Locale.ENGLISH);
	
	public final static SimpleDateFormat EEEhh_mm_aa =
			new SimpleDateFormat("EEE, MM-dd-yy, hh:mm aa", Locale.ENGLISH);
	
	public final static SimpleDateFormat yyyy_mm_dd_hh_mm_aa =
			new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.ENGLISH);
	
	public final static SimpleDateFormat yyyy_mm_dd_hh_mm_ss =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	//public static final String FCM_TOKEN = "FirebaseToken";
	
	public static Context mContext;
	
	static int mMaxWidth, mMaxHeight;
	
	static AlertDialog alertDialog;
	
	// for username string preferences
	public static void setSharedPreference(Context context, String name, String value) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(name, value);
		editor.commit();
	}
	
	public static void setSharedPreference2(Context context, String name, String value) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences("Additional", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(name, value);
		editor.commit();
	}
	
	public static void clearSharedPreference(Context context) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	
	// for username string preferences
	public static void setIntegerSharedPreference(Context context, String name, int value) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name, value);
		editor.commit();
	}
	
	//Drawable
	public static void setDrawableSharedPreference(Context context, String name, int value) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name, value);
		editor.commit();
	}
	
	public static String getSharedPreferences(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getString(name, null);
	}
	
	public static String getSharedPreferences2(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences("Additional", 0);
		return settings.getString(name, null);
	}
	
	public static int getIngerSharedPreferences(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getInt(name, 0);
	}
	
	public static void setSharedPreferenceBoolean(Context context, String name, boolean value) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}
	
	public static String parseTime(long minutes) {
		return String.format(Locale.ENGLISH, "%02d:%02d", TimeUnit.MINUTES.toHours(minutes),
				TimeUnit.MINUTES.toMinutes(minutes) - TimeUnit.HOURS.toMinutes(
						TimeUnit.MINUTES.toHours(minutes)));
	}
	
	public static boolean getSharedPreferencesBoolean(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getBoolean(name, false);
	}
	
	public static void clearSpecificSharedPreference(Context context, String name) {
		mContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(name);
		editor.commit();
	}
	
	public static void alertBoxShow(Context context, int msg) {
		// set dialog_category for user's current location set for searching theaters.
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Alert");
		dialog.setMessage(msg);
		dialog.setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}
	
	public static void alertBoxShow(Context context, String msg) {
		// set dialog_category for user's current location set for searching theaters.
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Alert");
		dialog.setMessage(msg);
		dialog.setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}
	
	public static byte[] scaleImage(Context context, Uri photoUri) throws IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();
		
		int rotatedWidth, rotatedHeight;
		int orientation = 0;// getOrientation(context, photoUri);
		
		if (orientation == 90 || orientation == 270) {
			rotatedWidth = dbo.outHeight;
			rotatedHeight = dbo.outWidth;
		} else {
			rotatedWidth = dbo.outWidth;
			rotatedHeight = dbo.outHeight;
		}
		
		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
			float maxRatio = Math.max(widthRatio, heightRatio);
			
			// Create the bitmap from file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = (int) maxRatio;
			srcBitmap = BitmapFactory.decodeStream(is, null, options);
		} else {
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();
		
		/*
		 * if the orientation is not 0 (or -1, which means we don't know), we
		 * have to do a rotation.
		 */
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);
			
			srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
					srcBitmap.getHeight(), matrix, true);
		}
		
		String type = context.getContentResolver().getType(photoUri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		/*
		 * if (type.equals("image/png")) {
		 * srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); } else if
		 * (type.equals("image/jpg") || type.equals("image/jpeg")) {
		 * srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); }
		 */
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return bMapArray;
	}
	
	//============================================ Resized Image
	// =======================================================//
	public static Bitmap loadResizedImage(Context mContext, final File imageFile) {
		WindowManager windowManager =
				(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		mMaxWidth = display.getWidth();
		mMaxHeight = display.getHeight();
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		int scale = calculateInSampleSize(options, mMaxWidth, mMaxHeight);
		while (options.outWidth / scale > mMaxWidth || options.outHeight / scale > mMaxHeight) {
			scale++;
		}
		Bitmap bitmap = null;
		Bitmap scaledBitmap = null;
		if (scale > 1) {
			try {
				scale--;
				options = new BitmapFactory.Options();
				options.inSampleSize = scale;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				options.inPurgeable = true;
				options.inTempStorage = new byte[16 * 100];
				bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
				if (bitmap == null) {
					return null;
				}
				
				// resize to desired dimensions
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				double newWidth;
				double newHeight;
				if ((double) width / mMaxWidth < (double) height / mMaxHeight) {
					newHeight = mMaxHeight;
					newWidth = (newHeight / height) * width;
				} else {
					newWidth = mMaxWidth;
					newHeight = (newWidth / width) * height;
				}
				
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, Math.round((float) newWidth),
						Math.round((float) newHeight), true);
				bitmap.recycle();
				bitmap = scaledBitmap;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			}
			System.gc();
		} else {
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		}
		
		return rotateImage(bitmap, imageFile);
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
	                                        int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	//============================= Image Rotation
	// =================================================//
	public static Bitmap rotateImage(final Bitmap bitmap, final File fileWithExifInfo) {
		if (bitmap == null) {
			return null;
		}
		Bitmap rotatedBitmap = bitmap;
		int orientation = 0;
		try {
			orientation = getImageOrientation(fileWithExifInfo.getAbsolutePath());
			if (orientation != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(orientation, (float) bitmap.getWidth() / 2,
						(float) bitmap.getHeight() / 2);
				rotatedBitmap =
						Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
								matrix, true);
				bitmap.recycle();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotatedBitmap;
	}
	
	public static int getImageOrientation(final String file) throws IOException {
		ExifInterface exif = new ExifInterface(file);
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return 0;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			default:
				return 0;
		}
	}
	
	//=============================== Set Font
	// ===========================================================//
	public static void SetFont(TextView text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-Regular.otf");
		text.setTypeface(type);
	}
	
	public static void SetFontMedium(TextView text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-Medium.otf");
		text.setTypeface(type);
	}
	
	public static void SetFontBold(TextView text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-SemiBold.otf");
		text.setTypeface(type);
	}
	
	public static void SetFont(TextInputLayout text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-Regular.otf");
		text.setTypeface(type);
	}
	
	public static void SetFontMedium(TextInputLayout text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-Medium.otf");
		text.setTypeface(type);
	}
	
	public static void SetFontBold(TextInputLayout text, Context ct) {
		Typeface type =
				Typeface.createFromAsset(ct.getResources().getAssets(), "Montserrat-SemiBold.otf");
		text.setTypeface(type);
	}
	
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivity =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (NetworkInfo networkInfo : info)
					if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
			}
		}
		return false;
	}
	
	public static void removepreference(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		settings.edit().remove(name).apply();
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
	
	public static boolean isValidEmail(String email) {
		String EMAIL_PATTERN =
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\" + ".[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public static boolean isValidName(String name) {
		String NAME_PATTERN = "^[A-Za-z]+$";
		Pattern pattern = Pattern.compile(NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
	
	public static void printKeyHash(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("DeveloperKeyHash:",
						"DeveloperKeyHash ====> " + Base64.encodeToString(md.digest(),
								Base64.DEFAULT));
				MessageDigest mda = MessageDigest.getInstance("SHA-1");
				mda.update(signature.toByteArray());
				Log.e("releseKeyHash:", "releseKeyHash ====> " + Base64.encodeToString(md.digest(),
						Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.d("KeyHash:", "NameNotFoundException === " + e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.d("KeyHash:", "NoSuchAlgorithmException === " + e.toString());
		}
	}
	
	public static void hideKeyBoard(EditText edt, Context ct) {
		InputMethodManager imm =
				(InputMethodManager) ct.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
	}
	
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}
	
	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

    /*public static void ShowLoading(Context appContext, String msg) {
        spotsDialog = new SpotsDialog(appContext, msg);
        spotsDialog.setCancelable(false);
        spotsDialog.show();
    }

    public static void HideDialog() {
        if (spotsDialog != null) {
            spotsDialog.dismiss();
        }
    }*/
	
	public static void ChangeLang(Context appContext, int i) {
		String lang = "";
		switch (i) {
			case 0:
				lang = "en";
				break;
			case 1:
				lang = "ar";
				break;
		}
		Locale locale = new Locale(lang);
		Locale.setDefault(locale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = locale;
		appContext.getResources().updateConfiguration(config,
				appContext.getResources().getDisplayMetrics());
	}
	
	public static String SaveImage(Bitmap finalBitmap, String fileName, String filePath) {
		File myDir = new File(filePath);
		if (!myDir.exists()) {
			myDir.mkdirs();
		}
		String filename = fileName + ".jpg";
		File file = new File(myDir, filename);
		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String path = "" + file;
		Log.e("SaveImage Path", "SaveImage Path === " + path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/*String date_after = formateDateFromstring("yyyy-MM-dd", "dd, MMM yyyy", date_before);*/
	/*Method for change date format*/
	public static String ChangeDateFormat(String inputFormat, String outputFormat,
	                                      String inputDate) {
		Date parsed = null;
		String outputDate = "";
		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);
		} catch (ParseException e) {
		
		}
		return outputDate;
	}
	
	/*Method for change time format*/
	public static String Time(String inputFormat, String outputFormat, String time) {
		SimpleDateFormat parseFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
		SimpleDateFormat displayFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
		String str = "";
		try {
			Date date = parseFormat.parse(time);
			str = displayFormat.format(date);
		} catch (ParseException e) {
			//            e.printStackTrace();
		}
		return str;
	}
	
	public static void hideKeyboard(Context ctx) {
		InputMethodManager inputManager =
				(InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		// check if no view has focus:
		View v = ((Activity) ctx).getCurrentFocus();
		if (v == null)
			return;
		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	public static void hideKeyboard(View view, Context context) {
		InputMethodManager in =
				(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static String MakeDir(String filepath, Context appContext) {
		File path;
		if (!isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			path = new File(Utility.SaveFileIntoDir(filepath, appContext), filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
		} else {
			path = new File(appContext.getExternalFilesDir(filepath), filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
		}
		return path.toString();
	}
	
	public static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(extStorageState);
	}
	
	public static boolean isExternalStorageReadOnly() {
		String extStorageState = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
	}
	
	public static File SaveFileIntoDir(String filepath, Context appContext) {
		File directory = appContext.getDir(filepath, Context.MODE_PRIVATE);
		return directory;
	}
	
	public static void goToMarket(Context context) {
		context.startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=" + context.getPackageName())));
	}
	
	public static void ShowToast(Context mContext, String msg) {
		Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		ViewGroup group = (ViewGroup) toast.getView();
		
		toast.getView().setBackgroundResource(R.drawable.orange_round_bg);
		TextView text = toast.getView().findViewById(android.R.id.message);
		toast.setGravity(Gravity.CENTER, 0, 0);
		
		//Shadow of the Of the Text Color
		text.setShadowLayer(0, 0, 0, ContextCompat.getColor(mContext, R.color.transparent_black));
		text.setTextColor(Color.WHITE);
		text.setTextSize(18);
		text.setPadding(20, 20, 20, 20);
		toast.show();
	}
	
	public static boolean isValidMobile(String phone) {
		boolean check = false;
		if (!Pattern.matches("[a-zA-Z]+", phone)) {
			check = phone.length() >= 10 && phone.length() <= 13;
		} else {
			check = false;
		}
		return check;
	}
	
	public static boolean isValidUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
		} catch (MalformedURLException ignored) {
		}
		return false;
	}
	
	public static boolean isvalidatePassword(final String password) {
		Pattern pattern;
		Matcher matcher;
		final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		
		return matcher.matches();
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		return metrics.widthPixels;
	}
	
	public static boolean isLocationEnabled(Context context) {
		int locationMode = 0;
		String locationProviders;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				locationMode = Settings.Secure.getInt(context.getContentResolver(),
						Settings.Secure.LOCATION_MODE);
			} catch (Settings.SettingNotFoundException e) {
				e.printStackTrace();
			}
			return locationMode != Settings.Secure.LOCATION_MODE_OFF;
		} else {
			locationProviders = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.LOCATION_MODE);
			return !TextUtils.isEmpty(locationProviders);
		}
	}
	
	public static void buildAlertMessageNoGps(Context context) {
		
		new AlertDialog.Builder(context).setTitle(R.string.Location_permission)  // GPS not found
				.setMessage(R.string.Location_permission_txt) // Want to enable?
				.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton(R.string.cancel_text, null).show();
		
	}
	
	public static String getCalculatedDate(String FromDate, String dateFormat,
	                                       int days) throws ParseException {
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		c.setTime(sdf.parse(FromDate));
		
		c.add(Calendar.DATE, days);
		return sdf.format(c.getTime());
	}
	
	public static void SetAvailableDate(Context context, List<String> arrayList) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
		Gson gson = new Gson();
		
		String json = gson.toJson(arrayList);
		
		prefsEditor.putString("availableDate", json);
		prefsEditor.apply();
	}
	
	public static void loadFragment(FragmentActivity activty, Fragment fragment, boolean backstack,
	                                String tagName) {
		// load fragment0.....
		
		FragmentTransaction transaction = activty.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		
		if (backstack) {
			transaction.addToBackStack(tagName);
		}
		transaction.commit();
	}
	
	public static void removeAllFragment(FragmentActivity activty, String tagName) {
		FragmentManager fm = activty.getSupportFragmentManager();
		
		for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
           /* if (!fm.getBackStackEntryAt(i).getName().equalsIgnoreCase(tagName)) {
                fm.popBackStack();
            }*/
			fm.popBackStack();
			
		}
	}
	
	public static void showSnackBar(View view, String message) {
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
	}
	
	public void removeFragments(FragmentActivity activty) {
		activty.getSupportFragmentManager().popBackStack("F",
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		
	}
	
}