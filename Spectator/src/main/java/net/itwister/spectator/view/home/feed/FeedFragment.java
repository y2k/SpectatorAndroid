package net.itwister.spectator.view.home.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.itwister.spectator.Constants;
import net.itwister.spectator.R;
import net.itwister.spectator.broadcast.SearchReceiver;
import net.itwister.spectator.data.Snapshot;
import net.itwister.spectator.loaders.JTaskCursorLoaderCallbacks;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.StashModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.viewsnapshot.PagerSnapshotActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import javax.inject.Inject;

import bindui.UICursorViewTemplate;
import bindui.adapters.UICursorAdapter;
import bindui.annotations.ContentView;
import bindui.annotations.InjectView;
import bindui.extra.ImageDownloader;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FeedFragment extends SpectatorFragment {

	private static final String ARG_LIST_ID = Fragments.PARAMETER;
	private static final String ARG_TITLE = Fragments.PARAMETER3;
	private static final String ARG_QUERY = Fragments.PARAMETER4;

	@InjectView(android.R.id.list) private GridView list;
	@Inject	private SyncModel model;
    @Inject	private AnalyticsModel analytics;
    @Inject	private SnapshotModel snapModel;

    private UICursorAdapter adapter;
    private PullToRefreshLayout pullToRefreshLayout;
    private SearchReceiver receicer;
	private SyncObject syncObject;
	private NewPageLoadListener pageListner;

	private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
            // FIXME
            // ((FeedFragmentActivity) getActivity()).getPullToRefresh().setRefreshing(model.isSyncInProgress(syncObject));
		}
	};

	public int getArgumentSubscriptionId() {
		return getArguments().getInt(ARG_LIST_ID);
	}

	public String getArgumentTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String t = getArguments().getString(ARG_TITLE);
		getActivity().getActionBar().setTitle(t != null ? t : getString(R.string.title_feed));

		syncObject = model.createSyncSnapshots(getArguments().getInt(ARG_LIST_ID), getArguments().getString(ARG_QUERY));
		pageListner = new NewPageLoadListener();

		list.setAdapter(adapter = new UICursorAdapter(getActivity(), FeedItemTemplate.class));
		list.setOnScrollListener(pageListner);

		if (savedInstanceState == null) pageListner.loadNewPage(0);

		receicer = new SearchReceiver(getActivity(), getArguments().getInt(ARG_LIST_ID));

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor c = (Cursor) parent.getItemAtPosition(position);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(c.getString(c.getColumnIndex(Snapshot.SOURCE)))));
				return true;
			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				analytics.eventOpenSnapshot(id);
				startActivity(PagerSnapshotActivity.newIntent(getActivity(), syncObject, position, parent.getCount()));
			}
		});

//		adapter.setOnSubscriptionClickListner(new P2<Integer, String>() {
//
//			@Override
//			public void call(Integer subscriptionId, String title) {
//				((MenuFragmentHost) getActivity()).onSubscriptionSelected(subscriptionId, title);
//			}
//		});

		initializeLoader();
		initializePullToRefresh();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_feed, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		receicer.close();

//		((FeedFragmentActivity) getActivity()).getPullToRefresh().removeRefreshableView(list);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(syncReceiver);
	}

    private void initializeLoader() {
        Bundle a = getArguments();
        new JTaskCursorLoaderCallbacks(snapModel.getTask(a.getInt(ARG_LIST_ID), a.getString(ARG_QUERY)),adapter){

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                super.onLoadFinished(loader, data);
                if (adapter.getCount() > 0 && list.getVisibility() != View.VISIBLE) {
                    list.setVisibility(View.VISIBLE);
                    if (isResumed()) {
                        list.setAlpha(0);
                        list.animate().alpha(1);
                    }
                }
            }
        }.initialize(this, 0);
    }

	private void initializePullToRefresh() {
        pullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto((ViewGroup) getView())
                .theseChildrenArePullable(list)
                .listener(new OnRefreshListener() {

                    @Override
                    public void onRefreshStarted(View view) {
                        pageListner.forceReload();
                    }
                })
                .setup(pullToRefreshLayout);

		model.registerObserverReceiver(syncReceiver);
	}

	// ==============================================================
	// Статические методы
	// ==============================================================

	public static FeedFragment newInstance(int listId, String title, String query) {
		return Fragments.instance(FeedFragment.class, listId, false, title, query);
	}

	// ==============================================================
	// Вложенные классы
	// ==============================================================

