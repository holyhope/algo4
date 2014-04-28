package fr.coffeepot.projet.licence.S6.algo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JComponent;

import fr.upem.pperonne.caterer.Node;

@SuppressWarnings({ "serial", "rawtypes" })
public class DisplayNode extends JComponent implements MouseListener {
	private final Node node;
	private static Color defaultColor = Color.WHITE;
	private static Color overColor = Color.PINK;
	private static Color clickColor = Color.RED;
	private Color color = defaultColor;

	public DisplayNode( Node node, Dimension range ) throws IllegalArgumentException {
		if ( node == null ) {
			throw new IllegalArgumentException( "Impossible d'afficher un sommet null." );
		}
		this.node = node;
		Random rand = new Random();
		setLocation( new Point(
			rand.nextInt( range.width ),
			rand.nextInt( range.height )
		) );
		addMouseListener( this );
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof DisplayNode ) ) {
			return false;
		}
		return node.equals( ((DisplayNode) o).node );
	}

	@Override
	public synchronized void paint( Graphics graphics ) {
		super.paint( graphics );
		Color def = graphics.getColor();

		Dimension dimensionSommet = getSize();
		graphics.setColor( color );
		graphics.fillOval(
			0,
			0,
			dimensionSommet.width,
			dimensionSommet.height
		);

		String poids = new String( node.get() + "" );
		char[] name = poids.toCharArray();
		graphics.setColor( def );
		int width = graphics.getFontMetrics().charsWidth( name, 0, name.length );
		graphics.drawChars(
			name,
			0,
			name.length,
			( dimensionSommet.width - width ) / 2,
			( dimensionSommet.height * 2 ) / 3
		);

		graphics.setColor( def );
	}

	public boolean isNode( Node s ) {
		return node.equals( s );
	}

	@Override
	public void mouseClicked( MouseEvent e ) {}

	@Override
	public void mousePressed( MouseEvent e ) {
		color = clickColor;
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		color = overColor;
	}

	@Override
	public void mouseEntered( MouseEvent e ) {
		color = overColor;
	}

	@Override
	public void mouseExited( MouseEvent e ) {
		color = defaultColor;
	}
}
