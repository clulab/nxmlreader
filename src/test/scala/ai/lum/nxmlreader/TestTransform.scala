package ai.lum.nxmlreader

import scala.xml.Node

class TestTransform extends Test {

  def transform(n: Node): String = n match {
    // case <sub>{text @ _*}</sub> => // old version
    case <sub>{Seq(text @ _*)}</sub> => // new version
      text.text
    case <sub/> =>
      ""
    case _ => "non-match"
  }

  behavior of "transform"

  it should "match full text" in {
    val text = "text"
    val actual = transform(<sub>{text}</sub>)
    val expected = text

    actual should be (expected)
  }

  it should "match blank text" in {
    val text = " "
    val actual = transform(<sub>{text}</sub>)
    val expected = text

    actual should be (expected)
  }

  it should "match empty text" in {
    val text = ""
    val actual = transform(<sub>{text}</sub>)
    val expected = text

    actual should be (expected)
  }

  it should "match non-existent text" in {
    val text = ""
    val actual = transform(<sub></sub>)
    val expected = text

    actual should be (expected)
  }

  it should "match stand-alone tag" in {
    val text = ""
    val actual = transform(<sub/>)
    val expected = text

    actual should be (expected)
  }
}
