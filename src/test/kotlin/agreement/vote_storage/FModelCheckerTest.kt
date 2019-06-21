package agreement.vote_storage

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.BehaviorSpec


class FModelCheckerTest : BehaviorSpec({
    Given("intialized FModelChecker with 7f + 1 model") {
        val checker: MajorityChecker = FModelChecker(7, 5)
        val all = 8

        // -----------------------------------| hasOutcome test |-------------------------------------------------------
        for (voted in 1..5) {
            When("check hasOutcome for value $voted of of $all") {
                Then("there is no outcome") {
                    checker.hasOutcome(1, voted, all).shouldBeFalse()
                }
            }
        }

        for (voted in 7..8) {
            When("check hasOutcome for value $voted of of $all") {
                Then("outcome is present") {
                    checker.hasOutcome(1, voted, all).shouldBeTrue()
                }
            }
        }

        // -----------------------------------| hasCommit test |--------------------------------------------------------

        for (frequent in 0..5) {
            When("check hasCommit for frequent value $frequent of of $all") {
                Then("there is no commit") {
                    checker.hasCommit(frequent, all).shouldBeFalse()
                }
            }
        }

        for (frequent in 6..8) {
            When("check hasCommit for frequent value $frequent of of $all") {
                Then("outcome is present") {
                    checker.hasCommit(frequent, all).shouldBeTrue()
                }
            }
        }

        // -----------------------------------| hasReject test |--------------------------------------------------------
        for (voted in 1..8) {
            for (frequent in 1..voted) {
                When("check reject for voted[$voted] with most frequent[$frequent] of all[$all]") {
                    if (checker.hasCommit(frequent, all)) {
                        checker.hasReject(frequent, voted, all).shouldBeFalse()

                        val revertedIndex = voted - frequent
                        checker.hasReject(revertedIndex, voted, all).shouldBeTrue()
                    }
                }
            }
        }

        // print a map of outcomes
        for (voted in 1..8) {
            for (frequent in 1..voted) {
                print("[")
                if (checker.hasReject(frequent, voted, all)) {
                    print("R")
                }
                if (checker.hasCommit(frequent, all)) {
                    print("C")
                }
                if (checker.hasOutcome(frequent, voted, all)) {
                    print("O")
                }
                print("]")
            }
            println()
        }
    }
})
