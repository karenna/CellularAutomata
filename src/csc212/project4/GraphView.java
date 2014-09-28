package csc212.project4;

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
//import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
//import android.ViewGroup.LayoutParams;
import android.view.View;
//COMMENTED OUT FOR PRODUCTION: import android.util.Log;


public class GraphView extends View {

	// class variables for storing graph and line information

	private LinearLayout lyoRendered;
/*
 *	private LinearLayout lyoOuter;
 *	private LinearLayout lyoCAfullDesc;
 *	private LinearLayout lyoCA3Columns;
 *	private LinearLayout lyoLeftCol;
 *	private LinearLayout lyoGrpBtns;
 *	private LinearLayout lyoTopBtns;
 *	private LinearLayout lyoMiddleBtn;
 *	private LinearLayout lyoBottomBtns;
 *	private LinearLayout lyoGraphicalDesc;
 *	private LinearLayout lyoDesc;
 *	private LinearLayout lyoRightCol;
 *	private LinearLayout lyoMiddleCol;
*/
	private int desiredWidth;
	private int desiredHeight;
	private int graphViewHeightDimension = 400;		// just initializing it
	private int graphViewWidthDimension  = 400;		// just initializing it
	private int graphViewLinearLyoHeight;
	private int graphViewLinearLyoWidth;
	//private boolean blnFlagLyoChange = false;
	int myCanvasWidth;
	int myCanvasHeight; 
	private int numberIterations;
	private float xOffset = 0.0F;
	private float yOffset = 0.0F;
	private boolean fakeOut = true;
	private Bitmap myBitmap;
	private Bitmap mySzdBitmap;
	private ArrayList<CellularAutomataCell> cells = new ArrayList<CellularAutomataCell>();
	//private boolean firstPass = true;
	private final int ITERATION_SIZE = 101; 	// For the time being, the width of the cellular automata is always fixed at 101 cells.

	// size of each CA "cell" when plotted on the graph--will get reset later based on the size of the graphView

	private float rectWidth = 1.0F;		// just initializing it
	private float rectHeight = 1.0F; 	// just initializing it

	// canvas paints

	private Paint cellPaintOn;
	private Paint cellPaintOff;
	private Paint variablePaint;
	private Paint graphPaint;

	// constructors

	public GraphView(Context context, AttributeSet attrs) {

		// Used when inflating the view from XML.
		
		super(context, attrs);
	      
		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "in GraphViews first constructor method");		

		Init();

	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		
		// Used when constructing the view via code.
		
