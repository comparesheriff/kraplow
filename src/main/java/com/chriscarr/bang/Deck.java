package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import java.util.ArrayList;
import java.util.Collections;

public class Deck extends ArrayList<Card> {
  private Discard discard;

  public Card pull() {
    if (isEmpty()) {
      while (!discard.isEmpty()) {
        add(discard.removeLast());
      }
      shuffle();
    }
    return removeLast();
  }

  public void shuffle() {
    Collections.shuffle(this);
  }

  public void setDiscard(Discard discard) {
    this.discard = discard;
  }
}
