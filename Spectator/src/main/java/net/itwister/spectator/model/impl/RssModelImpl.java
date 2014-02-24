package net.itwister.spectator.model.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import net.itwister.spectator.Constants;
import net.itwister.spectator.model.RssModel;
import net.itwister.spectator.model.helpers.SpectatorTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.net.Uri;
import bindui.extra.Task;

@Singleton
public class RssModelImpl implements RssModel {

	@Override
	public Task<RssItem[]> importRssAsync(final Uri url) {
		return new SpectatorTask<RssItem[]>() {

			@Override
			public RssItem[] onExecuteWithResult() throws Exception {
				Document doc = Jsoup.connect("" + url).userAgent(Constants.WEB_USER_AGENT).get();
				Set<RssItem> items = new HashSet<RssItem>();

				throwIfInterrupted();

				for (Element e : doc.select("link[type=application/atom+xml], link[type=application/rss+xml]")) {
					items.add(new RssItem(e.absUrl("href"), e.attr("title")));
				}
				if (items.isEmpty()) throw new Exception();

				return items.toArray(new RssItem[items.size()]);
			}
		}.async();
	}
}