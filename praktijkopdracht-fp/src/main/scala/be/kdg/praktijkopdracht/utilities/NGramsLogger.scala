package be.kdg.praktijkopdracht.utilities
import java.util.Date

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.{Logger, LoggerContext}

class NGramsLogger extends {
  @transient lazy val logger: Logger = LoggerContext.getContext.getRootLogger
  logger.setLevel(Level.INFO)

  def logProblem(errMsg: String): Unit = logger.error(errMsg)

  def logWordCount(count: Int): Unit = logCount("words", count)

  def logUniqueWordCount(count: Int): Unit = logCount("unique words", count)

  def logPunctuationCount(count: Int): Unit = logCount("punctuation characters", count)

  def logUppercaseLetterCount(count: Int): Unit = logCount("uppercase characters", count)

  def logLowercaseLetterCount(count: Int): Unit = logCount("lowercase characters", count)

  private def logCount(specific: String, count: Int) : Unit = logger.info(s"Amount of $specific in text: $count")

  def logAnalysis(startDate: Date, analysis: String, results: Map[String, AnyVal]): Unit =
    logger.info(s"$startDate: finished analysing \'$analysis\':\n $results")
}
