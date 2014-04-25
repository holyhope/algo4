package fr.upem.pperonne.caterer;

import java.util.Scanner;
import java.util.UUID;

public class Node {
	private String name = defaultName;
	private int degree;
	private static final String defaultName = "";
	private UUID id = UUID.randomUUID();

	public Node( Scanner scan ) throws IllegalArgumentException {
		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Impossible de lire le poids du sommet." );
		}
		degree = scan.nextInt();
	}

	public Node( Scanner scan, String name ) throws IllegalArgumentException {
		this( scan );
		this.name = name;
	}

	public Node() {
		this( 0 );
	}

	public Node( int degree ) {
		this.degree = degree;
	}

	public Node( String name, int poids ) {
		this( poids );
		if ( name != null ) {
			this.name = name;
		}
	}

	public Node( String name ) {
		this( name, 0 );
	}

	public Node( Node n ) {
		this( n.degree );
		id = n.id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getName() {
		return name;
	}

	@Override
	public Node clone() {
		Node s = new Node( name, degree );
		s.id = id;
		return s;
	}

	protected void clone( Node s ) {
		s.id = id;
		s.name = name;
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof Node ) ) {
			return false;
		}
		Node s = (Node) o;
		if ( s.name == defaultName ) {
			return id.equals( s.id );
		}
		return s.name.equals( name );
	}

	@Override
	public String toString() {
		return degree + "";
	}

	public void setDegree( int poids ) {
		this.degree = poids;
	}

	public int getDegree() {
		return degree;
	}

	public void addDegree( int degree ) {
		this.degree += degree;
	}

	public UUID getId() {
		return id;
	}
}
