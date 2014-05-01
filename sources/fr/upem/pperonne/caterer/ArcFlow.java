package fr.upem.pperonne.caterer;

public class ArcFlow extends Arc<NodeFlow> {
	private Integer max = Integer.MAX_VALUE, flow;

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout );
		this.max = capacity;
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
		return max;
	}

	@Override
	public void setCout( int cout ) throws IllegalArgumentException {
		if ( cout > max ) {
			throw new IllegalArgumentException( "La valeur dépasse la capacité." );
		}
		super.setCout( cout );
	}
	
	@Override
	protected ArcFlow clone() {
		return new ArcFlow( getOrigine(), getDestination(), getCout(), max );
	}
	
	public void setMax( Integer max ) {
		if ( max < 0 ) {
			this.max = Integer.MAX_VALUE;
		} else {
			this.max = max;
		}
	}

	public Integer getFlow() {
		return flow;
	}

	public void setFlow(Integer flow) {
		this.flow = flow;
	}
}
