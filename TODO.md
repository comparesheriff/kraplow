# TODO — Code Quality and Refactoring Plan (Expanded)

Updated: 2025-09-22

Purpose
- Provide clear, simple, and obtainable steps to improve code quality, guide refactorings, and reduce code smells.
- Keep tasks small and incremental so the game remains playable after each change.
- Add concrete examples, suggested names, and migration playbooks to make each task executable.

How to use this document
- Work top-to-bottom, one checkbox at a time.
- After each task: run tests (mvn -q -DskipTests=false test) and play a short manual round.
- When a task suggests creating a new class/interface, prefer moving existing logic rather than introducing new behavior.
- Keep public APIs stable during each sprint; use adapter layers and translation methods while migrating.

Project overview and hotspots

- com.chriscarr.bang.turn.Turn is very large (~1200 lines) and mixes responsibilities: turn flow, rules resolution,
  targeting/range, damage, discard/death handling, game-state mapping, and UI prompts. This is the main refactor
  hotspot.
- com.chriscarr.bang.cards.Card centralizes many card-name constants and embeds attack resolution logic (shoot) that overlaps with Turn responsibilities.
- Several card classes (e.g., Panic, CatBalou, Conestoga) duplicate “choose opponent → choose card in hand/in-play → take/discard → print info” logic.
- User interfaces (JSPUserInterface, WebGameUserInterface) contain AI, messaging, and UI concerns together; Turn calls directly into UI, creating tight coupling.
- Game state DTOs (com.chriscarr.bang.gamestate) are read through Turn, further coupling domain and view.

Guiding principles
- Strive for Single Responsibility: each class should own one thing (e.g., damage resolution, range calculation, UI communication, DTO mapping).
- Prefer composition over inheritance unless behavior naturally forms a hierarchy.
- Reduce magic strings: use enums or types for card names and roles.
- Keep the domain model UI-agnostic; communicate via interfaces and events.
- Make state transitions explicit (context objects) and side effects visible.

Global improvements (small, safe steps)
- [ ] Introduce a coding style and formatter (Google Java Format or Spotless).
  - Suggested: Spotless plugin in pom.xml (report-only first):
    - Plugin name: com.diffplug.spotless:spotless-maven-plugin:2.43.0
    - Goal: spotless:apply (manual), spotless:check (CI)
    - Include src/main/java and src/test/java
  - Command examples:
    - mvn spotless:apply
    - mvn spotless:check
- [ ] Add Checkstyle and SpotBugs with a minimal ruleset (report-only at first).
  - Checkstyle plugin: maven-checkstyle-plugin:3.3.1 with google_checks (customized)
  - SpotBugs plugin: com.github.spotbugs:spotbugs-maven-plugin:4.8.6.4 with low-effort detectors
  - Start with: unused imports, long method warnings, cyclomatic complexity hints
  - Run manually: mvn -DskipTests=false test checkstyle:check spotbugs:check (do not fail build initially)
- [x] Replace magic strings with enums/typed constants.
  - Example: CardName enum with entries SHOOT, MISSED, BEER, PANIC, CAT_BALOU, SCHOFIELD, VOLCANIC, REMINGTON, WINCHESTER, REV_CARBINE, DYNAMITE, JAIL, etc.
  - Migration layer: add Card.getCardName(): Optional<CardName> and keep getName() returning String; call getCardName().orElse(null) in transitional checks.
  - Replace usages: if (card.getName().equals(Card.CARDBANG)) → if (card.getCardName().orElse(null) == CardName.SHOOT)
- [x] Prefer Optional over null for lookups that can miss.
  - Example: Turn.getPlayerForName(String) → Optional<Player> getPlayerByName(String name)
  - Call sites: Optional<Player> p = turn.getPlayerByName("Alice"); p.ifPresent(...)
- [ ] Add equals/hashCode/toString for value-like classes.
  - Card: Define identity semantics explicitly. Suggested approach during migration: identity by object (default Object.equals) for cards in play/hand; add a valueToString() for logging. For DTOs (GameStateCard, GameStatePlayer): use value semantics (all fields).
  - Add toString() for Card as "Card{name='Beer', suit=HEARTS, value=NINE, type=BROWN}".
