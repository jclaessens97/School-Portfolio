package be.kdg.praktijkopdracht.utilities

import scala.io.Source


class IOManager
{
  def getText(path: String): String =
    removeSpaces(readFile(path).mkString(" "))

  def getTextVector(path: String): Vector[String] =
    getText(path).split(" ").toVector

  def getCharacters(path: String, language: String): Vector[String] =
    findCharacterSet(readFile(path).toVector, language)

  def getLanguages(path: String): Vector[String] =
    readFile(path).toVector.filter(_.contains(":")).map(_.replace(":",""))

  private def removeSpaces(text : String ) : String =
    text.replaceAll(" +", " ")

  def readFile(path: String): Iterator[String] = {
    val resource = getClass.getClassLoader.getResource(path)
    val res = if(resource != null) resource.getPath.replaceAll("%20", " ") else path
    val file = Source.fromFile(res, "UTF-8")
    val lines = file.getLines()
    //file.close()
    lines
  }

  def findCharacterSet(text: Vector[String], language: String): Vector[String] = {
    val languageIndex = text.indexWhere(_.toLowerCase.contains(language.toLowerCase))
    if(languageIndex == -1) return Vector()

    text.slice(languageIndex + 1, languageIndex + 4).map(_.replaceAll("(.* )", ""))
  }
}
