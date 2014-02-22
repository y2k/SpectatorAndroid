package net.itwister.spectator.view.viewsnapshot;

import javax.inject.Inject;

import net.itwister.spectator.Constants;
import net.itwister.spectator.R;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.view.common.P2;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.tools.widgets.ZoomImageView;
import net.itwister.tools.widgets.drawable.ZoomImageDrawable;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import bindui.annotations.InjectView;

public class ImageSnapshotFragment extends SpectatorFragment {

	private static final String ARG_THUMBNAIL_ID = Fragments.PARAMETER;

	@InjectView(R.id.image)
	private ZoomImageView imageView;

	@InjectView(R.id.thumbnail)
	private ImageView thumbnailView;

	@Inject
	private ImageModel imageModel;

	private ImageFragmentHost hostActivity;

	private final P2<Integer, Integer> progressCallback = new P2<Integer, Integer>() {

		@Override
		public void call(Integer current, Integer max) {
			SnapshotPageFragment host = (SnapshotPageFragment) getParentFragment();
			host.setProgress(true, false, (float) current / max);
		}
	};
	private final P2<ZoomImageDrawable, Exception> finishCallback = new P2<ZoomImageDrawable, Exception>() {

		@Override
		public void call(final ZoomImageDrawable image, Exception e) {
			if (image != null) {
				final SnapshotPageFragment host = (SnapshotPageFragment) getParentFragment();
				host.setProgress(true, true, 0);

				image.setInvalidateListener(new Runnable() {

					@Override
					public void run() {
						thumbnailView.setImageBitmap(null);
						host.setProgress(false, false, 0);
					}
				});
				imageView.setImage(image);
			}
		}
	};
	private final P2<Bitmap, Exception> finishThumbnailCallback = new P2<Bitmap, Exception>() {

		@Override
		public void call(Bitmap image, Exception e) {
			if (imageView.getImage() == null) thumbnailView.setImageBitmap(image);
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		hostActivity = (ImageFragmentHost) getActivity();

		imageView.setIgnoreUserTouch(!hostActivity.isFullscreenEnabled());
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hostActivity.onToggleFullscreen();
				imageView.setIgnoreUserTouch(!hostActivity.isFullscreenEnabled());
				if (!hostActivity.isFullscreenEnabled())
				imageView.setZoomToFullscreen();
			}
		});

		String imageUrl = String.format("%s/Image/Index/%s?max_pix=%s", Constants.Url.URL_HOST, getArguments().getInt(ARG_THUMBNAIL_ID), 4000000);
		String thumbailUrl = imageModel.getThumbnailUrl(getArguments().getInt(ARG_THUMBNAIL_ID), getResources().getDimensionPixelSize(R.dimen.image_snapshot_thumbnail));

		imageModel.globalInitializeDownload("" + getArguments().getInt(Fragments.PARAMETER2), imageUrl, progressCallback, finishCallback);
		imageModel.globalInitializeThumbnailDownload("t" + getArguments().getInt(Fragments.PARAMETER2), thumbailUrl, finishThumbnailCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_image_fullscreen, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		imageModel.globalDetachCallbacks(progressCallback, finishCallback);

		ZoomImageDrawable i = imageView.getImage();
		if (i != null) i.close();
	}

	public static ImageSnapshotFragment newInstance(int thumbnailId, int position) {
		return Fragments.instance(ImageSnapshotFragment.class, thumbnailId, position);
	}

	public interface ImageFragmentHost {

		boolean isFullscreenEnabled();

		void onToggleFullscreen();
	}
}