- [x] Introduce lightweight logging via slf4j + logback.
  - Add dependencies: org.slf4j:slf4j-api and ch.qos.logback:logback-classic (test/runtime).
  - Usage example: private static final Logger log = LoggerFactory.getLogger(Turn.class); log.debug("Bangs played: {}", bangsPlayed);
  - Keep userInterface.printInfo for user-facing messages only.
- [ ] Add unit-test scaffolding.
  - PlayerBuilder (test-only) with withHealth(int), withHand(Card...), withInPlay(Card...), withCharacter(Character)
  - DeckStub implements Deck-like API: deterministic pull(), seeded order
  - CardsInPlayBuilder for setting gun, items quickly

Common helpers/abstractions to extract
- [ ] TargetingService (or RangeService)
  - Suggested name: TargetingService
  - Package: com.chriscarr.bang.services
  - Key methods:
    - List<Player> getPlayersWithinRange(Player source, List<Player> all)
    - List<Player> playersWithCards(List<Player> players)
    - int effectiveRange(Player source, Player target) // accounts for gun, Mustang/Scope
  - Replace: Turn.getPlayersWithinRange, Turn.getPlayersWithCards, parts of Bang.targets and Panic.targets
- [ ] CardTransferHelper
  - Package: com.chriscarr.bang.services
  - Purpose: Handle “askOthersCard + move/remove card + print info” for Panic, CatBalou, Conestoga
  - Suggested API:
    - enum SelectionArea { HAND, IN_PLAY, GUN }
    - static Selection pickSelection(UserInterface ui, Player actor, Player target)
    - static TransferResult take(UserInterface ui, Player actor, Player target)
    - static TransferResult discardFrom(UserInterface ui, Player actor, Player target, Discard discard)
    - record Selection(SelectionArea area, int index)
    - record TransferResult(SelectionArea area, Card card)
  - Replace magic indices (-1, -2) with SelectionArea
  - Example: Panic.play → CardTransferHelper.take(...)
- [ ] DamageResolver
  - Package: com.chriscarr.bang.services
  - Purpose: Centralize damage flow: barrels, Missed/Dodge, double-miss (Slab), Apache Kid diamond immunity, Molly Stark draw, Bible single-use, damage application
  - Suggested API:
    - boolean resolveAttack(Card attackCard, Player attacker, Player defender, List<Player> all, Deck deck, Discard discard, UserInterface ui, Turn turn)
    - int barrelSaves(Player defender, Deck deck, Discard discard, UserInterface ui, int needed, Player attacker)
    - int applyDamage(Player defender, int amount, Player source, Deck deck, Discard discard, UserInterface ui, Turn turn)
  - Initial usage: Card.shoot delegates to DamageResolver.resolveAttack
- [ ] GameStateMapper
  - Package: com.chriscarr.bang.gamestate
  - Move from Turn: cardToGameStateCard, getGameStatePlayers, getDiscardTopCard
  - Suggested API:
    - static List<GameStatePlayer> mapPlayers(List<Player> players, Player current)
    - static GameStateCard mapCard(Card c)
    - static GameStateCard topOfDiscard(Discard discard)
- [ ] TurnContext
  - Package: com.chriscarr.bang
  - Fields: Player currentPlayer; Deck deck; Discard discard; UserInterface ui; int bangsPlayed; int joseActions; int uncleWillActions
  - Usage: replace long parameter lists incrementally (start with Card.shoot and Turn.damagePlayer)

Package-by-package plan (with examples, names, and steps)

