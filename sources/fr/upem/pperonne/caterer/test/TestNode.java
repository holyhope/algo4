package fr.upem.pperonne.caterer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import fr.upem.pperonne.caterer.Node;

public class TestNode extends Node {
	@Test
	public void equals() {
		Node s0 = new Node(),
			s1 = new Node( "A", 0 ),
			s2 = new Node( "A" ),
			s3 = new Node( "B" ),
			s4 = new Node( "A", 2 );
		assertEquals( s0, s0 );
		assertEquals( s0, s0.clone() );
		assertEquals( s1, s2 );
		assertNotEquals( s1, s3 );
		assertEquals( s1, s4 );
	}
}
