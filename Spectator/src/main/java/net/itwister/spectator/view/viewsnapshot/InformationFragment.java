package net.itwister.spectator.view.viewsnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.data.generic.SnapshotWrapper;
import net.itwister.spectator.loaders.JTaskLoaderCallbacks;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.model.database.ObserverManager;
import net.itwister.spectator.view.common.base.SpectatorDialogFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.helpers.UiHelper;
import net.itwister.spectator.view.screenshots.ScreenshotsActivity;
import net.itwister.spectator.view.viewsnapshot.information.StaticImageGrid;

import org.ocpsoft.prettytime.PrettyTime;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import bindui.annotations.InjectView;

public class InformationFragment extends SpectatorDialogFragment {

	private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	@InjectView(R.id.group)
	private TextView group;

	@InjectView(R.id.title)
	private TextView title;

	@InjectView(R.id.source)
	private TextView source;

	@InjectView(R.id.date)
	private TextView date;

	@InjectView(R.id.screenshots)
	private LinearLayout screenshots;

	@InjectView(R.id.progress)
	private ContentLoadingProgressBar progress;

	private StaticImageGrid staticGrid;

	@Inject
	private SnapshotModel model;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setProgressVisible(true);

		final SyncObject syncObject = (SyncObject) getArguments().getSerializable(Fragments.PARAMETER);

		staticGrid = new StaticImageGrid(screenshots);
		staticGrid.setOnItemClickListener(new StaticImageGrid.OnItemClickListener() {

			@Override
			public void onItemClicked(int position, int count) {
				startActivity(ScreenshotsActivity.newIntent(getActivity(), syncObject, position, count));
			}
		});

		if (savedInstanceState == null) model.syncTask(syncObject).async();
		initializeLoader(syncObject);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = new Dialog(getActivity(), android.R.style.Theme_Holo_NoActionBar);
		d.setTitle(R.string.title_information);
		d.setCanceledOnTouchOutside(true);

		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
		lp.width = UiHelper.px(getActivity(), 280);
		lp.height = LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.RIGHT | Gravity.TOP;
		d.getWindow().setAttributes(lp);

		return d;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_information, null);
	}

//	@Override
//	public void onPause() {
//		super.onPause();
//		staticGrid.onPause();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		staticGrid.onResume();
//	}

	private void initializeLoader(final SyncObject syncObject) {
        new JTaskLoaderCallbacks<SnapshotWrapper>(model.getSnapshotTask(syncObject), ObserverManager.SNAPSHOT) {

            @Override
            public void onLoadFinished(Loader<SnapshotWrapper> loader, SnapshotWrapper data) {
                group.setText(data.groupTitle);
                group.setVisibility(TextUtils.isEmpty(data.groupTitle) ? View.GONE : View.VISIBLE);

                title.setText(data.snapshot.title);
                title.setVisibility(TextUtils.isEmpty(data.snapshot.title) ? View.GONE : View.VISIBLE);
                source.setText(data.snapshot.source);

                date.setText(String.format("%s (%s)", DATE_FORMAT.format(data.snapshot.updated), new PrettyTime().format(data.snapshot.updated)));

                staticGrid.changeData(data.screenshots);

                setProgressVisible(false);
            }
        }.initialize(this, 0);
	}

	private void setProgressVisible(boolean visible) {
		View v = getView().findViewById(R.id.root);
		if (visible ^ v.getVisibility() == View.VISIBLE) return;

		progress.hide();
		if (visible) {
			progress.show();
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
			v.setAlpha(0);
			v.animate().alpha(1);
		}
	}

	@Deprecated
	public static InformationFragment newInstance(int snapshotId) {
		return newInstance(new SyncObject(snapshotId));
	}

	public static InformationFragment newInstance(SyncObject syncObject) {
		return Fragments.instance(InformationFragment.class, syncObject);
	}
}