1) com.chriscarr.bang (core game logic)
- Turn class
  - [ ] Create TurnContext
    - File: src/main/java/com/chriscarr/bang/TurnContext.java
    - Minimal constructor: TurnContext(Player current, Deck deck, Discard discard, UserInterface ui)
    - Add counters later; keep getters only to stay immutable for now
  - [ ] Extract TargetingService
    - Move Turn.getPlayersWithinRange(Player, List<Player>) verbatim; write unit tests; then optimize
    - Replace Bang.targets and Panic.targets to call service
  - [ ] Extract DamageResolver
    - Move Turn.isBarrelSave, Turn.validPlayMiss, Turn.validRespondTwoMiss, and parts of Card.shoot that apply damage
    - Keep Turn.damagePlayer as public but delegate to DamageResolver.applyDamage
  - [ ] Extract EndOfTurnService (suggested name)
    - Methods to host: discard(Player), deadDiscardAll(Player), handleDeath(...)
    - Keep same params initially; later change to TurnContext
  - [ ] Extract GameOverService
    - Methods: isGameOver(List<Player>), getWinners(List<Player>), getRoles(List<Player>)
    - Replace internal calls in Turn with service
  - [ ] Move GameState mapping methods to GameStateMapper
    - Replace Turn.getGameStatePlayers, Turn.cardToGameStateCard, Turn.getDiscardTopCard with GameStateMapper calls
  - [ ] Reduce UI coupling
    - Introduce interface TurnUI in package com.chriscarr.bang.userinterface.adapters
    - Methods: chooseTarget(List<Player> options, Player actor), chooseCardToDiscard(Player owner), notify(String message)
    - WebGameUserInterface and JSPUserInterface implement TurnUI; Turn uses TurnUI only
  - [ ] Polling loops → adapter
    - Replace while loops that call userInterface repeatedly with adapter methods that encapsulate waiting/timeout
- Player, Hand, Deck, Discard, CardsInPlay
  - [ ] Encapsulate invariants
    - Methods: Player.addHealth(int) should cap at max; Player.removeHealth(int) should not go below 0 (caller still checks death)
  - [ ] CardsInPlay API cleanup
    - Add Optional<Card> removeItemByName(String name), Optional<Card> removeGun(), boolean hasGun(), boolean isGunVolcanic()
    - Avoid using == for identity; prefer equals or explicit identity policy documented in Card
  - [ ] Test builders
    - PlayerBuilder, CardsInPlayBuilder with withGun(CardName), withItem(CardName)

2) com.chriscarr.bang.cards (cards and behaviors)
- Card and constants
  - [ ] Introduce CardName enum
    - File: src/main/java/com/chriscarr/bang/cards/CardName.java
    - Enum values: SHOOT("Shoot"), MISSED("Missed!"), BEER("Beer"), PANIC("Panic!"), CAT_BALOU("Cat Balou"), DUEL("Duel"), STAGECOACH("Stagecoach"), INDIANS("Indians!"), GENERAL_STORE("General Store"), GATLING("Gatling"), SALOON("Saloon"), WELLS_FARGO("Wells Fargo"), RAG_TIME("Rag Time"), DODGE("Dodge"), WHISKY("Whisky"), HIDEOUT("Hideout"), SILVER("Silver"), PUNCH("Punch"), BRAWL("Brawl"), TEQUILA("Tequila"), SPRINGFIELD("Springfield"), CONESTOGA("Conestoga"), BUFFALO_RIFLE("Buffalo Rifle"), CAN_CAN("Can Can"), HOWITZER("Howitzer"), SOMBRERO("Sombrero"), BIBLE("Bible"), CANTEEN("Canteen"), IRON_PLATE("Iron Plate"), KNIFE("Knife"), PEPPERBOX("Pepperbox"), DERRINGER("Derringer"), TEN_GALLON_HAT("Ten Gallon Hat"), PONY_EXPRESS("Pony Express"), BARREL("Barrel"), SCOPE("Scope"), MUSTANG("Mustang"), JAIL("Jail"), DYNAMITE("Dynamite"), SCHOFIELD("Schofield"), VOLCANIC("Volcanic"), REMINGTON("Remington"), WINCHESTER("Winchester"), REV_CARBINE("Rev. Carbine")
    - Add method static Optional<CardName> fromString(String s)
  - [ ] Move static methods to components
    - Card.getRange(String gunName) → Gun.getRange(CardName gun)
    - Card.multiBang(String gunName) → Gun.canMultiBang(CardName gun)
    - Card.isExplode(Card card) → ExplosiveRules.isExplode(Card)
  - [ ] Narrow Playable to a single signature (later sprint)
    - Future: boolean play(ActionContext ctx)
