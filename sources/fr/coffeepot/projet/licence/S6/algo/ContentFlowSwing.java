package fr.coffeepot.projet.licence.S6.algo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.ArcFlow;
import fr.upem.pperonne.caterer.GraphFlow;

@SuppressWarnings({ "serial", "rawtypes" })
public class ContentFlowSwing extends ContentSwing<GraphFlow> {
	public ContentFlowSwing( GraphFlow graph, String title ) {
		super( graph, title );
	}

	protected void paintArc( Graphics graphics, Arc<?> arc ) throws IllegalArgumentException {
		super.paintArc( graphics, arc );

		NodeSwing origine = getNodeSwing( arc.getOrigine() ),
			destination = getNodeSwing( arc.getDestination() );
		Dimension dimensionOri = origine.getSize(),
			dimensionDest = destination.getSize();
		Point pointOri = origine.getLocation(),
			pointDest = destination.getLocation();
		pointOri.x += dimensionOri.width  / 2;
		pointOri.y += dimensionOri.height / 2;
		pointDest.x += dimensionDest.width  / 2;
		pointDest.y += dimensionDest.height / 2;

		String flow = new String( ((ArcFlow) arc).getFlow() + "" );
		Color color = graphics.getColor();
		graphics.setColor( Color.CYAN );
		char name[] = flow.toCharArray();
		graphics.drawChars(
			name,
			0,
			name.length,
			( pointOri.x + pointDest.x ) / 2,
			( pointOri.y + pointDest.y ) / 2 + 10
		);
		graphics.setColor( color );
		add( new InfoSwing() );
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof ContentFlowSwing ) ) {
			return false;
		}
		return super.equals( o );
	}
}
