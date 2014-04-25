package fr.upem.pperonne.caterer.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestNode.class, TestArc.class, TestGraph.class })
public class Tests {
	public static void main( String[] args ) {
		Result result = JUnitCore.runClasses( Tests.class );
		for ( Failure fail: result.getFailures() ) {
			System.out.println( fail.toString() );
		}
	}
}