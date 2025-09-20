package java.com.chriscarr;

import java.com.chriscarr.bang.AlivePlayers;

import junit.framework.TestCase;

public class DistanceTest extends TestCase {
	public void testSetup(){
		assertEquals(1, AlivePlayers.getDistance(0, 1, 2));
	}
	
	public void testSetupReverse(){		
		assertEquals(1, AlivePlayers.getDistance(1, 0, 2));
	}
	
	public void testSetupThree(){
		assertEquals(1, AlivePlayers.getDistance(0, 2, 3));
	}
		
	public void testSetupFour(){
		assertEquals(2, AlivePlayers.getDistance(0, 2, 4));
	}
	
	public void testSetupSeven(){
		assertEquals(1, AlivePlayers.getDistance(0, 6, 7));
	}
	public void testSetupSevenTwo(){
		assertEquals(2, AlivePlayers.getDistance(0, 5, 7));
	}
}
