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
public class NodeSwing<N extends Node> extends JComponent implements MouseListener {
	private final N node;
	protected char[] name;
	private static Color defaultColor = Color.WHITE;
	private static Color overColor = Color.YELLOW;
	private static Color clickColor = Color.ORANGE;
	private Color color = defaultColor;

	public NodeSwing( N node, Dimension range ) throws IllegalArgumentException {
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
		String name = "";
		this.name = name.toCharArray();
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof NodeSwing ) ) {
			return false;
		}
		return node.equals( ((NodeSwing) o).node );
	}
	
	public void update( N node ) {
		String degree = new String( node.get() + "" );
		name = degree.toCharArray();
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
