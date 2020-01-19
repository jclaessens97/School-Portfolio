package be.kdg.praktijkopdracht.model


class NGramsGenerator
{
  def unigrams(charString: String): Vector[String] = {
    charString.toVector.map(_.toString)
  }

  def bigrams(charString: String): Vector[String] = {
    multigrams(charString)
  }

  //creates all possible combinations, with '_' as middle character
  def skipgrams(charString: String): Vector[String] = {
    multigrams(charString, "_")
  }

  //creates all possible combinations with c as middle character
  //finally reduces the returned vectors to a single vector
  def trigrams(charString: String): Vector[String] = {
    charString.toVector
      .map(c => multigrams(charString, c.toString))
      .reduce(_++_)
  }

  //maps every character to a vector with all the possible combinations for that character
  //then reduces every vector to a single vector with all the possible combinations for a characterset
  private def multigrams(charString: String, tussenChar:String=""): Vector[String] = {
    charString.toVector
      .map(c => charString.toVector
        .map(cha => c.toString+tussenChar+cha))
      .reduce(_++_)
  }
}
