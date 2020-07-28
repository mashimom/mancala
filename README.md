# Mancala for bol.com assignment 

The task [assignment](./TechAssignment_2020.pdf)

![Java CI with Gradle](https://github.com/mashimom/mancala/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

## The plan

* [x] setup project properly from spring initialzr and testing layer perspective
* [x] get minimal engine implementation with tests and tdd
    * [x] set model
    * [x] set operations
    * [x] set end game
* [x] add REST interface with controllers
* [ ] minimal UI with templates (not a front-end guy)
* [ ] refactor
* [ ] integration tests (TBD)
* [ ] add bells and whistles, HATEOAS, DB, monitoring
* [ ] refactor
* [ ] pretty-fy UI

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
 
### Tests

* Aim on high unit test coverage for fast builds and some Integration test coverage for correctness when multiple actions and state are relevant.
* single letter variables denote a mock or that the variable is not relevant but required to process
