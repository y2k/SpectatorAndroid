//package net.itwister.spectator.view.subscriptions;
//
//import net.itwister.spectator.R;
//import net.itwister.spectator.view.base.SpectatorActivity;
//import net.itwister.spectator.view.home.feed.FeedFragment;
//import roboguice.inject.ContentView;
//import roboguice.inject.InjectExtra;
//import roboguice.inject.InjectView;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//@ContentView(R.layout.activity_subscriptions)
//public class SubscriptionListActivity extends SpectatorActivity {
//
//	private static final String EXTRA_TAG_ID = "extra_tag_id";
//	private static final String EXTRA_TAG_NAME = "extra_tag_name";
//
//	//	private final int selectedSubscriptionId = 2;
//
//	@InjectView(R.id.pager)
//	private ViewPager pager;;
//
//	@InjectExtra(value = EXTRA_TAG_ID, optional = true)
//	private int tagId;
//
//	@InjectExtra(value = EXTRA_TAG_NAME, optional = true)
//	private String tagName;
//
//	//	@Override
//	//	public void onChangeSubscription(int subscriptionId) {
//	//		// TODO Auto-generated method stub
//	//		selectedSubscriptionId = subscriptionId;
//	//		pager.getAdapter().notifyDataSetChanged();
//	//	}
//
//	private final BroadcastReceiver receiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			pager.setCurrentItem(1);
//		}
//	};
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//
//		//		if (savedInstanceState == null) {
//		//			getSupportFragmentManager()
//		//					.beginTransaction()
//		//					.replace(android.R.id.content, SubscriptionListFragment.newInstance(tagId, tagName))
//		//					.commit();
//		//		}
//
//		pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
//
//			@Override
//			public int getCount() {
//				return 2;
//			}
//
//			@Override
//			public Fragment getItem(int position) {
//				if (position == 0) return SubscriptionListFragment.newInstance(0, null);
//				//				return FeedFragment.newInstance(selectedSubscriptionId, false, null, null);
//				return new FeedContainerFragment();
//			}
//
//			@Override
//			public float getPageWidth(int position) {
//				if (position == 0) {
//					float f = (float) getResources().getDimensionPixelSize(R.dimen.subscription_panel_width) / pager.getMeasuredWidth();
//					return Math.min(1, f);
//				}
//				return 1;
//			}
//
//			//			@Override
//			//			public float getPageWidth(int position) {
//			//				float f = (float) getResources().getDimensionPixelSize(R.dimen.slide_menu_width)
//			//						/ getResources().getDisplayMetrics().widthPixels;
//			//				return position == 0 ? f : 1;
//			//			}
//		});
//	};
//
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(FeedContainerFragment.ACTION_FEED_CONTAINER));
//	}
//
//	public static Intent newIntent(Context context, int tagId, String tagName) {
//		return new Intent(context, SubscriptionListActivity.class)
//				.putExtra(EXTRA_TAG_ID, tagId)
//				.putExtra(EXTRA_TAG_NAME, tagName);
//	}
//
//	public static class FeedContainerFragment extends Fragment {
//
//		public static final String ACTION_FEED_CONTAINER = "action_feed_container";
//		public static final String EXTRA_INDEX = "extra_index";
//		public static final String EXTRA_TITLE = "extra_title";
//
//		private final BroadcastReceiver receiver = new BroadcastReceiver() {
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				// TODO Auto-generated method stub
//				FeedFragment f = FeedFragment.newInstance(
//						intent.getIntExtra(EXTRA_INDEX, 0), intent.getStringExtra(EXTRA_TITLE),
//						null);
//				getChildFragmentManager()
//						.beginTransaction()
//						.replace(R.id.feedContainer, f)
//						.commit();
//			}
//		};
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//			// TODO Auto-generated method stub
//			return inflater.inflate(R.layout.fragment_feed_container, null);
//		}
//
//		@Override
//		public void onPause() {
//			// TODO Auto-generated method stub
//			super.onPause();
//			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
//		}
//
//		@Override
//		public void onResume() {
//			super.onResume();
//			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(ACTION_FEED_CONTAINER));
//		}
//	}
//}