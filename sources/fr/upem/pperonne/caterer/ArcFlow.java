package fr.upem.pperonne.caterer;
/**
 * classe sur les arc contenant des flots
 * @author pierre 
 * @author jeremy
 *
 */
public class ArcFlow extends Arc<NodeFlow> {
	private Integer max = Integer.MAX_VALUE, flow = 0;

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout,
			int flow,
			int capacity
		) throws IllegalArgumentException {
		this( origine, destination, cout, flow );
		this.max = capacity;
	}

	public ArcFlow(
			NodeFlow origine,
			NodeFlow destination,
			int cout,
			int flow
		) throws IllegalArgumentException {
		super( origine, destination, cout );
		this.flow = flow;
	}

	public ArcFlow( Arc<NodeFlow> arc, int flow ) {
		super( arc );
		this.flow = flow;
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
		ArcFlow a = new ArcFlow( getOrigine(), getDestination(), getCout(), getFlow(), max );
		a.flow = flow;
		return a;
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
