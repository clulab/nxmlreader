package ai.lum.nxmlreader

import java.io.{BufferedOutputStream, File, FileOutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.StandardCharsets
import scala.io.Source
import scala.util.Using

class TestNxmlReader extends Test {
  val utf8: String = StandardCharsets.UTF_8.toString
  val dir = "./src/test/resources/"
  val inSubdir = "in/"
  val outSubdir = "out/"
  val filenames = Seq("PMC1702562.nxml", "PMC2958468.nxml")
  val nxmlReader = new NxmlReader()

  def textFromFile(path: String): String = {
    Using.resource(Source.fromFile(path, utf8)) { source =>
      source.mkString
    }
  }

  def printWriterFromFile(path: String): PrintWriter = {
    val bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))

    new PrintWriter(new OutputStreamWriter(bufferedOutputStream, utf8))
  }

  def mkExpectedOutput(filename: String): Unit = {
    val textIn = textFromFile(dir + inSubdir + filename)
    val nxmlReader = new NxmlReader()
    val nxmlDocument = nxmlReader.parse(textIn)
    val textOut = nxmlDocument.text

    Using.resource(printWriterFromFile(dir + outSubdir + filename)) { printWriter =>
      printWriter.println(textOut)
    }
  }

  filenames.foreach { filename =>
    if (!new File(dir + outSubdir, filename).exists) {
      mkExpectedOutput(filename)

      assert(new File(dir + outSubdir, filename).exists)
    }
  }

  behavior of "NxmlReader"

  def testFilename(filename: String): Unit = {
    it should "reproduce " + filename in {
      val textIn = textFromFile(dir + inSubdir + filename)
      val nxmlDocument = nxmlReader.parse(textIn)
      val actualTextOut = nxmlDocument.text
      val expectedTextOut = textFromFile(dir + outSubdir + filename)

      actualTextOut should be (expectedTextOut)
    }
  }

  filenames.foreach { filename =>
    testFilename(filename)
  }
}
