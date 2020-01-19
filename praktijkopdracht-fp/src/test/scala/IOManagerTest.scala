import be.kdg.praktijkopdracht.utilities.IOManager
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IOManagerTest extends FunSuite {
  val ioManager = new IOManager;

  test("find the right language vowels") {
    val sets = Vector("Deens:","volledig     -> abcdefghijklmnopqrstuvwxyzæåø","klinkers     -> aeiouæåø","medeklinkers -> bcdfghjklmnpqrstvwxyz",
                "Fins:","volledig     -> abcdefghijklmnopqrstuvwxyzäö","klinkers     -> aeiouäö","medeklinkers -> bcdfghjklmnpqrstvwxyz",
                "Italiaans:","volledig     -> abcdefghilmnopqrstuvz","klinkers     -> aeiou","medeklinkers -> bcdfghlmnpqrstvz",
                "Nederlands:","volledig     -> abcdefghijklmnopqrstuvwxyz","klinkers     -> aeiou","medeklinkers -> bcdfghjklmnpqrstvwxyz")
    val result = ioManager.findCharacterSet(sets, "fins")
    assert(result.drop(1).head == "aeiouäö")
  }
}
