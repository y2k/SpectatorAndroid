package net.itwister.spectator.view.settings;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.view.common.base.SpectatorPreferenceActivity;
import net.itwister.spectator.view.helpers.VersionHelper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SettingsActivity extends SpectatorPreferenceActivity {

	@Inject
	private AnalyticsModel analytics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main);
		Preference version = findPreference("version");

		version.setSummary(VersionHelper.getVersion(this));
		initializeClickListeners();
	}

	private void feedback() {
		analytics.eventUserWantSendFeedback();

		Intent i = new Intent(Intent.ACTION_SENDTO);
		i.setData(Uri.parse("mailto:spectator-support@i-twister.net"));
		i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_feedback_spectator, VersionHelper.getVersion(this)));
		i.putExtra(Intent.EXTRA_TEXT, String.format(
				getString(R.string.feedback_message_template),
				Build.MODEL, Build.VERSION.RELEASE, getString(R.string.app_name), VersionHelper.getVersion(this)));

		try {
			startActivity(i);
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(this, R.string.error_not_email_client, Toast.LENGTH_SHORT).show();
		}
	}

	private void initializeClickListeners() {
		findPreference("feedback").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				feedback();
				return true;
			}
		});
		findPreference("source_code").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/y2k/Spectator")));
				return true;
			}
		});
	}
}