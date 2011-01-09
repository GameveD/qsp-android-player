package com.qsp.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStroke;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;





/*public class jniResult
{
	boolean success;
	
	int int1;
	int int2;
	int int3;

	String str1;
	String str2;
	String str3;
};
*/

public class QspPlayerStart extends Activity implements UrlClickCatcher, OnGesturePerformedListener{

    public static final int SWIPE_MIN = 120;
    public static final int WIN_INV = 0;
    public static final int WIN_MAIN = 1;
    public static final int WIN_EXT = 2;
    public static final int SLOTS_MAX = 5;
    public static final int ACTIVITY_SELECT_GAME = 0;
	Resources res;
	
	private Menu menuMain;
	
	boolean invUnread, varUnread;
	int invBack, varBack;
	int currentWin;
	
	final private Context uiContext = this;
	final private ReentrantLock musicLock = new ReentrantLock();
	
	private boolean gui_debug_mode = true; 


	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    	//�������� UI
		if(gesture.getLength()>SWIPE_MIN) {
			ArrayList<GestureStroke> strokes = gesture.getStrokes();
			float[] points = strokes.get(0).points; 
			if(points[0]<points[points.length-1]){
                //swipe left
            	if(currentWin>0)
            		currentWin--;
            	else
            		currentWin = 2;
			}else{
            	if(currentWin<2) 
            		currentWin++;
            	else
            		currentWin = 0;
			}
			setCurrentWin(currentWin); 				
		}
	}
	
	public QspPlayerStart() {
    	//�������� UI
    	Utility.WriteLog("constructor\\");
    	
		gameIsRunning = false;
		qspInited = false;
		waitForImageBox = false;

        //������� ������ ��� ������ � ������
        mediaPlayersList = new Vector<MusicContent>();
        
        //������� ������ ��� ������������ ����
        menuList = new Vector<QspMenuItem>();

        //������� ������ ��� �������
        timerHandler = new Handler();

        //��������� ����� ����������
        StartLibThread();
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
    	Utility.WriteLog("onCreate\\");
    	//�������� UI
        super.onCreate(savedInstanceState);
        //����� ������������ ���� ��� ���������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        res = getResources();


        //���������� �����
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);
        
        //������� ������ ��� ��������� ������
        qspLinkMovementMethod = QspLinkMovementMethod.getQspInstance();
        qspLinkMovementMethod.setCatcher(this);
        
        //������� ������ ����� ������
        LayoutInflater factory = LayoutInflater.from(uiContext);
        View textEntryView = factory.inflate(R.layout.inputbox, null);
        inputboxDialog = new AlertDialog.Builder(uiContext)
        .setView(textEntryView)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	EditText edit = (EditText)inputboxDialog.findViewById(R.id.inputbox_edit);
            	inputboxResult = edit.getText().toString();
				dialogHasResult = true;
				Utility.WriteLog("InputBox(UI): OK clicked, unparking library thread");
            	setThreadUnpark();
            }
        })
        .setCancelable(false)
        .create();
        
        if (!gameIsRunning)
        {
       		//������� ��� - �������� ��������
        	invBack = 0; //��� ����
        	varBack = 0; //��� ����
        	setCurrentWin(currentWin=WIN_MAIN);
        	Intent myIntent = new Intent();
        	myIntent.setClassName("com.qsp.player", "com.qsp.player.QspGameStock");
        	startActivityForResult(myIntent, ACTIVITY_SELECT_GAME);
        }

    	Utility.WriteLog("onCreate/");
    }
    
    @Override
    public void onResume()
    {
    	Utility.WriteLog("onResume\\");
    	//�������� UI
    	super.onResume();
    	
    	if (gameIsRunning && !waitForImageBox)
    	{
    		//��������� ������
            timerHandler.postDelayed(timerUpdateTask, timerInterval);

            //��������� ������
    	    PauseMusic(false);
    	}
    	waitForImageBox = false;
    	
    	Utility.WriteLog("onResume/");    	
    }
    
    @Override
    public void onPause() {
    	//�������� UI
    	Utility.WriteLog("onPause\\");
    	
    	if (gameIsRunning && !waitForImageBox)
    	{
        	Utility.WriteLog("onPause: pausing game");    	
    	    //������������� ������
    	    timerHandler.removeCallbacks(timerUpdateTask);
    	    
    	    //���������������� ������
    	    PauseMusic(true);
    	}
    	
    	Utility.WriteLog("onPause/");  
    	super.onPause();
    }
    
    @Override
    public void onDestroy() {
    	//�������� UI
    	Utility.WriteLog("onDestroy\\");
    	FreeResources();
    	Utility.WriteLog("onDestroy/");  
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	//�������� UI
    	//����� ������ "Back", � �� ��������� ��������, � ������
    	//���������� � ��������� (��� ����� ������ "Home")
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	Utility.WriteLog("Back pressed! Going to background");
        	moveTaskToBack(true);
        	return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//�������� UI
        // ��������� ������
        menuMain = menu;
        
        // ��������� ���� �� XML-�����
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	//�������� UI
    	//������ ��� ���������� ������ ������� ���� "������ ������", "���������", "���������"
    	menu.setGroupVisible(R.id.menugroup_running, gameIsRunning);
    	if (gameIsRunning)
    	{
    		//��������� �����
    		MenuItem loadItem = menu.findItem(R.id.menu_loadgame);
    		LoadSlots(loadItem, "���������");
    		MenuItem saveItem = menu.findItem(R.id.menu_savegame);
    		LoadSlots(saveItem, "���������");
    	}
    	return true;
    }
    
    private void LoadSlots(MenuItem rootItem, String name)
    {
    	//�������� UI
		if (rootItem == null)
			return;
		
		SubMenu slotsMenu = null;
		
		if (rootItem.hasSubMenu())
		{
			slotsMenu = rootItem.getSubMenu();
			slotsMenu.clear();
		}
		else
		{
			int id = rootItem.getItemId();
			menuMain.removeItem(id);
			slotsMenu = menuMain.addSubMenu(Menu.NONE, id, Menu.NONE, name);
			slotsMenu.setHeaderTitle("�������� ����");
		}
		
		for (int i=0; i<SLOTS_MAX; i++)
		{
			String title = String.valueOf(i + 1).concat(": ");
			String Slotname = String.valueOf(i + 1).concat(".sav");
			File checkSlot = new File(curGameDir.concat(Slotname));
			if (checkSlot.exists())
			{
				String datetime = (String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", checkSlot.lastModified());
				title = title.concat(datetime);
			}
			else
				title = title.concat("[�����]");
			slotsMenu.add(title);
		}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//�������� UI
        switch (item.getItemId()) {
            case R.id.menu_gamestock:
                //�������� ����
            	Intent myIntent = new Intent();
            	myIntent.setClassName("com.qsp.player", "com.qsp.player.QspGameStock");
            	startActivityForResult(myIntent, ACTIVITY_SELECT_GAME);
                return true;

            case R.id.menu_options:
                //!!! STUB
                return true;

            case R.id.menu_about:
            	//!!! STUB
                return true;

            case R.id.menu_newgame:
            	String gameFile = curGameFile;
            	StopGame(true);
            	runGame(gameFile);
                return true;

            case R.id.menu_loadgame:
            	//!!! STUB
                return true;

            case R.id.menu_savegame:
            	//!!! STUB
                return true;
                
            default:
            {
            	MenuItem l = menuMain.findItem(R.id.menu_loadgame);
            	SubMenu ls = l.getSubMenu();
            	if (ls != null)
            	{
            		for (int i=0;i<SLOTS_MAX; i++)
            		{
            			MenuItem li = ls.getItem(i);
            			if (li == item)
            			{
                   			LoadSlot(i + 1);                   			
            			}
            		}
            	}
            	MenuItem s = menuMain.findItem(R.id.menu_savegame);
            	SubMenu ss = s.getSubMenu();
            	if (ss != null)
            	{
            		for (int i=0;i<SLOTS_MAX; i++)
            		{
            			MenuItem si = ss.getItem(i);
            			if (si == item)
            			{
                   			SaveSlot(i + 1);
            			}
            		}
            	}
            }
            	break;
        }        
        return false;
    }
    
    private void LoadSlot(int index)
    {
    	String path = curGameDir.concat(String.valueOf(index)).concat(".sav");
    	File f = new File(path);
    	if (f.exists())
    	{
    		//!!! STUB
    		//QSPOpenSavedGameFromData(String str, boolean isRefresh);
    	}
    }
    
    private void SaveSlot(int index)
    {
    	String path = curGameDir.concat(String.valueOf(index)).concat(".sav");
    	File f = new File(path);
    	//!!! STUB
    	//QSPSaveGameAsData(boolean isRefresh);
    	
    }

    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == ACTIVITY_SELECT_GAME) {
            if (resultCode == RESULT_OK) {
                //���� �������, ��������� ��
            	if (data == null)
            		return;
            	String file_name = data.getStringExtra("file_name");
            	if (file_name == null)
            		return;
            	runGame(file_name);
            }
        }
    }
    
    //******************************************************************************
    //******************************************************************************
    //****** / THREADS \ ***********************************************************
    //******************************************************************************
    //******************************************************************************
    /** �������-������������� ��������� ����, � ��������� �� ���� ��������� � parkThread */
    protected void setThreadPark()    {
    	Utility.WriteLog("setThreadPark: enter ");    	
    	//�������� ����������
    	if (libThread == null)
    	{
    		Utility.WriteLog("setThreadPark: failed, libthread is null");
    		return;
    	}
        parkThread = libThread;
        LockSupport.park();
    	Utility.WriteLog("setThreadPark: success ");    	
    }
    
    /** ������������ ������ ����� ������������ � ��������� parkThread */
    protected boolean setThreadUnpark()    {
    	Utility.WriteLog("setThreadUnPark: enter ");
    	//�������� UI
        if (parkThread!=null && parkThread.isAlive()) {
            LockSupport.unpark(parkThread);
        	Utility.WriteLog("setThreadUnPark: success ");    	
            return true;
        }
    	Utility.WriteLog("setThreadUnPark: failed, ");
    	if (parkThread==null)
        	Utility.WriteLog("parkThread is null ");
    	else
        	Utility.WriteLog("parkThread is dead ");
        return false;
    }
    
    protected void StartLibThread()
    {
    	Utility.WriteLog("StartLibThread: enter ");    	
    	//�������� UI
    	if (libThread!=null)
    	{
        	Utility.WriteLog("StartLibThread: failed, libThread is null");    	
    		return;
    	}
    	//��������� ����� ����������
    	Thread t = new Thread() {
            public void run() {
    			Looper.prepare();
    			libThreadHandler = new Handler();
            	Utility.WriteLog("LibThread runnable: libThreadHandler is set");    	
        		Looper.loop();
            	Utility.WriteLog("LibThread runnable: library thread exited");    	
            }
        };
        libThread = t;
        t.start();
    	Utility.WriteLog("StartLibThread: success ");    	
    }
    
    protected void StopLibThread()
    {
    	Utility.WriteLog("StopLibThread: enter ");    	
    	//�������� UI
    	//������������� ����� ����������
       	libThreadHandler.getLooper().quit();
		libThread = null;
    	Utility.WriteLog("StopLibThread: success ");    	
    }
    //******************************************************************************
    //******************************************************************************
    //****** \ THREADS / ***********************************************************
    //******************************************************************************
    //******************************************************************************

    //������������� ����� ��������� ����
    private void setTitle(String second) {
   		TextView winTitle = (TextView) findViewById(R.id.title_text);
   		winTitle.setText(second);
		updateTitle();
    }	
    
    //�������� ������ ��� ����� ����������� ������� ����
    private void updateTitle() {
		ImageButton image = (ImageButton) findViewById(R.id.title_button_1);
		image.clearAnimation();
		if(invUnread){
    		Animation update = AnimationUtils.loadAnimation(this, R.anim.update);
    		image.startAnimation(update);
    		image.setBackgroundResource(invBack=R.drawable.btn_bg_pressed);
    		invUnread = false;
    	}
		image = (ImageButton) findViewById(R.id.title_button_2);
		image.clearAnimation();
    	if(varUnread){
    		Animation update = AnimationUtils.loadAnimation(this, R.anim.update);
    		image.startAnimation(update);
    		image.setBackgroundResource(varBack=R.drawable.btn_bg_pressed);
    		varUnread = false;
    	}	
    }	
    
    //���������� "��������" � ���������
    public void onHomeClick(View v) {
    	setCurrentWin(WIN_MAIN);
    }
    
    //���������� "���������" � ���������
    public void onInvClick(View v) {
    	setCurrentWin(WIN_INV);
    }
    
    //���������� "���" � ���������
    public void onExtClick(View v) {
    	setCurrentWin(WIN_EXT);
    }
    
    //����� ��������� ������
    private void setCurrentWin(int win) {
    	switch(win){
    	case WIN_INV: 
       		toggleInv(true);
       		toggleMain(false);
       		toggleExt(false);
       		invUnread = false;
       		invBack = 0;
       		setTitle("���������");
       		break;
    	case WIN_MAIN: 
       		toggleInv(false);
       		toggleMain(true);
       		toggleExt(false);
       		setTitle("��������");
       		break;
    	case WIN_EXT: 
       		toggleInv(false);
       		toggleMain(false);
       		toggleExt(true);
       		varUnread = false;
       		varBack = 0;
       		setTitle("���. ��������");
       		break;
    	}
    }
    
    private void toggleInv(boolean vis) {
   		findViewById(R.id.inv).setVisibility(vis ? View.VISIBLE : View.GONE);
		findViewById(R.id.title_button_1).setBackgroundResource(vis ? R.drawable.btn_bg_active : invBack);
    }
    
    private void toggleMain(boolean vis) {
   		findViewById(R.id.main_desc).setVisibility(vis ? View.VISIBLE : View.GONE);
   		findViewById(R.id.acts).setVisibility(vis ? View.VISIBLE : View.GONE);
		findViewById(R.id.title_home_button).setBackgroundResource(vis ? R.drawable.btn_bg_active : 0);
    }
    
    private void toggleExt(boolean vis) {
   		findViewById(R.id.vars_desc).setVisibility(vis ? View.VISIBLE : View.GONE);
		findViewById(R.id.title_button_2).setBackgroundResource(vis ? R.drawable.btn_bg_active : varBack);
   }
    
    private Runnable timerUpdateTask = new Runnable() {
    	//�������� UI
		public void run() {
			libThreadHandler.post(new Runnable() {
				public void run() {
					if (!gameIsRunning)
						return;
					if (libraryThreadIsRunning)
						return;
			    	libraryThreadIsRunning = true;
				   	QSPExecCounter(true);
					libraryThreadIsRunning = false;
				}
			});
			timerHandler.postDelayed(this, timerInterval);
		}
	};
    
    //LINKS HACKS
    static class InternalURLSpan extends ClickableSpan {
    	//�������� UI
    	OnClickListener mListener;

    	public InternalURLSpan(OnClickListener listener) {
    		mListener = listener;
    	}

    	@Override
    	public void onClick(View widget) {
    		mListener.onClick(widget);
    	}
    }
    
    private void FreeResources()
    {
    	//�������� UI
    	
    	//��������� "��������" ������������� ���� �������� - � �.�. ��������� ������ ����������
    	//������ �� ���������� ������, �.�. � ��� ��� �����. ������ ����.

    	//��������� ������ ��� �������� �������� � ����������� onDestroy(���������� ��������),
    	//�� ����� �� ���������� ��� ���������� ������, �.�. ������ Back �� ���������, � ������
    	//������������� ��������.
    	
    	//������� ��� �� ������
    	if (qspInited)
    	{
        	Utility.WriteLog("onDestroy: stopping game");
    		StopGame(false);
    	}
    	//������������� ����� ����������
   		StopLibThread();
    }
    
    private void runGame(String fileName)
    {
    	//�������� UI
    	if (libThreadHandler==null)
    	{
    		Utility.WriteLog("runGame: failed, libThreadHandler is null");
    		return;
    	}

		if (libraryThreadIsRunning)
		{
    		Utility.WriteLog("runGame: failed, library thread is already running");
			return;
		}
    	
        final boolean inited = qspInited;
    	qspInited = true;
    	final String gameFileName = fileName;
    	curGameFile = gameFileName;
        curGameDir = gameFileName.substring(0, gameFileName.lastIndexOf(File.separator, gameFileName.length() - 1) + 1);
        imgGetter.SetDirectory(curGameDir);
        imgGetter.SetScreenWidth(getWindow().getWindowManager().getDefaultDisplay().getWidth());
        
        libThreadHandler.post(new Runnable() {
    		public void run() {
        		if (!inited)
        			QSPInit();
    	        File tqsp = new File (gameFileName);
    	        FileInputStream fIn = null;
    	        int size = 0;
    			try {
    				fIn = new FileInputStream(tqsp);
    			} catch (FileNotFoundException e) {
    	        	e.printStackTrace();
    			}
    			try {
    				size = fIn.available();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    	        
    			byte[] inputBuffer = new byte[size];
    			try {
    			// Fill the Buffer with data from the file
    			fIn.read(inputBuffer);
    			fIn.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}

    			final boolean gameLoaded = QSPLoadGameWorldFromData(inputBuffer, size, gameFileName);
    			
    			runOnUiThread(new Runnable() {
    				public void run() {
    	    			TextView tv = (TextView) findViewById(R.id.main_desc); 
    	    	        
    	    	        if (gameLoaded)
    	    	        {
        		    		//init acts callbacks
    	    	            ListView lvAct = (ListView)findViewById(R.id.acts);
    	    	            lvAct.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	    	            lvAct.setFocusableInTouchMode(true);
    	    	            lvAct.setFocusable(true);
    	    	            lvAct.setItemsCanFocus(true);
    	    	            lvAct.setOnItemClickListener(actListClickListener);
    	    	            lvAct.setOnItemSelectedListener(actListSelectedListener);        

    	    	            //init objs callbacks
    	    	            ListView lvInv = (ListView)findViewById(R.id.inv);
    	    	            lvInv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	    	            lvInv.setFocusableInTouchMode(true);
    	    	            lvInv.setFocusable(true);
    	    	            lvInv.setItemsCanFocus(true);
    	    	            lvInv.setOnItemClickListener(objListClickListener);
    	    	            lvInv.setOnItemSelectedListener(objListSelectedListener);        

    	    	            //��������� ������
    	    	            timerInterval = 500;
    	    	            timerStartTime = System.currentTimeMillis();
    	    	            timerHandler.removeCallbacks(timerUpdateTask);
    	    	            timerHandler.postDelayed(timerUpdateTask, timerInterval);
    	    	            
    	    	            //��������� ������� �����������
    	    	            gameStartTime = System.currentTimeMillis();

    	    	            //��� ������, ��������� ����
    	    	            libThreadHandler.post(new Runnable() {
    	    	        		public void run() {
    	    	                	libraryThreadIsRunning = true;
    	    	        			QSPRestartGame(true);
    	    	                	libraryThreadIsRunning = false;
    	    	        		}
    	    	            });
    	    	            
    	    	            gameIsRunning = true;
    	    	        }
    	    	        else
    	    	        {
    	    	        	String s = "Not able to parse file: "+Integer.toString(QSPGetLastErrorData());
    	    	        	tv.setText(s);
    	    	        }
    				}
    			});
    		}
    	});
    }
    
    private void StopGame(boolean restart)
    {
    	//�������� UI
		if (gameIsRunning)
		{
            //������������� ������
    	    timerHandler.removeCallbacks(timerUpdateTask);

    	    //������������� ������
            CloseFileUI(null);
            
            //��������� ������� ��������
            ListView lvAct = (ListView)findViewById(R.id.acts);
            lvAct.setOnItemClickListener(null);
            lvAct.setOnItemSelectedListener(null);        

            //��������� ������� ���������
            ListView lvInv = (ListView)findViewById(R.id.inv);
            lvInv.setOnItemClickListener(null);
            lvInv.setOnItemSelectedListener(null);
            
            gameIsRunning = false;
		}
		curGameDir = "";
		curGameFile = "";

		//������� ����������
		if (restart || libraryThreadIsRunning)
			return;

		qspInited = false;
        libThreadHandler.post(new Runnable() {
    		public void run() {
            	libraryThreadIsRunning = true;
        		QSPDeInit();
            	libraryThreadIsRunning = false;
    		}
        });
    }
    
    private void PlayFileUI(String file, int volume)
    {
    	//�������� UI
    	if (file == null || file.length() == 0)
    		return;
    	
    	//���������, ������������� �� ��� ���� ����.
    	//���� �������������, ������ �� ������.
    	if (IsPlayingFileUI(file))
    		return;

		//���������, ���������� �� ����.
		//���� ���, ������ �� ������.
		File mediaFile = new File(curGameDir, file);
        if (!mediaFile.exists())
        	return;
    	
    	MediaPlayer mediaPlayer = new MediaPlayer();
	    try {
			mediaPlayer.setDataSource(curGameDir + file);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		final String fileName = file;
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
		        musicLock.lock();
		        try {
			    	for (int i=0; i<mediaPlayersList.size(); i++)
			    	{
			    		MusicContent it = mediaPlayersList.elementAt(i);    		
			    		if (it.path.equals(fileName))
			    		{
			    			mediaPlayersList.remove(it);
			    			break;
			    		}
			    	}
		        } finally {
		        	musicLock.unlock();
		        }
			}
		});
	    mediaPlayer.start();
	    MusicContent musicContent = new MusicContent();
	    musicContent.path = file;
	    musicContent.player = mediaPlayer;
        musicLock.lock();
        try {
        	mediaPlayersList.add(musicContent);
        } finally {
        	musicLock.unlock();
        }
    }

    private boolean IsPlayingFileUI(String file)
    {
    	//�������� UI
    	if (file == null || file.length() == 0)
    		return false;
    	boolean foundPlaying = false; 
        musicLock.lock();
        try {
	    	for (int i=0; i<mediaPlayersList.size(); i++)
	    	{
	    		MusicContent it = mediaPlayersList.elementAt(i);
	    		if (it.path.equals(file))
	    		{
	    			foundPlaying = true;
	    			break;
	    		}
	    	}
        } finally {
        	musicLock.unlock();
        }
    	return foundPlaying;
    }
    
    private void CloseFileUI(String file)
    {
    	//�������� UI
    	//���� ������ ����� ����� ������ null, ������ ��������� ��� �����(CLOSE ALL)
    	boolean bCloseAll = false;
    	if (file == null)
    		bCloseAll = true;
    	else if (file.length() == 0)
    		return;
        musicLock.lock();
        try {
	    	for (int i=0; i<mediaPlayersList.size(); i++)
	    	{
	    		MusicContent it = mediaPlayersList.elementAt(i);    		
	    		if (bCloseAll || it.path.equals(file))
	    		{
	    			if (it.player.isPlaying())
	    				it.player.stop();
	    			it.player.release();
	    			mediaPlayersList.remove(it);
	    			break;
	    		}
	    	}
        } finally {
        	musicLock.unlock();
        }
    }
    
    private void PauseMusic(boolean pause)
    {
    	//�������� UI
    	//pause == true : ����������������
    	//pause == false : ���������
        musicLock.lock();
        try {
	    	for (int i=0; i<mediaPlayersList.size(); i++)
	    	{
	    		MusicContent it = mediaPlayersList.elementAt(i);    		
    			if (pause)
    			{
    				if (it.player.isPlaying())
    					it.player.pause();
    			}
    			else
    				it.player.start();
	    	}
	    } finally {
	    	musicLock.unlock();
	    }
    }
    
    //******************************************************************************
    //******************************************************************************
    //****** / QSP  LIBRARY  REQUIRED  CALLBACKS \ *********************************
    //******************************************************************************
    //******************************************************************************
    private void RefreshInt() 
    {
    	//�������� ����������
    	JniResult htmlResult = (JniResult) QSPGetVarValues("USEHTML", 0);
    	final boolean html = htmlResult.success && (htmlResult.int1 == 1);
    	
    	
    	//�������� ��������
    	if (QSPIsMainDescChanged())
    	{
			final String txtMainDesc = QSPGetMainDesc();
			runOnUiThread(new Runnable() {
				public void run() {
					TextView tvDesc = (TextView) findViewById(R.id.main_desc);
					if (html)
					{
						tvDesc.setText(Utility.QspStrToHtml(txtMainDesc, imgGetter));
						tvDesc.setMovementMethod(QspLinkMovementMethod.getInstance());
					}
					else
						tvDesc.setText(Utility.QspStrToStr(txtMainDesc));
				}
			});
    	}
    
    	//������ ��������
    	if (QSPIsActionsChanged())
    	{
	        int nActsCount = QSPGetActionsCount();
			if (html)
			{
		        final Spanned []acts = new Spanned[nActsCount];
		        for (int i=0;i<nActsCount;i++)
		        {
		        	JniResult actsResult = (JniResult) QSPGetActionData(i);
		        	acts[i] = Utility.QspStrToHtml(actsResult.str1, imgGetter);
		        }
				runOnUiThread(new Runnable() {
					public void run() {
				        ListView lvAct = (ListView)findViewById(R.id.acts);
				        lvAct.setAdapter(new ArrayAdapter<Spanned>(uiContext, R.layout.act_item, acts));
				        //������������� ������ ��������
				        Utility.setListViewHeightBasedOnChildren(lvAct);
					}
				});
			}
			else
			{
				final String []acts = new String[nActsCount];
		        for (int i=0;i<nActsCount;i++)
		        {
		        	JniResult actsResult = (JniResult) QSPGetActionData(i);
		        	acts[i] = actsResult.str1;
		        }
				runOnUiThread(new Runnable() {
					public void run() {
				        ListView lvAct = (ListView)findViewById(R.id.acts);
				        lvAct.setAdapter(new ArrayAdapter<String>(uiContext, R.layout.act_item, acts));
				        //������������� ������ ��������
				        Utility.setListViewHeightBasedOnChildren(lvAct);
					}
				});
			}
    	}
        
        //���������
    	if (QSPIsObjectsChanged())
    	{
			runOnUiThread(new Runnable() {
				public void run() {
					if(currentWin!=WIN_INV){
						invUnread = true;
						updateTitle();
					}
				}
			});
	        int nObjsCount = QSPGetObjectsCount();
			if (html)
			{
		        final Spanned []objs = new Spanned[nObjsCount];
		        for (int i=0;i<nObjsCount;i++)
		        {
		        	JniResult objsResult = (JniResult) QSPGetObjectData(i);
		        	objs[i] = Utility.QspStrToHtml(objsResult.str1, imgGetter);
		        }
				runOnUiThread(new Runnable() {
					public void run() {
				        ListView lvInv = (ListView)findViewById(R.id.inv);
				        lvInv.setAdapter(new ArrayAdapter<Spanned>(uiContext, R.layout.obj_item, objs));
					}
				});
			}
			else
			{
				final String []objs = new String[nObjsCount];
		        for (int i=0;i<nObjsCount;i++)
		        {
		        	JniResult objsResult = (JniResult) QSPGetObjectData(i);
		        	objs[i] = objsResult.str1;
		        }
				runOnUiThread(new Runnable() {
					public void run() {
				        ListView lvInv = (ListView)findViewById(R.id.inv);
				        lvInv.setAdapter(new ArrayAdapter<String>(uiContext, R.layout.obj_item, objs));
					}
				});
			}
    	}
        
        //���. ��������
    	if (QSPIsVarsDescChanged())
    	{
			final String txtVarsDesc = QSPGetVarsDesc();			
			runOnUiThread(new Runnable() {
				public void run() {
					if(currentWin!=WIN_EXT) {
						varUnread = true;
						updateTitle();
					}
					TextView tvVarsDesc = (TextView) findViewById(R.id.vars_desc);
					if (html)
					{
						tvVarsDesc.setText(Utility.QspStrToHtml(txtVarsDesc, imgGetter));
						tvVarsDesc.setMovementMethod(QspLinkMovementMethod.getInstance());
					}
					else
						tvVarsDesc.setText(txtVarsDesc);
				}
			});
    	}
    }
    
    private void SetTimer(int msecs)
    {
    	//�������� ����������
    	final int timeMsecs = msecs;
		runOnUiThread(new Runnable() {
			public void run() {
				timerInterval = timeMsecs;
			}
		});
    }

    private void ShowMessage(String message)
    {
    	//�������� ����������
		if (libThread==null)
		{
			Utility.WriteLog("ShowMessage: failed, libThread is null");
			return;
		}

		String msgValue = "";
		if (message != null)
			msgValue = message;
		
		dialogHasResult = false;

    	final String msg = msgValue;
		runOnUiThread(new Runnable() {
			public void run() {
		    	new AlertDialog.Builder(uiContext)
		        .setMessage(msg)
		        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialogHasResult = true;
						Utility.WriteLog("ShowMessage(UI): OK clicked, unparking library thread");
		            	setThreadUnpark();
		            }
		        })
		        .setCancelable(false)
		        .show();
				Utility.WriteLog("ShowMessage(UI): dialog showed");
			}
		});
    	
		Utility.WriteLog("ShowMessage: parking library thread");
        while (!dialogHasResult) {
        	setThreadPark();
        }
        parkThread = null;
		Utility.WriteLog("ShowMessage: library thread unparked, finishing");
    }
    
    private void PlayFile(String file, int volume)
    {
    	//�������� ����������
    	final String musicFile = file;
    	final int musicVolume = volume;
    	runOnUiThread(new Runnable() {
    		public void run() {
    			PlayFileUI(musicFile, musicVolume);
    		}
    	});
    }
    
    private boolean IsPlayingFile(String file)
    {
    	//�������� ����������
    	return IsPlayingFileUI(file);
    }

    private void CloseFile(String file)
    {
    	//�������� ����������
    	final String musicFile = file;
    	runOnUiThread(new Runnable() {
    		public void run() {
    			CloseFileUI(musicFile);
    		}
    	});
    }
    
    private void ShowPicture(String file)
    {
    	//�������� ����������
    	if (file == null || file.length() == 0)
    		return;
    	
    	final String fileName = file;
    	
		runOnUiThread(new Runnable() {
			public void run() {
		    	String prefix = "";
		    	if (curGameDir != null)
		    		prefix = curGameDir;
		    	
		    	//���������, ���������� �� ����.
		    	//���� ��� - �������
		    	File gfxFile = new File(prefix.concat(fileName));
		        if (!gfxFile.exists())
		        	return;
		        
		        waitForImageBox = true;
		
		    	Intent imageboxIntent = new Intent();
		    	imageboxIntent.setClassName("com.qsp.player", "com.qsp.player.QspImageBox");
		    	Bundle b = new Bundle();
		    	b.putString("imageboxFile", prefix.concat(fileName));
		    	imageboxIntent.putExtras(b);
		    	startActivity(imageboxIntent);
			}
		});    	    	
    }
    
    private String InputBox(String prompt)
    {
    	//�������� ����������
		if (libThread==null)
		{
			Utility.WriteLog("InputBox: failed, libThread is null");
			return "";
		}
    	
		String promptValue = "";
		if (prompt != null)
			promptValue = prompt;
		
		dialogHasResult = false;

    	final String inputboxTitle = promptValue;
    	
		runOnUiThread(new Runnable() {
			public void run() {
				inputboxResult = "";
			    inputboxDialog.setTitle(inputboxTitle);
			    inputboxDialog.show();
				Utility.WriteLog("InputBox(UI): dialog showed");
			}
		});
    	
		Utility.WriteLog("InputBox: parking library thread");
        while (!dialogHasResult) {
        	setThreadPark();
        }
        parkThread = null;
		Utility.WriteLog("InputBox: library thread unparked, finishing");
    	return inputboxResult;
    }
    
    private int GetMSCount()
    {
    	//�������� ����������
    	return (int) (System.currentTimeMillis() - gameStartTime);
    }
    
    private void AddMenuItem(String name, String imgPath)
    {
    	//�������� ����������
    	QspMenuItem item = new QspMenuItem();
    	item.imgPath = imgPath;
    	item.name = name;
    	menuList.add(item);
    }
    
    private void ShowMenu()
    {
    	//�������� ����������
		if (libThread==null)
		{
			Utility.WriteLog("ShowMenu: failed, libThread is null");
			return;
		}
    	
		dialogHasResult = false;
		menuResult = -1;

		int total = menuList.size();
        final CharSequence[] items = new String[total];
        for (int i=0; i<total; i++)
        {
        	items[i] = menuList.elementAt(i).name;
        }
    	
		runOnUiThread(new Runnable() {
			public void run() {
		        new AlertDialog.Builder(uiContext)
		        .setItems(items, new DialogInterface.OnClickListener()
		        {
		    		@Override
		    		public void onClick(DialogInterface dialog, int which) 
		    		{
		               	menuResult = which;
		    			dialogHasResult = true;
		    			Utility.WriteLog("ShowMenu(UI): menu item selected, unparking library thread");
		               	setThreadUnpark();
		    		}
		        })
		        .setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialogHasResult = true;
						Utility.WriteLog("ShowMenu(UI): menu cancelled, unparking library thread");
			           	setThreadUnpark();
					}
				})
				.show();
			    Utility.WriteLog("ShowMenu(UI): dialog showed");
			}
		});
    	
		Utility.WriteLog("ShowMenu: parking library thread");
        while (!dialogHasResult) {
        	setThreadPark();
        }
        parkThread = null;
		Utility.WriteLog("ShowMenu: library thread unparked, finishing");
    	
		if (menuResult != -1)
			QSPSelectMenuItem(menuResult);
    }
    
    private void DeleteMenu()
    {
    	//�������� ����������
    	menuList.clear();
    }
    //******************************************************************************
    //******************************************************************************
    //****** \ QSP  LIBRARY  REQUIRED  CALLBACKS / *********************************
    //******************************************************************************
    //******************************************************************************
    
    
    public void OnUrlClicked (String href)
    {
    	//�������� UI
    	String tag = href.substring(0, 5).toLowerCase();
    	if (tag.equals("exec:"))
    	{
    		if (libraryThreadIsRunning)
    			return;
    		final String code = href.substring(5);
    		libThreadHandler.post(new Runnable() {
    			public void run() {
    	    		if (libraryThreadIsRunning)
    	    			return;
                	libraryThreadIsRunning = true;
                	
        	    	boolean bExec = QSPExecString(code, true);
        	    	if (!bExec)
        	    	{
        	    		int nError = QSPGetLastErrorData();
        	    		final String txtError = "Error: "+String.valueOf(nError);  
        	    		runOnUiThread(new Runnable() {
        	    			public void run() {
		        	    		new AlertDialog.Builder(uiContext)
		        	            .setMessage(txtError)
		        	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        	                public void onClick(DialogInterface dialog, int whichButton) { }
		        	            })
		        	            .show();
        	    			}
        	    		});
        	    	}
                	
            		libraryThreadIsRunning = false;
    			}
    		});
    	}
    }

	//Callback for click on selected act
    private OnItemClickListener actListClickListener = new OnItemClickListener() 
    {
    	//�������� UI
    	@Override
    	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) 
    	{
    		if (libraryThreadIsRunning)
    			return;
    		final int actionIndex = position;
    		libThreadHandler.post(new Runnable() {
    			public void run() {
    	    		if (libraryThreadIsRunning)
    	    			return;
                	libraryThreadIsRunning = true;
            		QSPSetSelActionIndex(actionIndex, false);
            		QSPExecuteSelActionCode(true);
            		libraryThreadIsRunning = false;
    			}
    		});
    	}
    };
    
    //Callback for select act
    private OnItemSelectedListener actListSelectedListener = new OnItemSelectedListener() 
    {
    	//�������� UI
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
    		if (libraryThreadIsRunning)
    			return;
    		final int actionIndex = arg2;
    		libThreadHandler.post(new Runnable() {
    			public void run() {
    	    		if (libraryThreadIsRunning)
    	    			return;
                	libraryThreadIsRunning = true;
    				QSPSetSelActionIndex(actionIndex, true);
            		libraryThreadIsRunning = false;
    			}
    		});
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
		}
    };
    
    //Callback for click on selected object
    private OnItemClickListener objListClickListener = new OnItemClickListener() 
    {
    	//�������� UI
    	@Override
    	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) 
    	{
    		if (libraryThreadIsRunning)
    			return;
    		final int itemIndex = position;
    		libThreadHandler.post(new Runnable() {
    			public void run() {
    	    		if (libraryThreadIsRunning)
    	    			return;
                	libraryThreadIsRunning = true;
            		QSPSetSelObjectIndex(itemIndex, true);
            		libraryThreadIsRunning = false;
    			}
    		});
    	}
    };
    
    //Callback for select object
    private OnItemSelectedListener objListSelectedListener = new OnItemSelectedListener() 
    {
    	//�������� UI
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
    		if (libraryThreadIsRunning)
    			return;
    		final int itemIndex = arg2;
    		libThreadHandler.post(new Runnable() {
    			public void run() {
    	    		if (libraryThreadIsRunning)
    	    			return;
                	libraryThreadIsRunning = true;
            		QSPSetSelObjectIndex(itemIndex, true);
            		libraryThreadIsRunning = false;
    			}
    		});
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
		}
    };
    
    //��� ����������� �������� � HTML
    static QspImageGetter imgGetter = new QspImageGetter();
    
    //������� ��� UI-������
    final Handler uiThreadHandler = new Handler();

    //������� ��� ������ ����������
    private Handler libThreadHandler;
    
    //����� ����������
    private Thread libThread;
    private Thread parkThread;
    
    //������� �� ����� ����������
    boolean					libraryThreadIsRunning = false;
 
    //���� ����� �� MessageBox, InputBox ���� Menu
    private boolean 		dialogHasResult;
    String					inputboxResult;
    int						menuResult;
    AlertDialog				inputboxDialog;
    
    String 					curGameDir;
    String					curGameFile;
    Vector<MusicContent>	mediaPlayersList;
    Handler					timerHandler;
	long					timerStartTime;
	long					gameStartTime;
	int						timerInterval;
	boolean					gameIsRunning;
	boolean					qspInited;
	boolean					waitForImageBox;
	Vector<QspMenuItem>		menuList;
	
    
    QspLinkMovementMethod 	qspLinkMovementMethod; 
    
    
    //control
    public native void 		QSPInit();
    public native void 		QSPDeInit();
    public native boolean 	QSPIsInCallBack();
    public native void 		QSPEnableDebugMode(boolean isDebug);
    public native Object 	QSPGetCurStateData();//!!!STUB
    public native String 	QSPGetVersion();
    public native int 		QSPGetFullRefreshCount();
    public native String 	QSPGetQstFullPath();
    public native String 	QSPGetCurLoc();
    public native String 	QSPGetMainDesc();
    public native boolean 	QSPIsMainDescChanged();
    public native String 	QSPGetVarsDesc();
    public native boolean 	QSPIsVarsDescChanged();
    public native Object 	QSPGetExprValue();//!!!STUB
    public native void 		QSPSetInputStrText(String val);
    public native int 		QSPGetActionsCount();
    public native Object 	QSPGetActionData(int ind);//!!!STUB
    public native boolean 	QSPExecuteSelActionCode(boolean isRefresh);
    public native boolean 	QSPSetSelActionIndex(int ind, boolean isRefresh);
    public native int 		QSPGetSelActionIndex();
    public native boolean 	QSPIsActionsChanged();
    public native int 		QSPGetObjectsCount();
    public native Object 	QSPGetObjectData(int ind);//!!!STUB
    public native boolean 	QSPSetSelObjectIndex(int ind, boolean isRefresh);
    public native int 		QSPGetSelObjectIndex();
    public native boolean 	QSPIsObjectsChanged();
    public native void 		QSPShowWindow(int type, boolean isShow);
    public native Object 	QSPGetVarValuesCount(String name);
    public native Object 	QSPGetVarValues(String name, int ind);//!!!STUB
    public native int 		QSPGetMaxVarsCount();
    public native Object 	QSPGetVarNameByIndex(int index);//!!!STUB
    public native boolean 	QSPExecString(String s, boolean isRefresh);
    public native boolean 	QSPExecLocationCode(String name, boolean isRefresh);
    public native boolean 	QSPExecCounter(boolean isRefresh);
    public native boolean 	QSPExecUserInput(boolean isRefresh);
    public native int 		QSPGetLastErrorData();//!!!STUB
    public native String 	QSPGetErrorDesc(int errorNum);
    public native boolean 	QSPLoadGameWorld(String fileName);
    public native boolean 	QSPLoadGameWorldFromData(byte data[], int dataSize, String fileName);
    public native boolean 	QSPSaveGame(String fileName, boolean isRefresh);
    public native Object 	QSPSaveGameAsData(boolean isRefresh);//!!!STUB
    public native boolean 	QSPOpenSavedGame(String fileName, boolean isRefresh);
    public native Object 	QSPOpenSavedGameFromData(String str, boolean isRefresh);//!!!STUB
    public native boolean 	QSPRestartGame(boolean isRefresh);
    public native void		QSPSelectMenuItem(int index); 
    //public native void QSPSetCallBack(int type, QSP_CALLBACK func) 

    static {
	    System.loadLibrary("ndkqsp");
	}
}