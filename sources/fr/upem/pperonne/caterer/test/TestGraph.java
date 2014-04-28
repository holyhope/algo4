package fr.upem.pperonne.caterer.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.FunctionNode;
import fr.upem.pperonne.caterer.Graph;
import fr.upem.pperonne.caterer.Node;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestGraph extends Graph {
	@Test(expected=IllegalArgumentException.class)
	public void addTwiceSommet() {
		Node s = new Node();
		Graph<Node,Arc<Node>> G = new Graph<Node,Arc<Node>>();
		G.add( s );
		G.add( s );
	}

	@Test(expected=IllegalArgumentException.class)
	public void addTwiceArc() {
		Arc a = new Arc( new Node(), new Node() );
		Graph G = new Graph();
		G.add( a );
		G.add( a );
	}
	
	@Test
	public void add() {
		Node s1 = new Node(),
			s2 = new Node();
		Arc a1 = new Arc( s1, s2 ),
			a2 = new Arc( s2, s1 );
		Graph G = new Graph();
		G.add( s1 );
		assertTrue( G.getNodes().contains( s1 ) );
		G.add( s2 );
		assertTrue( G.getNodes().contains( s2 ) && G.getNodes().contains( s1 ) );
		G.add( a1 );
		assertTrue( G.getArcs().contains( a1 ) );
		G.add( a2 );
		assertTrue( G.getArcs().contains( a2 ) && G.getArcs().contains( a1 ) );
	}

	@Test
	public void parcoursPrefix() {
		final ArrayList<Node> list = new ArrayList<>();
		Graph G = init( 3 );
		ArrayList<Node> nodes = G.getNodes();
		Object[] result = nodes.toArray();
		G.add( new Arc( nodes.get( 0 ), nodes.get( 1 ) ) );
		G.add( new Arc( nodes.get( 1 ), nodes.get( 0 ) ) );
		FunctionNode f = new FunctionNode() {
			public void accept( Node S ) {
				list.add( S );
			}
		};
		G.runPrefix( f );
		assertArrayEquals( result, list.toArray() );
	}

	@Test
	public void parcoursSuffix() {
		final ArrayList<Node> list = new ArrayList<>();
		Graph G = init( 3 );
		ArrayList<Node> nodes = G.getNodes(), result = new ArrayList<>();
		result.add( nodes.get( 1 ) );
		result.add( nodes.get( 0 ) );
		result.add( nodes.get( 2 ) );
		G.add( new Arc( nodes.get( 0 ), nodes.get( 1 ) ) );
		G.add( new Arc( nodes.get( 1 ), nodes.get( 0 ) ) );
		FunctionNode f = new FunctionNode() {
			public void accept( Node S ) {
				list.add( S );
			}
		};
		G.runSuffix( f );
		assertArrayEquals( result.toArray(), list.toArray() );
	}

	@Test
	public void read() {
		try {
			File file = File.createTempFile( "caterer", null );
			file.setReadable( true );
			file.setWritable( true );

			PrintWriter print = new PrintWriter( file );

			Graph test = new Graph();

			print.println( "3" );
			Node n0, n1, n2;

			print.println( "5 10 50" );
			test.add( n0 = new Node( 5 ) );
			test.add( n1 = new Node( 10 ) );
			test.add( n2 = new Node( 50 ) );

			print.println( "0 2 10" );
			test.add( new Arc( n0, n2, 10 ) );

			print.println( "0 1 0" );
			test.add( new Arc( n0, n1, 0 ) );

			print.println( "2 1 -10" );
			test.add( new Arc( n2, n1, -10 ) );

			print.println( "1 2 15" );
			test.add( new Arc( n1, n2, 15 ) );
	
			print.close();

			Scanner scanner = new Scanner( file );
			Graph G = new Graph( scanner );

			assert( G.equals( test ) );
		} catch ( SecurityException | IOException e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void tostring() {
		StringBuilder string = new StringBuilder();

		Graph G = new Graph();

		Node n0, n1, n2;
		string.append( "3" )
			.append( "\n" );

		G.add( n0 = new Node( 5 ) );
		string.append( "5" );
		G.add( n1 = new Node( 10 ) );
		string.append( "10" );
		G.add( n2 = new Node( 50 ) );
		string.append( "50" )
			.append( "\n" );

		G.add( new Arc( n0, n2, 10 ) );
		string.append( "0 2 10" )
		.append( "\n" );

		G.add( new Arc( n0, n1, 0 ) );
		string.append( "0 1 0" )
		.append( "\n" );

		G.add( new Arc( n2, n1, -10 ) );
		string.append( "2 1 -10" )
		.append( "\n" );

		G.add( new Arc( n1, n2, 15 ) );
		string.append( "1 2 15" )
		.append( "\n" );

		assert( string.toString().equals( G ) );
	}
	
	@Test
	public void voisins() {
		Graph G = init( 4 );
		ArrayList<Node> nodes = G.getNodes(),
			result = new ArrayList<>();
		G.add( new Arc( nodes.get( 0 ), nodes.get( 1 ) ) );
		G.add( new Arc( nodes.get( 1 ), nodes.get( 2 ) ) );
		G.add( new Arc( nodes.get( 1 ), nodes.get( 3 ) ) );
		G.add( new Arc( nodes.get( 2 ), nodes.get( 2 ) ) );
		result.add( nodes.get( 2 ) );
		result.add( nodes.get( 3 ) );
		assertArrayEquals( result.toArray(), G.children( nodes.get( 1 ) ).toArray() );
	}
	
	@Test
	public void sorties() {
		Graph G = init( 4 );
		ArrayList<Node> nodes = G.getNodes();
		ArrayList<Arc> result = new ArrayList<>();
		result.add( new Arc( nodes.get( 1 ), nodes.get( 2 ) ) );
		result.add( new Arc( nodes.get( 1 ), nodes.get( 3 ) ) );
		G.add( new Arc( nodes.get( 0 ), nodes.get( 1 ) ) );
		G.add( result.get( 0 ) );
		G.add( result.get( 1 ) );
		G.add( new Arc( nodes.get( 2 ), nodes.get( 2 ) ) );
		assertArrayEquals( result.toArray(), G.outArcs( nodes.get( 1 ) ).toArray() );
	}

	private ArrayList<Node> initNode( int nb ) {
		ArrayList<Node> list = new ArrayList<>();
		for ( int i = 0; i < nb; i++ ) {
			list.add( new Node( "S" + i ) );
		}
		return list;
	}

	private Graph init( int nbSommet ) {
		ArrayList<Node> list = initNode( nbSommet );
		Graph G = new Graph();
		G.add( list );
		return G;
	}

	@Test
	public void degrees() {
		Graph G = init( 3 );
		ArrayList<Node> list = G.getNodes();
		Node root = list.get( 1 );
		G.add( new Arc( list.get( 0 ), root ) );
		G.add( new Arc( root, list.get( 2 ) ) );
		G.add( new Arc( list.get( 0 ), list.get( 2 ) ) );
		assertEquals( 1, G.degree( root ) );
		assertEquals( 2, G.degreeMax() );
		assertEquals( 0, G.degreeMin() );
	}

	@Test
	public void neighbours() {
		Graph G = init( 4 );
		ArrayList<Node> list = G.getNodes();
		Node root = list.get( 1 );
		G.add( new Arc( list.get( 0 ), root ) );
		G.add( new Arc( root, list.get( 2 ) ) );
		G.add( new Arc( list.get( 0 ), list.get( 2 ) ) );
		List<Node> test, result = new ArrayList<>();
		result.add( list.get( 2 ) );
		test = G.children( root );
		assertTrue( result.containsAll( test ) && test.containsAll( result ) );
		result.clear();
		result.add( list.get( 0 ) );
		test = G.parents( root );
		assertTrue( result.containsAll( test ) && test.containsAll( result ) );
		result.clear();
		result.add( list.get( 0 ) );
		result.add( list.get( 2 ) );
		test = G.neighbours( root );
		assertTrue( result.containsAll( test ) && test.containsAll( result ) );
	}
}
