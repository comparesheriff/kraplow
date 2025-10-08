package com.chriscarr.bang.cards;

public enum CardName {
  // Blue – Items / Guns
  BARREL("Barrel", CardType.ITEM, Card::new),
  SCOPE("Scope", CardType.ITEM, Card::new),
  MUSTANG("Mustang", CardType.ITEM, Card::new),
  JAIL("Jail", CardType.ITEM, Jail::new),
  DYNAMITE("Dynamite", CardType.ITEM, Card::new),
  SCHOFIELD("Schofield", CardType.GUN, Gun::new),
  VOLCANIC("Volcanic", CardType.GUN, Gun::new),
  REMINGTON("Remington", CardType.GUN, Gun::new),
  WINCHESTER("Winchester", CardType.GUN, Gun::new),
  REV_CARBINE("Rev. Carbine", CardType.GUN, Gun::new),
  SILVER("Silver", CardType.ITEM, Card::new),
  HIDEOUT("Hideout", CardType.ITEM, Card::new),

  // Brown – Plays
  BANG("Shoot", CardType.PLAY, Bang::new),
  MISSED("Missed!", CardType.PLAY, Missed::new),
  BEER("Beer", CardType.PLAY, Beer::new),
  PANIC("Panic!", CardType.PLAY, Panic::new),
  CAT_BALOU("Cat Balou", CardType.PLAY, CatBalou::new),
  DUEL("Duel", CardType.PLAY, Duel::new),
  STAGECOACH("Stagecoach", CardType.PLAY, Stagecoach::new),
  INDIANS("Indians!", CardType.PLAY, Indians::new),
  GENERAL_STORE("General Store", CardType.PLAY, GeneralStore::new),
  GATLING("Gatling", CardType.PLAY, Gatling::new),
  SALOON("Saloon", CardType.PLAY, Saloon::new),
  WELLS_FARGO("Wells Fargo", CardType.PLAY, WellsFargo::new),
  RAG_TIME("Rag Time", CardType.PLAY, RagTime::new),
  DODGE("Dodge", CardType.PLAY, Dodge::new),
  WHISKY("Whisky", CardType.PLAY, Whisky::new),
  PUNCH("Punch", CardType.PLAY, Punch::new),
  BRAWL("Brawl", CardType.PLAY, Brawl::new),
  TEQUILA("Tequila", CardType.PLAY, Tequila::new),
  SPRINGFIELD("Springfield", CardType.PLAY, Springfield::new),

  // Green – Single-use items (Sidestep expansion)
  CONESTOGA("Conestoga", CardType.SINGLE_USE_ITEM, Conestoga::new),
  BUFFALO_RIFLE("Buffalo Rifle", CardType.SINGLE_USE_ITEM, BuffaloRifle::new),
  CAN_CAN("Can Can", CardType.SINGLE_USE_ITEM, CanCan::new),
  HOWITZER("Howitzer", CardType.SINGLE_USE_ITEM, Howitzer::new),
  CANTEEN("Canteen", CardType.SINGLE_USE_ITEM, Canteen::new),
  KNIFE("Knife", CardType.SINGLE_USE_ITEM, Knife::new),
  PEPPERBOX("Pepperbox", CardType.SINGLE_USE_ITEM, Pepperbox::new),
  DERRINGER("Derringer", CardType.SINGLE_USE_ITEM, Derringer::new),
  PONY_EXPRESS("Pony Express", CardType.SINGLE_USE_ITEM, PonyExpress::new),
  SOMBRERO("Sombrero", CardType.SINGLE_USE_ITEM, SingleUseMissed::new),
  BIBLE("Bible", CardType.SINGLE_USE_ITEM, SingleUseMissed::new),
  IRON_PLATE("Iron Plate", CardType.SINGLE_USE_ITEM, SingleUseMissed::new),
  TEN_GALLON_HAT("Ten Gallon Hat", CardType.SINGLE_USE_ITEM, SingleUseMissed::new),

  // Base-game fallback if needed
  COLT("Colt .45", CardType.GUN, Gun::new);

  private final String display;
  private final CardType defaultType;
  private final CardFactory factory;

  CardName(String display, CardType defaultType, CardFactory factory) {
    this.display = display;
    this.defaultType = defaultType;
    this.factory = factory;
  }

  public String getDisplayName() {
    return display;
  }

  @Override
  public String toString() {
    return display;
  }

  public Card newCard(CardSuit suit, CardValue value) {
    return factory.createCard(this, suit, value, defaultType);
  }

  public CardType defaultType() {
    return defaultType;
  }
}
