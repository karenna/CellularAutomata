package csc212.project4;

import java.util.ArrayList;
import android.util.Log;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class DisplayRawDataActivity extends ListActivity 
{

	final int ITERATION_SIZE = 101;		// For now, all Cellular Automata will be 101 columns wide.
	final int bundleSize = 20;			// Eventually should pass this in from Rule30Activity et al. For the time being, the number of iterations (i.e., number of CA rows we'll bundle together and pass via an extra is 20)
	private Thread rawDataThread;
	private Button btnReturnToPriorActivity;
	private TextView txtProgress;
	private int returnActivity;
	
	// Will use a collection to store all CellularAutomataCells that comprise the specific CA for which
	// we want to display the raw data.
	
	private ArrayList<CellularAutomataCell> genericCA = new ArrayList<CellularAutomataCell>();

	// Establish ListView items that comprise the raw data for the individual CA cells and the adapter for the list.
	
	private ArrayList<String> caCellListItems = new ArrayList<String>();
	private ArrayAdapter<String> listAdapter;
	private volatile boolean stopRequested = false;		// a flag used to stop the rawDataRunnable thread
	String msgString = "*ABOUT TO COLLECT DATA*";   	// initialize status string

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//COMMENTED OUT FOR PRODUCTION: log.i(getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display_raw_data_layout);

		// Create the listView adapter.
		listAdapter = new ArrayAdapter<String>(this, R.layout.display_raw_data_listview_item, caCellListItems);		
		setListAdapter(listAdapter);

		// Obtain and save the ids (handles) for the views (components) on the DisplayRawData layout that will
		// require listeners.
		
		btnReturnToPriorActivity = (Button)findViewById(R.id.btn_return_to_prior_from_rawdata);
	
		txtProgress = (TextView) findViewById(R.id.txtvw_dsply_raw_data_prgrs);

		// Want to display an appropriate label for btnReturnToPriorActivity (based on the activity that was running
		// when the user chose to see the raw data). A number representing an activity was passed as an "extra" with
		// the intent to start this DisplayRawData activity.

		String key = "rtnActivity";
		Integer returnActivity = getIntent().getExtras().getInt(key);
		//COMMENTED OUT FOR PRODUCTION://KEEPLog.i(getClass().getSimpleName(), "Integer representation of activity to return to: " + returnActivity);

		switch (returnActivity)
		{

			case 1: returnActivity = 1;	// 1 = MainActivity

		    	btnReturnToPriorActivity.setText("Bk 2 Main");
		        break;

		    case 2: returnActivity = 2;	// 2 = Rule30Activity

     			btnReturnToPriorActivity.setText("Bk 2 Rule30");
     			break;

		    case 3: returnActivity = 3;	// 3 = Rule90Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule90");
				break;

		    case 4: returnActivity = 4;	// 4 = Rule110Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule110");
				break;

		    case 5: returnActivity = 5;	// 5 = Rule250Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule250");
				break;

		}

		txtProgress.setText("*PLEASE BE PATIENT . . . THE RAW DATA WILL DISPLAY SHORTLY.*");
		
		// Establish the listener (nested static interface) for the button that returns to the prior activity.	 
		
		btnReturnToPriorActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Finish this activity; whatever the activity was that took us to this DisplayRawData activity
				// should be what remains on the stack, thus should automatically return to it.

    			requestStop();	// sets stopRequested to false to end the rawDataThread
				finish(); 

			}

		});


	}		
	
	public void displayRequestedBundle()
	{

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside displayRequestedBundle");

		// Updating a user interface element from within a thread other than the main thread violates a key rule of
		// Android development, so to update the user interface, will implement a Handler for the second thread.
		// Thread handlers are implemented in the main thread of an application and are primarily used to make updates to
		// the user interface in response to messages sent by another thread running within the application’s process.

		// Override the handleMessage() callback method within the Handler subclass, which will be called when messages
		// are sent to the handler by a thread.
	
		final Handler rawDataThreadHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
			  
			  // Extract the string from the bundle object in the message and display it on the TextView object.
			  
			  Bundle messageBundle = msg.getData();
			  String messageString = messageBundle.getString("rawDataStatus");	// rawDataStatus is the key for retrieval of the correct info from the bundle
			  txtProgress.setText(messageString);

			  // The underlying data for the listAdapter has changed, but it won't be displayed until the listview is
			  // refreshed with that data.
			  
      		  listAdapter.notifyDataSetChanged();     		  
 
			}
		};		// end rawDataThreadHandler
	
		// Will initiate a second thread for the time-intensive operations involved with populating the listview, i.e.,
		// for getting the data passed into this activity in a bundle and for formatting this data for display purposes,
		// which will be used to populate the listview with the CA's "raw data."  To create a new thread, the code that will be
		// executed in that thread must be placed within the Run() method of a Runnable instance. Then a new Thread object
		// must be created and a reference to the Runnable instance is passed to the thread's constructor. Lastly, the 
		// Thread object's start() method must be called to start running the thread.        
	        		        	
		Runnable rawDataRunnable = new Runnable()
		{

			public void run()
			{     	

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside rawDataRunnable");

				while (!stopRequested)			// only will loop once--to handle 1 bundle--stopRequested will get set to 1 after 1 pass to stop the thread
					//while(!isInterrupted()	
				{

					System.out.println("Inside rawDataRunnable and current thread is " + Thread.currentThread().getName());	

					try				        	// Do all the heavy lifting here . . .
					{

						//blnInterrupted = false;

						// Get the CA cell data passed in a bundle via extras to this activity with the intent
						// (from Rule30Activity or Rule90Activity or Rule110Activity or Rule250Activity).
	
						int cellCount = 0;

						/////for(int i = 1; i <= numBundles; i++)
						/////{
	
						//COMMENTED OUT FOR PRODUCTION: /KEEPLog.i(getClass().getSimpleName(), "About to obtain the CA cell data passed to this activity");

						String bundleKey = getPackageName() + ".CAcellbundle";
						//COMMENTED OUT FOR PRODUCTION: //KEEPLog.i(getClass().getSimpleName(), "About to getBundleExtra for key = " + bundleKey);
						Bundle genericCA_Bundle = getIntent().getBundleExtra(bundleKey);
							
						// Determine the number of CA cells in the current bundle and use this number to
						// get the individual components of this many CA cells from the bundle.
	
						//COMMENTED OUT FOR PRODUCTION: int numCells = genericCA_Bundle.getInt(getPackageName() + ".numcells", 0);
						//COMMENTED OUT FOR PRODUCTION: //KEEPLog.i(getClass().getSimpleName(), "Num CA cells in this bundle: " + numCells);

					    int startIndex = genericCA_Bundle.getInt(getPackageName() + "iteratorStart", 0);
					    int endIndex   = genericCA_Bundle.getInt(getPackageName() + "iteratorEnd", 0);
						
						caCellListItems.clear();		// clear out existing list before re-populate it with new bundle
						
					    for (int j = startIndex; j <= endIndex; j++)	
			    		{
			    			// Get each component comprising a CellularAutomataCell.
					    	//COMMENTED OUT FOR PRODUCTION: String component = getPackageName() + ".cellrow" + j;
			    			//COMMENTED OUT FOR PRODUCTION: Boolean containsKey = genericCA_Bundle.containsKey(component);
							///COMMENTED OUT FOR PRODUCTION: /KEEPLog.i(getClass().getSimpleName(), "containsKey: " + containsKey);

			    			int cellRow = genericCA_Bundle.getInt(getPackageName() + ".cellrow" + j, 0);
			    			String strRow = "cell row: " + cellRow;
			    			//COMMENTED OUT FOR PRODUCTION: //KEEPLog.i(getClass().getSimpleName(), strRow);
		
			    			int cellColumn = genericCA_Bundle.getInt(getPackageName() + ".cellcol" + j, 0);
			    			String strCol = "cell column: " + cellColumn;
			    			//COMMENTED OUT FOR PRODUCTION: //KEEPLog.i(getClass().getSimpleName(), strCol);
		
			    			int cellColor = genericCA_Bundle.getInt(getPackageName() + ".cellcolor" + j, 0);
			    			String strColor = "cell color: " + cellColor;
			    			//COMMENTED OUT FOR PRODUCTION: //KEEPLog.i(getClass().getSimpleName(), strColor);
		
			    			// Want to create a nicely formatted string relevant to each CA cell--build a string from the above data,
			    			// and create an array of such strings that will populate the string array adapter in turn used to
			    			// display the info to the user via the listview.
			    			// 
			    			// obtain the specified CA's raw data passed into this activity, using this raw data to create a separate string
			    			// constructed for each cell comprising the specified CA based on the cell's attributes (cell row, cell column,
			    			// & cell color), storing each such string in an array which will supply the adapter for the listview.
	
			    			cellCount = cellCount + 1;
			    			StringBuilder strCA_Cell = new StringBuilder();
			    			// strCA_Cell.append("CellularAutomataCell #" + (j + 1) + ' ' + strRow + ' ' + strCol + ' ' + strColor);
			    			strCA_Cell.append("CellularAutomataCell #" + (cellCount) + ' ' + strRow + ' ' + strCol + ' ' + strColor);
	
			    			// Add this string to the array list of such strings.
		
			    			//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Adding this string to the array list: " + strCA_Cell.toString());
			    			caCellListItems.add(strCA_Cell.toString());
			    			//txtProgress.setText(Integer.toString(j.intValue()));
			    			//txtProgress.setText(Integer.toString(j));
			    		}
/*
			    		/////}

			    		// THIS DID NOT WORK. PUTTING THE THREAD TO SLEEP DID NOT THROW THE INTERRUPTED EXCEPTION AS EXPECTED.
			    		//Log.i(getClass().getSimpleName(), "About to go to sleep briefly.");
			    		//try
			    		//{
    			    		//rawDataThread.sleep((long) 15000);		// Do this to purposefully throw the Interrupted Exception
    			    		//Thread.sleep((long) 10000);			// Do this to purposefully throw the Interrupted Exception
    			    		//Log.i(getClass().getSimpleName(), "Nap over.");
		        
			    		//} catch (InterruptedException e)
			    		//{
    			    		//blnInterrupted = true;
    			    		//Log.i(getClass().getSimpleName(), "InterruptedException e: " + e);
	    			
    			    		/////requestStop();	// sets stopRequested to false
			    		//}
*/
					}
	    			catch (Exception e)
		        	{
	        		
		        		Log.e(getClass().getSimpleName(), e.toString());
		    			e.printStackTrace();
	
		    			// Send a message to the thread handler that things failed.
		    			// EVENTUALLY: WHAT'S OUTSIDE OF THE TRY-CATCH BLOCK MAY HAVE TO BE MOVED.
	
		    			Message rawDataMsg = rawDataThreadHandler.obtainMessage();
	
			        	// The message is expected as a bundle.
			
		    			Bundle rawDataBundle = new Bundle();
						msgString = "*ERROR OCCURRED WHILE CREATING caCellListItems*";
	
		        	}
			        finally
			        {
	
				        // As only the UI thread can interact with UI objects, now that all raw data for the CA has been stored
			        	// in the array for the listview, will alert the main thread so it can in turn inform the listview to
			        	// refresh itself. First, make a call to the obtainMessage() method of the Handler object to get a
			        	// Message object from the message pool.
			
						if ( !"*ERROR OCCURRED WHILE CREATING caCellListItems*".equals(msgString) )
						{
							msgString = "*ALL RAW DATA HAS BEEN DISPLAYED*";

						}
	
			        	Message rawDataMsg = rawDataThreadHandler.obtainMessage();
			        	
			        	// The message is expected as a bundle.
			
			        	Bundle rawDataBundle = new Bundle();
	
						rawDataBundle.putString("rawDataStatus", msgString);
			            rawDataMsg.setData(rawDataBundle);
			            rawDataThreadHandler.sendMessage(rawDataMsg);
			        	
			            requestStop();		// stop this thread
			
			        }
	    		}
	
	    	}	// end run method

		}; 	// end runnable

	    System.out.println(Thread.currentThread().getName() + " is current thread b4 instantiate rawDataThread");
		rawDataThread = new Thread(rawDataRunnable);
		System.out.println(Thread.currentThread().getName() + " is current thread just after instantiated rawDataThread");
		rawDataThread.start();
	    System.out.println(Thread.currentThread().getName() + " is current thread just after rawDataThread was started");
	    //rawDataThread.join();			// 
		try
		{
			rawDataThread.join();		// UI thread joins rawDataThread (so the main thread is going to wait for the rawDataThread to finish).
		    System.out.println(Thread.currentThread().getName() + " is current thread after join");
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	
	}

	// This method will be called from the rawDataRunnable thread to stop the thread after one execution of the runnable code.

	public void requestStop()
	{

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside requestStop");
		stopRequested = true;

	}
	
	// Create a method that will derive CA Cell data from a Bundle object (a bundle object containing this
	// information was passed into this activity with the intent to start the activity). 

	private void unbundleCells(Bundle caCellBundle)
	{

	   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside unbundleCells");
	   // Start by clearing the CA Cell collection.

	   genericCA.clear();

	   // Determine the number of CA cells in the bundle and use this number to get the individual components of this many
	   // CA cells from the bundle.

	   int numCells = caCellBundle.getInt(getPackageName() + ".numcells", 0);

	   for (int i = 0; i < numCells; ++i)
	   {

	      // Create new CA cell.

	      CellularAutomataCell cell = new CellularAutomataCell();

	      // Get each component comprising a CellularAutomataCell.

	      int cellRow = caCellBundle.getInt(getPackageName() + ".cellrow" + i, 0);

	      int cellColumn = caCellBundle.getInt(getPackageName() + ".cellcol" + i, 0);

	      int cellColor = caCellBundle.getInt(getPackageName() + ".cellcolor" + i, 0);

	      // Use this retrieved data to create a Cellular Automata Cell.

	      cell.setCellRow(cellRow);

	      cell.setCellColumn(cellColumn);

	      cell.setCellColor(cellColor);

	      // Now add this new CellularAutomataCell to the collection that will store all such cells comprising
	      // the requested CA.

	      genericCA.add(cell);

	   }

	}
	
	@Override
	protected void onStart()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStart");
		super.onStart();

	}
	
	// We created a method to unbundle data so we can persist our CellularAutomataCells in the genericCA collection.
	// (i.e., we'll use this to restore any data that exists to be persisted).
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onRestoreInstanceState");

		// First restore the parent state with the bundle passed into this method.

		super.onRestoreInstanceState(savedInstanceState);

		// Then, provided there actually is something in the bundle to be restored . . .

	    if (savedInstanceState != null)

	    {

	        // . . . Restore the state contained in this bundled data (i.e., retrieve the individual
	    	// CellularAutomataCells in the bundle and put them into our genericCA collection).
	    	
	    	unbundleCells(savedInstanceState);

	    }

	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestart()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onResume");
		super.onResume();
		
		      		    stopRequested = false;

				displayRequestedBundle();
	}

	@Override
	protected void onPause()
	{
		// onPause is the one method that is always guaranteed to be called when your Activity loses focus 
	
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onPause");
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onCreateOptionsMenu");

		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.display_raw_data_menu, menu);
		return true;
	}

	@Override
	   public boolean onOptionsItemSelected(MenuItem item)
	   {

		   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside onOptionsItemSelected for DisplayRawDataActivity");

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

		   key = "rtnActivity";
		   returnActivity = getIntent().getExtras().getInt(key);

		   switch (item.getItemId()) {

			   case R.id.menuMain:
		   		   Intent startActIntnt_Main = new Intent(getApplicationContext(), MainActivity.class);			     			
		   		   //startActIntnt_Main.putExtra("iterations", iterationsChoice);   // pass on the value indicating which activity initiated this DisplayRawDataActivity
		   		   //startActIntnt_Main.putExtra("rtnActivity", returnActivity);	// 2 will indicate got there from Rule30Activity
		   		   startActivity(startActIntnt_Main);
		   		   return true;

			   case R.id.menuRule30:
		    	   Intent startActIntnt_Rule30 = new Intent(getApplicationContext(), Rule30Activity.class);
		    	   startActIntnt_Rule30.putExtra("iterations", iterationsChoice);   // pass on the value indicating which activity initiated this DisplayRawDataActivity    	   
		    	   //startActIntnt_Rule30.putExtra("rtnActivity", returnActivity);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule30);
		           return true;
		 
		       case R.id.menuRule90:
		    	   Intent startActIntnt_Rule90 = new Intent(getApplicationContext(), Rule90Activity.class);			     			
		    	   startActIntnt_Rule90.putExtra("iterations", iterationsChoice);   // pass on the value indicating which activity initiated this DisplayRawDataActivity
		    	   //startActIntnt_Rule90.putExtra("rtnActivity", returnActivity);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule90);
		    	   return true;
		 
		       case R.id.menuRule110:
		    	   Intent startActIntnt_Rule110 = new Intent(getApplicationContext(), Rule110Activity.class);
		    	   startActIntnt_Rule110.putExtra("iterations", iterationsChoice);  // pass on the value indicating which activity initiated this DisplayRawDataActivity	 						
		    	   //startActIntnt_Rule110.putExtra("rtnActivity", returnActivity);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule110);
		    	   return true;
		
		       case R.id.menuRule250:
		    	   Intent startActIntnt_Rule250 = new Intent(getApplicationContext(), Rule250Activity.class);
		    	   startActIntnt_Rule250.putExtra("iterations", iterationsChoice); 	// pass on the value indicating which activity initiated this DisplayRawDataActivity
		    	   //startActIntnt_Rule250.putExtra("rtnActivity", returnActivity);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule250);
		           return true;
		  
		       case R.id.menuOverview:
		    	   Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
		    	   startActIntnt_Overview.putExtra("rtnActivity", returnActivity);	// pass on the value indicating which activity initiated this DisplayRawDataActivity
		    	   startActIntnt_Overview.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
		    	   startActivity(startActIntnt_Overview);
		    	   return true;

		       default:
		          return super.onOptionsItemSelected(item);
	       }
	   }
}