		super(context, attrs, defStyle);

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "in GraphViews second constructor method");

		Init();
		
	}

	public GraphView(Context context) {

		// Used when inflating the view from XML.
		
		super(context);

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "in GraphViews third constructor method");

		Init();

	}

	// Initialize the Paint objects.

	public void Init() {

		graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		graphPaint.setStyle(Paint.Style.STROKE);
		graphPaint.setStrokeWidth(1);
		graphPaint.setColor(Color.YELLOW);		// inconsequential--will never show

		// Cell paint on (filled in black squares)
		cellPaintOn = new Paint(Paint.ANTI_ALIAS_FLAG);
		cellPaintOn.setStyle(Paint.Style.FILL);
		cellPaintOn.setColor(Color.BLUE);

		// Cell paint off (filled in white squares)
		cellPaintOff = new Paint(Paint.ANTI_ALIAS_FLAG);
		cellPaintOff.setStyle(Paint.Style.FILL);
		cellPaintOff.setColor(Color.WHITE);

		variablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		variablePaint.setStyle(Paint.Style.FILL);
		variablePaint.setColor(Color.TRANSPARENT); 		// initialize. Will point to the other paints at various times
	}

	// This is the critical method that does all the hard work--it iterates through the respective CellularAutomataCell collection
	// passed in from Rule30Activity, Rule90Activity, Rule110Activity, or Rule250Activity, using its data to determine the paint color
	// to use and the grid placement to use for the squares it will draw to create the requested CA.

	@Override
	protected void onDraw(Canvas canvas)
	{

		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING THIS.GETLEFT = " +  this.getLeft());
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING THE HEIGHT OF THE GRAPHVIEW IS = " +  this.getHeight());
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING THE WIDTH OF THE GRAPHVIEW IS = " +  this.getWidth());
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING THE HEIGHT OF THE CANVAS IS = " +  canvas.getHeight());
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING THE WIDTH OF THE CANVAS IS = " +  canvas.getWidth());
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST ENTERED ONDRAW() AND B4 I DO ANYTHING Measured Height and Width (H x W) of this view = " +  this.getMeasuredHeight() + " x " + this.getMeasuredWidth());

		//if (!blnFlagLyoChange) {this.setVisibility(INVISIBLE);}

		//COMMENTED OUT FOR PRODUCTION: (getClass().getSimpleName(), "in GraphViews onDraw method");	

		// Cannot really do any work until we have lyoRendered set to the id for the linearlayout that contains this GraphView.
		//if (firstPass) {	// Only want to do these steps once even though the onDraw method may be called multiple times.

			if (lyoRendered != null)
			{
				if (lyoRendered.getHeight() > 0)
				{

						graphViewLinearLyoHeight = lyoRendered.getHeight();
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just set my variables graphViewLinearLyoHeight to lyo.getHeight = " + this.getGraphViewLinearLyoHeight());
						graphViewLinearLyoWidth = lyoRendered.getWidth();
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just set my variables graphViewLinearLyoWidth to lyoRendered.getWidth = " + this.getGraphViewLinearLyoWidth());
/*
 * 						Log.i(getClass().getSimpleName(), "lyoOuter.getHeight = " + lyoOuter.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoCAfullDesc.getHeight = " + lyoCAfullDesc.getHeight());					
 *						Log.i(getClass().getSimpleName(), "lyoCA3Columns.getHeight = " + lyoCA3Columns.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoLeftCol.getHeight = " + lyoLeftCol.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoGrpBtns.getHeight = " + lyoGrpBtns.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoTopBtns.getHeight = " + lyoTopBtns.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoMiddleBtn.getHeight = " + lyoMiddleBtn.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoBottomBtns.getHeight = " + lyoBottomBtns.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoGraphicalDesc.getHeight = " + lyoGraphicalDesc.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoDesc.getHeight = " + lyoDesc.getHeight());					
 *						Log.i(getClass().getSimpleName(), "lyoRightCol.getHeight = " + lyoRightCol.getHeight());
 *						Log.i(getClass().getSimpleName(), "lyoMiddleCol.getHeight = " + lyoMiddleCol.getHeight());
 *	
 *						Log.i(getClass().getSimpleName(), "tho think won't use it, just set my variables graphViewLinearLyoWidth to lyoRendered.getWidth = " + this.getGraphViewLinearLyoWidth());
 *						Log.i(getClass().getSimpleName(), "lyoOuter.getWidth = " + lyoOuter.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoCAfullDesc.getWidth = " + lyoCAfullDesc.getWidth());					
 *						Log.i(getClass().getSimpleName(), "lyoCA3Columns.getWidth = " + lyoCA3Columns.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoLeftCol.getWidth = " + lyoLeftCol.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoGrpBtns.getWidth = " + lyoGrpBtns.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoTopBtns.getWidth = " + lyoTopBtns.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoMiddleBtn.getWidth = " + lyoMiddleBtn.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoBottomBtns.getWidth = " + lyoBottomBtns.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoGraphicalDesc.getWidth = " + lyoGraphicalDesc.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoDesc.getWidth = " + lyoDesc.getWidth());					
 *						Log.i(getClass().getSimpleName(), "lyoRightCol.getWidth = " + lyoRightCol.getWidth());
 *						Log.i(getClass().getSimpleName(), "lyoMiddleCol.getWidth = " + lyoMiddleCol.getWidth());
*/						
						rectWidth = ((float)( (float)(graphViewLinearLyoWidth) ) / ITERATION_SIZE ); 
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "Difference between the width of the canvas and the width of the LinearLayout = " +  (canvas.getWidth() - lyoRendered.getWidth()));
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "Difference between the height of the canvas and the height of the LinearLayout = " +  (canvas.getHeight() - lyoRendered.getHeight()));		
						// Match the rectHeight to the rectWidth UNLESS by doing so will use up more real estate than is available.
						if ((rectWidth * (float)numberIterations) > (float)graphViewLinearLyoHeight)
						{
							rectHeight = ((float)(graphViewLinearLyoHeight ) / numberIterations);
						}
						else {
							rectHeight = rectWidth;
						}

						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just set my variables rectWidth/rectHeight = " +  rectWidth + "/" + rectHeight);
	
					//}

						int tempCanvasDensity = canvas.getDensity();
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "tempCanvasDensity = " + tempCanvasDensity);
						myCanvasWidth = canvas.getWidth();
						myCanvasHeight = canvas.getHeight();
						myBitmap = Bitmap.createBitmap(this.getGraphViewLinearLyoWidth(), this.getGraphViewLinearLyoHeight(), Config.RGB_565);
						//RETURN HERE: USE WHEN NEED TO FAKE OUT GRAPHICAL LAYOUT SOFTWARE:
						//myBitmap = Bitmap.createBitmap(598, 480, Config.RGB_565);
						myBitmap.setDensity(160);
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just created myBitmap and its size (H x W) = " +  myBitmap.getHeight() + " x " + myBitmap.getWidth());

						int myBitmapWidth = myBitmap.getWidth();
						int myBitmapHeight = myBitmap.getHeight();
						mySzdBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmapWidth, myBitmapHeight, false);
						//RETURN HERE: USE WHEN NEED TO FAKE OUT GRAPHICAL LAYOUT SOFTWARE:
						//mySzdBitmap = Bitmap.createScaledBitmap(myBitmap, 598, 480, false);
						mySzdBitmap.setDensity(160);
						//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just created mySzdBitmap and its size (H x W) = " +  mySzdBitmap.getHeight() + " x " + mySzdBitmap.getWidth());
		
						// Set background color for the canvas.
					
						canvas.drawColor(Color.GREEN);	// not used
						canvas.setDensity(160);
			
						if (mySzdBitmap != null)
						{

								// parameters for canvas.drawBitmap:
								// (1) the bitmap to draw onto the canvas/associate with the canvas,
								// (2) source: i.e., a rectangle defining the subset of the bitmap to be drawn (specifying the upper left
								// corner to the lower right corner of the rectangle)--will be using the entire bitmap here,
								// (3) destination: i.e., the rectangle (rectangular region) that the bitmap will be
								// scaled/translated to fit into (again specifying the upper left corner to the lower right corner of the
								// rectangle).
	
								variablePaint = graphPaint;
								canvas.drawBitmap(mySzdBitmap, new Rect(0,0, canvas.getWidth(),canvas.getHeight()), new Rect(0,0, canvas.getWidth(),canvas.getHeight()), variablePaint);
	
								// IT SHOULD NOT WORK THIS WAY BUT IT DOES: the bitmap is sized the way I want it to be sized--to match the
								// size of the layout containing this GraphView, but the canvas passed into the GraphView's ondraw method 
								// is invariable and not determined by me and is sized to match the entire screen of the device and its
								// color defaults to black. So after drawing the graph, there is remnant black canvas exposed on the sides
								// and bottom I do not want. The solution was to do a canvas.drawARGB using the color selected for
								// the background of the layout and the GraphView--this is equivalent to changing the background color of
								// the canvas because it fills the entire canvas's bitmap with this new color.
	
								canvas.drawARGB(255, 28, 134, 238);		// Red=28, Green=134, Blue=238 is the background color of the layout (equivalent to colors.xml color of opaque_medium_blue = hex 1c86ee)
	
								//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST DID THE DRAWARGB TO FILL THE ENTIRE CANVAS WITH BLUE");
								//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just drew on the canvas and its size (H x W) = " +  canvas.getHeight() + " x " + canvas.getWidth());
								//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "just drew on the canvas and densities (myBitmap/mySzdBitmap/canvas = " +  myBitmap.getDensity() + "/" + mySzdBitmap.getDensity() + "/" + canvas.getDensity());
						
								// parent draw
						
								super.onDraw(canvas);
	
							   // canvas.save();			// save current state of the canvas		
						
								int cellColor;
								int cellRow;
								int cellCol;
	
								//COMMENTED OUT FOR PRODUCTION: ////Log.i(getClass().getSimpleName(), "about to iterate through cells array and cells.size =" +  cells.size());
								
								for (Iterator<CellularAutomataCell> it=cells.iterator(); it.hasNext();)
								{
	
									CellularAutomataCell tmp = it.next();
						
									cellColor = tmp.getCellColor();
									cellRow = tmp.getCellRow();
									cellCol = tmp.getCellColumn();
									
									if ( (cellRow >= 0) && (cellCol >= 0) )
									{
	
										xOffset = (float)(cellCol - 1) * rectWidth;
										yOffset = (float)(cellRow - 1) * rectHeight;
			
										//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "xOffset/yOffset =  " +  xOffset + "/" + yOffset);
	
										canvas.translate((xOffset), (yOffset));
					
								    	if (cellColor == 0)
										{
								    		variablePaint = cellPaintOff;
										}
										else
										{
								    		variablePaint = cellPaintOn;
										}
	
										canvas.drawRect( (float)0, (float)0, rectWidth, rectHeight, variablePaint );
	
										canvas.translate((float)(-xOffset), (float)(-yOffset));		// undo the translation
										
									}
	
								}	// end iterating through CellularAutomataCell collection
/*
 *								Log.i(getClass().getSimpleName(), "AFTER-just set my variables graphViewLinearLyoWidth to lyoRendered.getWidth = " + this.getGraphViewLinearLyoWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoOuter.getHeight = " + lyoOuter.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoCAfullDesc.getHeight = " + lyoCAfullDesc.getHeight());					
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoCA3Columns.getHeight = " + lyoCA3Columns.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoLeftCol.getHeight = " + lyoLeftCol.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoGrpBtns.getHeight = " + lyoGrpBtns.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoTopBtns.getHeight = " + lyoTopBtns.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoMiddleBtn.getHeight = " + lyoMiddleBtn.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoBottomBtns.getHeight = " + lyoBottomBtns.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoGraphicalDesc.getHeight = " + lyoGraphicalDesc.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoDesc.getHeight = " + lyoDesc.getHeight());					
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoRightCol.getHeight = " + lyoRightCol.getHeight());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoMiddleCol.getHeight = " + lyoMiddleCol.getHeight());
 *	
 *								Log.i(getClass().getSimpleName(), "AFTER-tho think won't use it, just set my variables graphViewLinearLyoWidth to lyoRendered.getWidth = " + this.getGraphViewLinearLyoWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoOuter.getWidth = " + lyoOuter.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoCAfullDesc.getWidth = " + lyoCAfullDesc.getWidth());					
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoCA3Columns.getWidth = " + lyoCA3Columns.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoLeftCol.getWidth = " + lyoLeftCol.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoGrpBtns.getWidth = " + lyoGrpBtns.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoTopBtns.getWidth = " + lyoTopBtns.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoMiddleBtn.getWidth = " + lyoMiddleBtn.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoBottomBtns.getWidth = " + lyoBottomBtns.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoGraphicalDesc.getWidth = " + lyoGraphicalDesc.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoDesc.getWidth = " + lyoDesc.getWidth());					
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoRightCol.getWidth = " + lyoRightCol.getWidth());
 *								Log.i(getClass().getSimpleName(), "AFTER-lyoMiddleCol.getWidth = " + lyoMiddleCol.getWidth());
*/
						}	// end if mySzdBitmap != null
		
				}	// end lyoRendered.height > 0
	
			}	// end lyoRendered != to null

			//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "JUST BEFORE LEAVE ONDRAW ROUTINE, SIZES ARE (canvas, myBitmap, mySzdBitmap)(H x W) = " +  canvas.getHeight() + " x " + canvas.getWidth() + "; " + myBitmap.getHeight() + " x " + myBitmap.getWidth() + "; " + mySzdBitmap.getHeight() + " x " + mySzdBitmap.getWidth());
			//firstPass = false;
			//if (blnFlagLyoChange) {this.setVisibility(VISIBLE);}

		//}		// end if firstPass (is true)

	}	// end method
 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		
		// Method used to compute the view's height and width. Default size is 100 x 100.

		// This is what the layout will use to establish the amount of room the graph takes up on the layout.

		//COMMENTED OUT FOR PRODUCTION: ////Log.i(getClass().getSimpleName(), "in onMeasure and about to call super onMeasure with desiredWidth = " + desiredWidth + " and desiredHeight = " + desiredHeight);
	    
		// Calling setMeasuredDimension() in onMeasure() should definitely make it be the size you specify. 
	    // Make sure you are not calling the super class implementation or doing your code after
	    // calling the super class, since there is a default implementation in View to set the measurements.
		// Also make sure you are using wrap_content for the view, not match_parent.

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
		if (this.getFakeOut()) {

			// This is what the layout will use to establish the amount of room the graph takes up on the layout.

			desiredWidth = 200;

			// Again, this is what the layout will use.

			desiredHeight = 100;

			int useWidth  = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.UNSPECIFIED);
		    int useHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.UNSPECIFIED);

		    setMeasuredDimension(useWidth, useHeight);

		} else {

			if (lyoRendered != null)
			{
				//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "lyoRendered ISNT NULL!");

				if (lyoRendered.getHeight() > 0)
				{

					//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "lyoRendered HAS A NON-ZERO HEIGHT of" + lyoRendered.getHeight());
					
					// This is what the layout will use to establish the amount of room the graph takes up on the layout.

					if (graphViewLinearLyoWidth != 0)
					{
						desiredWidth  = graphViewLinearLyoWidth;
					} else {
						//desiredWidth = graphViewWidthDimension;		// after screen orientation chg this variable is getting redeclared and initialized to 0 and ondraw is NOT getting called before this could end up setting the desiredWidth to 0, in which case ondraw() will NEVER get called
						desiredWidth = lyoRendered.getWidth();
					}
					if (graphViewLinearLyoHeight != 0)
					{
						desiredHeight = graphViewLinearLyoHeight;
					} else {
						//desiredHeight = graphViewHeightDimension;	// ditto
						desiredHeight = lyoRendered.getHeight();
					}
					
					//blnFlagLyoChange = true;

				} else {

					// These two values are what the layout will use to establish the amount of room the graph takes up on the layout initially.

					desiredHeight = graphViewHeightDimension;		// has an initial default value
					desiredWidth  = graphViewWidthDimension;		// has an initial default value

				}

			} else {

				desiredHeight = graphViewHeightDimension;		// has an initial default value
				desiredWidth  = graphViewWidthDimension;		// has an initial default value

			}

			int useWidth  = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.UNSPECIFIED);
		    int useHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.UNSPECIFIED);

		    setMeasuredDimension(useWidth, useHeight);
			
		}

	  //COMMENTED OUT FOR PRODUCTION: ////Log.i(getClass().getSimpleName(), "in onMeasure and just called setMeasuredDimension with useWidth = " + useWidth + " and useHeight = " + useHeight);

	}

	private int measureWidth(int widthMeasureSpec)
	{
		int specSize = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingLeft() - this.getPaddingRight();
	    return specSize;
	}
	 
	private int measureHeight(int heightMeasureSpec)
	{
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
	    return specSize;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		//COMMENTED OUT FOR PRODUCTION: ////Log.i(getClass().getSimpleName(), "in onSizechanged and about to call super.onSizeChanged with w/h/oldw/oldh values of: " + w + "/" + h + "/" + oldw + "/" + oldh);
		super.onSizeChanged(w, h, oldw, oldh);
		double sizeChangedWidth = (double) this.getWidth();
		double sizeChangedHeight = (double) this.getHeight();
		//COMMENTED OUT FOR PRODUCTION: Log.i(getClass().getSimpleName(), "in onSizechanged after call to super.onSizeChanged, sizeChangedWidth=this.getWidth() = " + sizeChangedWidth + " and sizeChangedHeight=this.getHeight() = " + sizeChangedHeight);

	}

	public int getGraphViewLinearLyoHeight() {
		return graphViewLinearLyoHeight;
	}

	public void setGraphViewLinearLyoHeight(int graphViewLinearLyoHeight) {
		this.graphViewLinearLyoHeight = graphViewLinearLyoHeight;
	}

	public int getGraphViewLinearLyoWidth() {
		return graphViewLinearLyoWidth;
	}

	public void setGraphViewLinearLyoWidth(int graphViewLinearLyoWidth) {
		this.graphViewLinearLyoWidth = graphViewLinearLyoWidth;
	}

	public LinearLayout getLyoRendered() {
		return lyoRendered;
	}
	public void setLyoRendered(LinearLayout lyoRendered) {
		this.lyoRendered = lyoRendered;
	}

