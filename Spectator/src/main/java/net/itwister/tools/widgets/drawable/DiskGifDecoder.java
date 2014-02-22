package net.itwister.tools.widgets.drawable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.itwister.spectator.BuildConfig;
import net.itwister.tools.inner.Ln;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.SparseArray;

public class DiskGifDecoder extends GifDecoder {

	private RandomAccessFile file;

	private SparseArray<State> framePositions;
	private boolean isAnimationInProgress;

	private ByteBuffer dest;

	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			if (BuildConfig.DEBUG) e.printStackTrace();
		}
	}

	@Override
	public Bitmap getFrame(int n) {
		if (frameCount <= 0) return null;
		n = n % frameCount;

		restorePosition(n);

		readBitmap();
		return image;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void read(File path) {
		try {
			file = new RandomAccessFile(path, "r");
			in = new InputStreamStub();

			init();
			readHeader();
			if (!err()) {
				readContents();
				if (frameCount < 0) {
					status = STATUS_FORMAT_ERROR;
				}
			}

			isAnimationInProgress = true;
		} catch (Exception e) {
			throw new RuntimeException("path (File) = " + path, e);
		}
	}

	@Override
	@Deprecated
	/** Метод не поддерживается */
	public int read(InputStream is) {
		throw new UnsupportedOperationException();
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	// ==============================================================
	// Защищенные методы
	// ==============================================================

	@Override
	protected void init() {
		super.init();
		framePositions = new SparseArray<State>();
		isAnimationInProgress = false;
	}

	@Override
	protected int read() {
		int curByte = 0;
		try {
			curByte = file.read();
		} catch (Exception e) {
			status = STATUS_FORMAT_ERROR;
		}
		return curByte;
	}

	@Override
	protected void readBitmap() {
		if (!isAnimationInProgress) savePosition();

		ix = readShort(); // (sub)image position & size
		iy = readShort();
		iw = readShort();
		ih = readShort();
		int packed = read();
		lctFlag = (packed & 0x80) != 0; // 1 - local color table flag interlace
		lctSize = (int) Math.pow(2, (packed & 0x07) + 1);
		// 3 - sort flag
		// 4-5 - reserved lctSize = 2 << (packed & 7); // 6-8 - local color
		// table size
		interlace = (packed & 0x40) != 0;
		if (lctFlag) {
			lct = readColorTable(lctSize); // read table
			act = lct; // make local table active
		} else {
			act = gct; // make global table active
			if (bgIndex == transIndex) bgColor = 0;
		}
		int save = 0;
		if (transparency) {
			save = act[transIndex];
			act[transIndex] = 0; // set transparent color if specified
		}
		if (act == null) status = STATUS_FORMAT_ERROR; // no color table defined
		if (err()) return;

		if (isAnimationInProgress) decodeBitmapData(); // decode pixel data
		else read();

		skip();
		if (err()) return;

		if (isAnimationInProgress) {
			// image = Bitmap.createBitmap(width, height, Config.ARGB_4444);
			setPixels(); // transfer pixel data to image
		} else {
			image = null;
			frameCount++;
			frames.addElement(new GifFrame(image, delay)); // add image to frame
		}

		// list
		if (transparency) act[transIndex] = save;
		resetFrame();
	}

	@Override
	protected int[] readColorTable(int ncolors) {
		int nbytes = 3 * ncolors;
		int[] tab = null;
		byte[] c = new byte[nbytes];
		int n = 0;
		try {
			n = file.read(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (n < nbytes) {
			status = STATUS_FORMAT_ERROR;
		} else {
			tab = new int[256]; // max size to avoid bounds checks
			int i = 0;
			int j = 0;
			while (i < ncolors) {
				int r = (c[j++]) & 0xff;
				int g = (c[j++]) & 0xff;
				int b = (c[j++]) & 0xff;
				tab[i++] = 0xff000000 | (b << 16) | (g << 8) | r;
			}
		}
		return tab;
	}

	@Override
	protected void setPixels() {
		if (dest == null) dest = ByteBuffer.allocate(width * height * 4).order(ByteOrder.nativeOrder());
		//		dest.position(0);
		//		for (int i = 0; i < dest.limit() / 4; i++) {
		//			dest.putInt(0);
		//		}
		dest.position(0);

		Ln.v("setPixels(), lastDispose = %s, dispose = %s, lastBitmap = %s", lastDispose, dispose, lastBitmap);

		// fill in starting image contents based on last image's dispose code
		if (lastDispose > 0) {
			if (lastDispose == 3) {
				// use image before last
				int n = frameCount - 2;
				if (n > 0) {
					lastBitmap = getFrame(n - 1);
				} else {
					lastBitmap = null;
				}
			}
			if (lastBitmap != null) {
				lastBitmap.copyPixelsToBuffer(dest.position(0));

				// copy pixels
				if (lastDispose == 2) {
					// fill last image rect area with background color
					int c = 0;
					if (!transparency) {
						c = lastBgColor;
					}
					for (int i = 0; i < lrh; i++) {
						int n1 = (lry + i) * width + lrx;
						int n2 = n1 + lrw;
						for (int k = n1; k < n2; k++) {
							// dest[k] = c;
							dest.putInt(4 * k, c);
						}
					}
				}
			}
		}
		// copy each source line to the appropriate place in the destination
		int pass = 1;
		int inc = 8;
		int iline = 0;
		for (int i = 0; i < ih; i++) {
			int line = i;
			if (interlace) {
				if (iline >= ih) {
					pass++;
					switch (pass) {
						case 2:
							iline = 4;
							break;
						case 3:
							iline = 2;
							inc = 4;
							break;
						case 4:
							iline = 1;
							inc = 2;
							break;
						default:
							break;
					}
				}
				line = iline;
				iline += inc;
			}
			line += iy;
			if (line < height) {
				int k = line * width;
				int dx = k + ix; // start of line in dest
				int dlim = dx + iw; // end of dest line
				if ((k + width) < dlim) {
					dlim = k + width; // past dest edge
				}
				int sx = i * iw; // start of line in source
				while (dx < dlim) {
					// map color and insert in destination
					int index = (pixels[sx++]) & 0xff;
					int c = act[index];
					if (c != 0) {
						dest.putInt(4 * dx, c);
					}
					dx++;
				}
			}
		}

		if (image == null) image = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		image.copyPixelsFromBuffer(dest.position(0));
	}

	// ==============================================================
	// Скрытые методы
	// ==============================================================

	private void restorePosition(int frame) {
		try {
			State s = framePositions.get(frame);

			dispose = s.dispose;
			lastDispose = s.lastDispose;

			this.lrx = s.lrx;
			this.lry = s.lry;
			this.lrw = s.lrw;
			this.lrh = s.lrh;
			this.lastBgColor = s.lastBgColor;
			this.transparency = s.transparency;
			this.delay = s.delay;
			this.lct = s.lct;

			file.seek(s.position);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void savePosition() {
		try {
			State s = new State();

			s.position = file.getFilePointer();

			s.dispose = dispose;
			s.lastDispose = lastDispose;

			s.lrx = this.lrx;
			s.lry = this.lry;
			s.lrw = this.lrw;
			s.lrh = this.lrh;
			s.lastBgColor = this.lastBgColor;
			s.transparency = this.transparency;
			s.delay = this.delay;
			s.lct = this.lct;

			framePositions.append(framePositions.size(), s);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// ==============================================================
	// Вложенные классы
	// ==============================================================

	private class InputStreamStub extends InputStream {

		@Override
		public int read() throws IOException {
			return file.read();
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			return file.read(buffer);
		}

		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			return file.read(buffer, offset, length);
		}
	}

	private static class State {

		long position;

		int dispose;
		int lastDispose;

		int lrx;
		int lry;
		int lrw;
		int lrh;
		int lastBgColor;
		boolean transparency;
		int delay;
		int[] lct;
	}
}