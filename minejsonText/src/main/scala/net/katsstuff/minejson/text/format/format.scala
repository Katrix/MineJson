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
package net.katsstuff.minejson.text.format

case class CompositeTextStyle(styles: Map[TextStyle, Boolean]) {

  def +(style: (TextStyle, Boolean)): CompositeTextStyle = copy(styles + style)
  def -(style: TextStyle): CompositeTextStyle            = copy(styles - style)
  def combine(other: CompositeTextStyle): CompositeTextStyle = {
    def combineStyle(self: Option[Boolean], other: Option[Boolean]): Option[Boolean] =
      self match {
        case Some(selfState) =>
          other match {
            case Some(otherState) =>
              if (selfState != otherState) None
              else self
            case None => self
          }
        case None => other
      }

    val combined = TextStyle.AllStyles.map(style => style -> combineStyle(styles.get(style), other.styles.get(style)))

    CompositeTextStyle(combined.collect {
      case (k, Some(v)) => (k: TextStyle) -> v
    }.toMap)
  }
}
object CompositeTextStyle {
  final val None                                    = CompositeTextStyle()
  def apply(styles: TextStyle*): CompositeTextStyle = CompositeTextStyle(styles.map(_ -> true).toMap)
  def fromOptions(styles: Seq[(TextStyle, Option[Boolean])]): CompositeTextStyle = {
    val filtered = styles.collect {
      case (k, Some(v)) => k -> v
    }
    CompositeTextStyle(filtered.toMap)
  }
}

case class TextFormat(color: TextColor = TextColor.NoColor, style: CompositeTextStyle = CompositeTextStyle.None) {

  def combine(other: TextFormat): TextFormat = {
    val otherColor = other.color
    val colorToUse = if (otherColor == TextColor.NoColor) {
      this.color
    } else if (otherColor == TextColor.Reset) {
      TextColor.NoColor
    } else otherColor

    TextFormat(colorToUse, this.style combine other.style)
  }

  override def toString: String = {
    val buffer    = new StringBuilder("TextFormat(")
    var addedText = false

    if (color != TextColor.NoColor) {
      buffer.append(color)
      addedText = true
    }
    if (style != CompositeTextStyle.None) {
      if (addedText) buffer.append(s", $style") else buffer.append(style)
    }
    buffer.append(")").mkString
  }
}
object TextFormat {

  final val None = TextFormat()
}
