package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Graph <N extends Node<?>,A extends Arc<N>> {
	protected ArrayList<N> nodes = new ArrayList<N>();
	protected ArrayList<A> arcs = new ArrayList<A>();

	public Graph() {}

	@SuppressWarnings("unchecked")
	public Graph( Scanner scan ) throws IllegalArgumentException {
		if ( ! scan.hasNextInt() ) {
			throw new IllegalArgumentException( "Le nombre de sommet doit être indiqué au début." ); 
		}
		int nbSommet = scan.nextInt();
		for ( int i = 0; i < nbSommet; i++ ) {
			add( (N) new NodeInt( scan, "S" + i ) );
		}
		List<N> nodes = new ArrayList<>();
		nodes.addAll( this.nodes );
		while ( scan.hasNextInt() ) {
			try {
				add( (A) new Arc<N>( scan, nodes ) );
			} catch ( IllegalArgumentException e ) {
				throw new IllegalArgumentException( "Un arc n'a pas été bien formaté." );
			}
		}
	}

	public boolean add( A arc ) throws IllegalArgumentException {
		if ( ! nodes.contains( arc.getOrigine() ) ) {
			throw new IllegalArgumentException( arc.getOrigine() + " (origine) n'existe pas." );
		}
		if ( ! nodes.contains( arc.getDestination() ) ) {
			throw new IllegalArgumentException( arc.getDestination() + " (destination) n'existe pas." );
		}
		return arcs.add( arc );
	}

	public boolean add( N S ) throws IllegalArgumentException {
		if ( nodes.contains( S ) ) {
			throw new IllegalArgumentException( S + " est déjà dans le graphe." );
		}
		return nodes.add( S );
	}

	@SuppressWarnings("unchecked")
	public boolean add( ArrayList<?> list ) throws IllegalArgumentException {
		boolean flag = true;
		if ( list == null ) {
			throw new IllegalArgumentException( "La liste de sommets à ajouter ne peut pas être null." );
		}
		for ( Object o: list ) {
			if ( o instanceof Arc ) {
				if ( ! add( (A) o ) ) {
					flag = false;
				}
			} else if ( o instanceof Node ) {
				if ( ! add( (N) o ) ) {
					flag = false;
				}
			}
		}
		return flag;
	}

	public boolean remove( A arc ) throws IllegalArgumentException {
		if ( ! arcs.contains( arc ) ) {
			throw new IllegalArgumentException( arc + " n'existe pas." );
		}
		return arcs.remove( arc );
	}

	public boolean remove( N S ) throws IllegalArgumentException {
		if ( ! nodes.contains( S ) ) {
			throw new IllegalArgumentException( S + " n'existe pas." );
		}
		return nodes.remove( S );
	}

	@SuppressWarnings("unchecked")
	public boolean remove( ArrayList<?> list ) throws IllegalArgumentException {
		boolean flag = true;
		if ( list == null ) {
			throw new IllegalArgumentException( "La liste de sommets à ajouter ne peut pas être null." );
		}
		for ( Object o: list ) {
			if ( o instanceof Arc ) {
				if ( ! add( (A) o ) ) {
					flag = false;
				}
			} else if ( o instanceof Node ) {
				if ( ! add( (N) o ) ) {
					flag = false;
				}
			}
		}
		return flag;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		string.append( nodes.size() ).append( '\n' );
		Iterator<N> it = nodes.iterator();
		while ( it.hasNext() ) {
			string.append( it.next() );
			if ( it.hasNext() ) {
				string.append( ' ' );
			}
		}
		string.append( '\n' );
		for ( A a: arcs ) {
			string.append( a.toString( nodes ) ).append( '\n' );
		}

		return string.toString();
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private <E> E next( ArrayList<E> visited, E o ) throws IllegalStateException {
		Node dest;

		if ( o instanceof Node ) {
			for ( A arc: outArcsLocal( (N) o ) ) {
				dest = arc.getDestination();
				if ( ! visited.contains( dest ) ) {
					return (E) dest;
				}
			}
		} else if ( o instanceof Arc ) {
			for ( A arc: outArcsLocal( ((A) o).destination ) ) {
				if ( ! visited.contains( arc ) ) {
					return (E) arc;
				}
			}
		} else {
			throw new IllegalStateException( "Impossible de chercher l'objet suivant." );
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<A> inArcs( N node ) {
		ArrayList<A> arcs = new ArrayList<>();

		for ( A arc: this.arcs ) {
			if ( arc.getOrigine().equals( node ) ) {
				arcs.add( (A) arc.clone() );
			}
		}

		return arcs;
	}
	
	private ArrayList<A> inArcsLocal( N node ) {
		ArrayList<A> arcs = new ArrayList<>();

		for ( A arc: this.arcs ) {
			if ( arc.origine.equals( node ) ) {
				arcs.add( arc );
			}
		}

		return arcs;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<A> outArcs( N node ) {
		ArrayList<A> arcs = new ArrayList<>();

		for ( A arc: this.arcs ) {
			if ( arc.getOrigine().equals( node ) ) {
				arcs.add( (A) arc.clone() );
			}
		}

		return arcs;
	}

	protected ArrayList<A> outArcsLocal( N node ) {
		ArrayList<A> arcs = new ArrayList<>();

		for ( A arc: this.arcs ) {
			if ( arc.origine.equals( node ) ) {
				arcs.add( arc );
			}
		}

		return arcs;
	}

	public void runPrefix( FunctionNode<N> f ) {
		ArrayList<N> visited = new ArrayList<>();
		for ( N S: nodes ) {
			runPrefix( S, f, visited );
		}
	}

	public void runPrefix( N S, FunctionNode<N> f ) {
		runPrefix( S, f, new ArrayList<N>() );
	}

	private void runPrefix(
			N S,
			FunctionNode<N> f,
			ArrayList<N> visited
		) {
		N T;

		if ( visited.contains( S ) ) {
			return;
		}

		f.accept( S );
		visited.add( S );

		while ( null != ( T = next( visited, S ) ) ) {
			runPrefix( T, f, visited );
		}
	}

	public void runPrefix( FunctionArc<A> f ) {
		ArrayList<A> visited = new ArrayList<>();
		for ( A a: arcs ) {
			runPrefix( a, f, visited );
		}
	}

	public void runPrefix( A a, FunctionArc<A> f ) {
		runPrefix( a, f, new ArrayList<A>() );
	}

	private void runPrefix(
			A a,
			FunctionArc<A> f,
			ArrayList<A> visited
		) {
		A T;

		if ( visited.contains( a ) ) {
			return;
		}

		f.accept( a );
		visited.add( a );

		while ( null != ( T = next( visited, a ) ) ) {
			runPrefix( T, f, visited );
		}
	}

	public void runSuffix( FunctionNode<N> f ) {
		ArrayList<N> visited = new ArrayList<>();
		for ( N S: nodes ) {
			runSuffix( S, f, visited );
		}
	}

	public void runSuffix( N S, FunctionNode<N> f ) {
		runSuffix( S, f, new ArrayList<N>() );
	}

	private void runSuffix(
			N S,
			FunctionNode<N> f,
			ArrayList<N> visited
		) {
		N T;

		if ( visited.contains( S ) ) {
			return;
		}

		visited.add( S );

		while ( null != ( T = next( visited, S ) ) ) {
			runSuffix( T, f, visited );
		}

		f.accept( S );
	}

	public List<N> neighbours( N S ) {
		List<N> list = new ArrayList<>();
		list.addAll( children( S ) );
		list.addAll( parents( S ) );
		return list;
	}

	protected ArrayList<N> neighboursLocal( N S ) {
		ArrayList<N> list = new ArrayList<>();
		list.addAll( childrenLocal( S ) );
		list.addAll( parentsLocal( S ) );
		return list;
	}

	public boolean isAccessible( N origine, N dest ) {
		return isAccessible( origine, dest, new ArrayList<N>() );
	}

	private boolean isAccessible(
			N origine,
			N dest,
			ArrayList<N> visited
		) {
		visited.add( origine );

		for ( A arc: outArcsLocal( origine ) ) {
			if ( arc.getDestination().equals( dest ) ) {
				return true;
			}
			if ( ! visited.contains( arc.destination ) &&
					isAccessible( arc.destination, dest, visited )
				) {
					return true;
			}
		}

		return false;
	}
	
	public int degree( N S ) {
		return outArcs( S ).size();
	}
	
	public int degreeMax() {
		int max = 0, deg;
		for ( N S: nodes ) {
			deg = degree( S );
			if ( deg > max ) {
				max = deg;
			}
		}
		return max;
	}
	
	public int degreeMin() {
		int min = nodes.size(), deg;
		for ( N S: nodes ) {
			deg = degree( S );
			if ( deg < min ) {
				min = deg;
			}
		}
		return min;
	}
	
	public boolean pathExists( N origine, final N dest ) {
		final ArrayList<Boolean> access = new ArrayList<>();

		FunctionNode<N> f = new FunctionNode<N>() {
			public void accept( N S ) {
				if ( S.equals( dest ) ) {
					access.add( true );
				}
			}
		};
		runPrefix( origine, f );
		
		return access.size() > 0;
	}

	public boolean isConnexe() {
		for ( N origine: nodes ) {
			for ( N dest: nodes ) {
				if ( ! ( pathExists( origine, dest ) ) ) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<N> parents( N node ) {
		List<N> parents = new ArrayList<>();
		for ( A a: arcs ) {
			if ( a.getDestination().equals( node ) ) {
				parents.add( a.getOrigine() );
			}
		}
		return parents;
	}

	private ArrayList<N> parentsLocal( N node ) {
		ArrayList<N> parents = new ArrayList<>();
		for ( A a: inArcsLocal( node ) ) {
			parents.add( a.origine );
		}
		return parents;
	}
	
	public List<N> children( N node ) {
		List<N> children = new ArrayList<>();
		for ( A a: arcs ) {
			if ( a.getOrigine().equals( node ) ) {
				children.add( a.getDestination() );
			}
		}
		return children;
	}

	private ArrayList<N> childrenLocal( N node ) {
		ArrayList<N> parents = new ArrayList<>();
		for ( A a: outArcsLocal( node ) ) {
			parents.add( a.destination );
		}
		return parents;
	}

	public boolean isCyclic() {
		for ( N S: nodes ) {
			if ( isCyclic( S, new ArrayList<N>() ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean isCyclicNoSens() {
		for ( N S: nodes ) {
			if ( isCyclicNoSens( null, S, new ArrayList<N>() ) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isCyclic( N s, ArrayList<N> visitedNodes ) {
		if ( neighbours( s ).size() == 0 ){
			return false;
		}
		ArrayList<N> visisted = new ArrayList<>();
		visisted.addAll( visitedNodes );
		visisted.add( s );
		for ( N n: childrenLocal( s ) ) {
			if ( visisted.contains( n ) ){
				return true;
			}
			if ( isCyclic( n, visisted ) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isCyclicNoSens( A previous, N s, ArrayList<N> visitedNodes ) {
		if ( neighbours( s ).size() == 0 ){
			return false;
		}
		ArrayList<N> visisted = new ArrayList<>();
		ArrayList<A> neighbours = outArcsLocal( s );
		N n;
		visisted.addAll( visitedNodes );
		visisted.add( s );
		neighbours.remove( previous );
		for ( A a: neighbours ) {
			n = a.destination;
			if ( visisted.contains( n ) ||
					isCyclicNoSens( a, n, visisted ) ) {
				return true;
			}
		}
		return false;
	}

	public PathArc<A> smallestPath( N origine, N dest ) {
		PathArc<A> path = null;
		ArrayList<A> out = outArcs( origine );
		A first = null;

		for ( A arc: out ) {
			if ( arc.getDestination().equals( dest ) ) {
				if ( first == null || first.getCout() > arc.getCout() ) {
					first = arc;
				}
			}
		}
		if ( first != null ) {
			path = new PathArc<>();
			path.add( first );
			return path;
		}

		PathArc<A> subPath;
		int cout, coutMinimal = 0;
		for ( A arc: out ) {
			subPath = smallestPath( arc.destination, dest );
			if ( subPath.size() > 0 ) {
				cout = subPath.getCout() + arc.getCout();
				if ( path == null || cout < coutMinimal ) {
					path = new PathArc<>();
					path.add( arc );
					path.addAll( subPath );
					coutMinimal = cout;
				}
			}
		}

		if ( path == null ) {
			return new PathArc<>();
		}

		return path;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Arc> getArcs() {
		ArrayList<Arc> list = new ArrayList<>();
		for ( A arc: arcs ) {
			list.add( arc.clone() );
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<N> getNodes() {
		ArrayList<N> list = new ArrayList<>();
		for ( N s: nodes ) {
			list.add( (N) s.clone() );
		}
		return list;
	}
	
	protected N get( N node ) throws NullPointerException {
		Objects.requireNonNull( node );
		for ( N S: nodes ) {
			if ( node.equals( S ) ) {
				return S;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected A get( Arc<Node> arc ) throws NullPointerException {
		Objects.requireNonNull( arc );
		for ( A A: arcs ) {
			if ( arc.equals( A ) ) {
				return A;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof Graph ) ) {
			return false;
		}
		Graph<Node,Arc<Node>> G = (Graph<Node, Arc<Node>>) o;

		if ( nodes.size() != G.nodes.size() || arcs.size() != G.arcs.size() ) {
			return false;
		}

		ArrayList<Node> nodes = new ArrayList<>();
		nodes.addAll( this.nodes );
		nodes.removeAll( G.nodes );
		if ( nodes.size() != 0 ) {
			return false;
		}

		ArrayList<A> arcs = new ArrayList<>();
		arcs.addAll( this.arcs );
		arcs.removeAll( G.arcs );

		return arcs.size() == 0;
	}
	
	@Override
	protected Graph<N,A> clone() {
		Graph<N,A> g = new Graph<>();
		g.add( getArcs() );
		g.add( getNodes() );
		return g;
	}
}