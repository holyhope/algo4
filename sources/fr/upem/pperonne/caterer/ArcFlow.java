package fr.upem.pperonne.caterer;

public class ArcFlow<N extends Node> extends Arc<N> {
	private Integer capacity = Integer.MAX_VALUE, min = Integer.MIN_VALUE;

	public ArcFlow(
			N origine,
			N destination,
			int cout,
			int min,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout, capacity );
		this.min = min;
	}

	public ArcFlow(
			N origine,
			N destination,
			int cout,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout );
		this.capacity = capacity;
	}

	public ArcFlow(
			N origine,
			N destination,
			int cout
		) throws IllegalArgumentException {
		super( origine, destination, cout );
	}
	
	public ArcFlow( Arc<N> arc ) {
		super( arc );
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public void setCout( int cout ) throws IllegalArgumentException {
		if ( cout > capacity ) {
			throw new IllegalArgumentException( "La valeur dépasse la capacité." );
		}
		if ( cout < min ) {
			throw new IllegalArgumentException( "La valeur est inférieur à la quantité minimale." );
		}
		super.setCout( cout );
	}
	
	@Override
	protected Arc<N> clone() {
		return new ArcFlow<N>( getOrigine(), getDestination(), getCout(), min, capacity );
	}
}
