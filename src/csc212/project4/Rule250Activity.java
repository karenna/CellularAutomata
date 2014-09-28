package csc212.project4;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//COMMENTED OUT FOR PRODUCTION: import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.view.ViewGroup.LayoutParams;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class Rule250Activity extends Activity {

	private Button btnMain;
	private Button btnOverview;
	private Button btnRaw;
	private Button btnNextRows;
	private Button btnPriorRows;
	private GraphView gvwCA250;
	private LinearLayout lyoRendered;			// rendered landscape--contains the GraphView
	private ProgressDialog CArawDataDialog;
	private int numIterations;
	private int numBundles;
	private int currentBundle;			// the bundle of raw data currently to be displayed
	private int requestedBundle;		// the bundle of raw data that has been requested and needs to be processed next
	private Boolean firstBundle = true;
	private Boolean blnPortrait = true;
	final int ITERATION_SIZE = 101; 	// For the time being, the width of the cellular automata is always fixed at 101 cells.
	final int BUNDLE_SIZE = 20;			// For the time being, the number of iterations--i.e., number of CA rows we'll bundle together and pass via an extra--is 20
	final Context context = this;
	private int screenWidth;

	// Store a collection of Cellular Automata cell objects (in an ArrayList) to comprise the full CA. Each cell is
	// comprised of a row, a column, and a color. The collection will be empty until filled by the setCellColor method.

	private ArrayList<CellularAutomataCell> ca250 = new ArrayList<CellularAutomataCell>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "OnCreate");
		super.onCreate(savedInstanceState);
	
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			//setContentView(R.layout.rule250_layout);
			blnPortrait = true;
		} else {
			//setContentView(R.layout.rule250_layout_landscape);
			blnPortrait = false;
		}

		setContentView(R.layout.rule250_layout);

		// Obtain and save the ID's (handles) for the views (components) on the Rule250 layout that will require listeners.

		btnMain 	 = (Button)findViewById(R.id.btn_return_to_main_from_rule250);
		btnOverview  = (Button)findViewById(R.id.btn_overview_from_rule250);
		btnRaw 		 = (Button)findViewById(R.id.btn_display_raw_data_from_rule250);
		btnNextRows  = (Button)findViewById(R.id.btn_next_rows);
		btnPriorRows = (Button)findViewById(R.id.btn_prior_rows);

		if (blnPortrait) {
			gvwCA250 	 = (GraphView)findViewById(R.id.grphvw_rule250);
			lyoRendered      = (LinearLayout)findViewById(R.id.linlyo_rule250_CArendered);
			gvwCA250.setLyoRendered(lyoRendered);
		} else {
			gvwCA250 	 = (GraphView)findViewById(R.id.grphvw_rule250Landscape);
			lyoRendered      = (LinearLayout)findViewById(R.id.linlyo_rule250_RightColumn);
			gvwCA250.setLyoRendered(lyoRendered);
		}

		// Default the btnNextRows and btnPriorRows labels to blank until some raw data is displayed; thereafter will display
		// "Next Group" when there is 1 or more remaining bundles-worth of rows of raw data to display and/or
		// "Prior Group" when there is 1 or more previous bundles-worth of rows of raw data to display.

		btnNextRows.setEnabled(false);
		btnNextRows.setTextColor(getResources().getColor(R.color.opaque_black));
		btnPriorRows.setEnabled(false);
		btnPriorRows.setTextColor(getResources().getColor(R.color.opaque_black));

		// Will always display only 20 raws of raw data at a time and this will be passed as a bundle. So if have 40 iterations,
		// will have a bundle 1 and a bundle 2 to conceivably display (will always start by displaying raw data from the first
		// bundle).

		currentBundle   = 1;
		requestedBundle = 1;

	    // There are two things that have to be sized: the GraphView and the bitmap upon which the graph will be painted. 
	    // Here deal with the canvas by obtaining the size of the device's screen width and setting the
	    // GraphView's screenWidth variable to this value (the canvas size will actually be established by
	    // its onMeasure() method.

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "screenWidth (pixels) determined by the Window Manager's getMetrics method = " + screenWidth);

		// Want to change some of the layout parameters for the existing GraphView on the layout for this activity.

		// The number of iterations selected on the Main activity screen was passed as an "extra" with
		// the intent to start this Rule250 activity.

		String key = "iterations";
		numIterations = getIntent().getExtras().getInt(key);
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Number of iterations selected by the user: " + numIterations);

		numBundles = numIterations/BUNDLE_SIZE;			// will be an integer, so number of full bundles
		// If there is a remainder, then need another bundle, i.e., if have 1 to BUNDLE_SIZE-1 more rows, will need another bundle.
		if (numIterations % BUNDLE_SIZE > 0) {numBundles = numBundles + 1;}


		gvwCA250.setNumberIterations(numIterations);	// in essence, pass the value for the chosen number of iterations to the graphview

		// Initialize (clear) the collection of Cellular Automata Cells.

		ca250.clear();

  	    // The ca250 collection of CA cells is empty but will be filled by the setCellColor method (and the filled array will be
	    // returned).

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to call setCellColor from onCreate to fill ca250.");
		setCellColor(ca250, numIterations);
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "collection ca250s size after filling is: " + ca250.size());

		// Now update the GraphView so it can display the data stored in the ca250 collection.  
				
		Update();

	    // Establish the listener (nested static interface) for the button that provides background info for Cellular
		// Automata (CA) and how to use this app.
			
		btnOverview.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "inside btnOverview.setOnClickListener/onclick method");

				// Start the activity that provides background info for CA and how to use this app.
				// Pass info to be utilized by the Overview activity to display an appropriate label on the "return" button.

				Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
				startActIntnt_Overview.putExtra("rtnActivity", 5); // 5 will indicate got there from Rule250Activity
				startActivity(startActIntnt_Overview);

			}

		});

		// Establish the listener (nested static interface) for the button that returns to the main display.
			
		btnMain.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "inside btnMain.setOnClickListener/onclick method");

				// Finish this activity; Main Activity should be left on the stack, so will return automatically to it.
				
				finish(); 

			}

		});

		btnRaw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "inside btnRaw.setOnClickListener/onclick method");

				prepRawDataBundles();
				firstBundle = false;

			}

		});

	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onRestoreInstanceState");
		
		// Restore PARENT state.

		super.onRestoreInstanceState(savedInstanceState);

		// Get what was saved out of the bundle made to save the state in order to restore the state
		// (provided the state was saved off).

		if(savedInstanceState != null)
		{

			unbundleCells(savedInstanceState);

			// blnFirstBundle is critical to the enabling/disabling of btnRaw, btnNextRows, and btnPriorRows. Get the
			// state of its value prior to when onSavedInstanceState was called (like when the screen was rotated) and
			// re-establish these values. Also need to get prior state of the variable requestedBundle.

			numIterations   = savedInstanceState.getInt(getPackageName() + ".numIterations");
			firstBundle     = savedInstanceState.getBoolean(getPackageName() + ".firstBundle");
			numBundles      = savedInstanceState.getInt(getPackageName() + ".numBundles");
			currentBundle   = savedInstanceState.getInt(getPackageName() + ".currentBundle");
			requestedBundle = savedInstanceState.getInt(getPackageName() + ".requestedBundle");

		}
	}
   
	@Override
	protected void onStart() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStart");

		super.onStart();
	}

	@Override
	protected void onRestart() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onRestart");

		super.onRestart();

	}

	@Override
	protected void onResume()
	{

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onResume");

		if(CArawDataDialog != null) {CArawDataDialog.cancel();}

		if (!firstBundle)
		{
			if (numBundles > 1)
			{
				btnRaw.setEnabled(false);
				btnRaw.setTextColor(getResources().getColor(R.color.opaque_black));

				if (currentBundle < numBundles)		// there is at least one more bundle-worth of raw data
				{
					btnNextRows.setEnabled(true);
					btnNextRows.setTextColor(getResources().getColor(R.color.opaque_dark_cornflwrblue));
				} else						// already displaying the last bundle of raw data (should never be < 0 though)
				{
					btnNextRows.setEnabled(false);
					btnNextRows.setTextColor(getResources().getColor(R.color.opaque_black));	
				}

				if (currentBundle > 1)		// there is at least one prior bundle-worth of raw data
				{
					btnPriorRows.setEnabled(true);
					btnPriorRows.setTextColor(getResources().getColor(R.color.opaque_dark_cornflwrblue));
				} else						// already displaying the last bundle of raw data (should never be < 0 though)
				{
					btnPriorRows.setEnabled(false);
					btnPriorRows.setTextColor(getResources().getColor(R.color.opaque_black));
				}
	
			} else {
				btnRaw.setEnabled(true);
				btnRaw.setTextColor(getResources().getColor(R.color.opaque_dark_cornflwrblue));
			}
		} else {
			btnRaw.setEnabled(true);
			btnRaw.setTextColor(getResources().getColor(R.color.opaque_dark_cornflwrblue));
		}
	
		// Will only display one bundle-worth of data at a time (takes too long otherwise and can exceed the
		// Display_Raw_DataActivity's adapter's size limitations otherwise).
		// Establish the listener (nested static interface) for the button that results in the prior bundle
		// of data being displayed (provided a prior bundle had been displayed).	 
		
		btnPriorRows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside btnPriorRows.setOnClickListener");

      		    requestedBundle = requestedBundle - 1;
      		    currentBundle   = requestedBundle;

				prepRawDataBundles();
			}

		});

		// Will only display one bundle-worth of data at a time (takes too long otherwise and can exceed the
		// Display_Raw_DataActivity's adapter's size limitations otherwise).
		// Establish the listener (nested static interface) for the button that results in the next bundle
		// of data being displayed (provided there are more bundles).	 
		
		btnNextRows.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside btnNextRows.setOnClickListener");
				
  		    	requestedBundle = requestedBundle + 1;
      		    currentBundle   = requestedBundle;

				prepRawDataBundles();
			}
			
		});

		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onSaveInstanceState");

		// Package up a bundle with the info that is to be persisted (passed into this method as the bundle outState).
		
		//Start with the array containing all the CA data for the graph.

		bundleCells(outState, 0, ca250.size() - 1);

		// Also put the value of firstbundle, numBundles, and currentBundle into this bundle because will need to restore
		// the current state of these variables after a screen orientation change in order to persist the enabled-state
		// of btnRaw, btnNextRows, and btnPriorRows.
		// And save off requestedBundle (this is at least needed after returning from having selected Overview from the
		// menu. Ditto for numIterations.

		outState.putInt(getPackageName() + ".numIterations", numIterations);
		outState.putBoolean(getPackageName() + ".firstBundle", firstBundle);
		outState.putInt(getPackageName() + ".numBundles", numBundles);
		outState.putInt(getPackageName() + ".currentBundle", + currentBundle);
		outState.putInt(getPackageName() + ".requestedBundle", + requestedBundle);

		// Save parent state.

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onPause");

		if(CArawDataDialog != null) {CArawDataDialog.cancel();}
		super.onPause();

	}

	@Override
	protected void onStop() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStop");
		super.onStop();

	}

	@Override
	protected void onDestroy() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onDestroy"); 
		super.onDestroy();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.rule250_menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside onOptionsItemSelected for Rule250Activity");

		   int numIterations;
		   String key = "iterations";
		   numIterations = getIntent().getExtras().getInt(key);
		   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Number of iterations selected by the user on the MainActivity: " + numIterations);
		   int iterationsChoice = numIterations;
		   if (iterationsChoice == 0)
		   {

			   // If the user hasn't yet selected a number of iterations, establish a default value.

			   iterationsChoice = 10;
			   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Number of iterations set to default value, iterationsChoice: " + iterationsChoice);

		   }

		   switch (item.getItemId()) {

       	   	   case R.id.menuMain:
	       		   Intent startActIntnt_Main = new Intent(getApplicationContext(), MainActivity.class);			     			
	       		   startActivity(startActIntnt_Main);
	       		   return true;

       	       case R.id.menuRule30:
		    	   Intent startActIntnt_Rule30 = new Intent(getApplicationContext(), Rule30Activity.class);
		    	   startActIntnt_Rule30.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
		    	   startActIntnt_Rule30.putExtra("rtnActivity", 5);	// 5 will indicate got there from Rule250Activity
		    	   startActivity(startActIntnt_Rule30);
		           return true;
		 
		       case R.id.menuRule90:
		    	   Intent startActIntnt_Rule90 = new Intent(getApplicationContext(), Rule90Activity.class);			     			
		    	   startActIntnt_Rule90.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
		    	   startActIntnt_Rule90.putExtra("rtnActivity", 5);	// 5 will indicate got there from Rule250Activity
		    	   startActivity(startActIntnt_Rule90);
		    	   return true;
		 
		       case R.id.menuRule110:
		    	   Intent startActIntnt_Rule110 = new Intent(getApplicationContext(), Rule110Activity.class);
		    	   startActIntnt_Rule110.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA	 						
		    	   startActIntnt_Rule110.putExtra("rtnActivity", 5);	// 5 will indicate got there from Rule250Activity
		    	   startActivity(startActIntnt_Rule110);
		    	   return true;
		  
		       case R.id.menuOverview:
		    	   Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
		    	   startActIntnt_Overview.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
		    	   startActIntnt_Overview.putExtra("rtnActivity", 5);	// 5 will indicate got there from Rule250Activity
		    	   startActivity(startActIntnt_Overview);
		    	   return true;

		       default:
		          return super.onOptionsItemSelected(item);
	       }
	}

	public ArrayList<CellularAutomataCell> setCellColor(ArrayList<CellularAutomataCell> cells, int iterations) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "inside setCellColor");

		// The Rule 250 algorithm for setting the current cell is that you look at its top diagonal neighbor cells
		// (i.e., the cells represented by the current row number - 1 with the current column number -1 and
		// the current column number +1); it is a logical OR situation, i.e., if either of these neighbor cells--including both of them--
		// are black, then set the current cell's color black.

		cells.clear();
		
		// The number of iterations selected by the user in the Main activity has been passed into this method in the parameter
		// iterations; it will be used to establish the number of rows of cells. The graph is always going to be 101 cells wide
		// for now, which is stored in the constant ITERATION_SIZE.

		// The starting condition required by Rule 250 is the top row of cells are all white except the central cell.
		// (thus for row 1, cells 1-50 = white, cell 51=black, and cells 52-101=white);

		for (int i=1; i<51; i++)
		{
			CellularAutomataCell currentCell = new CellularAutomataCell();
			currentCell.setCellColumn(i);
			currentCell.setCellRow(1);
			currentCell.setCellColor(0);
			cells.add(currentCell);
			//COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "currentCell Column " + currentCell.getCellColumn() + "currentCell Row: " + currentCell.getCellRow()  + "currentCellColor: " + currentCell.getCellColor());
		}

		CellularAutomataCell currentCell = new CellularAutomataCell();
		currentCell.setCellColumn(51);
		currentCell.setCellRow(1);
		currentCell.setCellColor(1);
		cells.add(currentCell);
		//COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "currentCell Column " + currentCell.getCellColumn() + "currentCell Row: " + currentCell.getCellRow()  + "currentCellColor: " + currentCell.getCellColor());

		for (int i=52; i<102; i++)
		{
			currentCell = new CellularAutomataCell();
			currentCell.setCellColumn(i);
			currentCell.setCellRow(1);
			currentCell.setCellColor(0);
			cells.add(currentCell);
			//COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "currentCell Column " + currentCell.getCellColumn() + "currentCell Row: " + currentCell.getCellRow()  + "currentCellColor: " + currentCell.getCellColor());
		}

		// For the remaining iterations-number of rows, an outer loop will have j representing the row for the currentCell we want to
		// create and an inner loop will have i representing the column for the currentCell we want to create. . . .

		int rightNeighborColor = 0;
		int leftNeighborColor  = 0;
		CellularAutomataCell leftNeighborCell, rightNeighborCell;
		
		int col;
		int row = 2;
		for (int j=row; j<=iterations; j++)
		{
			//COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "Looping through rows and ROW NUMBER = " + j);

			col = 1;
			for (int i = col; i <=ITERATION_SIZE; i++)
			{

				//COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "Looping through columns and COLUMN NUMBER = " + i);

				// For the first column of a row, there won't be a left-diagonal neighbor, but it must be treated as if it
				// does exist and as its color is set to 0 (white). So if the currentCell's column = 1, set
				// leftNeighborColor = 0 and skip the rest of the block:

				if (i == 1) {

					leftNeighborColor = 0;		// white

				} else {

					// Similarly, for the last column of a row, there won't be a right-diagonal neighbor, but it must be treated as
					// if it does exist and as its color is set to 0 (white). So if the currentCell's column = ITERATION_SIZE,
					// set rightNeighborColor = 0 and skip the rest of the block:

					if (i == ITERATION_SIZE) {

						rightNeighborColor = 0;	// white

					} else {

						int indx;
						indx = (j - 2) * ITERATION_SIZE + (i - 2);
						leftNeighborCell   = cells.get(indx);
						rightNeighborCell  = cells.get(indx+2);
						leftNeighborColor  = leftNeighborCell.getCellColor();
						rightNeighborColor = rightNeighborCell.getCellColor();
					}
				}

				// Now that we have saved off the color values for the current cell's 2 diagonal neighbors, we can use the logical OR
				// of these values to set the current cell's cell color.

				currentCell = new CellularAutomataCell();
				currentCell.setCellRow(j);
				currentCell.setCellColumn(i);
				currentCell.setCellColor(leftNeighborColor | rightNeighborColor);
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "ABOUT TO ADD NEW CELL FOR COLUMN: " + currentCell.getCellColumn() + " ROW: " + currentCell.getCellRow()  + " COLOR: " + currentCell.getCellColor());
				cells.add(currentCell);

				//COMMENTED OUT FOR PRODUCTION: // For debugging purposes:
				//COMMENTED OUT FOR PRODUCTION: int a = currentCell.getCellRow();
				//COMMENTED OUT FOR PRODUCTION: int b = currentCell.getCellColumn();
				//COMMENTED OUT FOR PRODUCTION: int c = currentCell.getCellColor();
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "JUST ADDED NEW CELL FOR ROW: " + a + " COLUMN: " + b  + " COLOR: " + c);

			}	// end looping through columns

		}		// end looping through rows

		return cells;	// Return the Arraylist collection of CA cells.

	}

	private void prepRawDataBundles()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "inside prepRawDataBundles");

		CArawDataDialog = ProgressDialog.show(Rule250Activity.this,
				"Your screen will go blank but please wait patiently . . .", "generating the raw data for your Rule 250 cellular automaton per your specifications", true );
		CArawDataDialog.setCancelable(true);

		// Create an intent to start the Display Raw Data activity (start the activity that provides the dump
		// of the individual CA cells from the ArrayList collection).

		Intent startActIntnt_Raw = new Intent(getApplicationContext(), DisplayRawDataActivity.class);

		// Package up a bundle with the cellular automata cells (in the collection ca250 that has already been filled)
		// that are to be passed to the Display Raw Data activity. Bundles passed to an activity have a size limit, which was
		// getting exceeded, and the calculations for passing multiple bundles was leading to the Android OS timing out
		// the operation and returning to the Main Activity. Therefore, will:
		// a) initially bundle raw data for the first 20 rows (equivalent to iterations and stored in the constant
		// BUNDLE_SIZE) worth of CA raw data into a single bundle, pass it to the Display Raw Data Activity for
		// display purposes;
		// b) upon return to this activity, will enable the Next Group button if there is more than 20 rows.
		// If clicked, the next bundle of row 21 up to row 40 will be handled;
		// c) upon return again from the Raw Data Activity, will enable the Next Group button if there are
		// additional rows of raw data and will enable the Prior Group button to allow the user to re-view
		// the prior 20 rows. The rows involved will be determined using the variables currentBundle and requestedBundle,
		// and will use the arguments iteratorStart and iteratorEnd with the bundleCells method
		// to indicate which cells to bundle (note that for the time being, the width of the cellular automata
		// is always fixed at 101 cells, which is stored in the constant ITERATION_SIZE).

		int startIteration = (currentBundle * BUNDLE_SIZE) - (BUNDLE_SIZE-1);
		int endIteration = currentBundle * BUNDLE_SIZE;
		if (numIterations < endIteration) {endIteration = numIterations;}

		int iterationBeingProcessed = startIteration;  
		int startValue = (startIteration - 1) * ITERATION_SIZE;
		int endValue = startValue - 1;	// because the array is zero-based

		while (iterationBeingProcessed <= endIteration)
		{

			//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "iterationBeingProcessed = " + iterationBeingProcessed);
			endValue = endValue + ITERATION_SIZE;
			//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "startValue = " + startValue + " and endValue = " + endValue);

			if ( (iterationBeingProcessed % BUNDLE_SIZE == 0) || (iterationBeingProcessed == endIteration) )  // it's time to create a bundle for bundleSize-number or fewer iterations
			
			{
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "iterationBeingProcessed%BUNDLE_SIZE = " + iterationBeingProcessed % BUNDLE_SIZE);
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to create the bundle");
				Bundle caCellBundle = new Bundle();
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to call bundleCells with startValue = " + startValue + " and endValue = " + endValue);
				bundleCells(caCellBundle, startValue, endValue);
				String key = getPackageName() + ".CAcellbundle";
				
				//Send this CA cell bundle or bundles with the Intent to this activity.

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to putExtra for key = " + key);
				startActIntnt_Raw.putExtra(key, caCellBundle);

			}

			iterationBeingProcessed = iterationBeingProcessed + 1;
		}

		// Send info about the number of bundles with the Intent to this activity.
		// Also pass info (about which activity is calling the DisplayRawDataActivity) to be utilized by
		// the DisplayRawData activity to show an appropriate label on the "return" button.

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to do putExtra with rtnActivity.");
		startActIntnt_Raw.putExtra("rtnActivity", 5); 	// 5 will indicate got there from Rule250Activity
 	    startActIntnt_Raw.putExtra("iterations", numIterations); // not needed by DisplayRawDataActivity per se but that activity may need to pass this value along to another activity selected from the menu

 	    // Start the activity that provides the dump of the individual CA cells from the ArrayList collection.

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "About to start Display Raw Data activity.");
		startActivity(startActIntnt_Raw);

	}

	// Package CA cells into a Bundle.

	private void bundleCells(Bundle cellBundle, Integer iteratorStart, Integer iteratorEnd)
	{

		// Load the cellBundle with the traits that comprise a CA cell. Bundles passed to an activity have a size limit,
		// which was getting exceeded, and the calculations for passing multiple bundles was leading to the Android OS
		// timing out the operation and returning to the Main Activity. Therefore, will: a) bundle raw data for the first
		// 20 rows (which is stored in the constant BUNDLE_SIZE), pass it to the Display Raw Data Activity for 
		// display purposes; b) upon return to this activity, will enable the Next Group button if there is more
		// than 20 rows. If clicked, the next bundle of rows 21 up to row 40 will be handled; c) upon return again
		// from the Raw Data Activity, will enable the Next Group button if there are additional rows of raw data
		// and will enable the Prior Group button to allow the user to re-view the prior 20 rows. So the appropriate
		// range of rows will be passed into this method through iteratorStart and iteratorEnd. For now, the width
		// of the cellular automata is always fixed at 101 cells, which is stored in the constant ITERATION_SIZE.

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside bundleCells with parameters iteratorStart = " + iteratorStart + " and iteratorEnd = " + iteratorEnd);

	    int i = 0;
	    int bundledCells = 0;

	    for (i = iteratorStart; i <= iteratorEnd; ++i)
	    {

	    	  CellularAutomataCell tmpCell = ca250.get(i);
	    	  //COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "just got ca250.get(" + i + ")");

	    	  //COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "CA cell number: " + i);        

	    	  //COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "Sending ca cell row: " + tmpCell.getCellRow());
	          cellBundle.putInt(getPackageName() + ".cellrow" + i, tmpCell.getCellRow());
	          
	          //COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "Sending ca cell column: " + tmpCell.getCellColumn());
	          cellBundle.putInt(getPackageName() + ".cellcol" + i, tmpCell.getCellColumn());
	          
	          //COMMENTED OUT FOR PRODUCTION: //Log.i(getClass().getSimpleName(), "Sending ca cell color: " + tmpCell.getCellColor());
	          cellBundle.putInt(getPackageName() + ".cellcolor" + i, tmpCell.getCellColor());

	          bundledCells = bundledCells + 1;
	          //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "bundledCells = " + bundledCells);
	    }

	    cellBundle.putInt(getPackageName() + "iteratorStart", iteratorStart);
	    cellBundle.putInt(getPackageName() + "iteratorEnd", iteratorEnd);
	    //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Sending number of CA cells in this bundle: " + bundledCells);
	    cellBundle.putInt(getPackageName() + ".numcells", bundledCells);

	}
	
	// Create a method that will be called should the state need to be restored that will derive CA Cell data from
	// a Bundle object (as in the event onSaveInstanceState was called, the ca250 cell collection would have been
	// saved into a bundle object). 

	private void unbundleCells(Bundle caCellBundle)
	{

	   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside unbundleCells");

	   // As a precaution, start by clearing the collection of Cellular Automata Cells that we will be refilling.

	    ca250.clear();

	   // Determine the number of CA cells in the bundle and use this number to get the individual components of
	   // this many CA cells from the bundle.

	   int UBnumCells = caCellBundle.getInt(getPackageName() + ".numcells", 0);

	   for (int i = 0; i < UBnumCells; ++i)
	   {

	      // Create new CA cell.

	      CellularAutomataCell UBcell = new CellularAutomataCell();

	      // Get each component comprising a CellularAutomataCell.

	      int UBcellRow = caCellBundle.getInt(getPackageName() + ".cellrow" + i, 0);
	      int UBcellColumn = caCellBundle.getInt(getPackageName() + ".cellcol" + i, 0);
	      int UBcellColor = caCellBundle.getInt(getPackageName() + ".cellcolor" + i, 0);

	      // Use this retrieved data to create a Cellular Automata Cell.

	      UBcell.setCellRow(UBcellRow);
	      UBcell.setCellColumn(UBcellColumn);
	      UBcell.setCellColor(UBcellColor);

	      // Now add this new CellularAutomataCell to the collection that will store all such cells comprising
	      // the requested CA.

	      ca250.add(UBcell);

	   }

	   // Now update the graph view with the restored collection of Cellular Automata Cells.
				
	   Update();
		
	}
		
	private void Update()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside Update");

		// Update the GraphView: need to pass CellularAutomataCell collection, ca250, to the GraphView gvwCA250.
		// The invalidate method will result in the onDraw method being called--essentially, this causes a repaint.
		// That method will iterate through the ca250 collection, using its data to determine the paint color to use and the
		// grid placement to use for the squares it will draw.
		// Note that the variable fakeOut is being used within the GraphView/GraphViewLandscape's onMeasure methods for purposes of
		// the IDE's graphical layout editor which appears to require a hard-coded value for desiredHeight/desiredWidth.

			gvwCA250.setCells(ca250);		
			gvwCA250.setFakeOut(false);
			gvwCA250.invalidate();

	}

}