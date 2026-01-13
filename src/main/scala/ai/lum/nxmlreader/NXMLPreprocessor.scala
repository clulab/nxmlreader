package ai.lum.nxmlreader

import scala.xml.{ Elem, Node, Text }
import scala.xml.transform.RewriteRule


trait Preprocessor extends RewriteRule

case class NXMLPreprocessor(
  sectionsToIgnore: Set[String],
  ignoreFloats: Boolean,
  transformText: String => String
) extends Preprocessor {

  // surrounds sup/sub content with spaces
  override def transform(n: Node): Seq[Node] = n match {

    // handle tag representing a line break
    case <break/> => Text("\n")

    // This below is the Scala 2 version.
    // surround subscripts and superscripts with spaces.
    // It should be used with TestNxmlReader to populate
    // the test output directory for regression testing.
//    case <sup>{text @ _*}</sup> =>
//      println(s"found sup with text ${text.text}")
//      <sup> {transformText(text.text)} </sup>
//    case <sub>{text @ _*}</sub> =>
//      println(s"found sub with text ${text.text}")
//      <sub> {transformText(text.text)} </sub>

    // remove latex and tables from text
//    case <tex-math>{text @ _*}</tex-math> =>
//      println(s"Found text-math with text ${text.text}")
//      Nil

    // This below is the Scala 2-3 version with some warnings for Scala 2.
    // surround subscripts and superscripts with spaces
    case <sup>{Seq(text @ _*)}</sup> =>
      println(s"found sup with text ${text.text}")
      <sup> {transformText(text.text)} </sup>
    case <sup/> =>
      println(s"found sup with no text")
      <sup>  </sup>
    case <sub>{Seq(text @ _*)}</sub> =>
      println(s"found sub with text ${text.text}")
      <sub> {transformText(text.text)} </sub>
    case <sub/> =>
      println(s"found sub with no text")
      <sub>  </sub>
    // remove latex and tables from text
    case <tex-math>{Seq(text @ _*)}</tex-math> =>
      println(s"Found tex-math with text ${text.text}")
      Nil
    case <tex-math/> =>
      println(s"Found tex-math with no text")
      Nil

    // This is generic again.
    case e: Elem if e.label == "table-wrap" => Nil // FIXME this is removing captions too

    // append dots to title and surround it with newlines
    case e: Elem if e.label == "article-title" =>
      val e2 = transformChildren(e)
      val txt = Text(s"${transformText(e2.text)}.\n\n")
      <article-title>{txt}</article-title>

    case e: Elem if e.label == "abstract" =>
      val e2 = transformChildren(e)
      val txt = Text(s"\n\n${transformText(e2.text)}\n\n")
      <abstract>{txt}</abstract>

    case e: Elem if e.label == "title" =>
      val e2 = transformChildren(e)
      val txt = Text(s"\n\n${transformText(e2.text)}.\n\n")
      <title>{txt}</title>

    // append newlines to paragraphs
    case e: Elem if e.label == "p" =>
      val e2 = transformChildren(e)
      // FIXME make sure you call transformText() on the children
      // val txt = Text(s"${transformText(e2.text)}\n\n")
      // <p>{txt}</p>
      e2

    // remove floats
    case e: Elem if ignoreFloats && attr(e, "position", "float") => Nil

    // NOTE we are removing graphics that are not floats too
    // maybe we need to ignore tables and figures instead of floats?
    case e: Elem if e.label == "fig" => Nil

    // remove some sections
    case e: Elem if sectionsToIgnore.contains(e.label) | attr(e, "sec-type", sectionsToIgnore) => Nil

    case e: Elem if e.label == "xref" => e

    // recurse
    case e: Elem => transformChildren(e)

    // return unmodified
    case other => other

  }

  def transformChildren(e: Elem): Seq[Node] = {
    e.copy(child = e.child.flatMap(this.transform))
  }

  def transformChildren(e: Elem, append: Node): Seq[Node] = {
    e.copy(child = e.child.flatMap(this.transform) :+ append)
  }

  def transformChildren(e: Elem, prepend: Node, append: Node): Seq[Node] = {
    e.copy(child = prepend +: e.child.flatMap(this.transform) :+ append)
  }

  def attr(e: Elem, name: String, value: String): Boolean = {
    e.attribute(name).exists(_.text == value)
  }

  def attr(e: Elem, name: String, values: Set[String]): Boolean = {
    e.attribute(name).exists(values contains _.text)
  }

}
