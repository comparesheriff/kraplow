package java.com.chriscarr;

import java.com.chriscarr.bang.Setup;
import java.com.chriscarr.bang.userinterface.ManualUserInterface;

public class ManualTest {

	public ManualTest(){
		ManualUserInterface x = new ManualUserInterface();
		new Setup(7, x, x);
	}
	
	public static void main(String[] args) {
		new ManualTest();
	}

}
