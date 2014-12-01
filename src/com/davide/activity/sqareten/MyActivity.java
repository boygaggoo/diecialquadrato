package com.davide.activity.sqareten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyActivity extends Activity  {
    
	private static ShareActionProvider myShareActionProvider ;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.layout.action_bar_share_menu, menu);
	    MenuItem item = menu.findItem(R.id.menu_item_share);
	    myShareActionProvider = (ShareActionProvider) item.getActionProvider();
	    Intent myIntent = new Intent();
	    myIntent.setAction(Intent.ACTION_SEND);
	    myIntent.putExtra(Intent.EXTRA_TEXT, "#sq10 i scored "+mybest+" on SquareTen! Try to beat me http://jo.my/sq10");
	    myIntent.setType("text/plain");
	    myShareActionProvider.setShareIntent(myIntent);
	    
	    item.getActionView().setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
	        	Intent myIntent = new Intent();
	    	    myIntent.setAction(Intent.ACTION_SEND);
	    	    myIntent.putExtra(Intent.EXTRA_TEXT, "#sq10 i scored "+actualn+" on SquareTen! Try to beat me http://jo.my/sq10");
	    	    myIntent.setType("text/plain");
	    	    myShareActionProvider.setShareIntent(myIntent);
	    	}
	    });
	    return true;
	    
	}
	

	/*
	 * APP SETTINGS
	 */
	public static final String MyStatus = "MyStatus" ;
    public static final String myBest = "myBest";
    public static final String myWorst = "myWorst";
    public static final String myBestMoves = "myBestMoves";
    public static final String myWorstMoves = "myWorstMoves";

    public int previousID = -1;
    public int beforeID = -1;
    public int actualn = 0;
    public int moves = 0;
    public int mybest = 0;
    public String history = "";
    public static ArrayList<Integer> values ;

    GridView gridview ;
    CellAdapter cellAdapter;

    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private int dx, dy, pdown;
    
    /**
     * Called when the activity is first created.
     */

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        System.out.println("ONCREATE");
        setContentView(R.layout.main);
        values = new ArrayList<Integer>(100);
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                values.add(Integer.parseInt("1" + r + "" + c));
            }
        }
        gridview = (GridView) findViewById(R.id.gridView);
        cellAdapter = new CellAdapter(this, R.layout.gridcell, values);
        gridview.setAdapter(cellAdapter);
        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                switch(action) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        downY = event.getY();
                        dx = 0;
                        dy = 0;
                        pdown = gridview.pointToPosition((int) event.getX(), (int) event.getY());
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                            upX = event.getX();
                            upY = event.getY();
                            float deltaX = downX - upX;
                            float deltaY = downY - upY;
                            if (Math.abs(deltaX) > MIN_DISTANCE) {
                                if (deltaX < 0) { dx = +1; }
                                if (deltaX > 0) { dx = -1; }
                            }
                            if (Math.abs(deltaY) > MIN_DISTANCE) {
                                if (deltaY < 0) { dy = -1; }
                                if (deltaY > 0) { dy = 1; }
                            }
                            if (dx != 0 || dy != 0) {
                                if(previousID > -1) {
                                    Mossa m = Mossa.load(dx, dy);
                                    String str1RC = String.valueOf(previousID);
                                    int y = Integer.parseInt(str1RC.substring(1, 2)) + m.getSkipy();
                                    int x = Integer.parseInt(str1RC.substring(2, 3)) + m.getSkipx();
                                    if (x > -1 && x < 10 && y > -1 && y < 10) {
                                        int n = y * 10 + x + 100;
                                        next(findViewById(n));
                                    }
                                }
                            } else if (dx == 0 && dy == 0) {
                                int pu = gridview.pointToPosition((int) event.getX(), (int) event.getY());
                                if (pu > -1 && pu < 100) {
                                    next(findViewById(values.get(pu)));
                                }
                            }
                        return true;
                    }
                }
                return false;
                }
        });
        loadScoreboard();
        
    }
       
    public void loadScoreboard(){
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyStatus, Context.MODE_PRIVATE);
        TextView best = (TextView)findViewById(R.id.highestscore);
        TextView bestMoves = (TextView)findViewById(R.id.highestmoves);
        TextView worst = (TextView)findViewById(R.id.lowestscore);
        TextView worstMoves = (TextView)findViewById(R.id.lowestmoves);
        best.setText("#"+sharedpreferences.getInt(myBest,0));
        mybest = sharedpreferences.getInt(myBest,0);
        bestMoves.setText("#"+sharedpreferences.getInt(myBestMoves,0));
        worst.setText("#"+sharedpreferences.getInt(myWorst,0));
        worstMoves.setText("#"+sharedpreferences.getInt(myWorstMoves,100));
    }

    public void updateScoreboard(boolean uw){

        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyStatus, Context.MODE_PRIVATE);
        TextView best = (TextView)findViewById(R.id.highestscore);
        TextView bestMoves = (TextView)findViewById(R.id.highestmoves);
        TextView worst = (TextView)findViewById(R.id.lowestscore);
        TextView worstMoves = (TextView)findViewById(R.id.lowestmoves);

        // BEST SCORE & MOVES
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(actualn == sharedpreferences.getInt(myBest,0) ) {
            if(uw && ( sharedpreferences.getInt(myBestMoves,0) >= moves || sharedpreferences.getInt(myBestMoves,0) == 0) ) {
                editor.putInt(myBestMoves, moves);
                bestMoves.setText("#" + moves);
            }
        }else if(actualn > sharedpreferences.getInt(myBest,0)){
            editor.putInt(myBest, actualn);
            best.setText("#"+actualn);
            editor.putInt(myBestMoves, moves);
            bestMoves.setText("#" + moves);
        }

        if(uw) {
            if ( actualn < sharedpreferences.getInt(myWorst, 100) ) {
                editor.putInt(myWorst, actualn);
                worst.setText("#" + actualn);
                editor.putInt(myWorstMoves, moves);
                worstMoves.setText("#" + moves);

            } else if (actualn == sharedpreferences.getInt(myWorst, 100) ) {
                if (sharedpreferences.getInt(myWorstMoves, 0) > moves || sharedpreferences.getInt(myBestMoves, 0) == 0) {
                    editor.putInt(myWorstMoves, moves);
                    worstMoves.setText("#" + moves);
                }
            }
        }
        editor.commit();

    }

    public void reset(View view){
        actualn = 0;
        moves = 0;
        previousID = -1;
        beforeID = -1;
        history = "";
        gridview.setAdapter(cellAdapter);
        /*TextView move = (TextView)findViewById(R.id.scoremoves);
        move.setText("#");
        TextView score = (TextView)findViewById(R.id.scoren);
        score.setText("#");*/
        loadScoreboard();
    }

    public void help(View view){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("goal of the game")
                .setMessage("Challenge yourself trying to fill the square with numbers from 1 up to 100. " +
                        "Skip 1 square moving slantwise or skip 2 squares moving straight!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void next(final View view) {
        final TextView button = (TextView) view;
        int rc = button.getId();
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyStatus, Context.MODE_PRIVATE);
        if(previousID > -1){
            if(possible(beforeID, previousID, rc)){
                possibilities(previousID, false);
                setUpNext(button, previousID, rc, sharedpreferences);
                if(!possibilities(rc, true)){
                    if(actualn < 100) {
                        updateScoreboard(true);
                        new AlertDialog.Builder(this)
                                .setTitle("Game Over")
                                .setMessage("You reached " + actualn + " !! ")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
									public void onClick(DialogInterface dialog, int which) {
                                        reset(null);
                                    }
                                })
                                .setNegativeButton(R.string.backtoplay, new DialogInterface.OnClickListener() {
                                    @Override
									public void onClick(DialogInterface dialog, int which) {
                                    	back(null);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_lock_lock)
                                .show();
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("!!!!!!!!! 100  !!!!!!!!")
                                .setMessage("You win !! ")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
									public void onClick(DialogInterface dialog, int which) {
                                        reset(null);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_lock_power_off)
                                .show();
                    }
                }
            }
        }else{
            possibilities(rc, true);
            setUpNext(button, previousID, rc, sharedpreferences);
        }
    }

    public boolean possibilities(int rc, boolean pressed){
        int yy = (rc-100) / 10 ;
        int xx = (rc-100) % 10;
        boolean res = false;
        for(Mossa m: Mossa.values()){
            int x = xx + m.getSkipx();
            int y = yy + m.getSkipy();
            if( x < 10 && x > -1 && y<10 && y > -1){
                int resID = 100 + x + y*10;
                TextView b = (TextView)findViewById(resID);
                if(b.getText() == null || b.getText().toString().equals("")) {
                    res = true;
                    b.setPressed(pressed);
                }
            }
        }
        return res;
    }

    public void setUpNext(TextView button, int prev, int rc, SharedPreferences sharedpreferences) {
        actualn++;
        moves++;
        /*TextView move = (TextView)findViewById(R.id.scoremoves);
        move.setText("#"+moves);
        TextView score = (TextView)findViewById(R.id.scoren);
        score.setText("#"+actualn);*/
        if(previousID > -1){
            TextView spegnimi = (TextView)findViewById(previousID);
            spegnimi.setPressed(false);
            spegnimi.setTextColor(Color.parseColor("#A9A9A9"));
        }
        if(sharedpreferences.getInt(myBest,0) <= actualn) {
            updateScoreboard(false);
        }
        previousID = rc;
        beforeID = prev;
        
        button.setText(String.valueOf(actualn));
        button.setSelected(true);
        button.setTextColor(Color.BLACK);
        addStep(rc);
    
    }

    public void back(View view){
        String strSteps = history;
        String[] steps = strSteps.split(":");
        if(steps.length > 4) {
            moves++;
            /*TextView move = (TextView)findViewById(R.id.scoremoves);
            move.setText("#"+moves);*/
            updateScoreboard(false);
            actualn = steps.length - 2 ;
            possibilities(previousID, false);
            
            TextView scelto = (TextView)findViewById(previousID);
            scelto.setPressed(false);
            scelto.setSelected(false);
            scelto.setText("");
            
            
            previousID = Integer.parseInt(steps[  steps.length - 2 ]);
            possibilities(previousID, true);
            
            TextView back = (TextView)findViewById(previousID);
            back.setSelected(true);
            back.setPressed(false);
            back.setTextColor(Color.BLACK);
            
            beforeID = Integer.parseInt(steps[  steps.length - 3 ]);
            history = strSteps.substring(0, strSteps.lastIndexOf(":"));
        }
    }

    public void addStep(int id){
        history += ":" + String.valueOf(id) ;
    }

    public boolean possible(int back, int previous, int rc){
        if(previous == rc || back == rc) {
            return false;
        }
        int yy = (previous-100) / 10 ;
        int xx = (previous-100) % 10;
        for(Mossa m: Mossa.values()){
            int x = xx + m.getSkipx();
            int y = yy + m.getSkipy();
            if( x < 10 && x > -1 && y<10 && y > -1){
                int resID = 100 + y*10 + x;
                if(resID == rc){
                    TextView scelto = (TextView)findViewById(resID);
                    if(scelto.getText() != null && !scelto.getText().toString().equals("")) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
}