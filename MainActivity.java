package com.expir3dcow.irc;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.net.*;
import java.io.*;

public class MainActivity extends Activity
{
	EditText et;
	TextView log;
	BufferedWriter bout  = null;
	
    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		log = (TextView)findViewById(R.id.log);
		et = (EditText)findViewById(R.id.mainEditText);
		
    }
	
	public void sendText(View v){
		if(!(bout  == null)){
		if( !(et.getText().toString().equals(""))){
			try{
				sendMessage(bout,"PRIVMSG #ModPEScripts :"+et.getText().toString());
				et.setText("");
			}catch(Exception e){print(e.toString());}
		}
	}
	}
	
	public void sendMessage(BufferedWriter bw, String s){
		try{
			bw.write(s + "\r\n");
			bw.flush();
		}catch(Exception e){print(e.toString());}
	}
	
	public void print(String text){
		log.setText(log.getText() + "\n>>> " + text);
	}
	
	public void klik(View v){
		try{
		new connect().execute();
		}catch(Exception e){
			Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
		}
	}
	
	
	class connect extends AsyncTask<Void,Void,Void>
	{

		public void pprint(final String s){
			MainActivity.this.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					print(s);
				}
			});
		}
		@Override
		protected Void doInBackground(Void[] p1)
		{
			try{

				Socket socket = new Socket("irc.freenode.net",6667);
				pprint("*Socket created*");

				BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				pprint("*Got BufferedWriter");
				bout = bwriter;
				
				BufferedReader breader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
				pprint("*Got bufferedreader*");

				sendMessage(bwriter,"Nick expir3dcow");
				pprint("*Sent nick*");

				sendMessage(bwriter,"USER expir3dcow 8 * :Usayd Callender");
				pprint("*Sent user*");

				String line = null;
				while( (line = breader.readLine()) != null){
					if(line.indexOf("004") >= 0){
						pprint("You are now logged in");
						break;
					}
					else if(line.indexOf("433") >= 0){
						pprint("Nickname is already in use.");
						return null;
					}
				}
				
				sendMessage(bwriter,"JOIN #ModPEScripts");
				pprint("joining #ModPEScripts..");
				
				new Listen(breader,bwriter).start();
				
				
				
			}catch(Exception e){pprint(e.toString());}
		
			return null;
		}
		
		
		
		public void sendMessage(BufferedWriter bw, String s){
			try{
				bw.write(s + "\r\n");
				bw.flush();
			}catch(Exception e){pprint(e.toString());}
		}
		
	
	}
	
	
	class Listen extends Thread{
		String line = null;
		BufferedReader in;
		BufferedWriter out;
		public Listen(BufferedReader in,BufferedWriter out){
			this.in = in;
			this.out = out;
		}
		
		public void pprint(final String s){
			MainActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						print(s);
					}
				});
			}
		
		
		public void sendMessage(BufferedWriter bw, String s){
			try{
				bw.write(s + "\r\n");
				bw.flush();
			}catch(Exception e){pprint(e.toString());}
		}
				
		@Override
		public void run(){
			try{
			while( (line = in.readLine()) !=null){
				if(line.toLowerCase().startsWith("PING")){
					sendMessage(out,"PONG "+line.substring(5));
					sendMessage(out,"PRIVMSG #ModPEScripts :I got pinged");
				}
				else{
					pprint(line);
				}
			}
			}catch(Exception e){pprint(e.toString());}
		}
	}
}
