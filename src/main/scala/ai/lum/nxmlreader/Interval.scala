package ai.lum.nxmlreader

class Interval private (val start: Int, val end: Int) extends Ordered[Interval] {

  override def toString: String = {
    if (isEmpty) "{}"
    else if (length == 1) s"{$start}"
    else s"[$start, $end)"
  }

  // Since Intervals will be used as keys in a map,
  // both equals and hashCode are necessary.
  override def equals(that: Any): Boolean = that match {
    case that: Interval => that.canEqual(this) && {
      if (this.isEmpty) that.isEmpty
      else {
        // Since this is not empty, it will not match
        // an empty that.
        this.start == that.start &&
        this.end == that.end
      }
    }
    case _ => false
  }

  def canEqual(that: Any): Boolean = that.isInstanceOf[Interval]

  override def hashCode: Int = {
    start * 23 + end
  }

  def length = end - start

  def isEmpty: Boolean = length <= 0

  def intersects(that: Interval): Boolean = {
    if (this.isEmpty || that.isEmpty)
      false
    else if (this.start < that.start)
      this.end > that.start
    else if (that.start < this.start)
      that.end > this.start
    else
      true
  }

  def borders(that: Interval): Boolean = {
    if (this.isEmpty || that.isEmpty) false
    else {
      this.start == that.end ||
      that.start == this.end
    }
  }

  override def compare(that: Interval): Int = {
    if (this.start > that.start) 1
    else if (this.start < that.start) -1
    else this.length - that.length
  }

  def union(that: Interval): Interval = {
    if (this.isEmpty) that
    else if (that.isEmpty) this
    else {
      require(this.borders(that) || this.intersects(that))
      Interval.open(
        math.min(this.start, that.start),
        math.max(this.end,   that.end)
      )
    }
  }
}

object Interval {
  val emptyInterval = new Interval(0, 0)

  def apply(start: Int, end: Int): Interval = {
    require(start <= end, s"end < start: $end < $start")
    if (start < end) new Interval(start, end)
    else emptyInterval
  }

  def singleton(i: Int): Interval = apply(i, i + 1)

  def open(start: Int, end: Int): Interval = apply(start, end)

  def ofLength(start: Int, length: Int): Interval = open(start, start + length)

  def union(col: Seq[Interval]): Interval = {
    val sorted = col.sorted

    try {
      sorted.reduceLeft(_.union(_))
    }
    catch {
      case _: IllegalArgumentException =>
        throw new IllegalArgumentException("gap in intervals: " + sorted)
    }
  }
}