//	public interface FeedFragmentActivity {
//
//		PullToRefreshAttacher getPullToRefresh();
//	}

	class NewPageLoadListener implements OnScrollListener {

		private int lastToId = -1;

		public void forceReload() {
			lastToId = -1;
			loadNewPage(0);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (totalItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
				loadNewPage((int) view.getItemIdAtPosition(view.getCount() - 1));
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}

		private void loadNewPage(int toId) {
			if (lastToId != toId) {
				lastToId = toId;
				syncObject.reset = toId == 0 ? true : false;
				model.startSync(syncObject);
			}
		}
	}

    @ContentView(R.layout.item_feed)
    public static class FeedItemTemplate extends UICursorViewTemplate {

        private @InjectView(R.id.title)             TextView text;
        private @InjectView(R.id.date)              TextView date;
        private @InjectView(R.id.image)             ImageView image;
        private @InjectView(R.id.subscriptionIcon)  ImageView subIconView;
        private @InjectView(R.id.stash)             ImageView stash;

        private @Inject ImageModel imageModel;
        private @Inject StashModel stashModel;

        private PrettyTime pTime = new PrettyTime();
        private int iconSize;

        @Override
        public View onNewView(Context context) {
            View v = super.onNewView(context);

            float d = context.getResources().getDisplayMetrics().density;
			int col = Math.max(1, (int) (context.getResources().getDisplayMetrics().widthPixels / 240f / d));
			int w = context.getResources().getDisplayMetrics().widthPixels / col;

            View iv = v.findViewById(R.id.image);
			ViewGroup.LayoutParams lp = iv.getLayoutParams();
			lp.height = (int) (w * 0.7f);
            iv.setLayoutParams(lp);

            iconSize = (int) (d * Constants.ICON_SIZE);

            return v;
        }

        @Override
        public void onBindView(Context context, Cursor cursor) {
            FeedItemTemplate h = this;
            Cursor c = cursor;

            h.text.setText(c.getString(c.getColumnIndex(Snapshot.TITLE)));
            h.date.setText(pTime.format(new Date(c.getLong(c.getColumnIndex(Snapshot.CREATED)))));

            {
//                viewsPendingImage.remove(h.image);
                h.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageModel.deleteTokenFromQueue(image);
                image.setImageDrawable(null);

                int thumb = c.getInt(c.getColumnIndex(Snapshot.THUMBNAIL));
                if (thumb > 0) {
                    String url = imageModel.getSquareThumbnailUrl(thumb, 300);

                    imageModel.addImageTaskToQueue(image, url, new ImageDownloader.ImageDownloaderCallback() {

                        @Override
                        public void complete(Bitmap bitmap) {
//                            image.setScaleType(ImageView.ScaleType.CENTER);
                            image.setImageBitmap(bitmap);
                        }
                    });

//                    Bitmap img = model.getImageFromMemoryCacheOrDownload(url);
//                    h.image.setImageBitmap(img);
//                    if (img == null) {
//
//                        AnimationDrawable d = (AnimationDrawable) context.getResources().getDrawable(R.drawable.ellipsis_drawable);
//                        h.image.setImageDrawable(d);
//                        h.image.setScaleType(ImageView.ScaleType.CENTER);
//                        d.start();
//
//                        viewsPendingImage.put(h.image, url);
//                    }
                } else {
                    h.image.setImageBitmap(null);
                }
            }

            {
                imageModel.deleteTokenFromQueue(subIconView);
                subIconView.setImageDrawable(null);

//                viewsPendingImage.remove(h.subIcon);
                int subIcon = c.getInt(c.getColumnIndex(Snapshot.SUBSCRIPTION_ICON));
                if (subIcon > 0) {
                    String url = imageModel.getSquareThumbnailUrl(subIcon, iconSize);
                    imageModel.addImageTaskToQueue(subIconView, url, new ImageDownloader.ImageDownloaderCallback() {

                        @Override
                        public void complete(Bitmap bitmap) {
                            subIconView.setImageBitmap(bitmap);
                        }
                    });

//                    Bitmap img = model.getImageFromMemoryCacheOrDownload(url);
//                    h.subIcon.setImageBitmap(img);
//                    if (img == null) viewsPendingImage.put(h.subIcon, url);
                } else {
                    h.subIconView.setImageResource(R.drawable.ic_html_subscription_stub);
                }
            }

//            if (subscriptionClickListener != null) {
//                final int id = c.getInt(c.getColumnIndex(Snapshot.SUBSCRIPTION_ID));
//                final String title = c.getString(c.getColumnIndex(Snapshot.SUBSCRIPTION_NAME));
//                h.subscriptionButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        subscriptionClickListener.call(id, title);
//                    }
//                });
//            }

            boolean isStash = c.getInt(c.getColumnIndex(Snapshot.EXTRA_STASH)) != 0;
            h.stash.setImageResource(isStash ? R.drawable.ic_rating_important : R.drawable.ic_rating_not_important);

            final int id = c.getInt(c.getColumnIndex(Snapshot.ID));
            h.stash.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    stashModel.toggleAsync(id);
                }
            });
        }
    }
}