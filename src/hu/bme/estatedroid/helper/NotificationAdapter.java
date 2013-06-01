package hu.bme.estatedroid.helper;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.Notification;

import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
//TODO átírni Listesre
public class NotificationAdapter extends BaseAdapter {
	private Context context;
	private Notification[] notificationValue;
	private Map<Integer, String> notificationType;

	public NotificationAdapter(Context context,
			Notification[] notificationValue,
			Map<Integer, String> notificationType) {
		this.context = context;
		this.notificationValue = notificationValue;
		this.notificationType = notificationType;

	}

	public int getCount() {
		return notificationValue.length;
	}

	public Notification getItem(int position) {
		return notificationValue[position];
	}

	public Notification getItemById(long id) {
		for (Notification n : notificationValue) {
			if (n.getId() == (int) id) {
				return n;
			}
		}
		return null;
	}

	public void setItem(int position, Notification notification) {
		notificationValue[position] = notification;
	}

	public void setItemById(long id) {
		for (int i = 0; i < notificationValue.length; i++) {
			if (notificationValue[i].getId() == (int) id) {
				notificationValue[i]
						.setIsread(!notificationValue[i].isIsread());
				return;
			}
		}
	}

	public long getItemId(int position) {
		return notificationValue[position].getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listView;
		if (convertView == null) {

			listView = new View(context);

			listView = inflater.inflate(R.layout.notification_thumb, null);

			TextView propertyTextView = (TextView) listView
					.findViewById(R.id.notification_property);
			propertyTextView.setText("#"
					+ notificationValue[position].getProperty());

			TextView typeTextView = (TextView) listView
					.findViewById(R.id.notification_type);
			typeTextView.setText(notificationType
					.get(notificationValue[position].getType()));

			TextView contentTextView = (TextView) listView
					.findViewById(R.id.notification_content);
			contentTextView.setText(notificationValue[position].getContent());

			TextView timeTextView = (TextView) listView
					.findViewById(R.id.notification_time);
			timeTextView.setText(notificationValue[position].getTimestamp());

			if (!notificationValue[position].isIsread()) {
				propertyTextView.setTypeface(null, Typeface.BOLD);
				typeTextView.setTypeface(null, Typeface.BOLD);
				contentTextView.setTypeface(null, Typeface.BOLD);
				timeTextView.setTypeface(null, Typeface.BOLD);
			}

		} else {
			listView = (View) convertView;
		}

		return listView;
	}

}
