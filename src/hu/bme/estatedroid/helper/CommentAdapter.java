package hu.bme.estatedroid.helper;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.Comment;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private Context context;
	private List<Comment> commentValue;

	public CommentAdapter(Context context, List<Comment> commentValue) {
		this.context = context;
		this.commentValue = commentValue;
	}

	public int getCount() {
		return commentValue.size();
	}

	public Comment getItem(int position) {
		return commentValue.get(position);
	}

	public Comment getItemById(int id) {
		for (Comment p : commentValue) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	public long getItemId(int position) {
		return commentValue.get(position).getId();
	}

	public void add(Comment property) {
		commentValue.add(property);
	}

	public void remove(Comment property) {
		commentValue.remove(property);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View baseView;
		if (convertView == null && commentValue.get(position) != null) {

			baseView = new View(context);

			baseView = inflater.inflate(R.layout.list_item_comment, null);

			TextView idTextView = (TextView) baseView
					.findViewById(R.id.idTextView);
			idTextView.setText("#" + commentValue.get(position).getId());

			TextView userTextView = (TextView) baseView
					.findViewById(R.id.userTextView);
			userTextView.setText("" + commentValue.get(position).getUser());

			TextView timestampTextView = (TextView) baseView
					.findViewById(R.id.timestampTextView);
			timestampTextView.setText(""
					+ commentValue.get(position).getTimestamp());

			if (commentValue.get(position).getCommentId() != 0) {
				TextView parentTextView = (TextView) baseView
						.findViewById(R.id.parentTextView);
				parentTextView.setText(context
						.getString(R.string.parentcomment)
						+ " #"
						+ commentValue.get(position).getCommentId());
			}

			TextView commentTextViews = (TextView) baseView
					.findViewById(R.id.commentTextViews);
			commentTextViews.setText(""
					+ commentValue.get(position).getComment());

		} else {
			baseView = (View) convertView;
		}

		return baseView;
	}
}
