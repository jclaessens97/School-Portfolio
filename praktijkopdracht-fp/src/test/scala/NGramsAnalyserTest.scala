import be.kdg.praktijkopdracht.model.NGramsAnalyser
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NGramsAnalyserTest extends FunSuite{
  val analyser = new NGramsAnalyser

  test("correctly remove invalid characters"){
    val characterSet = "abc"
    val text = "AbCdef"
    val result = analyser.removeNonCharacters(text, characterSet)

    assert(result == "AbC")
  }

  test("Correct words starting with character"){
    val words = Vector("revenue", "rectangle", "circle", "avenue", "robust", "aperture", "regional")
    val count = analyser.wordsStartingWithChar(words, 'r')
    assert(count == 4)
  }

  test("Correct words ending with character"){
    val words = Vector("spam", "ham", "bar", "eggs", "prom")
    val count = analyser.wordsEndingWithChar(words, 'm')
    assert(count == 3)
  }

  test("Correct words starting with bigram"){
    val words = Vector("revenue", "rectangle", "circle", "avenue", "robust", "aperture", "regional")
    val count = analyser.wordsStartingWithBigram(words, "re")
    assert(count == 3)
  }

  test("Correct words ending with bigram"){
    val words = Vector("spam", "ham", "bar", "eggs", "prom")
    val count = analyser.wordsEndingWithBigram(words, "am")
    assert(count == 2)
  }

  test("Correct character frequency of text"){
    val characterSet = "abcdefghijklmnopqrstuvwxyz"
    val text = analyser.removeNonCharacters("Thisis is a test, mist", characterSet)
    val freq = analyser.characterFrequency(text, 's')
    assert(freq == analyser.round(5/17.toFloat))
  }

  test("Correct multigram occurrences of text"){
    val text = "is thisis a test, mist"
    val occurences = analyser.multigramOccurrences(text, "is")

    assert(occurences == 4)
  }

  test("Correct skipgram occurrences of text"){
    val text = "is tisis a test, mists"
    val occurences = analyser.skipgramOccurrences(text, "t_s", "abcdefghijklmnopqrstuvwxyz")

    assert(occurences == 3)
  }

  test("Correct sorting of multigrams based on occurences"){
    val bigrams = Vector("is", "si", "st", "mi", "te")
    val text = "is thisis a test, mist"
    val sorted = analyser.sortMultigrams(bigrams, bigrams.map(analyser.multigramOccurrences(text, _)))

    assert(sorted.head == "is")
  }

  test("Correct multigram-frequencies of text"){
    val bigrams = Vector("is", "si", "mi", "st")
    val text = "is isis test mist"
    val frequencies = analyser.multigramFrequencies(text, bigrams)

    assert(frequencies.head._1 == "is")
  }

  test("Correct skipgram-frequency of text"){
    val set = "abcdefghijklmnopqrstuvwxyz"
    val skipgrams = Vector("i_y", "t_i", "h_n")
    val text = "this thin thiny shiny tai limy shady honduras hundai"
    val frequencies = analyser.skipgramFrequencies(text, skipgrams, set)

    assert(frequencies.head._1 == "h_n")
  }
}
