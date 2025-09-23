![Kraplow logo](https://raw.github.com/ccarrster/kraplow/master/westerncardgame/logo2.png)

# Kraplow
A web Western card game you can play online — implements the same rules as a popular western card game. Play multiplayer, solo, or mix AI and humans. Features chat, a game log, and Ajax polling.

Note: No database is used; game state is stored in server session memory.

## Tech stack
- Language: Java
- Build/tooling: Maven (packaging: WAR)
- Web: Jakarta Servlet API 6 (Servlet containers like Tomcat 10.1+ or Jetty 12)
- UI: Static HTML/JS (see WebContent/index.html)
- Tests: JUnit (JUnit 5 on the classpath with JUnit 3/4 style tests) and Mockito (test scope)

## Requirements
- JDK 25 or newer (enforced by Maven Enforcer Plugin)
- Maven 3.9+
- A Servlet 6–compatible container (e.g., Apache Tomcat 10.1+ or Jetty 12) to deploy the WAR

## Getting started
1. Clone the repository
   - git clone https://github.com/ccarrster/kraplow.git
   - cd kraplow
2. Build
   - mvn clean package
   - The WAR will be at target/westerncardgame-1.0.0-SNAPSHOT.war
3. Deploy
   - Copy the WAR to your servlet container’s deployment directory (e.g., TOMCAT_HOME/webapps)
   - Start or restart the server

## Run and access
- Default context path (from artifactId): /westerncardgame
- Frontend: http://localhost:8080/westerncardgame/index.html
- Backend servlet endpoint: /westerncardgame/chat
  - This is handled by the servlet com.chriscarr.game.AjaxServlet (see WebContent/WEB-INF/web.xml and src/main/java/com/chriscarr/game/AjaxServlet.java)
- The client uses a getServletUrl() function in WebContent/index.html which currently returns "/westerncardgame/chat". If you deploy under a different context path, update this function accordingly.

## Scripts and commands
Primary (Maven):
- Build: mvn clean package
- Tests only: mvn test
- Clean: mvn clean

Legacy Ant build:
- Ant has been disabled in this repo (see build.xml). Use Maven instead.

## Environment variables
- None required for local development by default.
- TODO: Document any optional environment configuration if/when introduced (ports, context path overrides, etc.).

## Tests
- Test framework: JUnit (mix of classic TestCase-based tests and newer JUnit 5 engine on the classpath)
- Run all tests: mvn test
- Test sources: src/test/java

Some representative tests:
- com.chriscarr.SetupTest — validates deck setup and role distribution
- com.chriscarr.CardsInPlayTest — validates items in play logic
- com.chriscarr.TurnTest — extensive turn flow and card interactions

## Entry points
- Server: com.chriscarr.game.AjaxServlet mapped to /chat (see WebContent/WEB-INF/web.xml)
- Client: WebContent/index.html (drives the UI and calls the servlet API)

## Project structure
- WebContent/
  - index.html and assets (client UI)
  - WEB-INF/web.xml (servlet mappings; maps /chat to AjaxServlet)
- src/main/java/
  - com/chriscarr/bang/... (core game logic, cards, turns, UI abstractions)
  - com/chriscarr/game/AjaxServlet.java (HTTP API)
- src/test/java/
  - com/chriscarr/... (unit tests)
- pom.xml (Maven build; packaging: war)
- build.xml (Ant stub; disabled — points to Maven)
- target/ (build outputs)

Note: Web resources currently live under WebContent/. For Maven standard layout, these typically live under src/main/webapp. If you plan to rely solely on Maven to assemble the WAR, consider moving WebContent/ to src/main/webapp or configuring the maven-war-plugin to include WebContent. TODO: Align web resource location with Maven.

## Deployment notes
- Tomcat: Drop the WAR into webapps and access http://localhost:8080/westerncardgame/
- Jetty: Deploy the WAR via your Jetty configuration
- If you see 404s for /chat, verify that web.xml maps /chat and that the servlet class is com.chriscarr.game.AjaxServlet

## Roadmap / TODOs
- Sidestep Township expansion: UI toggle exists (beta). TODO: finalize wording and add images for new blue/brown/green cards.
- TODO: Show card suits for Apache Kid in UI.
- TODO: Fix known/reported bugs (see issue tracker).

## License
- TODO: Add a LICENSE file and state the project’s license. The top-level repository currently lacks a LICENSE.

## Acknowledgements
- The UI credits in index.html thank artists for artwork contributions.
