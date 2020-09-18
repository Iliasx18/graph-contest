import jdg.graph.DirectedGraph;
import jdg.io.GraphLoader;
import jdg.io.GraphReader_Json;
import jdg.io.GraphWriter_Json;
import processing.core.PApplet;

	/**
	 * Main program that takes as input a JSON and compute an upward grid drawing
	 * that minimizes the number of crossings.
	 * 
	 * @author Luca Castelli Aleardi (INF421, 2019)
	 *
	 */

public class ComputeUpwardGridDrawing {
	
	public static void main(String[] args) {
		System.out.println("Tools for the \"Graph Drawing Contest 2020: Live Challenge\"");
		if(args.length<1) {
			System.out.println("Error: one argument required: input file in JSON format");
			System.exit(0);
		}
		
		String inputFile=args[0]; // input file, encoded as JSON format
		DirectedGraph g=GraphLoader.loadGraph(inputFile); // input graph
		int[] drawingBounds=GraphReader_Json.readDrawingAreaBounds(inputFile); // reading width and height of the drawing area
		
		// set the input parameters for drawing the graph layout
		GraphRenderer.sizeX=600; // setting canvas width (number of pixels)
		GraphRenderer.sizeY=600; // setting canvas height (pixels)
		GraphRenderer.inputGraph=g; // set the input graph (for rendering)
		GraphRenderer.drawingWidth=drawingBounds[0]; // setting the width of the drawing area
		GraphRenderer.drawingHeight=drawingBounds[1];  // setting the width of the drawing area
		
		if(UpwardDrawing.hasInitialLayout(g)) // check whether the nodes are provided with an initial embedding
			System.out.println("The input graph has an initial embedding");
		else
			System.out.println("The input graph is not provided with an initial embedding");
		
		// initialize the upward drawing
		UpwardDrawing ud=new UpwardDrawing(g, GraphRenderer.drawingWidth, GraphRenderer.drawingHeight);
		
		
//		System.out.println("Computing valid initial layout :");
//		long startTime=System.nanoTime(), endTime;
//		ud.computeValidInitialLayout(); // first phase: compute a valid drawing (if necessary)
//		endTime=System.nanoTime();
//        double duration=(double)(endTime-startTime)/1000000000.;
//    	System.out.println("Initial layout computed in "+duration+" seconds");
//    	boolean isValid=ud.checkValidity();// check whether the result is a valid drawing
//    	String s;
//		if (isValid==true) s = "valid";
//		else s = "invalid";
//		System.out.println("Initial graph is "+s);
//		int crossings=ud.computeCrossings(); // count the number of crossings
//		System.out.println("Initial number of crossings : "+crossings);
//		System.out.println("Launching local search heuristic :");
//		startTime=System.nanoTime();
//		ud.localSearchHeuristic();
//		endTime=System.nanoTime();
//		duration=(double)(endTime-startTime)/1000000000.;
//    	System.out.println("Local search time : "+duration+" seconds");
//    	isValid=ud.checkValidity();// check whether the result is a valid drawing
//    	if (isValid==true) s = "valid";
//		else s = "invalid";
//		System.out.println("Graph after local search is : "+s);
//		crossings=ud.computeCrossings(); // count the number of crossings
//		System.out.println("Crossings after local search :"+ud.computeCrossings());
//		System.out.println("Now launching force directed heuristics :");
//		startTime=System.nanoTime();
//		ud.forceDirectedHeuristic();
//		
//		endTime=System.nanoTime();
//		duration=(double)(endTime-startTime)/1000000000.;
//    	System.out.println("Force directed heuristic time "+duration+" seconds");
//		isValid=ud.checkValidity();// check whether the result is a valid drawing
//    	if (isValid==true) s = "valid";
//		else s = "invalid";
//		System.out.println("Graph after force directed heuristics is : "+s);
//		crossings=ud.computeCrossings(); // count the number of crossings
//		System.out.println("Final number of crossings : "+crossings);
		
		ud.computeUpwardDrawing(); // second phase: minimize the number of crossings
		
		
		// write the upward drawing computed by your program to a Json file
		GraphWriter_Json.write(g, GraphRenderer.drawingWidth, GraphRenderer.drawingHeight, "output.json");
		
		// uncomment the line below to show a 2D layout of the graph
		PApplet.main(new String[] { "GraphRenderer" }); // launch the Processing viewer

	}

}
