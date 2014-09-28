package csc212.project4;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
//COMMENTED OUT FOR PRODUCTION: import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	// protected static final char none = 0;
	
	// Resource fields.

	private Spinner spnrRules;
	private Spinner spnrIterations;
	private Button btnDisplay;
	private Button btnOverview;
	//private ProgressBar prgrsBar;
	private Integer CAchoice;
	private Integer iterationsChoice;

	CA_User_Spec userSpec = new CA_User_Spec();
	private ProgressDialog CAdialog;
	
	// temp--debugging: 
	// final Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);	 	// call parent method

		setContentView(R.layout.main_layout); 	// set main_layout as the layout (content) for the Main activity

		// Obtain and save the ids (handles) for the views (components) on the main layout that will require listeners.

		spnrRules = (Spinner)findViewById(R.id.spnr_rules);
		spnrIterations = (Spinner)findViewById(R.id.spnr_iterations);
		btnDisplay = (Button)findViewById(R.id.btn_display);
		btnOverview = (Button)findViewById(R.id.btn_overview_from_main);
		//prgrsBar = (ProgressBar)findViewById(R.id.prgrs_bar);

		// Will initiate the correct activity based on the selected value in spnrRules as tracked by CAchoice;
		// initialize the value to represent the default that displays in spnrRules [from which the user shall be forced
		// to select one of the CAs (Cellular Automaton/Cellular Automata)]--this "default" is not acceptable as it just 
		// states that a CA has not been chosen. This is 0-based. spnrIterations works similarly.

		CAchoice = 0;
		iterationsChoice = 0;

		// Establish the listener (nested static interface) for the button that displays the CA based on
		// the chosen options.
		
		btnDisplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside btnDisplay.setOnClickListener");
				// Store the selected value in spnrRules that will shortly be used to initiate the associated activity 
				// for the CA specs chosen. This is 0-based.
				
				CAchoice = spnrRules.getSelectedItemPosition();
				userSpec.setCAchoice(CAchoice);
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "CA choice: " + userSpec.getCAchoice());

				
				// Store the selected value in spnrIterations.
				
				iterationsChoice = spnrIterations.getSelectedItemPosition();
				userSpec.setIterationsChoice(iterationsChoice);
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Iterations choice: " + userSpec.getIterationsChoice());
				
				// If the user has specified both required criteria for the CA, we're good to go; otherwise
				// (CAchoice and/or iterationsChoice will still be 0), display a dialog window to notify user
				// that values for both must be selected.
				
				if ( (CAchoice == 0) || (iterationsChoice == 0) )
				{

					// Display a short-duration dialog to notify the user of the problem.
					Toast.makeText(getApplicationContext(), "You need to select both a Type of CA and the number of iterations to display.", Toast.LENGTH_SHORT).show();

				} else
				{

	     			CAdialog = ProgressDialog.show(MainActivity.this,
	    					"Please wait . . .", "Generating your cellular automaton per your specifications", true );
	     			CAdialog.setCancelable(true);

	     			// Display the CA for the chosen options--i.e., call the appropriate activity--based on the
					// value the user chose in spnrRules as represented in CAchoice. Note: may eventually want to use the
					// array position to search the actual array that populated the spinner.

					switch (CAchoice) {	// spinner choices are 0-based; but only got to this stmt if CAchoice <> 0

			    		case 1: CAchoice = 1;
						
			     			Intent startActIntnt_Rule30 = new Intent(getApplicationContext(), Rule30Activity.class);
			     			startActIntnt_Rule30.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA

			     			//ProgressDialog dialog = new ProgressDialog(context);
			        	    //dialog.setMessage("Thinking...");
			        	    //dialog.setIndeterminate(true);
			        	    //dialog.setCancelable(false);
			        	    //dialog.show();

			     			startActivity(startActIntnt_Rule30);
			     			break;

			    		case 2: CAchoice = 2;

			     			Intent startActIntnt_Rule90 = new Intent(getApplicationContext(), Rule90Activity.class);			     			
			     			startActIntnt_Rule90.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
			     			startActivity(startActIntnt_Rule90);
			     			break;

			    		case 3: CAchoice = 3;

	 						Intent startActIntnt_Rule110 = new Intent(getApplicationContext(), Rule110Activity.class);
			     			startActIntnt_Rule110.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA	 						
	 						startActivity(startActIntnt_Rule110);
	 						break;

			    		case 4: CAchoice = 4;

 							Intent startActIntnt_Rule250 = new Intent(getApplicationContext(), Rule250Activity.class);
			     			startActIntnt_Rule250.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
 							startActivity(startActIntnt_Rule250);
 							break;
					}
			     }
			}
		});
		
		// Establish the listener (nested static interface) for the button that displays the background for CA and
		// how to use the app.
		
		btnOverview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside btnOverview.setOnClickListener");
				// Start the activity that provides background info for Cellular Automata and how to use this app.
				// Pass info to be utilized by the Overview activity to display an appropriate label on the "return" button. 

				Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
				startActIntnt_Overview.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
				startActivity(startActIntnt_Overview);

			}

		});

	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onCreateOptionsMenu");

		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;

	}

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {

	   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside onOptionsItemSelected for MainActivity");
	   iterationsChoice = spnrIterations.getSelectedItemPosition();
	   if (iterationsChoice == 0) { iterationsChoice = 10;}	// if user hasn't yet selected a number of iterations, establish a default value
	   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "ITERATIONS CHOICE, userSpec: " + userSpec.getIterationsChoice());
	   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "ITERATIONS CHOICE, iterationsChoice: " + iterationsChoice);

	   switch (item.getItemId()) {
 
	       case R.id.menuRule30:
	    	   Intent startActIntnt_Rule30 = new Intent(getApplicationContext(), Rule30Activity.class);
	    	   startActIntnt_Rule30.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
	    	   startActIntnt_Rule30.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
	    	   startActivity(startActIntnt_Rule30);
	           return true;
	 
	       case R.id.menuRule90:
	    	   Intent startActIntnt_Rule90 = new Intent(getApplicationContext(), Rule90Activity.class);			     			
	    	   startActIntnt_Rule90.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
	    	   startActIntnt_Rule90.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
	    	   startActivity(startActIntnt_Rule90);
	    	   return true;
	 
	       case R.id.menuRule110:
	    	   Intent startActIntnt_Rule110 = new Intent(getApplicationContext(), Rule110Activity.class);
	    	   startActIntnt_Rule110.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA	 						
	    	   startActIntnt_Rule110.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
	    	   startActivity(startActIntnt_Rule110);
	    	   return true;
	
	       case R.id.menuRule250:
	    	   Intent startActIntnt_Rule250 = new Intent(getApplicationContext(), Rule250Activity.class);
	    	   startActIntnt_Rule250.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
	    	   startActIntnt_Rule250.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
	    	   startActivity(startActIntnt_Rule250);
	           return true;
	  
	       case R.id.menuOverview:
	    	   Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
	    	   startActIntnt_Overview.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
	    	   startActIntnt_Overview.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
	    	   startActivity(startActIntnt_Overview);
	    	   return true;

	       default:
	          return super.onOptionsItemSelected(item);
       }
   }
	
	@Override
	protected void onStart() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStart");
		super.onStart();

	}

	@Override
	protected void onResume() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onResume");
		super.onResume();

		if(CAdialog != null) {CAdialog.cancel();}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onSaveInstanceState");
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onDestroy() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onDestroy");
		super.onDestroy();

	}

	@Override
	protected void onPause() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onPause");
		super.onPause();

	}

	@Override
	protected void onStop() {

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "onStop");
		super.onStop();

	}

}