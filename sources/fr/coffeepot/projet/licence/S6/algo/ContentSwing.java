package fr.coffeepot.projet.licence.S6.algo;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.Graph;
import fr.upem.pperonne.caterer.Node;

@SuppressWarnings({ "serial", "rawtypes" })
public class ContentSwing extends Container implements MouseMotionListener {
	private final Graph<?,?> graph;
	private int /*poidsMax, poidsMin,*/ coutMax;
	private Point min = new Point( 0, 0 ), max = new Point( 1, 1 );
	private static Dimension dimensionSommet = new Dimension( 30, 20 );
	private String title;

	public ContentSwing( final Graph<?,?> graph, String title ) {
		this.graph = graph;
		this.title = title;
	}

	private void updateMinMaxCoords( Point position ) {
		if ( min == null || max == null ) {
			min = new Point( position );
			max = new Point( position );
		} else {
			min.x = Math.min( min.x, position.x );
			min.y = Math.min( min.y, position.y );
			max.x = Math.max( max.x, position.x );
			max.y = Math.max( max.y, position.y );
		}
	}

	private boolean hasNode( Node S ) {
		for ( Component component: getComponents() ) {
			if ( component instanceof DisplayNode && ((DisplayNode) component).isNode( S ) ) {
				return true;
			}
		}
		return false;
	}

	private DisplayNode updateList( Node S ) {
		if ( ! hasNode( S ) ) {
			DisplayNode ds = new DisplayNode( S, getSize() );
			ds.addMouseMotionListener( this );
			add( ds );
			ds.setSize( dimensionSommet );
			return ds;
		}
		return getDisplayNode( S );
	}
/*
	private void updateMinMaxPoids( int poids ) {
		if ( poidsMin > poidsMax ) {
			poidsMin = poids;
			poidsMax = poids;
		} else {
			poidsMin = Math.min( poidsMin, poids );
			poidsMax = Math.max( poidsMax, poids );
		}
	}
*/
	@SuppressWarnings("unchecked")
	public synchronized void actionPerformed( ActionEvent e ) {
	/*	poidsMax = 0;
		poidsMin = 1;
	*/	min = max = null;
		DisplayNode ds;
		for ( Node S: graph.getNodes() ) {
			ds = updateList( S );
			updateMinMaxCoords( ds.getLocation() );
		//	updateMinMaxPoids( S.getDegree() );
		}

		coutMax = 1;
		for ( Arc<Node> arc: graph.getArcs() ) {
			if ( coutMax < arc.getCout() ) {
				coutMax = arc.getCout();
			}
		}
		repaint();
	}

	@Override
	public synchronized void paint( Graphics graphics ) {
		Dimension dimension = getSize();
		BufferedImage img = new BufferedImage(
			dimension.width,
			dimension.height,
			BufferedImage.TYPE_INT_RGB
		);
		Graphics g = img.getGraphics();
		paintArcs( g, graph.getArcs() );
		super.paint( g );
	
		graphics.drawImage( img, 0, 0, this );
	}

	private DisplayNode getDisplayNode( Node S ) throws IllegalArgumentException {
		for ( Component component: getComponents() ) {
			if ( component instanceof DisplayNode &&
					((DisplayNode) component).isNode( S )
				) {
				return (DisplayNode) component;
			}
		}
		throw new IllegalArgumentException( "Le sommet n'a pas encore �t� enregistr�" );
	}

	@SuppressWarnings({ "unchecked" })
	private void paintArcs(
			Graphics graphics,
			Set<Arc> set
		) {
		for ( Arc arc: set ) {
			try {
				paintArc( graphics, arc );
			} catch ( IllegalArgumentException e ) {
				System.err.println( "L'arc n'a pas put �tre d�ssin�." );
			}
		}
	}

	private void paintArc( Graphics graphics, Arc<Node> arc ) throws IllegalArgumentException {
		DisplayNode origine = getDisplayNode( arc.getOrigine() ),
			destination = getDisplayNode( arc.getDestination() );
		Dimension dimensionOri = origine.getSize(),
			dimensionDest = destination.getSize();
		Point pointOri = origine.getLocation(),
			pointDest = destination.getLocation();
		pointOri.x += dimensionOri.width  / 2;
		pointOri.y += dimensionOri.height / 2;
		pointDest.x += dimensionDest.width  / 2;
		pointDest.y += dimensionDest.height / 2;

		graphics.drawLine( pointOri.x, pointOri.y, pointDest.x, pointDest.y );

		String cout = new String( arc.getCout() + "" );
		char name[] = cout.toCharArray();
		graphics.drawChars(
			name,
			0,
			name.length,
			( pointOri.x + pointDest.x ) / 2,
			( pointOri.y + pointDest.y ) / 2
		);
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof ContentSwing ) ) {
			return false;
		}
		return super.equals( o );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		Object o = e.getSource();
		if ( o instanceof DisplayNode ) {
			DisplayNode ds = (DisplayNode) o;
			Dimension dim = ds.getSize();
			Point position = ds.getLocation();
			Dimension limit = getSize();
			limit.width -= dim.width;
			limit.height -= dim.height;
			position.x += e.getX() - dim.width  / 2;
			position.y += e.getY() - dim.height / 2;
			if ( position.x < 0 ) {
				position.x = 0;
			} else if ( position.x > limit.width ) {
				position.x = limit.width;
			}
			if ( position.y < 0 ) {
				position.y = 0;
			} else if ( position.y > limit.height ) {
				position.y = limit.height;
			}
			ds.setLocation( position );
		}
	}

	@Override
	public void mouseMoved (MouseEvent e ) {}
}
