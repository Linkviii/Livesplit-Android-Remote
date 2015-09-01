package com.oldbadmangrey.livesplitremote;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private SimpleDateFormat dateFormater = new SimpleDateFormat("h:m:s");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			String ip = "130.108.222.129";
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void call(String str) {
		TextView txt = new TextView(this);
		Date curDate = new Date();
		String now = dateFormater.format(curDate);
		txt.setText(now + " " + str.substring(0, str.length() - 2));
		LinearLayout line = (LinearLayout) findViewById(R.id.line);
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

	public void reset(View view) {
		call(Command.RESET.toString());
	}

	public void skip(View view) {
		call(Command.SKIPSPLIT.toString());
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_DOWN) {
				// TODO
				call(Command.SPLIT.toString());
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				// TODO
				call(Command.UNSPLIT.toString());
			}
			return true;
		case KeyEvent.KEYCODE_HEADSETHOOK:
			if (action == KeyEvent.ACTION_DOWN) {
				call(Command.RESET.toString());
			}
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

}
