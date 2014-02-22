package net.itwister.spectator.model;

import java.io.Serializable;

import android.net.Uri;
import android.text.TextUtils;
import bindui.Task;

public interface RssModel {

	Task<RssItem[]> importRssAsync(Uri url);

	public static class RssItem implements Serializable {

		private static final long serialVersionUID = 7680088634668858930L;

		public String url;
		public String title;

		public RssItem(String url, String title) {
			this.url = url;
			this.title = TextUtils.isEmpty(title) ? url : title;
		}

		@Override
		public String toString() {
			return title + " | " + url;
		}
	}
}