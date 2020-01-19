package be.kdg.praktijkopdracht.presenter

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.util.Calendar

import be.kdg.praktijkopdracht.model.{NGramsAnalyser, NGramsGenerator, PreProcessor}
import be.kdg.praktijkopdracht.utilities.{IOManager, NGramsLogger}

class NGramsPresenter
{
  private val logger = new NGramsLogger
  private val io = new IOManager
  private val pp = new PreProcessor
  private val analyser = new NGramsAnalyser
  private val generator = new NGramsGenerator

  def preprocessText(text: String): Unit = {
    logger.logWordCount(pp.wordCount(text))
    logger.logUniqueWordCount(pp.wordCount(text, unique = true))
    logger.logPunctuationCount(pp.punctuationCount(text))
    logger.logLowercaseLetterCount(pp.charCountLower(text))
    logger.logUppercaseLetterCount(pp.charCountUpper(text))
  }

  def executeAnalysis(method: () => Map[String, Float], analysis: String): Map[String, Float] = {
    val startDate = Calendar.getInstance().getTime
    val results = method()
    logger.logAnalysis(startDate, analysis, results)
    results
  }

  def wordsStartingWith(words: Vector[String], ngrams: Vector[String], ngramType:String): Map[String, Float] = {
    val starts = ()=>analyser.wordsStartingWithElements(words, ngrams)
    executeAnalysis(starts, s"words starting with $ngramType")
  }

  def wordsEndingWith(words: Vector[String], ngrams: Vector[String], ngramType:String): Map[String, Float] = {
    val ends = ()=>analyser.wordsEndingWithElements(words, ngrams)
    executeAnalysis(ends, s"words ending with $ngramType")
  }

  def characterFreqs(text: String, characterSet: String):Map[String, Float]= {
    val freqs = ()=>analyser.charsetFrequencies(text, generator.unigrams(characterSet))
    executeAnalysis(freqs, "Character frequencies")
  }

  def vowelConsonantFreqs(text: String, characterSet: Vector[String]): Map[String, Float] = {
    val vowelConsFreqs = ()=>analyser.vowelConsonantFrequencies(text, characterSet.head, characterSet.last)
    executeAnalysis(vowelConsFreqs, "Vowel and Consonant frequencies")
  }

  def bigramTrigramFreqs(text: String, ngrams: Vector[String]): Map[String, Float] = {
    val biTriFreqs = () => analyser.multigramFrequencies(text,ngrams)
    executeAnalysis(biTriFreqs, "Top 25 most frequent bigrams + trigrams")
  }

  def skipgramFreqs(text: String, skipgrams: Vector[String], charset: String): Map[String, Float] = {
    val skipFreqs = () => analyser.multigramFrequencies(text, skipgrams, charset)
    executeAnalysis(skipFreqs, "Top 25 most frequent skipgrams")
  }

  def skipgramBigramFreqs(text: String, skipgrams: Vector[String], charset:String): Map[String, Float] = {
    val commonFreqs = () => {
      val skipFreqs = analyser.multigramFrequencies(text, skipgrams, charset)
      val biFreqs = analyser.multigramFrequencies(text, skipFreqs.keys.toVector.map(_.replace("_", "")))
      skipFreqs++biFreqs
    }
    executeAnalysis(commonFreqs, "Top 25 most frequent skipgrams and theit bigram counterparts")
  }

  //analysing
  def analyseLanguage(language: String, dir: String): Unit = {
    val textPath = createTextPath(language)
    val text = io.getText(textPath)
    val words = text.split(" ").toVector
    val characterSet = io.getCharacters("alfabetten.txt", language)


    val unigrams = generator.unigrams(characterSet.head)
    val bigrams = generator.bigrams(characterSet.head)
    val trigrams = generator.trigrams(characterSet.head)
    val skipgrams = generator.skipgrams(characterSet.head)
    val frequentBigrams = analyser.getTopMultigrams(text, bigrams)

    saveResult(wordsStartingWith(words, unigrams, "characters"),      dir, "startChar.ser")
    saveResult(wordsEndingWith(words, unigrams, "characters"),        dir, "endChar.ser")
    saveResult(wordsStartingWith(words, frequentBigrams, "bigrams"),  dir, "startBi.ser")
    saveResult(wordsEndingWith(words, frequentBigrams, "bigrams"),    dir, "endBi.ser")
    saveResult(characterFreqs(text, characterSet.head),                          dir, "freqChar.ser")
    saveResult(vowelConsonantFreqs(text,characterSet.tail),                      dir, "freqVowelCons.ser")
    saveResult(bigramTrigramFreqs(text, bigrams++trigrams),                      dir, "freqBiTri.ser")
    saveResult(skipgramFreqs(text,skipgrams, characterSet.head),                 dir, "freqSkip.ser")
    saveResult(skipgramBigramFreqs(text,skipgrams,characterSet.head),            dir, "freqSkipBi.ser")
  }

  def startAnalysis():Unit={
    io.getLanguages("alfabetten.txt").foreach(l => {
      val url = s"src/main/resources/results/$l/"
      new File(url).mkdirs()
      analyseLanguage(l, url)
    })
  }

  def saveResult(results: Map[String, Float], dir: String, filename: String): Unit = {
    try {
      val output = new ObjectOutputStream(new FileOutputStream(dir + filename))
      output.writeObject(results)
      output.close()
    } catch {
      case _: Throwable => logger.logProblem("Could not save result")
    }
  }

  def getResult(language: String, analysis: String):Map[String, Float] = {
    //look if file exists
    val url = s"src/main/resources/results/$language/$analysis.ser"
    val exists = new File(url).exists()

    //if file doesn't exist, analyse
    if(!exists) startAnalysis()

    //Find results
    try{
      val filestream = new FileInputStream(url)
      val resultReader = new ObjectInputStream(filestream)
      val res  = resultReader.readObject().asInstanceOf[Map[String, Float]]
      resultReader.close()
      filestream.close()
      res
    } catch {
      case _: Throwable =>
        logger.logProblem("Could not find result")
        Map(""->0)
    }
  }

  //IO
  private def createTextPath(language: String): String = {
    s"europarl/${language.toLowerCase}/Alldata ${language.capitalize}.txt"
  }

  def getLanguages(path: String): Vector[String] = {
    io.getLanguages(path)
  }
}
