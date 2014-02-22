package net.itwister.spectator.view.viewsnapshot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import net.itwister.spectator.Constants;
import net.itwister.spectator.R;
import net.itwister.spectator.data.WebContent;
import net.itwister.spectator.loaders.JTaskLoaderCallbacks;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.Fragments;

import java.util.Date;

import javax.inject.Inject;

import bindui.annotations.InjectView;

public class WebSnapshotFragment extends SpectatorFragment {

	public static final int TYPE_WEB = 0;
	public static final int TYPE_DIFF = 1;

	@InjectView(R.id.web)           WebView webView;
	@InjectView(R.id.date)          TextView date;
	@InjectView(R.id.loadImages)    CheckBox loadImages;

    @Inject SnapshotModel model;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		webView.getSettings().setLoadsImagesAutomatically(loadImages.isChecked());
		webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);

		loadImages.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				webView.getSettings().setLoadsImagesAutomatically(isChecked);
			}
		});

		Date d = (Date) getArguments().getSerializable(Fragments.PARAMETER3);
		date.setText(Constants.DATE_FORMAT.format(d));

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				((SnapshotPageFragment) getParentFragment()).setProgress(false, false, 0);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
				return true;
			}
		});

        int snapId = getArguments().getInt(Fragments.PARAMETER);
        int type = getArguments().getInt(Fragments.PARAMETER2);
        new JTaskLoaderCallbacks<>(model.getContent(snapId, type), null).initialize(this, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_web_snapshot, null);
	}

	public static WebSnapshotFragment newInstance(int snapshotId, int type, Date updated) {
		return Fragments.instance(WebSnapshotFragment.class, snapshotId, type, updated);
	}
}