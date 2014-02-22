//package net.itwister.spectator.view.home.menu;
//
//import net.itwister.spectator.App;
//import net.itwister.spectator.BuildConfig;
//import net.itwister.spectator.model.TagModel;
//import net.itwister.spectator.services.SyncService;
//import net.itwister.spectator.services.SyncService.SyncTarget;
//import net.itwister.spectator.view.base.SpectatorDialogFragment;
//import roboguice.util.RoboAsyncTask;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.google.inject.Inject;
//
//public class DeleteTagDialogFragment extends SpectatorDialogFragment {
//
//	private static final String ARG_TAG_ID = "arg_tag_id";
//
//	private boolean pendingDismiss;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		if (savedInstanceState == null) {
//			new RoboAsyncTask<Void>(getActivity()) {
//
//				@Inject
//				private TagModel model;
//
//				@Override
//				public Void call() throws Exception {
//					model.deleteTagOnWeb(getArguments().getInt(ARG_TAG_ID));
//					return null;
//				}
//
//				@Override
//				protected void onException(Exception e) throws RuntimeException {
//					if (BuildConfig.DEBUG) e.printStackTrace();
//					Toast.makeText(App.getInstance(), "Can't delete tag", Toast.LENGTH_LONG).show();
//				}
//
//				@Override
//				protected void onFinally() throws RuntimeException {
//					dismissOrPending();
//				}
//
//				@Override
//				protected void onSuccess(Void t) throws Exception {
//					SyncService.sync(SyncTarget.Tags);
//				}
//			}.execute();
//		}
//	}
//
//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		ProgressDialog d = new ProgressDialog(getActivity());
//		d.setMessage("Delete tag");
//		return d;
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (pendingDismiss) dismissOrPending();
//	}
//
//	private void dismissOrPending() {
//		if (isResumed()) dismiss();
//		else pendingDismiss = true;
//	}
//
//	public static DeleteTagDialogFragment newInstance(int tagId) {
//		Bundle args = new Bundle();
//		args.putInt(ARG_TAG_ID, tagId);
//		DeleteTagDialogFragment f = new DeleteTagDialogFragment();
//		f.setArguments(args);
//		return f;
//	}
//}