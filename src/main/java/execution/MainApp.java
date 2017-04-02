package execution;

import org.graphstream.graph.Graph;

import algorithms.SA;
import utils.ParseData;

/**
 * sa Algorithm aiming to find the shortest path between all nodes of a graph.
 *
 * @author Boulbes Thomas
 * @version 1.0
 * @since 23/03/2017
 */
public class MainApp {

	public static void main(String[] args) {
		try {

			System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			SA sa = new SA();
			Graph g = ParseData.generate();
			sa.setGraph(g);
			sa.setSource(g.getNode(0));
			sa.init();
			g.display(true);
			final long startTime = System.currentTimeMillis();
			sa.compute();
			final long endTime = System.currentTimeMillis();
			g.display(true);

			System.out.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
