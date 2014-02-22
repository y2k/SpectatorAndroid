package net.itwister.spectator.model;

import android.graphics.Bitmap;

import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.P2;
import net.itwister.tools.widgets.drawable.ZoomImageDrawable;

import bindui.extra.ImageDownloader;

public interface ImageModel {

	String getSquareThumbnailUrl(int imageId, int sizePx);

	String getThumbnailUrl(int imageId, int maxWidthPx);

	void globalCancelAllExcept(String[] keys);

	void globalDetachCallbacks(P2<Integer, Integer> progressCallback, P2<ZoomImageDrawable, Exception> finishCallback);

	void globalInitializeDownload(String key, String url, P2<Integer, Integer> progressCallback, P2<ZoomImageDrawable, Exception> finishCallback);

	void globalInitializeThumbnailDownload(String key, String url, P2<Bitmap, Exception> finishCallback);

//	String startDownload(SyncObject syncObject, int position, ImageModelListener callback);

    void deleteTokenFromQueue(Object token);

    void addImageTaskToQueue(Object token, String url, ImageDownloader.ImageDownloaderCallback callback);

//    public abstract class ImageModelListener {
//
//		public int position;
//		public int length;
//		public File file;
//		public Exception exception;
//
//		private SafeAsyncTask<?> task;
//
//		public void attach(SafeAsyncTask<?> task) {
//			this.task = task;
//		}
//
//		public void cancel() {
//			task.cancelWhenNotNull(true);
//		}
//
//		public abstract void onChanged();
//	}
}