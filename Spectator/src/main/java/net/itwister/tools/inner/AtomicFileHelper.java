package net.itwister.tools.inner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.support.v4.util.AtomicFile;

public class AtomicFileHelper {

	public static <T> List<T> loadList(File file) throws Exception {
		AtomicFile atom = new AtomicFile(file);
		FileInputStream stream = atom.openRead();
		try {
			ObjectInputStream in = new ObjectInputStream(stream);
			return (List<T>) in.readObject();
		} finally {
			stream.close();
		}
	}

	public static <T> void saveList(File file, List<T> data) throws Exception {
		AtomicFile atom = new AtomicFile(file);
		FileOutputStream stream = atom.startWrite();
		try {
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(data);
			atom.finishWrite(stream);
		} catch (Exception e) {
			atom.failWrite(stream);
			throw e;
		}
	}
}