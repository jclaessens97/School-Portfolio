package be.kdg.praktijkopdracht.view

import javafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, PieChart, XYChart}
import javafx.scene.control.{Button, ComboBox, Label, ToggleButton, ToggleGroup}
import be.kdg.praktijkopdracht.presenter.NGramsPresenter
import com.jfoenix.controls.JFXButton

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.jdk.CollectionConverters.SeqHasAsJava
import javafx.scene.image.{Image, ImageView}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{Insets, VPos}
import javafx.scene.layout._

class NGramsView(presenter: NGramsPresenter) extends BorderPane
{
  //SET ROOT SETTINGS
  getStyleClass.add("view")
  val lblAnalysisMethod = new Label("")

  // TOP CONTROLS
  val lblLanguage = new Label("Select a language")
  val pieIcon = new Image("/icons/pie-chart.png")
  val barIcon1 = new Image("/icons/bar-chart1.png")
  val barIcon2 = new Image("/icons/bar-chart2.png")
  val barIconView1 = new ImageView(barIcon1)
  val barIconView2 = new ImageView(barIcon2)
  val pieIconView = new ImageView(pieIcon)
  val ddLanguages = new ComboBox[String]()
  val chartToggleGroup = new ToggleGroup()
  val cConst = new ColumnConstraints()
  val toggleBar1 = new ToggleButton()
  val toggleBar2 = new ToggleButton()
  val togglePie = new ToggleButton()
  val rConst = new RowConstraints()
  val topContainer = new GridPane()
  val toggleBox = new HBox()


  togglePie.setToggleGroup(chartToggleGroup)
  toggleBar1.setToggleGroup(chartToggleGroup)
  toggleBar2.setToggleGroup(chartToggleGroup)
  toggleBar1.setGraphic(barIconView1)
  toggleBar2.setGraphic(barIconView2)
  togglePie.setGraphic(pieIconView)
  barIconView1.setFitHeight(24)
  barIconView2.setFitHeight(24)
  barIconView1.setFitWidth(24)
  barIconView2.setFitWidth(24)
  pieIconView.setFitHeight(24)
  ddLanguages.setMinWidth(150)
  pieIconView.setFitWidth(24)
  togglePie.setSelected(true)

  GridPane.setMargin(toggleBox, new Insets(10))
  topContainer.getColumnConstraints.add(cConst)
  topContainer.getRowConstraints.add(rConst)
  rConst.setValignment(VPos.CENTER)
  cConst.setPercentWidth(15)
  rConst.setMinHeight(50)


  topContainer.add(lblLanguage, 0, 0, 1, 1)
  topContainer.add(ddLanguages, 1, 0, 1, 1)
  topContainer.add(toggleBox,2,0,2,1)

  toggleBox.setSpacing(5);
  toggleBox.getChildren.addAll(togglePie, toggleBar1, toggleBar2)


  // RIGHT CONTROLS
  val btnFreqSkipBi = new JFXButton("Top 25 most frequent skipgrams and their bigram counterparts")
  val btnFreqBiTri = new JFXButton("Top 25 most frequent bigrams AND trigrams")
  val btnFreqSkip = new JFXButton("Top 25 most frequent skipgrams")
  val btnStartBi = new JFXButton("Top 25 most frequent bigrams")
  val btnFreqVowelCons = new JFXButton("Vowels and Consonants")
  val btnEndBi = new JFXButton("Top 25 most frequent bigrams")
  val btnStartChar = new JFXButton("Characters")
  val btnFreqChar = new JFXButton("Characters")
  val btnEndChar = new JFXButton("Characters")
  val rightContainer = new VBox(new Label("Words starting with:"), btnStartBi, btnStartChar,
                                new Label("Words ending with:"), btnEndBi, btnEndChar,
                                new Label("Frequencies of:"), btnFreqSkipBi, btnFreqBiTri, btnFreqSkip,
                                                                                  btnFreqVowelCons, btnFreqChar)
  rightContainer.setSpacing(10);

  // CENTER CONTROLS
  val main = new StackPane()
  val verticalBarChart = new BarChart(new CategoryAxis(), new NumberAxis())
  val horizontalBarChart = new BarChart(new NumberAxis(),new CategoryAxis())
  horizontalBarChart.setLegendVisible(false)
  verticalBarChart.setLegendVisible(false)
  horizontalBarChart.setAnimated(false)
  verticalBarChart.setAnimated(false)

  //SET CONTROLS
  setCenter(main)
  setRight(rightContainer)
  setTop(topContainer)

  //LOAD LANGUAGES
  val languages: Vector[String] = presenter.getLanguages("alfabetten.txt")
  languages.foreach(ddLanguages.getItems.add)
  ddLanguages.getSelectionModel.select(0)

