/*
 * This file is part of MineJson, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 Katrix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.katsstuff.minejson

import scala.collection.immutable.NumericRange
import scala.language.implicitConversions

import io.circe.{Encoder, Json}
import io.circe.syntax._
import net.katsstuff.minejson.text.Text

case class ResourceId(domain: String, path: String) {
  override def toString: String = s"$domain:$path"
}
object ResourceId {
  def apply(string: String): ResourceId = {
    if (string.contains(":")) {
      val strings = string.split(":", 2)
      ResourceId(strings(0), strings(1))
    } else ResourceId("minecraft", string)
  }

  implicit def mkId(string: String): ResourceId          = apply(string)
  implicit val encoder:              Encoder[ResourceId] = (a: ResourceId) => a.toString.asJson
}

sealed trait RangeOrSingle
object RangeOrSingle {
  implicit val encoder: Encoder[RangeOrSingle] = {
    case Single(i)       => i.asJson
    case Range(min, max) => Json.obj("min" := min, "max" := max)
  }

  implicit def mkRange(range: scala.Range.Inclusive): Range =
    Range(Some(range.start), Some(range.end))

  implicit def mkSingle(i: Int): Single = Single(i)
}
case class Single(i: Int)                                          extends RangeOrSingle
case class Range(min: Option[Int] = None, max: Option[Int] = None) extends RangeOrSingle

sealed trait DoubleRangeOrSingle
object DoubleRangeOrSingle {
  implicit val encoder: Encoder[DoubleRangeOrSingle] = {
    case DoubleSingle(i)       => i.asJson
    case DoubleRange(min, max) => Json.obj("min" := min, "max" := max)
  }

  implicit def mkRange(range: NumericRange.Inclusive[Double]): DoubleRange =
    DoubleRange(Some(range.start), Some(range.end))

  implicit def mkSingle(i: Double): DoubleSingle = DoubleSingle(i)
}
case class DoubleSingle(i: Double)                                             extends DoubleRangeOrSingle
case class DoubleRange(min: Option[Double] = None, max: Option[Double] = None) extends DoubleRangeOrSingle

sealed trait TextOrString
object TextOrString {
  implicit def liftString(str: String): TextOrStringAsString = TextOrStringAsString(str)
  implicit def liftText(text: Text):    TextOrStringAsText   = TextOrStringAsText(text)

  implicit val encoder: Encoder[TextOrString] = {
    case TextOrStringAsString(str) => str.asJson
    case TextOrStringAsText(text) =>
      import net.katsstuff.minejson.text.serializer.JsonTextSerializer._
      text.asJson
  }
}
case class TextOrStringAsString(str: String) extends TextOrString
case class TextOrStringAsText(text: Text)    extends TextOrString