- Behavioral abstractions
  - [ ] Create abstract templates
    - AttackCard: template method play() calls DamageResolver.resolveAttack
    - StealOrDiscardCard: provides common flow using CardTransferHelper
    - HealCard: provides heal logic; Beer/Saloon/Tequila extend it
    - EquipmentCard: base for in-play items; Gun extends with getRange()
    - MissResponder: unify SingleUseMissed + Missed/Dodge checks
  - [ ] Targeting helpers
    - SelfTarget: returns List.of(player)
    - RangeLimitedTarget: uses TargetingService.effectiveRange
    - EveryoneTarget: returns all opponents
    - JailTargeting: valid targets for Jail (non-Sheriff, etc.)
- Duplications to remove (examples)
  - [ ] askOthersCard loops → CardTransferHelper
    - Replace -1 (HAND) and -2 (GUN) with SelectionArea.HAND/GUN
    - Example replacement of Panic.play:
      - Selection sel = CardTransferHelper.pickSelection(ui, currentPlayer, other);
      - TransferResult tr = CardTransferHelper.take(ui, currentPlayer, other);
      - currentPlayer.getHand().add(tr.card()); discard.add(this);
  - [ ] Centralize Apache Kid and Slab the Killer checks in DamageResolver
    - From Card.shoot: move diamond immunity and double-miss logic
  - [ ] Replace string checks with enum switches
    - switch(card.getCardName().orElse(null)) { case DODGE -> ...; case MISSED -> ...; }
- Safety and clarity
  - [ ] Replace raw index arithmetic with SelectionArea and Selection
  - [ ] SingleUse.removeFromInPlay: use equals and return boolean; stop scanning with ==

3) com.chriscarr.bang.gamestate (DTOs and mapping)
- [ ] Make DTOs immutable where possible
  - Final fields, constructor injection, no setters; add builder if needed for tests
- [ ] Move mapping logic to GameStateMapper
  - Implement map methods and call them from GameStateImpl instead of Turn
- [ ] Stable view model for UI
  - Ensure GameState provides all fields UI needs (names, roles when public, health, in-play summaries)

4) com.chriscarr.bang.userinterface (UI and AI)
- [ ] Extract AI decision-making
  - Create com.chriscarr.bang.ai.AIDecider with methods: chooseTargetForAttack, chooseCardSelectionForPanic, shouldHealNow
  - WebGameUserInterface delegates to AIDecider when AI-controlled
- [ ] Introduce TurnUI adapter interface
  - Package: com.chriscarr.bang.userinterface.adapters
  - Methods: chooseTarget, chooseCard, confirm, notify
  - Turn depends on TurnUI; Web/JSP provide adapters to existing UI
- [ ] Centralize user-visible strings/messages
  - Create Messages class or enum; later consider i18n
- [ ] Timeouts and retries
  - Implement in adapter, not scattered in UI classes

Testing strategy (add as you refactor)
- [ ] Unit tests for TargetingService
  - Class: TargetingServiceTest; tests: scopeIncreasesRange(), mustangDecreasesRange(), neighborWrapsAround()
- [ ] Unit tests for DamageResolver
  - Class: DamageResolverTest; tests: barrelSavesOnce(), slabRequiresTwoMisses(), apacheKidImmuneToDiamonds(), mollyStarkDrawsOnDefense()
- [ ] Unit tests for CardTransferHelper
  - Class: CardTransferHelperTest; tests: panicStealsFromHand(), catBalouDiscardsGun(), conestogaPullsFromInPlayIndex()
- [ ] Contract tests for Turn phases
  - Class: TurnContractTest; tests: drawPhaseTwoCards(), endTurnDiscardToHandLimit(), dynamiteExplodesDealsThree()
- [ ] Test builders
  - Classes: PlayerBuilder, DeckStub, CardsInPlayBuilder; usage in the above tests

Prioritized sprints (bite-sized backlog)
Sprint 1 (1–2 days)
- [ ] Introduce CardName enum and a translation layer (no behavior change)
  - Add CardName enum; add Card.getCardName(); replace 3–5 obvious string comparisons with enum checks
- [ ] Extract GameStateMapper and move mapping methods out of Turn
  - Copy Turn.cardToGameStateCard/getGameStatePlayers/getDiscardTopCard to new class; wire GameStateImpl to call mapper