  //SET BEHAVIOUR
  ddLanguages.setOnAction(_ => changedLanguage(ddLanguages.getSelectionModel.getSelectedItem))
  btnFreqVowelCons.setOnAction(e => changedAnalysis(e, "freqVowelCons"))
  btnFreqSkipBi.setOnAction(e => changedAnalysis(e, "freqSkipBi"))
  btnFreqBiTri.setOnAction(e => changedAnalysis(e, "freqBiTri"))
  btnStartChar.setOnAction(e => changedAnalysis(e, "startChar"))
  btnFreqSkip.setOnAction(e => changedAnalysis(e, "freqSkip"))
  btnFreqChar.setOnAction(e => changedAnalysis(e, "freqChar"))
  btnStartBi.setOnAction(e => changedAnalysis(e, "startBi"))
  btnEndChar.setOnAction(e => changedAnalysis(e, "endChar"))
  btnEndBi.setOnAction(e => changedAnalysis(e, "endBi"))
  toggleBar1.setOnAction(_ => changeChart())
  toggleBar2.setOnAction(_ => changeChart())
  togglePie.setOnAction(_ => changeChart())


  def changedAnalysis(event: ActionEvent, analysis: String): Unit = {
    rightContainer.getChildren.forEach(n => n.getStyleClass.remove("active"))

    val source = event.getSource.asInstanceOf[JFXButton];
    source.getStyleClass.add("active");

    val language = ddLanguages.getSelectionModel.getSelectedItem
    val result = presenter.getResult(language, analysis)
    lblAnalysisMethod.setText(analysis)
    setChart(result)
  }

  def changedLanguage(language: String): Unit = {
    val analysis = lblAnalysisMethod.getText
    if(! (analysis.isBlank || analysis.isEmpty)) {
      val result = presenter.getResult(language, analysis)
      setChart(result)
    }
  }

  //CHARTS
  private def changeChart(): Unit = {
    //bestaat er al een chart
    if (main.getChildren.size() > 0){
      val chart = main.getChildren.get(0)
      //hergebruik de data van een vertical bar chart
      if(chart.equals(verticalBarChart)){
        setChart(verticalBarChart.getData.asScala.toList
          .map(d => listToMapBarVertical(d.getData.asScala.toList)).reduce(_++_))
      }
      //hergebruik de data van een horizontal bar chart
      else if(chart.equals(horizontalBarChart)){
        setChart(horizontalBarChart.getData.asScala.toList
          .map(d => listToMapBarHorizontal(d.getData.asScala.toList)).reduce(_++_))
      }
      //hergebruik de data van een piechart
      else setChart(listToMapPie(chart.asInstanceOf[PieChart].getData.asScala.toList))
    }
  }

  //MAPPING CHARTDATA
  private def listToMapBarVertical(data: List[XYChart.Data[String, Number]]): Map[String, Float] = {
    if(data.size == 1) return  Map(data.head.getXValue -> data.head.getYValue.floatValue())
    Map(data.head.getXValue -> data.head.getYValue.floatValue()) ++ listToMapBarVertical(data.tail)
  }

  private def listToMapBarHorizontal(data: List[XYChart.Data[Number,String]]): Map[String, Float] = {
    if(data.size == 1) return  Map(data.head.getYValue -> data.head.getXValue.floatValue())
    Map(data.head.getYValue -> data.head.getXValue.floatValue()) ++ listToMapBarHorizontal(data.tail)
  }

  private def listToMapPie(data: List[PieChart.Data]): Map[String, Float] = {
    if(data.size == 1) return Map(data.head.getName -> data.head.getPieValue.toFloat)
    Map(data.head.getName -> data.head.getPieValue.toFloat) ++ listToMapPie(data.tail)
  }

  //SET CHART DATA
  private def setChart(result: Map[String, Float]): Unit = {
    val selectedButton = chartToggleGroup.getSelectedToggle
    if(selectedButton != null){
      if (selectedButton.equals(togglePie)) setPieChartData(result)
      else if (selectedButton.equals(toggleBar1)) setBarChartData(result, vertical = false)
      else if (selectedButton.equals(toggleBar2)) setBarChartData(result, vertical = true)
    }
  }

  private def setPieChartData(results: Map[String, Float]): Unit = {
    main.getChildren.clear()
    val data = results.map((set:(String, Float)) => List(new PieChart.Data(set._1, set._2))).reduce(_++_)
    val piechart = new PieChart(FXCollections.observableList(data.asJava))
    piechart.setLegendVisible(false)
    piechart.setLegendVisible(false)
    main.getChildren.add(piechart)
  }

  private def setBarChartData(results: Map[String, Float], vertical: Boolean): Unit = {
    main.getChildren.clear()
    if(vertical){
      val dataSeries = new XYChart.Series[String, Number]()
      results.foreachEntry((k, v) => dataSeries.getData.add(new XYChart.Data[String, Number](k,v:Number)))
      verticalBarChart.getData.setAll(dataSeries)
      main.getChildren.add(verticalBarChart)
    }
    else{
      val dataSeries = new XYChart.Series[Number, String]()
      results.foreachEntry((k, v) => dataSeries.getData.add(new XYChart.Data[Number,String](v:Number,k)))
      horizontalBarChart.getData.setAll(dataSeries)
      main.getChildren.add(horizontalBarChart)
    }
  }
}
