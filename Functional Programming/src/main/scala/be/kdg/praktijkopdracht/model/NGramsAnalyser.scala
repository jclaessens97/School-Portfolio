package be.kdg.praktijkopdracht.model

class NGramsAnalyser {
  //region Start/End with
  private def wordsStartingWith(words: Vector[String], ngram: String): Float =
    words.count(w => w.toLowerCase.startsWith(ngram.toLowerCase))

  private def wordsEndingWith(words: Vector[String], ngram: String): Float =
    words.count(w => w.toLowerCase.endsWith(ngram.toLowerCase))

  def wordsStartingWithElements(words: Vector[String], elements: Vector[String]): Map[String, Float] =
    elements.map(el => Map(el -> wordsStartingWith(words, el))).reduce(_++_)

  def wordsEndingWithElements(words: Vector[String], elements: Vector[String]): Map[String, Float] =
    elements.map(el => Map(el -> wordsEndingWith(words, el))).reduce(_++_)
  //endregion




  //region char-frequencies
  private def characterOccurences(text: String, char: String): Int =
    text.toLowerCase.count(c => c.toString == char.toLowerCase())

  private def charsetOccurenceSum(text: String, charset:String): Int =
    charset.toVector.map(c => characterOccurences(text, c.toString)).sum

  private def vowelOccurence(text: String, vowels: String): Int =
    charsetOccurenceSum(text, vowels)

  private def consonantOccurence(text: String, consonants: String): Int =
    charsetOccurenceSum(text, consonants)

  def vowelConsonantFrequencies(text: String, consonants: String, vowels: String): Map[String, Float] = {
    val consonantCount = consonantOccurence(text, consonants)
    val vowelCount = vowelOccurence(text, vowels)
    val perc = (x:Int,y:Int) => x.toFloat / (x+y)

    Map("Consonants" -> perc(consonantCount, vowelCount) ,
      "Vowels" -> perc(vowelCount,consonantCount))
  }

  private def mapFrequencies(chars : Vector[String], freqs: Vector[Float]): Map[String, Float] = {
    if(chars.size == 1) Map(chars.head -> freqs.head)
    else Map(chars.head -> freqs.head)++mapFrequencies(chars.tail, freqs.tail)
  }

  def charsetFrequencies(text: String, charset: Vector[String]): Map[String, Float] = {
    val occurences = charset.map(ch => characterOccurences(text, ch))
    val frequencies = occurences.map(occ => occ.toFloat / occurences.sum)
    mapFrequencies(charset, frequencies)
  }
  //endregion



  //region multigram frequencies
  private def multigramOccurrences(text: String, multigram: String, charset: String=""): Int = {
    val regex = s"(${multigram.replace("_", "["+charset+"]?")})".r
    regex.findAllIn(text).size
  }

  private def sortMultigrams(multigrams: Vector[String], occurences: Vector[Int]): Vector[String] = {
    multigrams.sortWith((first,second) => occurences.drop(multigrams.indexOf(first)).head > occurences.drop(multigrams.indexOf(second)).head)
  }

  def multigramFrequencies(text: String, multigrams: Vector[String],skipgramCharset:String="", top:Int=25): Map[String, Float] = {
    val counts = multigrams.map(multigramOccurrences(text, _, skipgramCharset))
    val sortedMultigrams = sortMultigrams(multigrams, counts).take(top)
    sortedMultigrams.map(multigram => Map(multigram -> counts.sorted.reverse.drop(sortedMultigrams.indexOf(multigram)).head / counts.sum.toFloat)).reduce(_++_)
  }

  def getTopMultigrams(text: String, multigrams: Vector[String],skipgramCharset:String="", top:Int=25): Vector[String] = {
    val counts = multigrams.map(multigramOccurrences(text, _, skipgramCharset))
    sortMultigrams(multigrams, counts).take(top)
  }
  //endregion



  //region helpers
  def round(freq: Float, decimals: Int=4): Float =
    String.format(s"%.${decimals}f", freq).toFloat

  def removeNonCharacters(text: String, characterSet: String): String =
    text.filter(c => characterSet.contains(c.toLower))
  //endregion
}
