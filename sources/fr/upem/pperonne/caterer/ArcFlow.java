package fr.upem.pperonne.caterer;

public class ArcFlow extends Arc<NodeFlow> {
	private Integer capacity = Integer.MAX_VALUE, min = Integer.MIN_VALUE;

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout,
			int min,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout, capacity );
		this.min = min;
	}

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout );
		this.capacity = capacity;
	}

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout
		) throws IllegalArgumentException {
		super( origine, destination, cout );
	}
	
	public ArcFlow( Arc<NodeFlow> arc ) {
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
	protected ArcFlow clone() {
		return new ArcFlow( getOrigine(), getDestination(), getCout(), min, capacity );
	}
}
