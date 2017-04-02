package algorithms;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SA Algorithm aiming to find the shortest path between all nodes of a graph.
 *
 * @author Salomon Clément
 * @version 1.0
 * @since 27/03/2017
 */
public class SA {

	private double temperature = 10000;
	double coolingRate = 0.003;
	private Node source;
	private Graph graph;
	private long time = 1000;

	/**
	 * This method is used to get the input values from the user and to
	 * initiliaze the graph.
	 */
	public void init() {

		Exception exception;
		Integer result = null;
		JTextField initTemp = new JTextField("10000",5);
		JTextField coolingRateT = new JTextField("0.003",5);

		JPanel myPanel = new JPanel();
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(new JLabel("Initial temperature :"));
		myPanel.add(initTemp);
		myPanel.add(new JLabel("Cooling rate :"));
		myPanel.add(coolingRateT);

		do {
			try {
				exception = null;
				result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter values",
						JOptionPane.OK_CANCEL_OPTION);
				this.temperature = Float.parseFloat(initTemp.getText());
				this.coolingRate = Float.parseFloat(coolingRateT.getText());
			} catch (Exception e) {
				exception = e;
				e.printStackTrace();
			}
		} while (exception != null && result != JOptionPane.CANCEL_OPTION);
	}

	/**
	 * This method is used to compute the SA algorithm on the graph.
	 */
	public void compute() {
		ArrayList<Node> currentSolution = generateRandomTour(graph);
		System.out.println(temperature);
		ArrayList<Node> best = currentSolution;

		//till the system has cooled
		while (temperature>1) {

			// Create new neighbour tour
			ArrayList<Node> newSolution = currentSolution;

			// Get a random positions in the tour
			int tourPos1 = (int) (newSolution.size() * Math.random());
			int tourPos2 = (int) (newSolution.size() * Math.random());

			// Get the cities at selected positions in the tour
			Node firstSwap = newSolution.get(tourPos1);
			Node secondSwap = newSolution.get(tourPos2);

			// Swap them
			newSolution.set(tourPos2, firstSwap);
			newSolution.set(tourPos1, secondSwap);

			System.out.println("bonjour la boucle");
			// Get energy of solutions
			int currentEngery = getDistanceFromTour(currentSolution);
			int neighbourEngery = getDistanceFromTour(newSolution);

			// Decide if we should accept the neighbour
			if (neighbourEngery < currentEngery || metropolis(newSolution, currentSolution, temperature) > Math.random()) {
				currentSolution = newSolution;
			}

			// Keep track of the best solution found
			if (getDistanceFromTour(currentSolution) < getDistanceFromTour(best)) {
				best = currentSolution;
			}

			// Cool system
			temperature *= 1-coolingRate;
		}

		clearDisplayPath();
		displayBestPath(best);
		System.out.println("Best path : " + getPath(best));
		System.out.println("With a length of " + getDistanceFromTour(best));
	}

	/**
	 *	This method is used to generate the initial random tour for the
	 *	simulated annealing
	 */

	public ArrayList<Node> generateRandomTour(Graph graph) {
		ArrayList<Node> tour = new ArrayList<>();
		for(Node n : graph.getEachNode()){
			tour.add(n);
		}
		Collections.shuffle(tour);
		return tour;
	}

	/**
	 * 	This method is used to get the distance of a tour
	 */

	public int getDistanceFromTour(ArrayList<Node> tour) {
		int sum = 0;
		for(int i = 0; i < tour.size()-1; i++){
			sum += getEdgeWeight(tour.get(i), tour.get(i+1));
		}
		sum = sum + getEdgeWeight(tour.get(tour.size()-1), tour.get(0));
		return sum;
	}


	/**
	 *	This method is used to get the list of edge from an ordered list of Node (tour)
	 */
	public List<Edge> getPath(ArrayList<Node> tour){
		List<Edge> path = new ArrayList<>();
		for(int i = 0; i < tour.size()-1; i++){
			path.add(getEdge(tour.get(i), tour.get(i+1)));
		}
		path.add(getEdge(tour.get(tour.size()-1), tour.get(0)));
		return path;
	}

	/**
	 * This method is used to clear the display of all edges
	 *
	 */
	public void displayCurrentPath(ArrayList<Node> tour) {
		List<Edge> path;
		path = getPath(tour);
		for (Edge edge : path) {
			graph.getEdge(edge.getId()).removeAttribute("ui.hide");
		}
	}

	/**
	 * This method is used to clear the display of all edges
	 *
	 */
	public void clearDisplayPath() {
		for (Edge edge : graph.getEachEdge()) {
			graph.getEdge(edge.getId()).addAttribute("ui.hide");
		}
	}

	/**
	 * This method is used to clear the display of all edges
	 *
	 */
	public void displayBestPath(ArrayList<Node> best) {
		List<Edge> path;
		path = getPath(best);
		for (Edge edge : path) {
			graph.getEdge(edge.getId()).removeAttribute("ui.hide");
			graph.getEdge(edge.getId()).setAttribute("ui.class", "best");
		}
	}

	/**
	 *	This method is used to calculate the metropolis which evaluate the acceptance probability
	 *	@param v
	 *			is the arraylist of the neighbour
	 *	@param s
	 *			is the arraylist of the current tour
	 *	@param t
	 *			is the current temperature
	 */

	public double metropolis(ArrayList<Node> v, ArrayList<Node> s, double t){
		return Math.exp(-(Math.abs(getDistanceFromTour(s)-getDistanceFromTour(v))/t));
	}


	/**
	 * This method is used to retrieve an edge from its starting node and ending
	 * node.
	 * 
	 * @param start
	 *            This is the starting node.
	 * @param end
	 *            This is the ending node.
	 */
	public Edge getEdge(Node start, Node end) {
		return graph.getEdge(start.getId() + "-" + end.getId());
	}

	/**
	 * This method is used to retrieve an edge length attribute from its
	 * starting node and ending node.
	 *
	 * @param start
	 *            This is the starting node.
	 * @param end
	 *            This is the ending node.
	 */
	public int getEdgeWeight(Node start, Node end) {
		return getEdge(start, end).getAttribute("length");
	}


	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getCoolingRate() {
		return coolingRate;
	}

	public void setCoolingRate(double coolingRate) {
		this.coolingRate = coolingRate;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
