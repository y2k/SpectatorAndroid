package net.itwister.spectator.model.impl;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import net.itwister.spectator.Constants;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.model.web.SpectatorWebClient;
import net.itwister.spectator.view.common.P2;
import net.itwister.tools.widgets.drawable.ZoomImageDrawable;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import bindui.InjectService;
import bindui.extra.DiskCache;
import bindui.extra.ImageDownloader;

@Singleton
public class ImageModelImpl implements ImageModel {

	private static final Map<String, BaseDownloadImageTask<?>> sActiveTasks = new HashMap<>();

    @Inject private OrmLiteSqliteOpenHelper helper;
    @Inject private SpectatorWebClient web;
    private ImageDownloader imageDownloader;

    public ImageModelImpl() {
        imageDownloader = ImageDownloader.ImageDownloaderFabric.create(
                new ImageDownloader.HttpURLConnectionFactory() {

                    @Override
                    public HttpURLConnection create(URL url) throws Exception {
                        return web.open(url);
                    }
                },
                DiskCache.DiskCacheFabric.newCache(new File(InjectService.getInstance(Context.class).getExternalCacheDir(), "images"), 512 * 1024 * 1024),
                new ImageDownloader.MemoryCache<String, Bitmap>() {

                    private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(16 * 1024 * 1024) {

                        @Override
                        protected int sizeOf(String key, Bitmap value) {
                            return value.getByteCount();
                        }
                    };

                    @Override
                    public Bitmap get(String s) {
                        return cache.get(s);
                    }

                    @Override
                    public void put(String s, Bitmap bitmap) {
                        cache.put(s, bitmap);
                    }
                });
    }

	@Override
	public String getSquareThumbnailUrl(int imageId, int sizePx) {
		StringBuilder url = new StringBuilder(Constants.Url.URL_HOST + "/Image/Thumb/");
		url.append(imageId);
		url.append("?size=" + sizePx);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) url.append("&type=webp");
		return url.toString();
	}

	@Override
	public String getThumbnailUrl(int imageId, int maxWidthPx) {
		StringBuilder url = new StringBuilder(Constants.Url.URL_HOST + "/Image/Thumbnail/");
		url.append(imageId);
		url.append("?width=" + maxWidthPx);
		url.append("&height=" + maxWidthPx);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) url.append("&type=webp");
		return url.toString();
	}

	@Override
	public void globalCancelAllExcept(String[] keys) {
		Set<String> keep = new HashSet<String>(Arrays.asList(keys));
		for (String k : sActiveTasks.keySet().toArray(new String[0])) {
			if (!keep.contains(k)) {
				sActiveTasks.remove(k).cancel(true);
			}
		}
	}

	@Override
	public void globalDetachCallbacks(P2<Integer, Integer> progressCallback, P2<ZoomImageDrawable, Exception> finishCallback) {
		for (BaseDownloadImageTask<?> t : sActiveTasks.values()) {
			if (progressCallback != null && t.progressCallback == progressCallback) t.progressCallback = null;
			if (finishCallback != null && t.finishCallback == finishCallback) t.finishCallback = null;
		}
	}

	@Override
	public void globalInitializeDownload(String key, String url, P2<Integer, Integer> progressCallback, P2<ZoomImageDrawable, Exception> finishCallback) {
		// TODO Auto-generated method stub

//		@SuppressWarnings("unchecked")
//		BaseDownloadImageTask<ZoomImageDrawable> old = (BaseDownloadImageTask<ZoomImageDrawable>) sActiveTasks.get(key);
//
//		if (old != null) {
//
//			if (url.equals(old.url)) {
//				//
//
//				old.progressCallback = progressCallback;
//				old.finishCallback = finishCallback;
//				old.invalidateProgress();
//				return;
//
//			}
//
//			//
//			old.cancel(true);
//			sActiveTasks.remove(key);
//
//		}
//
//		BaseDownloadImageTask<ZoomImageDrawable> task = new BaseDownloadImageTask<ZoomImageDrawable>() {
//
//			@Override
//			protected ZoomImageDrawable getResult(File imagePath) {
//				DisplayMetrics m = App.getInstance().getResources().getDisplayMetrics();
//				return ZoomImageDrawable.fromFile(imagePath, m.widthPixels, m.heightPixels);
//			}
//
//			@Override
//			protected void onFinally() throws RuntimeException {
//				for (String key : new ArrayList<String>(sActiveTasks.keySet())) {
//					if (sActiveTasks.get(key) == this) sActiveTasks.remove(key);
//				}
//			}
//
//			@Override
//			protected void onSuccess(ZoomImageDrawable result) throws Exception {
//				if (finishCallback != null) finishCallback.call(result, null);
//				else result.close();
//			}
//		};
//		task.url = url;
//		task.progressCallback = progressCallback;
//		task.finishCallback = finishCallback;
//		task.execute();
//
//		sActiveTasks.put(key, task);
	}

	@Override
	public void globalInitializeThumbnailDownload(String key, String url, P2<Bitmap, Exception> finishCallback) {
		// TODO Auto-generated method stub

//		@SuppressWarnings("unchecked")
//		BaseDownloadImageTask<Bitmap> old = (BaseDownloadImageTask<Bitmap>) sActiveTasks.get(key);
//
//		if (old != null) {
//
//			if (url.equals(old.url)) {
//				//
//
//				old.finishCallback = finishCallback;
//				old.invalidateProgress();
//				return;
//
//			}
//
//			//
//			old.cancel(true);
//			sActiveTasks.remove(key);
//
//		}
//
//		BaseDownloadImageTask<Bitmap> task = new BaseDownloadImageTask<Bitmap>() {
//
//			@Override
//			protected Bitmap getResult(File imagePath) {
//				return RenderScriptHelper.gausseBitmap(BitmapFactory.decodeFile(imagePath.getAbsolutePath()), 2);
//			}
//
//			@Override
//			protected void onFinally() throws RuntimeException {
//				for (String key : new ArrayList<String>(sActiveTasks.keySet())) {
//					if (sActiveTasks.get(key) == this) sActiveTasks.remove(key);
//				}
//			}
//
//			@Override
//			protected void onSuccess(Bitmap result) throws Exception {
//				if (finishCallback != null) finishCallback.call(result, null);
//				else result.recycle();
//			}
//		};
//
//		task.url = url;
//		task.finishCallback = finishCallback;
//		task.execute();
//
//		sActiveTasks.put(key, task);

	}

