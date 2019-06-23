package data.internal

import data.internal.crypto.Signature
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class TestVote : BehaviorSpec({
    Given("Collection of votes for the same agreementRound") {
        val round = AgreementRound(1, 1)
        val collection = listOf(
            Vote(round, "h1", Signature(Peer("p1"), "s1")),
            Vote(round, "h2", Signature(Peer("p2"), "s2")),
            Vote(round, "h2", Signature(Peer("p3"), "s3")),
            Vote(round, "h3", Signature(Peer("p4"), "s4")),
            Vote(round, "h3", Signature(Peer("p5"), "s5")),
            Vote(round, "h3", Signature(Peer("65"), "h5"))
        )
        When("Collection<Vote> validate is invoked with SameRoundPredicate") {
            Then("It passes a validate") {
                collection.validate({ vote -> SameRoundPredicate()(vote) }).shouldBeTrue()
            }
        }

        When("create a histogram") {
            val histogram = collection.hashHistogram()
            Then("histogram has correct distribution") {
                histogram.size.shouldBe(3)
                histogram[0].shouldBe(HashHistogramItem("h1", 1))
                histogram[1].shouldBe(HashHistogramItem("h2", 2))
                histogram[2].shouldBe(HashHistogramItem("h3", 3))
            }
        }
        When("fetch the most frequent hash") {
            val hashPair = collection.getFrequentHash()
            Then("it contains the frequent hash") {
                hashPair.shouldBe(HashHistogramItem("h3", 3))
            }
        }
    }

    Given("Collection of votes for different rounds") {
        val round1 = AgreementRound(1, 1)
        val round2 = AgreementRound(1, 1, Phase.SECOND)
        val collection = listOf(
            Vote(round1, "h1", Signature(Peer("p1"), "s1")),
            Vote(round2, "h1", Signature(Peer("p2"), "s2"))
        )
        When("Collection<Vote> validate is invoked with SameRoundPredicate") {
            Then("It fails a validate") {
                collection.validate(SameRoundPredicate()).shouldBeFalse()
            }
        }
        When("Collection<Vote> validate is invoked with SameHashPredicate") {
            Then("It pass a validate") {
                collection.validate(SameHashPredicate()).shouldBeTrue()
            }
        }
    }
})
