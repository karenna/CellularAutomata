package csc212.project4;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//COMMENTED OUT FOR PRODUCTION: import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.text.Html;
//import android.content.Context;
//import android.content.DialogInterface;

public class OverviewActivity extends Activity {

	private Button btnReturnToPriorActivity;
	private TextView txtvwOverview1, txtvwOverview2, txtvwOverview3;

	// temp--debugging: 
	//final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview_layout);

		// Obtain and save the ids (handles) for the views (components) on the overview layout that will require listeners.
				
		btnReturnToPriorActivity = (Button)findViewById(R.id.btn_return_to_prior_from_overview);
		txtvwOverview1 = (TextView)findViewById(R.id.txtvw_overview_paragraph1);
		txtvwOverview2 = (TextView)findViewById(R.id.txtvw_overview_paragraph2);
		txtvwOverview3 = (TextView)findViewById(R.id.txtvw_overview_paragraph3);
		String par1 = getResources().getString(R.string.ca_ovrvw_par1);
		String par2 = getResources().getString(R.string.ca_ovrvw_par2);
		String par3 = getResources().getString(R.string.ca_ovrvw_par3);

		CharSequence styledText = Html.fromHtml(par1);
		txtvwOverview1.setText(styledText);
		styledText = Html.fromHtml(par2);
		txtvwOverview2.setText(styledText);
		styledText = Html.fromHtml(par3);
		txtvwOverview3.setText(styledText);

		// Want to display an appropriate label for btnReturnToPriorActivity (based on the activity that was running
		// when the user chose to see this overview). A number representing an activity was passed as an "extra" with
		// the intent to start this Overview activity.

		String key = "rtnActivity";
		Integer value = getIntent().getExtras().getInt(key);
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Integer representation of activity to return to: " + value);
		// temp--debugging
		//AlertDialog.Builder alertDialogBuilder12 = new AlertDialog.Builder(context);
		//alertDialogBuilder12
			//.setMessage("In OverviewActivity value for the key rtnActivity = " + value)
			//.setCancelable(false)
			//.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				//public void onClick(DialogInterface dialog, int id) {
					//dialog.cancel();
				//}
			//});
		//AlertDialog alertDialog12 = alertDialogBuilder12.create();
		//alertDialog12.show();

		switch (value) {

			case 1: value = 1;	// 1 = MainActivity

		    	btnReturnToPriorActivity.setText("Bk 2 Main");
		        break;

		    case 2: value = 2;	// 2 = Rule30Activity

     			btnReturnToPriorActivity.setText("Bk 2 Rule30");
     			break;

		    case 3: value = 3;	// 3 = Rule90Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule90");
				break;

		    case 4: value = 4;	// 4 = Rule110Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule110");
				break;

		    case 5: value = 5;	// 5 = Rule250Activity

				btnReturnToPriorActivity.setText("Bk 2 Rule250");
				break;

		}
		
		// Establish the listener (nested static interface) for the button that returns to the prior activity.
		
		btnReturnToPriorActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// Finish this activity; whatever the activity was that took us to this Overview activity should be
				// what remains on the stack, thus should automatically return to it.

				finish(); 

			}

		});

	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.overview_menu, menu);
		return true;

	}

	@Override
	   public boolean onOptionsItemSelected(MenuItem item)
	   {

		   //COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "Inside onOptionsItemSelected for OverviewActivity");

/*
  		   int numIterations;
 
		   String key = "iterations";
		   numIterations = getIntent().getExtras().getInt(key);
		   //COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "Number of iterations selected by the user on the MainActivity: " + numIterations);
		   int iterationsChoice = numIterations;
		   if (iterationsChoice == 0)
		   {
			   // If the user hasn't yet selected a number of iterations, establish a default value.
			   iterationsChoice = 10;
			   //COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "Number of iterations set to default value, iterationsChoice: " + iterationsChoice);
		   }
*/

		   switch (item.getItemId()) {

			   case R.id.menuMain:
		   		   Intent startActIntnt_Main = new Intent(getApplicationContext(), MainActivity.class);			     			
		   		   //startActIntnt_Main.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
		   		   //startActIntnt_Main.putExtra("rtnActivity", 2);	// 2 will indicate got there from Rule30Activity
		   		   startActivity(startActIntnt_Main);
		   		   return true;

/*
			   case R.id.menuRule30:
		    	   Intent startActIntnt_Rule30 = new Intent(getApplicationContext(), Rule30Activity.class);
		    	   startActIntnt_Rule30.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
		    	   //startActIntnt_Rule30.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule30);
		           return true;
		 
		       case R.id.menuRule90:
		    	   Intent startActIntnt_Rule90 = new Intent(getApplicationContext(), Rule90Activity.class);			     			
		    	   startActIntnt_Rule90.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
		    	   //startActIntnt_Rule90.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule90);
		    	   return true;
		 
		       case R.id.menuRule110:
		    	   Intent startActIntnt_Rule110 = new Intent(getApplicationContext(), Rule110Activity.class);
		    	   startActIntnt_Rule110.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA	 						
		    	   //startActIntnt_Rule110.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule110);
		    	   return true;
		
		       case R.id.menuRule250:
		    	   Intent startActIntnt_Rule250 = new Intent(getApplicationContext(), Rule250Activity.class);
		    	   startActIntnt_Rule250.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA
		    	   //startActIntnt_Rule250.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
		    	   startActivity(startActIntnt_Rule250);
		           return true;
		  
		       case R.id.menuOverview:
		    	   Intent startActIntnt_Overview = new Intent(getApplicationContext(), OverviewActivity.class);
		    	   //startActIntnt_Overview.putExtra("rtnActivity", 1);	// 1 will indicate got there from MainActivity
		    	   startActIntnt_Overview.putExtra("iterations", iterationsChoice); // need to pass the selected number of iterations to the Activity that builds the CA    	   
		    	   startActivity(startActIntnt_Overview);
		    	   return true;
*/
		       default:
		          return super.onOptionsItemSelected(item);
	       }
	   }	

}