//	@Override
//	public String startDownload(final int snapshotId, final int position, final ImageModelListener callback) {
//		//		SpectatorAsyncTask<File> t = new SpectatorAsyncTask<File>() {
//		//
//		//			@Override
//		//			public File call() throws Exception {
//		//				String url = getScreenshotUrl(snapshotId, position);
//		//				File f = ImageDownloadManager.getInstance().getPathForUrl(url);
//		//				if (f != null && f.exists()) return f;
//		//
//		//				if (Thread.interrupted()) throw new InterruptedException();
//		//
//		//				URLConnection conn = new URL(url).openConnection();
//		//				callback.length = conn.getContentLength();
//		//
//		//				invalidateProgress();
//		//
//		//				InputStream in = null;
//		//				FileOutputStream out = null;
//		//				File tmp = File.createTempFile("download_", null, ImageDownloadManager.getInstance().getTempDirectory());
//		//
//		//				try {
//		//					in = conn.getInputStream();
//		//					out = new FileOutputStream(tmp);
//		//
//		//					byte[] buf = new byte[16 * 1024];
//		//					int count = 0;
//		//					long lastShowed = 0;
//		//
//		//					while ((count = in.read(buf)) != -1) {
//		//						out.write(buf, 0, count);
//		//						callback.position += count;
//		//
//		//						if (Thread.interrupted()) throw new InterruptedException();
//		//
//		//						if (SystemClock.uptimeMillis() > lastShowed + 500) invalidateProgress();
//		//						lastShowed = SystemClock.uptimeMillis();
//		//					}
//		//				} finally {
//		//					//
//		//					IoHelper.close(in);
//		//					IoHelper.close(out);
//		//				}
//		//
//		//				ImageDownloadManager.getInstance().putToCache(url, tmp);
//		//
//		//				f = ImageDownloadManager.getInstance().getPathForUrl(url);
//		//				if (f == null || !f.exists()) throw new Exception("Can't download image, url = " + url);
//		//
//		//				return f;
//		//			}
//		//
//		//			@Override
//		//			protected void onException(Exception e) throws RuntimeException {
//		//				callback.exception = e;
//		//				callback.onChanged();
//		//			}
//		//
//		//			@Override
//		//			protected void onSuccess(File file) throws Exception {
//		//				callback.file = file;
//		//				callback.onChanged();
//		//			}
//		//
//		//			private void invalidateProgress() {
//		//				uiHandler.post(new Runnable() {
//		//
//		//					@Override
//		//					public void run() {
//		//						callback.onChanged();
//		//					}
//		//				});
//		//			}
//		//		};
//		//		t.execute();
//		//
//		//		callback.attach(t);
//		//		return null;
//
//		return startDownload(new SyncObject(snapshotId), position, callback);
//	}

