package java.com.chriscarr.bang;

import java.util.ArrayList;
import java.util.List;

public class Discard {

	List<Object> cards = new ArrayList<>();
	
	public void add(Object object) {
		cards.add(object);
	}

	public Object peek() {
		return cards.getLast();
	}

	public Object remove() {
		return cards.removeLast();
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	public int size() {
		return cards.size();
	}

}
