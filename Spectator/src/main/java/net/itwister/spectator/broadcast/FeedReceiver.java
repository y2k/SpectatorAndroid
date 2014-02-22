//package net.itwister.spectator.broadcast;
//
//import net.itwister.spectator.App;
//import net.itwister.spectator.BuildConfig;
//import net.itwister.spectator.R;
//import net.itwister.spectator.view.home.feed.FeedFragment;
//import net.itwister.tools.inner.Ln;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.LocalBroadcastManager;
//import bindui.InjectService;
//
//public class FeedReceiver extends BroadcastReceiver {
//
//	private final Fragment fragment;
//
//	public FeedReceiver(Fragment fragment) {
//		this.fragment = fragment;
//
//		InjectService.injectSimple(this);
//		onResume(null);
//	}
//
//	public void onPause(@Observes OnPauseEvent e) {
//		if (BuildConfig.DEBUG) Ln.v("onPause");
//		LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(this);
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		int listId = intent.getIntExtra("listId", 0);
//		String title = intent.getStringExtra("title");
//		fragment.getChildFragmentManager()
//				.beginTransaction()
//				.replace(R.id.container, FeedFragment.newInstance(listId, title, null))
//				.commit();
//	}
//
//	public void onResume(@Observes OnResumeEvent e) {
//		if (BuildConfig.DEBUG) Ln.v("onResume");
//		LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(this, new IntentFilter("home"));
//	}
//
//	public static Intent intent(int listId, boolean isTag, String title) {
//		return new Intent("home")
//				.putExtra("listId", listId)
//				.putExtra("isTag", isTag)
//				.putExtra("title", title);
//	}
//}