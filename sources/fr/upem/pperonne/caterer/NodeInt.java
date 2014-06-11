package fr.upem.pperonne.caterer;

import java.util.Scanner;

/**
 * structure sur les noeud d'entier
 * @author pierre
 * @author jeremy
 *
 */
public class NodeInt extends Node<Integer> {
	public NodeInt( Scanner scan ) throws IllegalArgumentException {
		super();
		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Impossible de lire le poids du sommet." );
		}
		setDegree( scan.nextInt() );
	}

	public NodeInt( Scanner scan, String name ) throws IllegalArgumentException {
		this( scan );
	}

	public NodeInt( String name ) {
		super( name, 0 );
	}

	public NodeInt( int degree ) {
		super( degree );
	}

	public NodeInt() {
		super();
	}

	public NodeInt( NodeInt n ) {
		super( n );
	}

	public NodeInt( String name, int degree ) {
		super( name, degree );
	}

	public void addDegree( int degree ) {
		setDegree( getDegree() + degree );
	}

	public void setDegree( int poids ) {
		set( poids );
	}

	public int getDegree() {
		return get();
	}
	
	@Override
	public NodeInt clone() {
		NodeInt n = new NodeInt( getName(), getDegree() );
		clone( n );
		return n;
	}
}
