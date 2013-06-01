package hu.bme.estatedroid.helper;

import hu.bme.estatedroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class PropertyImageThumbLoader {
	private Context context;
	private ImageView imageView;
	private LinearLayout linearLayout;
	private int propertyId;
	private String username;
	private String password;
	private String[] pictures;

	public PropertyImageThumbLoader(Context context, ImageView imageView,
			int propertyId, String username, String password) {
		super();
		this.imageView = imageView;
		this.propertyId = propertyId;
		this.context = context;
		this.username = username;
		this.password = password;
		this.linearLayout = null;
		pictures = null;

		new ImageAsyncTask().execute();
	}

	public PropertyImageThumbLoader(Context context, ImageView imageView,
			int propertyId, String username, String password,
			LinearLayout relativeLayout) {
		super();
		this.imageView = imageView;
		this.propertyId = propertyId;
		this.context = context;
		this.username = username;
		this.password = password;
		this.linearLayout = relativeLayout;
		pictures = null;

		new ImageAsyncTask().execute();
	}

	class ImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
			// TODO prekép
		}

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			final String url = context.getString(R.string.base_uri)
					+ "/v1/pictures/{propertyId}";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());

			String returnValue = "";
			Bitmap bimage = null;

			try {
				// Make the network request

				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();

				String[] objects = (new Gson()).fromJson(returnValue,
						String[].class);
				if (objects.length > 0) {
					pictures = objects;
					bimage = getBitmapFromURL(
							context.getString(R.string.base_uri) + objects[0],
							250);
				}

			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					// TODO kezelni, ha nem sikerült az authentikáció

				}
				// TODO többi hibaüzenetre is kezelni, pl nem megy a
				// szolgáltatás
			}

			return bimage;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);

			if (linearLayout != null) {
				for (int i = 0; i < getCount(); i++) {
					ImageView mImageView = new ImageView(context);
					linearLayout.addView(mImageView);
					// mImageView.setImageBitmap(getBitmapFromURL(context.getString(R.string.base_uri)
					// + pictures[i],100));
					getImageView(i, mImageView, 100);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);

					params.setMargins(2, 0, 2, 0);
					mImageView.setLayoutParams(params);
					mImageView.setTag(i);
					mImageView.setClickable(true);
					mImageView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							getImageView((Integer) v.getTag(), imageView, 250);

						}
					});

				}
			}
		}
	}

	class NewImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
		int id;
		ImageView image;
		int size;

		public NewImageAsyncTask(int id, ImageView image, int size) {
			this.id = id;
			this.image = image;
			this.size = size;
		}

		@Override
		protected void onPreExecute() {
			// TODO prekép
		}

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			Bitmap bimage = getBitmapFromURL(
					context.getString(R.string.base_uri) + pictures[id], size);

			return bimage;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			image.setImageBitmap(result);
		}
	}

	private static Bitmap getBitmapFromURL(String src, int size) {
		try {
			Log.e("src", src);
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);

			options.inSampleSize = calculateInSampleSize(options, size, size);
			options.inJustDecodeBounds = false;

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream in = conn.getInputStream();
			return BitmapFactory.decodeStream(in, null, options);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());
			return null;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public int getCount() {
		if (pictures == null) {
			return 0;
		} else {
			return pictures.length;
		}
	}

	public LinearLayout getImageList() {
		return linearLayout;
	}

	/*
	 * public ImageView getImageView(int id) {
	 * imageView.setImageBitmap(getBitmapFromURL(
	 * context.getString(R.string.base_uri) + pictures[id], 250)); return
	 * imageView; }
	 */

	public void getImageView(int id, ImageView image, int size) {
		NewImageAsyncTask newImageAsyncTask = new NewImageAsyncTask(id, image,
				size);
		newImageAsyncTask.execute();
	}
}
