package fr.upem.pperonne.caterer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.Node;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestArc extends Arc {
	public TestArc() {
		super( new Node(), new Node() );
	}

	@Test
	public void equals() {
		Node s1 = new Node( "A" ),
			s2 = new Node( "B" );
		Arc a1 = new Arc( s1, s2, 0 ),
			a2 = new Arc( s1, s2 ),
			a3 = new Arc( s1, s2, 1 );
		assertEquals( a1, a2 );
		assertNotEquals( a1, a3 );
	}
}
