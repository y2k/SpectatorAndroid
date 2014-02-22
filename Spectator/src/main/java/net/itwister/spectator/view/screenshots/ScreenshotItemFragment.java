package net.itwister.spectator.view.screenshots;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.tools.widgets.ZoomImageView;
import net.itwister.tools.widgets.drawable.ZoomImageDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;
import bindui.annotations.InjectView;

public class ScreenshotItemFragment extends SpectatorFragment {

	@InjectView(R.id.image)
	ZoomImageView image;

	@InjectView(R.id.animator)
	ViewAnimator animator;

	@InjectView(R.id.progress)
	TextView progress;

	@Inject
	ImageModel model;

//	ImageModelListener listener = new ImageModelListener() {
//
//		@Override
//		public void onChanged() {
//			if (!isResumed()) return;
//
//			if (file != null) {
//				// Картинка скачана
//				DisplayMetrics m = getResources().getDisplayMetrics();
//				animator.setDisplayedChild(1);
//				if (image.getImage() == null) image.setImage(ZoomImageDrawable.fromFile(file, m.widthPixels, m.heightPixels));
//			} else if (exception != null) {
//				// Во время закачки произошла ошибка
//			} else {
//				// Картинка качается
//				animator.setDisplayedChild(0);
//				progress.setText(String.format("%s / %s KB", position / 1024, length / 1024));
//				progress.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
//			}
//		}
//	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		image.setIgnoreUserTouch(true);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((OnScreenshotClickListner) getActivity()).onScreenshotClicked(image);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		SyncObject syncObject = (SyncObject) getArguments().getSerializable(Fragments.PARAMETER);
		int pos = getArguments().getInt(Fragments.PARAMETER2);
//		model.startDownload(syncObject, pos, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_screenshot, null);
	}

//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		listener.cancel();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		listener.onChanged();
//	}

	@Deprecated
	public static ScreenshotItemFragment newInstance(int snapshotId, int position) {
		return newInstance(new SyncObject(snapshotId), position);
	}

	public static ScreenshotItemFragment newInstance(SyncObject syncObject, int position) {
		return Fragments.instance(ScreenshotItemFragment.class, syncObject, position);
	}

	public interface OnScreenshotClickListner {

		void onScreenshotClicked(ZoomImageView image);
	}
}