//	@Override
//	public String startDownload(final SyncObject syncObject, final int position, final ImageModelListener callback) {
//		SpectatorTask<File> t = new SpectatorTask<File>() {
//
//			@Override
//			public File call() throws Exception {
//				int snapshotId = syncObject.getSnapshotId();
//
//				String url = getScreenshotUrl(snapshotId, position);
//				File f = ImageDownloadManager.getInstance().getPathForUrl(url);
//				if (f != null && f.exists()) return f;
//
//				if (Thread.interrupted()) throw new InterruptedException();
//
//				URLConnection conn = new URL(url).openConnection();
//				callback.length = conn.getContentLength();
//
//				invalidateProgress();
//
//				InputStream in = null;
//				FileOutputStream out = null;
//				File tmp = File.createTempFile("download_", null, ImageDownloadManager.getInstance().getTempDirectory());
//
//				try {
//					in = conn.getInputStream();
//					out = new FileOutputStream(tmp);
//
//					byte[] buf = new byte[16 * 1024];
//					int count = 0;
//					long lastShowed = 0;
//
//					while ((count = in.read(buf)) != -1) {
//						out.write(buf, 0, count);
//						callback.position += count;
//
//						if (Thread.interrupted()) throw new InterruptedException();
//
//						if (SystemClock.uptimeMillis() > lastShowed + 500) invalidateProgress();
//						lastShowed = SystemClock.uptimeMillis();
//					}
//				} finally {
//					//
//					IoHelper.close(in);
//					IoHelper.close(out);
//				}
//
//				ImageDownloadManager.getInstance().putToCache(url, tmp);
//
//				f = ImageDownloadManager.getInstance().getPathForUrl(url);
//				if (f == null || !f.exists()) throw new Exception("Can't download image, url = " + url);
//
//				return f;
//			}
//
//			@Override
//			protected void onException(Exception e) throws RuntimeException {
//				callback.exception = e;
//				callback.onChanged();
//			}
//
//			@Override
//			protected void onSuccess(File file) throws Exception {
//				callback.file = file;
//				callback.onChanged();
//			}
//
//			private void invalidateProgress() {
//				uiHandler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						callback.onChanged();
//					}
//				});
//			}
//		};
//		t.execute();
//
//		callback.attach(t);
//		return null;
//	}

    @Override
    public void deleteTokenFromQueue(Object token) {
        imageDownloader.removeFromQueue(token);
    }

    @Override
    public void addImageTaskToQueue(Object token, String url, ImageDownloader.ImageDownloaderCallback callback) {
        imageDownloader.requestDownload(token, url, callback);
    }

    // ==============================================================

	private String getScreenshotUrl(int snapshotId, int position) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select image_url from screenshots where snapshot_id = ? limit 1 offset ?";
		return DatabaseUtils.stringForQuery(db, sql, new String[] { "" + snapshotId, "" + position });
	}

	public static abstract class BaseDownloadImageTask<Result> extends SpectatorTask<Result> {

		String url;
		P2<Integer, Integer> progressCallback;
		P2<Result, Exception> finishCallback;

		int statusLength;
		int statusPosition;

		Handler uiHandler = new Handler(Looper.getMainLooper());

		@Override
		public Result onExecuteWithResult() throws Exception {
//			File f = ImageDownloadManager.getInstance().getPathForUrl(url);
//			if (f != null && f.exists()) return getResult(f);
//
//			if (Thread.interrupted()) throw new InterruptedException();
//
//			//			URLConnection conn = new URL(url).openConnection();
//			URLConnection conn = RoboGuice.getBaseApplicationInjector(App.getInstance()).getInstance(SpectatorWebClient.class).open(new URL(url));
//			statusLength = conn.getContentLength();
//
//			invalidateProgress();
//
//			InputStream in = null;
//			FileOutputStream out = null;
//			File tmp = File.createTempFile("download_", null, ImageDownloadManager.getInstance().getTempDirectory());
//
//			try {
//				in = conn.getInputStream();
//				out = new FileOutputStream(tmp);
//
//				byte[] buf = new byte[16 * 1024];
//				int count = 0;
//				long lastShowed = 0;
//
//				while ((count = in.read(buf)) != -1) {
//					out.write(buf, 0, count);
//					statusPosition += count;
//
//					if (Thread.interrupted()) throw new InterruptedException();
//
//					if (SystemClock.uptimeMillis() > lastShowed + 100) invalidateProgress();
//					lastShowed = SystemClock.uptimeMillis();
//				}
//			} finally {
//				//
//				IoHelper.close(in);
//				IoHelper.close(out);
//			}
//
//			ImageDownloadManager.getInstance().putToCache(url, tmp);
//
//			f = ImageDownloadManager.getInstance().getPathForUrl(url);
//			if (f == null || !f.exists()) throw new Exception("Can't download image, url = " + url);
//
//			return getResult(f);

            throw new UnsupportedOperationException();
		}

		protected abstract Result getResult(File imagePath);

		protected void invalidateProgress() {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					if (progressCallback != null) progressCallback.call(statusPosition, statusLength);
				}
			});
		}

//		@Override
//		protected void onException(Exception e) throws RuntimeException {
//			if (BuildConfig.DEBUG) e.printStackTrace();
//
//			if (finishCallback != null) finishCallback.call(null, e);
//		}
//
//		@Override
//		protected void onInterrupted(Exception e) {
//			Ln.i("onInterrupted, url = " + url);
//		}
	}
}