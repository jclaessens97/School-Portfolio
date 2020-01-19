package be.kdg.praktijkopdracht.model

class PreProcessor {
  def wordCount(text: String, unique: Boolean = false): Int = {
    val splitText = text.toLowerCase.split("\\W+").toVector.sorted
    if (unique) return splitText.distinct.length
    splitText.length
  }

  def punctuationCount(text: String): Int = {
    text.replaceAll("[\\w \\s]", "").length
  }

  def charCountLower(text: String): Int = {
    text.count(c => c.isLower)
  }

  def charCountUpper(text: String): Int = {
    text.count(c => c.isUpper)
  }
}
