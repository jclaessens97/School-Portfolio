import be.kdg.praktijkopdracht.model.PreProcessor
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PreProcessorTest extends FunSuite {
  val preProcessor = new PreProcessor

  test("Non-unique wordCount should return 6") {
    val text = "This is a test text test"
    val count = preProcessor.wordCount(text, unique = false)
    assert(count == 6)
  }

  test("Unique wordCount should return 5") {
    val text = "This is a test text test"
    val count = preProcessor.wordCount(text, unique = true)
    assert(count == 5)
  }

  test("Punctiation count should return 4") {
    val text = "This, is a test. text! test?"
    val count = preProcessor.punctuationCount(text)
    assert(count == 4)
  }

  test("Lowercase count should return 14") {
    val text = "This is a TEST text test"
    val count = preProcessor.charCountLower(text)
    assert(count == 14)
  }

  test("Uppercase count should return 5") {
    val text = "This is a TEST text test"
    val count = preProcessor.charCountUpper(text)
    assert(count == 5)
  }
}
