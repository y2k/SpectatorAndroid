package net.itwister.spectator.view.createsubscription;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.RssModel;
import net.itwister.spectator.model.RssModel.RssItem;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.view.common.base.SpectatorDialogFragment;
import net.itwister.spectator.view.helpers.UiHelper;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewAnimator;

import bindui.Task;
import bindui.annotations.InjectView;

public class ImportRssDialogFragment extends SpectatorDialogFragment {

	@InjectView(R.id.animator)
	private ViewAnimator animator;

	@InjectView(R.id.url)
	private EditText url;

	@InjectView(R.id.error)
	private TextView error;

	@Inject
	private RssModel rssModel;

	private Task<?> activeTask;

	private RssItem[] pendingResult;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		UiHelper.setOnClickListener(this, R.id.ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UiHelper.checkUrlValide(R.string.error_invalide_url, url)) {
					animator.setDisplayedChild(1);

					activeTask = rssModel.importRssAsync(Uri.parse("" + url.getText()))
							.withCallback(new Task.TaskCallback<RssItem[]>() {

                                @Override
                                public void onFail(Exception e) throws RuntimeException {
                                    error.setText(R.string.error_connect_to_server_and_find_rss);
                                    animator.setDisplayedChild(0);
                                }

                                @Override
                                public void onSuccess(RssItem[] data) {
                                    showCreateSubscriptionOrPenging(data);
                                }
                            });
				}
			}
		});
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return UiHelper.setTitle(super.onCreateDialog(savedInstanceState), R.string.title_create_rss_dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_create_rss, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		//		if (task != null) task.cancel(true);
		SpectatorTask.cancelWhenNotNull(activeTask);
	}

	@Override
	public void onResume() {
		super.onResume();
		showCreateSubscriptionOrPenging(pendingResult);
	}

	private void showCreateSubscriptionOrPenging(RssItem[] data) {
		if (isResumed() && data != null) {
			dismiss();
			CreateSubscriptionFromRssDialogFragment.newInstance(data).show(getFragmentManager(), null);
		} else {
			pendingResult = data;
		}
	}

	//	@Override
	//	public void onResume() {
	//		super.onResume();
	//		run();
	//	}

	//	@Override
	//	public void run() {
	//		if (!isResumed()) return;
	//
	//		if (task == null) animator.setDisplayedChild(0);
	//		else if (task.exception != null) {
	//			error.setText(R.string.error_connect_to_server_and_find_rss);
	//			animator.setDisplayedChild(0);
	//		} else if (task.rss == null) animator.setDisplayedChild(1);
	//		else {
	//			animator.setDisplayedChild(2);
	//			adapter.clear();
	//			adapter.addAll(task.rss);
	//		}
	//	}

	//	static class Task extends SpectatorAsyncTask<Void> {
	//
	//		String url;
	//		RssItem[] rss;
	//		WeakReference<Runnable> callbackRef;
	//		Exception exception;
	//
	//		@Override
	//		public Void call() throws Exception {
	//			Document doc = Jsoup.connect(url).userAgent(Constants.WEB_USER_AGENT).get();
	//			Set<RssItem> items = new HashSet<RssItem>();
	//			if (Thread.interrupted()) throw new InterruptedException();
	//
	//			for (Element e : doc.select("link[type=application/atom+xml], link[type=application/rss+xml]")) {
	//				items.add(new RssItem(e.absUrl("href"), e.attr("title")));
	//			}
	//			rss = items.toArray(new RssItem[items.size()]);
	//
	//			if (Thread.interrupted()) throw new InterruptedException();
	//			return null;
	//		}
	//
	//		@Override
	//		protected void onException(Exception e) throws RuntimeException {
	//			if (BuildConfig.DEBUG) e.printStackTrace();
	//			exception = e;
	//		}
	//
	//		@Override
	//		protected void onFinally() throws RuntimeException {
	//			Runnable c = callbackRef.get();
	//			if (c != null) c.run();
	//		}
	//
	////		static class RssItem {
	////
	////			String url;
	////			String title;
	////
	////			RssItem(String url, String title) {
	////				this.url = url;
	////				this.title = TextUtils.isEmpty(title) ? url : title;
	////			}
	////
	////			@Override
	////			public String toString() {
	////				return title;
	////			}
	////		}
	//	}
}