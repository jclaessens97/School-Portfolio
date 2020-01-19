import be.kdg.praktijkopdracht.presenter.NGramsPresenter
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PresenterTest extends FunSuite{
  test("Test") {
    val presenter = new NGramsPresenter
    presenter.preprocessText("Wat is wat de wat een, ?")
    assert(true)
  }
}
