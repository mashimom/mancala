# Kalah/Mancala assignment 

The task [assignment](./Coding_Challenge.pdf)

![Java CI with Gradle](https://github.com/mashimom/mancala/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

I had one implementation of the game I did earlier this year, which I adapted to have players on separate machines.

The original design split the game in three parts:
- Board: actual rules, mechanics and status
- Game: transactional game aspects, isolates players from the game board
- Player: here for the time restriction it is internally user and externally player. Which can be later split, but conceptually a player in this game maps to user.


## The plan

* [x] setup project properly from spring initialzr and testing layer perspective
* [x] get minimal engine implementation with tests and tdd
    * [x] set model
    * [x] set operations
    * [x] set end game
* [x] add REST interface with controllers
* [x] refactor/cleanup from legacy implementation
* [x] players are remote, from different machines
    + [x] add players, with:
        - id
        - alias
        - game count
        - win count
    + [ ] add authentication (reverse proxy?)
        - only logged in access
    + [ ] add authorization
        - pass player id to endpoint
* [x] wait room for players, join 2 *different* players in a game
* [ ] leaderboard to challenge other players (optional)
* [ ] list ongoing and finished games for player
* [x] open ongoing game
* [x] only moves own player game and pit
* [ ] only access own player's stats or games
* [ ] check other player's stats (optional)
* [ ] player gets message when it is not their turn yet (optional?)
* [ ] integration tests (postman on docker?)
* [x] add data store
* [ ] kubernetes deploy
* [ ] serve it (optional?)
* [ ] final clean-up refactor for release v1

### The compromises

1. the waiting room is an entity on database, it is a poor choice but for time constraints I am not building a queue.  
The good solution would be to queue players that want to participate in a game, probably use the `wins/games` rate to tier players in different queues for better experience. Then an async process would consume pairs of players from the queue and create the games.

2. In order to avoid dtos, and thus a lot of boilerplate code, there is a serialization hack to get public ids from entities.  
DTOs should not be first concern when creating a POC, they are a natural change that comes after the drift between the API aggregates and the data store entities.

### Reasoning

Instead of creating a game with a single user, I added the waiting room concept, which can later be used as means for leaderboard matching or to have games among a small selected group.

The intention behind _(defunct)_ rematch is for a player not only have a general score over all games regardless the opponents, but also have a specific score between a pair of players.

## How to build

```bash
gradle 
```
## How to run

### From sources

```bash
gradle && gradle bootRun
```

## Conventions

Testing with Spock first for readability, also to save time and portability with best in class reports.

### Test documentation

Run tests (`gradle check`) then check output at [./build/reports/spock-reports/test](./build/reports/spock-reports/test/index.html) and [./build/reports/spock-reports/integrationTest](./build/reports/spock-reports/integrationTest/index.html)

### Coverage report

Run tests (`gradle check`) then check JaCoCo output at [./build/reports/jacoco/test/html](./build/reports/jacoco/test/html/index.html)
 
### Tests

* Aim on high unit test coverage for fast builds and some Integration test coverage for correctness when multiple actions and state are relevant.
* single letter variables denote a mock or that the variable is not relevant but required to process

## Deployment

---
