package de.smasi.tickmate.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import de.smasi.tickmate.R;
import de.smasi.tickmate.database.TracksDataSource;
import de.smasi.tickmate.models.Track;

public class EditTrackActivity extends Activity {
	
	Track track;
	EditText edit_name;
	EditText edit_description;
	CheckBox edit_enabled;
	CheckBox edit_multiple_entries_enabled;
	String icon_name;
	ImageButton edit_icon;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_track);
		// Show the Up button in the action bar.
		
		TracksDataSource ds = new TracksDataSource(this);
		
		int track_id = getIntent().getExtras().getInt("track_id");
		
		ds.open();
		track = ds.getTrack(track_id);
		ds.close();
		
		loadTrack();
		
		edit_icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final IconChooserDialog i = new IconChooserDialog(context);
				i.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (i.getSelectedIcon() != null) {
							Resources r = context.getResources();
							icon_name = i.getSelectedIcon();
							edit_icon.setImageResource(r.getIdentifier(icon_name, "drawable", context.getPackageName()));
						}
					}
				});
				i.show();
			}
		});

		
		setupActionBar();
		setResult(RESULT_OK);		
	}
	
	private void loadTrack() {
		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_name.setText(track.getName());
		edit_enabled = (CheckBox) findViewById(R.id.edit_enabled);
		edit_enabled.setChecked(track.isEnabled());		
		edit_multiple_entries_enabled = (CheckBox) findViewById(R.id.multiple_entries_enabled);
		edit_multiple_entries_enabled.setChecked(track.multipleEntriesEnabled());
		edit_multiple_entries_enabled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onStop();
			}
	    });
		edit_description = (EditText) findViewById(R.id.edit_description);
		edit_description.setText(track.getDescription());
		icon_name = track.getIcon();
		edit_icon = (ImageButton) findViewById(R.id.edit_icon);
		edit_icon.setImageResource(track.getIconId(this));
		
	}

	private void saveTrack() {
		String newName = edit_name.getText().toString();
		String newDescription = edit_description.getText().toString();
		boolean newEnabled = edit_enabled.isChecked();
		boolean newMultipleEntriesEnabled = edit_multiple_entries_enabled.isChecked();
		
		if (!track.getName().equals(newName) 
				|| track.isEnabled() != newEnabled
				|| track.multipleEntriesEnabled() != newMultipleEntriesEnabled
				|| track.getDescription() != newDescription) {
			track.setIcon(icon_name);
			track.setName(newName);
			track.setEnabled(newEnabled);
			track.setMultipleEntriesEnabled(newMultipleEntriesEnabled);
			track.setDescription(newDescription);
			TracksDataSource ds = new TracksDataSource(this);
			ds.open();
			ds.storeTrack(track);
			ds.close();
		}
	}

	@Override
	protected void onPause() {
		saveTrack();
		super.onPause();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_track, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
