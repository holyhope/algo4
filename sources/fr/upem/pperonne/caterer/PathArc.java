package fr.upem.pperonne.caterer;

import java.util.ArrayList;

@SuppressWarnings({ "serial", "rawtypes" })
public class PathArc<A extends Arc> extends ArrayList<A> {
	public int getCout() {
		int total = 0;
		for ( A arc: this ) {
			total += arc.getCout();
		}
		return total;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		if ( size() == 0 ) {
			return "Le chemin est vide.";
		}

		for ( A arc: this ) {
			str.append( arc.getOrigine() ).append( " -> " );
		}
		str.append( get( size() - 1 ).getDestination() );
		return str.toString();
	}

	@Override
	public boolean add( A arc ) {
		if ( arc == null ) {
			return true;
		}
		if (
				size() > 0 &&
				get( size() - 1 ).getDestination().equals( arc.getOrigine() )
			) {
			return false;
		}
		return super.add( arc );
	}

	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof PathArc ) ) {
			return false;
		}
		return super.equals( o );
	}

	public boolean include( PathArc<A> p ) {
		if ( p == null ) {
			return true;
		}
		for ( int i = 0; i < size() - p.size(); i++ ) {
			if ( subList( i, i + p.size() ).equals( p ) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean add( PathArc<A> path ) {
		if ( path == null || path.size() == 0 ) {
			return true;
		}
		if (
				size() > 0 &&
				! get( size() - 1 ).equals( path.get( 0 ) )
			) {
			return false;
		}
		return addAll( path );
	}

	@Override
	public void add( int index, A arc ) throws IllegalArgumentException {
		if ( index > size() ) {
			throw new IllegalArgumentException( "Vous ne pouvez pas ajouter un arc en dehors du chemin." );
		}
		if ( index != 0 && ! get( index - 1 ).equals( arc.getOrigine() ) ) {
			throw new IllegalArgumentException( "L'arc ne peut pas se positionner à cet endroit." );
		}
		if ( index != size() && ! get( index ).equals( arc.getDestination() ) ) {
			throw new IllegalArgumentException( "L'arc ne peut pas se positionner à cet endroit." );
		}
		super.add( index, arc );
	}
}
