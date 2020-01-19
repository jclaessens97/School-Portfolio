import be.kdg.praktijkopdracht.model.{NGramsGenerator, NGramsAnalyser}
import be.kdg.praktijkopdracht.utilities.IOManager
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NGramsGeneratorTest extends FunSuite {
  val nGramsGenerator = new NGramsGenerator

  test("Unigram of alfabet should be correctly generated") {
    val alfabet = "abcdefghijklmnopqrstuvwxyz"
    val validationAlfabet = alfabet.toCharArray.toVector.sorted
    val unigram = nGramsGenerator.unigrams(alfabet).sorted
    assert(unigram == validationAlfabet)
  }

  test("Bigram of abc should be correctly generated") {
    val bigram = nGramsGenerator.bigrams("abc")
    val validationBigram = Vector("aa","ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc")

    assert(bigram.sorted == validationBigram.sorted)
  }

  test("Skipgrams of abc should be correctly generated") {
    val skipgram = nGramsGenerator.skipgrams("abc")
    val validationSkipgram = Vector("a_a","a_b", "a_c", "b_a", "b_b", "b_c", "c_a", "c_b", "c_c")

    assert(skipgram.sorted == validationSkipgram.sorted)
  }

  test("Trigrams of abc should be correctly generated") {
    val trigram = nGramsGenerator.trigrams("abc")
    val validationTrigram = Vector("aaa","aab","aac","aba","abb","abc","aca","acb","acc",
                                  "baa","bab","bac","bba","bbb","bbc","bca","bcb","bcc",
                                  "caa","cab","cac","cba","cbb","cbc","cca","ccb","ccc")

    assert(trigram.sorted == validationTrigram.sorted)
  }
}
