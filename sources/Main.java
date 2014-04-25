import java.util.ArrayList;

import fr.coffeepot.projet.licence.S6.algo.Display;
import fr.coffeepot.projet.licence.S6.algo.DisplaySwing;
import fr.upem.pperonne.caterer.Graph;

@SuppressWarnings("rawtypes")
public class Main {
	private ArrayList<Graph> graphs = new ArrayList<>();
	private static Display display;

	public Main() {
		DisplaySwing dis = (DisplaySwing) (display = new DisplaySwing());
		dis.pack();
		dis.setVisible( true );
		dis.repaint();
	}
	
	public void add( Graph graph, String title ) {
		graphs.add( graph );
		display.add( graph, title );
	}

	public static void main( String[] args ) {
		new Main();
	}
}