/*
	public LinearLayout getLyoOuter() {
		return lyoOuter;
	}

	public void setLyoOuter(LinearLayout lyoOuter) {
		this.lyoOuter = lyoOuter;
	}

	public LinearLayout getLyoCAfullDesc() {
		return lyoCAfullDesc;
	}

	public void setLyoCAfullDesc(LinearLayout lyoCAfullDesc) {
		this.lyoCAfullDesc = lyoCAfullDesc;
	}

	public LinearLayout getLyo3Columns() {
		return lyoCA3Columns;
	}

	public void setLyoCA3Columns(LinearLayout lyoCA3Columns) {
		this.lyoCA3Columns = lyoCA3Columns;
	}

	public LinearLayout getLyoLeftCol() {
		return lyoLeftCol;
	}

	public void setLyoLeftCol(LinearLayout lyoLeftCol) {
		this.lyoLeftCol = lyoLeftCol;
	}

	public LinearLayout getLyoGrpBtns() {
		return lyoGrpBtns;
	}

	public void setLyoGrpBtns(LinearLayout lyoGrpBtns) {
		this.lyoGrpBtns = lyoGrpBtns;
	}

	public LinearLayout getLyoTopBtns() {
		return lyoTopBtns;
	}

	public void setLyoTopBtns(LinearLayout lyoTopBtns) {
		this.lyoTopBtns = lyoTopBtns;
	}

	public LinearLayout getLyoMiddleBtn() {
		return lyoMiddleBtn;
	}

	public void setLyoMiddleBtn(LinearLayout lyoMiddleBtn) {
		this.lyoMiddleBtn = lyoMiddleBtn;
	}

	public LinearLayout getLyoBottomBtns() {
		return lyoBottomBtns;
	}

	public void setLyoBottomBtns(LinearLayout lyoBottomBtns) {
		this.lyoBottomBtns = lyoBottomBtns;
	}

	public LinearLayout getLyoGraphicalDesc() {
		return lyoGraphicalDesc;
	}

	public void setLyoGraphicalDesc(LinearLayout lyoGraphicalDesc) {
		this.lyoGraphicalDesc = lyoGraphicalDesc;
	}

	public LinearLayout getLyoDesc() {
		return lyoDesc;
	}

	public void setLyoDesc(LinearLayout lyoDesc) {
		this.lyoDesc = lyoDesc;
	}

	public LinearLayout getLyoRightCol() {
		return lyoRightCol;
	}

	public void setLyoRightCol(LinearLayout lyoRightCol) {
		this.lyoRightCol = lyoRightCol;
	}

	public LinearLayout getLyoMiddleCol() {
		return lyoMiddleCol;
	}

	public void setLyoMiddleCol(LinearLayout lyoMiddleCol) {
		this.lyoMiddleCol = lyoMiddleCol;
	}
*/
	public float getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(float rectWidth) {
		this.rectWidth = rectWidth;
	}

	public float getRectHeight() {
		return rectHeight;
	}

	public void setRectHeight(float rectHeight) {
		this.rectHeight = rectHeight;
	}

	public boolean getFakeOut() {
		return fakeOut;
	}

	public void setFakeOut(boolean fakeOut) {
		this.fakeOut = fakeOut;
	}

	public ArrayList<CellularAutomataCell> getCells() {
		return cells;
	}

	public void setCells(ArrayList<CellularAutomataCell> cells) {
		this.cells = cells;
	}

	public int getNumberIterations()
	{
		return numberIterations;
	}

	public void setNumberIterations(int numberIterations)
	{
		this.numberIterations = numberIterations;
	}

}