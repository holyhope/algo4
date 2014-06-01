package fr.upem.pperonne.caterer;

import java.util.List;
import java.util.Scanner;

@SuppressWarnings("rawtypes")
public class Arc<N extends Node<?>> {
	protected N origine;
	protected N destination;
	private int cout;

	public Arc( Scanner scan, List<N> nodes ) throws IllegalArgumentException {
		int size = nodes.size();

		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Impossible de lire l'origine de l'arc." );
		}
		int ori = scan.nextInt();
		if ( ori >= size ) {
			throw new IllegalArgumentException( "L'origine de l'arc n'existe pas." );
		}
		origine = nodes.get( ori );

		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Impossible de lire la destination de l'arc." );
		}
		int dest = scan.nextInt() ;
		if ( dest >= size ) {
			throw new IllegalArgumentException( "La destination de l'arc n'existe pas." );
		}
		destination = nodes.get( dest );

		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Impossible de lire le coût de l'arc." );
		}
		cout = scan.nextInt();
	}

	public Arc( N origine, N destination ) throws IllegalArgumentException {
		this( origine, destination, 0 );
	}
	
	public Arc( Arc<N> a ) {
		this( a.origine, a.destination, a.cout );
	}

	public Arc( N origine, N destination, int cout ) throws IllegalArgumentException {
		if ( origine == null ) {
			throw new IllegalArgumentException( "L'origine d'un arc ne peut pas être null." );
		}
		if ( destination == null ) {
			throw new IllegalArgumentException( "La destination d'un arc ne peut pas être null." );
		}
		this.origine = origine;
		this.destination = destination;
		this.cout = cout;
	}

	public void add( Arc<N> a ) throws IllegalArgumentException {
		if ( a == null ) {
			throw new IllegalArgumentException( "L'arc Ã  ajouter ne peut pas Ãªtre null." );
		}
		if ( ! destination.equals( a.origine ) ) {
			throw new IllegalArgumentException( "L'arc Ã  ajouter doit avoir pour origine la destination de l'arc courant." );
		}
		destination = a.destination;
		cout = a.cout;
	}

	public void setCout( int cout ) {
		this.cout = cout;
	}

	public int getCout() {
		return cout;
	}

	@SuppressWarnings("unchecked")
	public N getOrigine() {
		return (N) origine.clone();
	}

	@SuppressWarnings("unchecked")
	public N getDestination() {
		return (N) destination.clone();
	}

	@Override
	protected Arc<N> clone( ) {
		return new Arc<N>( origine, destination, cout );
	}

	@Override
	public String toString( ) {
		StringBuilder string = new StringBuilder();
		string
			.append( origine ).append( " " )
			.append( destination ).append( " " )
			.append( cout ).append( " " );
		return string.toString();
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof Arc ) ) {
			return false;
		}
		Arc a = (Arc) o;
		return 
			a.cout == cout &&
			origine.equals( a.origine ) &&
			destination.equals( a.destination );
	}

	public String toString( List<N> nodes ) {
		StringBuilder string = new StringBuilder();
		string
			.append( nodes.indexOf( origine ) ).append( " " )
			.append( nodes.indexOf( destination ) ).append( " " )
			.append( cout ).append( " " );
		return string.toString();
	}
}
