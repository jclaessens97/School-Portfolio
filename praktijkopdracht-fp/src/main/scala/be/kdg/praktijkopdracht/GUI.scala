package be.kdg.praktijkopdracht

import be.kdg.praktijkopdracht.presenter.NGramsPresenter
import be.kdg.praktijkopdracht.view.NGramsView
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class GUI extends Application
{
  override def start(primaryStage: Stage): Unit =
  {
    val view = new NGramsView(new NGramsPresenter)
    val scene: Scene = new Scene(view)
    scene.getStylesheets.add(getClass.getClassLoader.getResource("style.css").toExternalForm)

    primaryStage.setTitle("NGram Analyser")
    primaryStage.setMaximized(true)
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}