- [ ] Add TargetingService with getPlayersWithinRange moved from Turn
  - Replace Bang.targets and Panic.targets to use service
- [ ] Add basic tests for TargetingService and GameStateMapper
  - New tests under src/test/java/com/chriscarr

Sprint 2 (2–3 days)
- [ ] Create TurnContext and replace long parameter lists in 2–3 hotspots (Card.shoot, Turn.damagePlayer)
  - Add constructor and getters; no behavior change
- [ ] Extract DamageResolver; rewire Bang and one more attack-like card to use it
  - Keep Card.shoot but delegate to resolver; maintain skipDiscard behavior for now
- [ ] Extract CardTransferHelper; rewire Panic and CatBalou to use it
  - Keep UI messages the same; verify with tests and manual run
- [ ] Add tests for DamageResolver and CardTransferHelper

Sprint 3 (2–3 days)
- [ ] Introduce EquipmentCard and Gun with explicit range; migrate gun subclasses
  - Move range logic out of Card.getRange
- [ ] Replace magic index (-1, -2) protocol with explicit SelectionArea
  - Update Web UI prompts accordingly via adapter
- [ ] Move AI routines out of WebGameUserInterface into AIDecider

Sprint 4 (as needed)
- [ ] Reduce Turn further by extracting EndOfTurnService and GameOverService
- [ ] Consolidate Playable to a single play signature; use ActionContext/TurnContext
- [ ] Adopt formatter and enable Checkstyle/SpotBugs as build-breaking

Concrete places to start (file and method hints)
- Turn
  - getPlayersWithinRange(Player, List<Player>) → TargetingService.getPlayersWithinRange
  - isBarrelSave, validPlayMiss, validRespondTwoMiss, damagePlayer → DamageResolver
  - getGameStatePlayers, cardToGameStateCard, getDiscardTopCard → GameStateMapper
  - getPlayerForName(String) → Optional and Map index (e.g., name→player cache)
- Card
  - shoot(...) → delegate to DamageResolver.resolveAttack; keep Card as a thin facade
  - Static constants (CARDBANG, CARDMISSED, etc.) → CardName enum
- Cards (examples)
  - Panic/CatBalou/Conestoga → CardTransferHelper for choose/move/discard
  - Beer/Saloon/Tequila → HealCard abstraction

Definition of done for each refactor item
- Tests pass and new tests added where noted.
- No behavior changes unless explicitly called out; UI text remains the same.
- Public method signatures of Turn and Playable are preserved until a planned consolidation step.
- TODO.md item is checked off and any follow-up items are added if discovered.

Migration playbooks (risky changes)
- CardName enum
  - Step 1: Introduce enum + Card.getCardName(); do not remove String getName()
  - Step 2: Replace comparisons in Bang, Card.shoot, Panic, CatBalou with enum checks
  - Step 3: Convert constants users to CardName, then delete string constants
- TurnContext
  - Step 1: Create class with references only (no behavior)
  - Step 2: Overload methods to accept TurnContext while keeping old signatures; call new from old
  - Step 3: Migrate call sites gradually; remove old signatures when all migrated

Naming conventions (suggestions)
- Services: TargetingService, DamageResolver, EndOfTurnService, GameOverService, GameStateMapper
- Adapters: TurnUI, WebTurnUIAdapter, JSPTurnUIAdapter
- Builders (tests): PlayerBuilder, DeckStub, CardsInPlayBuilder
- Enums: CardName, SelectionArea
- DTOs: GameStatePlayerDto (if renaming), GameStateCardDto
- Packages: com.chriscarr.bang.services (services), com.chriscarr.bang.userinterface.adapters (adapters), com.chriscarr.bang.ai (AI)

Notes and observations from current code
- Turn is recursive via nextTurn() calling turnLoop; ensure extraction doesn’t alter control flow.
- Card.shoot manages: target choice, Apache Kid immunity, Slab the Killer double-miss, Barrel saves, Molly Stark draws, Dodge draw-on-play, and discard handling — a strong candidate to centralize.
- SingleUse.removeFromInPlay uses reference equality; define consistent identity rules for Cards in play.
- WebGameUserInterface mixes AI, messaging, and user interaction; a small adapter for Turn will simplify testing and enable alternate UIs.
