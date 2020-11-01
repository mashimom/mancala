# Mancala assignment 

The task [assignment](./Coding_Challenge.pdf)

![Java CI with Gradle](https://github.com/mashimom/mancala/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

## The plan

* [x] setup project properly from spring initialzr and testing layer perspective
* [x] get minimal engine implementation with tests and tdd
    * [x] set model
    * [x] set operations
    * [x] set end game
* [x] add REST interface with controllers
* [x] refactor/cleanup from legacy implementation
* [ ] players are remote, from different machines
    + [ ] add players, with:
        - id
        - alias
        - game count
        - win count
    + [ ] add authentication (reverse proxy?)
        - only logged in access
    + [ ] add authorization
        - pass player id to endpoint
* [ ] wait room for players, join 2 *different* players in a game
* [ ] leaderboard to challenge other players
* [ ] list ongoing and finished games for player
* [x] open ongoing game
* [ ] only moves own player game and pit
* [ ] only access own player's stats or games
* [ ] check other player's stats (optional?)
* [ ] player gets message when it is not their turn yet
* [ ] integration tests (postman on docker)
* [ ] add data store
* [ ] kubernetes deploy
* [ ] serve it (optional?)
* [ ] final clean-up refactor for release v1

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

Testing with Spock first for readability, also to save time and portability with best in class reports found at `build/spock-reports/index.html`.

### Test documentation

Run tests (`gradle check`) then check output at [build/spock-reports/index.html](./build/spock-reports/index.html)
 
### Tests

* Aim on high unit test coverage for fast builds and some Integration test coverage for correctness when multiple actions and state are relevant.
* single letter variables denote a mock or that the variable is not relevant but required to process

## Deployment

---
