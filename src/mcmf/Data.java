package mcmf;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;

/**
 * This class reads in the information required for the program, draws graph and
 * runs program, updates the data with and saves the results.
 */

public class Data {
	private int numStudents;
	private int numProjects;
	private String inputFileName;
	private String outputFileName;
	private int[][] studentPrefs;
	private int[][] studentRanks;
	private int[][] allocatedStudents;
	private int[] projectCaps;
	private int[] projectAllocs;
	private int[] profile;
	private int maxPrefListLen = 0;
	private boolean[] acceptable;
	private int size;
	private int cost = 0;
	private int algNum;
	private PrintWriter pr;

	public Data(String s1, String s2) {
		inputFileName = s1;
		outputFileName = s2;
		
	}

	/**
	 * reads in the data from text files
	 */
	public void readData() {
		try {
			FileReader fr = new FileReader(inputFileName);
			Scanner in = new Scanner(fr);
			String line = in.nextLine();
			numStudents = Integer.parseInt(line);
			line = in.nextLine();
			numProjects = Integer.parseInt(line);
			studentPrefs = new int[numStudents][numProjects];
			studentRanks = new int[numStudents][numProjects];
			projectCaps = new int[numProjects];
			projectAllocs = new int[numProjects];
			acceptable = new boolean[numProjects];
			allocatedStudents = new int[numProjects][numStudents];
			profile = new int[numProjects];
			for (int i = 0; i < numStudents; i++) {
				line = in.nextLine();
				String[] prefList = line.split("[()\t ]+");
				int j = 0;
				for (String e : prefList) {
					if (!e.equals("")) {
						int pref = Integer.parseInt(e);
						studentPrefs[i][j] = pref;
						// System.out.print(j+" "+pref+"   ");
						j++;
					}
				}
				// System.out.println();
				if (j > maxPrefListLen)
					maxPrefListLen = j;
				line = line + " ";
				int rank = 0;
				int tieElements = 0;
				boolean inNum = false;
				boolean inTie = false;
				for (j = 0; j < line.length(); j++) {
					char c = line.charAt(j);
					// System.out.println(c+" "+rank+" "+tieElements+" "+inNum+" "+inTie);
					if (c >= '0' && c <= '9') {
						if (!inNum) { // first occurrence of a number
							if (!inTie) {
								rank++; // not in tie
								tieElements = 1;
							} else
								tieElements++; // in a tie
							inNum = true;
						}
					} else if (c == '(') {
						inTie = true;
						inNum = false;
						rank++;
						tieElements = 0;
					} else if (c == ')') {
						inTie = false;
						inNum = false;
						int proj = studentPrefs[i][rank + tieElements - 2];
						acceptable[proj - 1] = true;
						studentRanks[i][proj - 1] = rank;
						// System.out.print(proj+" "+rank+" "+tieElements+"    ");
						rank += tieElements - 1;
						tieElements = 0;
					} else if (c == ' ' || c == '\t') {
						if (inNum) {
							inNum = false;
							int proj = studentPrefs[i][rank + tieElements - 2];
							acceptable[proj - 1] = true;
							studentRanks[i][proj - 1] = rank;
							// System.out.print(proj+" "+rank+" "+tieElements+"    ");
						}
					}
				}
				// System.out.println();
			}
			for (int j = 0; j < numProjects; j++) {
				line = in.nextLine();
				int cap = Integer.parseInt(line.trim());
				projectCaps[j] = cap;
			}

			in.close();
		} catch (IOException ioe) {
			System.out.println("Error reading from text file");
		} catch (NumberFormatException nf) {
			System.out.println("Error with number format");
		}
	}

	/**
	 * constructs a graph from the data and runs the minimum cost-maximum flow
	 * algorithm on the graph
	 */
	public void drawGraphAndFindMax() {
		for (algNum = 1; algNum <= 3; algNum++) {
			int numVertices = numStudents + numProjects + 2;
			Graph g = new Graph(numVertices); // plus source and sink

			g.addVertex(null); // add source, ignore return value
			// add vertex for each student and project
			for (int i = 0; i < numStudents; i++)
				g.addVertex(null);
			for (int i = 0; i < numProjects; i++)
				if (acceptable[i])
					g.addVertex(null);
				else
					g.incNumVertices();
			g.addVertex(null); // add sink, ignore return value

			// add edges
			Vertex[] vertices = g.getVertices();
			for (int i = 0; i < numStudents; i++) {
				g.addEdge(1, vertices[0], vertices[i + 1], 0); // source to
																// student
				int j = 0;
				while (j < numProjects && studentPrefs[i][j] > 0) {
					int pref = studentPrefs[i][j];
					int rank = studentRanks[i][pref - 1];
					long cost = 0;
					if (algNum == 1) // mincost
						cost = rank;
					else if (algNum == 2) { // generous
						// long maxCost = Math.pow((double) numStudents,
						// (double) numProjects - 1);
						cost = (long) (Math.pow((double) numStudents,
								(double) (rank - 1)) - 1.0);
						if (cost > Long.MAX_VALUE) {
							System.out.println("Weights are too large.");
							System.exit(0);
						}
					} else { // greedy
						long maxCost = (long) Math.pow((double) numStudents,
								(double) (maxPrefListLen - 1));
						if (maxCost == Long.MAX_VALUE) {
							double cost0 = Math.pow((double) numStudents,
									(double) (maxPrefListLen - 1));
							double cost1 = Math.pow((double) numStudents,
									(double) (maxPrefListLen - rank));
							cost = (long) (cost0 - cost1);
							if (cost == Long.MAX_VALUE) {
								System.out.println("Weights are too large.");
								System.exit(0);
							} else
								System.out
										.println("Weights are too large.  Possible loss of precision.");
						} else {
							long cost1 = (long) Math.pow((double) numStudents,
									(double) (maxPrefListLen - rank));
							cost = maxCost - cost1;
						}
					}
					// System.out.println((i+1)+" "+pref+" "+rank+"  "+cost);
					g.addEdge(1, vertices[i + 1], vertices[pref + numStudents],
							cost); // student to project
					j++;
				}
			}
			for (int j = 0; j < numProjects; j++)
				if (acceptable[j])
					g.addEdge(projectCaps[j], vertices[numStudents + j + 1],
							vertices[numVertices - 1], 0); // project to sink
			// System.out.println((j+1)+" "+projectCaps[j]);

			// perform allocation algorithm on graph and save the
			// results in results text file
			MinCostMaxFlow mf = new MinCostMaxFlow(g);
			mf.getMinCostMaxFlow();
			System.out.println();
			writeResults(g);
		}
	}

