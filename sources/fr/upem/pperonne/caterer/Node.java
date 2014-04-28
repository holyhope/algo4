package fr.upem.pperonne.caterer;

import java.util.UUID;

public class Node<O extends Object> {
	private String name = defaultName;
	private O degree = null;
	private static final String defaultName = "";
	private UUID id = UUID.randomUUID();

	public Node( Node<O> n ) {
		this( n.degree );
		id = n.id;
	}

	public Node( O o ) {
		degree = o;
	}

	public Node( String name, O o ) {
		this( o );
		this.name = name;
	}

	public Node( String name ) {
		this.name = name;
	}

	public Node() {}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getName() {
		return name;
	}

	@Override
	public Node<O> clone() {
		Node<O> s = new Node<O>( name, degree );
		s.id = id;
		return s;
	}

	protected void clone( Node<O> s ) {
		s.id = id;
		s.name = name;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals( Object o ) {
		if ( ! ( o instanceof Node ) ) {
			return false;
		}
		Node s = (Node) o;
		if ( ! s.name.equals( name ) ) {
			return false;
		}
		if ( s.name == defaultName ) {
			return id.equals( s.id );
		}
		return true;
	}

	@Override
	public String toString() {
		return degree + "";
	}
	
	public void set( O o ) {
		degree = o;
	}
	
	public O get() {
		return degree;
	}

	public UUID getId() {
		return id;
	}
}
