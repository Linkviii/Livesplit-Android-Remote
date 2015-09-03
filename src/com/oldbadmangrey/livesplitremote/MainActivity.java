package com.oldbadmangrey.livesplitremote;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final boolean DEBUG = false;

	private SimpleDateFormat dateFormater = new SimpleDateFormat("h:m:s");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent in = new Intent(this, AppSettings.class);
			startActivity(in);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MessageSenderTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			sendMsg(params[0]);
			return null;
		}

		private void sendMsg(String msg) {
			// String ip = "130.108.221.104";
			// String ip = "10.1.44.248";
			// String ip = "10.1.19.119";
			// String ip = "130.108.222.129";
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			String ip = sharedPref.getString("pref_ip", "");

			try (Socket socket = new Socket(ip, 16834);
					OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8")) {

				sendMsg(msg, osw);

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void sendMsg(String str, OutputStreamWriter o) throws IOException {
			o.write(str, 0, str.length());
			o.flush();

		}

	}

	private void call(String str) {
		TextView txt = new TextView(this);
		Date curDate = new Date();
		String now = dateFormater.format(curDate);
		txt.setText(now + " " + str.substring(0, str.length() - 2));
		LinearLayout line = (LinearLayout) findViewById(R.id.lineFoo);
		line.addView(txt);
		// sendMsg("starttimer\r\n");
		new MessageSenderTask().execute(str);
	}

	public void start(View view) {
		call(Command.STARTTIMER.toString());
	}

	public void pause(View view) {
		call(Command.PAUSE.toString());
	}

	public void resume(View view) {
		call(Command.RESUME.toString());
	}

	public void unsplit(View view) {
		call(Command.UNSPLIT.toString());
	}

	public void split(View view) {
		call(Command.SPLIT.toString());
	}

	public void startOrSplit(View view) {
		call(Command.START_OR_SPLIT.toString());
	}

	public void reset(View view) {
		call(Command.RESET.toString());
	}

	public void skip(View view) {
		call(Command.SKIPSPLIT.toString());
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		boolean useButtons = sharedPref.getBoolean("pref_use_buttons", true);

		if (useButtons) {
			String upFunction = sharedPref.getString("pref_volume_up_function", null);
			String downFunction = sharedPref.getString("pref_volume_down_function", null);
			String headsetFunction = sharedPref.getString("pref_headset_function", null);

			final String NONE = "nothing";
			int action = event.getAction();
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_DOWN && !upFunction.equals(NONE)) {

					call(upFunction + "\r\n");
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN && !downFunction.equals(NONE)) {

					call(downFunction + "\r\n");
				}
				return true;
			case KeyEvent.KEYCODE_HEADSETHOOK:
				if (action == KeyEvent.ACTION_DOWN && !headsetFunction.equals(NONE)) {
					call(headsetFunction + "\r\n");
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