	/**
	 * writes the results of the matchings to a text file
	 * 
	 * @param g
	 *            graph on which algorithm was run
	 */
	private void writeResults(Graph g) {
		
			Vertex[] vertices = g.getVertices();
			
			System.out.println("Min cost matching\n");
			System.out.println("Student   Project");
			
			for (int i = 0; i < numStudents; i++) {
				Vertex v1 = vertices[i + 1];
				Linked_List adjList = v1.getEdgesOut();
				adjList.setToStart();
				while (adjList.hasNext()) {
					Vertex v2 = (Vertex) adjList.getCurrentItem();
					Edge e = g.getEdge(v1, v2);
					if (e.getFlow() == 1) {
						int projectNum = v2.getIndex() - numStudents;
						int rank = studentRanks[i][projectNum - 1];
						System.out.printf("%5d     %5d    %n", i + 1, projectNum);
						profile[rank]++;
						allocatedStudents[projectNum - 1][projectAllocs[projectNum - 1]] = i + 1;
						projectAllocs[projectNum - 1]++;
						cost += rank;
					}
					adjList.increment();
				}
				Edge e = g.getEdge(vertices[0], v1);
				if (e.getFlow() == 0)
					System.out.printf("%5d   unmatched%n", i + 1);
			}
			System.out.println();
			System.out.println("Project   Capacity   Filled   Assignees");
			for (int j = 0; j < numProjects; j++) {
				System.out.printf("%5d     %5d     %5d       ", j + 1, projectCaps[j],
						projectAllocs[j]);
				for (int k = 0; k < projectAllocs[j]; k++) {
					System.out.print(allocatedStudents[j][k] + "  ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.print("Profile: (");
			boolean first = true;
			for (int j = 0; j < numProjects; j++)
				if (profile[j] > 0) {
					if (!first) {
						System.out.print(",");
					}
					System.out.print(profile[j]);
					first = false;
				}
			System.out.println(")");
			System.out.println("Size: " + size);
			System.out.println("Cost: " + cost);
			System.out.println();
			System.out.println();
			cost = 0;
			for (int j = 0; j < numProjects; j++) {
				profile[j] = 0;
				projectAllocs[j] = 0;
			}
	}
	
	/*
	 * ==================================== 	MODIFICATIONS 	=========================================== 
	 */
	
	public Data(){}
	
	/**
	 * constructs a mcmf graph and runs the algorithm
	 */
	public Graph drawGraphRunMinCost(ford_fulkerson.graph.Graph myGraph){
			int numVertices = myGraph.getVertices().size();
			
			Graph g = new Graph(numVertices); 
			
			// add vertices
			for (int i = 0; i < numVertices; i++){
				g.addVertex(null);
			}
			
			Vertex[] vertex = g.getVertices();
			// add edges
			for (ford_fulkerson.graph.Edge e : myGraph.getEdges()){
				int parentID;
				int destID;
				// retrieving parent and destination id's which are index numbers.
				
				if (e.getParent().equals(myGraph.source())){
					// if the edge is source-to-vertex, then parent index is 0
					parentID = 0;
				} else {
					parentID = e.getParent().getVertexID() ;
				}
				
				
				if (e.getDestination().equals(myGraph.sink())){
					// if the edge is vertex-to-sink, then destination index is the last vertex
					destID = vertex.length-1;
				} else {
					destID = e.getDestination().getVertexID();
				}
				//System.out.println("parent : " + parentID + " destination: " + destID + " array size: " + vertex.length + " numVertices: " + numVertices);
				g.addEdge(e.getCapacity(), vertex[parentID], vertex[destID], e.getWeight());
				
			}
			
			MinCostMaxFlow mf = new MinCostMaxFlow(g);
			Graph graph = mf.getMinCostMaxFlow();
			return graph;
	}
	
}
