package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class Graph <N extends Node<?>,A extends Arc<N>> {
	protected HashSet<N> nodes = new HashSet<N>();
	protected HashSet<A> arcs = new HashSet<A>();

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
	public boolean add( Collection<?> list ) throws IllegalArgumentException {
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
	public boolean remove( Collection<?> list ) throws IllegalArgumentException {
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
			string.append( a.toString( new LinkedList<N>( nodes ) ) ).append( '\n' );
		}

		return string.toString();
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public <E> E next( E o, Set<E> visited ) throws IllegalStateException {
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
	public Set<A> inArcs( N node ) {
		HashSet<A> arcs = new HashSet<>();

		for ( A arc: this.arcs ) {
			if ( arc.getOrigine().equals( node ) ) {
				arcs.add( (A) arc.clone() );
			}
		}

		return arcs;
	}
	
	private HashSet<A> inArcsLocal( N node ) {
		HashSet<A> arcs = new HashSet<>();

		for ( A arc: this.arcs ) {
			if ( arc.origine.equals( node ) ) {
				arcs.add( arc );
			}
		}

		return arcs;
	}

	@SuppressWarnings("unchecked")
	public Set<A> outArcs( N node ) {
		HashSet<A> arcs = new HashSet<>();

		for ( A arc: this.arcs ) {
			if ( arc.getOrigine().equals( node ) ) {
				arcs.add( (A) arc.clone() );
			}
		}

		return arcs;
	}

	protected HashSet<A> outArcsLocal( N node ) {
		HashSet<A> arcs = new HashSet<>();

		for ( A arc: this.arcs ) {
			if ( arc.origine.equals( node ) ) {
				arcs.add( arc );
			}
		}

		return arcs;
	}

	public <E> void runPrefix( FunctionRun<E> f ) {
		runPrefix( null, f, new HashSet<E>() );
	}

	public <E> void runPrefix( E S, FunctionRun<E> f ) {
		runPrefix( S, f, new HashSet<E>() );
	}

	private <E> void runPrefix(
			E S,
			FunctionRun<E> f,
			HashSet<E> visited
		) {
		E T = S;

		if ( visited.contains( S ) ) {
			return;
		}
		if ( S != null ) {
			f.accept( S );
		}

		while ( null != ( T = f.next( S, visited ) ) ) {
			f.accept( T );
			visited.add( T );
		}
	}

	public void runSuffix( FunctionNode<N> f ) {
		HashSet<N> visited = new HashSet<>();
		for ( N S: nodes ) {
			runSuffix( S, f, visited );
		}
	}

	public void runSuffix( N S, FunctionNode<N> f ) {
		runSuffix( S, f, new HashSet<N>() );
	}

	private void runSuffix(
			N S,
			FunctionNode<N> f,
			HashSet<N> visited
		) {
		N T;

		if ( visited.contains( S ) ) {
			return;
		}

		visited.add( S );

		while ( null != ( T = f.next( S, visited ) ) ) {
			runSuffix( T, f, visited );
		}

		f.accept( S );
	}

	public Set<N> neighbours( N S ) {
		Set<N> list = new HashSet<>();
		list.addAll( children( S ) );
		list.addAll( parents( S ) );
		return list;
	}

	protected HashSet<N> neighboursLocal( N S ) {
		HashSet<N> list = new HashSet<>();
		list.addAll( childrenLocal( S ) );
		list.addAll( parentsLocal( S ) );
		return list;
	}

	public boolean isAccessible( N origine, N dest ) {
		return isAccessible( origine, dest, new HashSet<N>() );
	}

	private boolean isAccessible(
			N origine,
			N dest,
			HashSet<N> visited
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
		final HashSet<Boolean> access = new HashSet<>();
		final Graph<N, A> graph = this;

		FunctionNode<N> f = new FunctionNode<N>() {
			@Override
			public void accept( N S ) {
				if ( S.equals( dest ) ) {
					access.add( true );
				}
			}

			@Override
			public N next( N S, Set<N> visited ) {
				return graph.next( S, visited );
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
	
	public Set<N> parents( N node ) {
		Set<N> parents = new HashSet<>();
		for ( A a: arcs ) {
			if ( a.getDestination().equals( node ) ) {
				parents.add( a.getOrigine() );
			}
		}
		return parents;
	}

	private HashSet<N> parentsLocal( N node ) {
		HashSet<N> parents = new HashSet<>();
		for ( A a: inArcsLocal( node ) ) {
			parents.add( a.origine );
		}
		return parents;
	}
	
	public Set<N> children( N node ) {
		Set<N> children = new HashSet<>();
		for ( A a: arcs ) {
			if ( a.getOrigine().equals( node ) ) {
				children.add( a.getDestination() );
			}
		}
		return children;
	}

	private HashSet<N> childrenLocal( N node ) {
		HashSet<N> parents = new HashSet<>();
		for ( A a: outArcsLocal( node ) ) {
			parents.add( a.destination );
		}
		return parents;
	}

	public boolean isCyclic() {
		for ( N S: nodes ) {
			if ( isCyclic( S, new HashSet<N>() ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean isCyclicNoSens() {
		for ( N S: nodes ) {
			if ( isCyclicNoSens( null, S, new HashSet<N>() ) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isCyclic( N s, HashSet<N> visitedNodes ) {
		if ( neighbours( s ).size() == 0 ){
			return false;
		}
		HashSet<N> visisted = new HashSet<>();
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

	private boolean isCyclicNoSens( A previous, N s, HashSet<N> visitedNodes ) {
		if ( neighbours( s ).size() == 0 ){
			return false;
		}
		HashSet<N> visisted = new HashSet<>();
		HashSet<A> neighbours = outArcsLocal( s );
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
		Set<A> out = outArcs( origine );
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
	public Set<Arc> getArcs() {
		Set<Arc> list = new HashSet<>();
		for ( A arc: arcs ) {
			list.add( arc.clone() );
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Set<N> getNodes() {
		Set<N> list = new HashSet<>();
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

	protected A get( Arc<N> arc ) throws NullPointerException {
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