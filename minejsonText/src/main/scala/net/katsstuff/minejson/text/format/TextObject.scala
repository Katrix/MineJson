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

sealed trait TextObject

sealed trait TextStyle extends TextObject
object TextStyle {

  val AllStyles = Seq(Bold, Underlined, Italic, StrikeThrough, Obfuscated)

  case object Bold          extends TextStyle
  case object Underlined    extends TextStyle
  case object Italic        extends TextStyle
  case object StrikeThrough extends TextStyle
  case object Obfuscated    extends TextStyle
}

sealed trait TextColor extends TextObject
object TextColor {

  /**
    * Not a real color, just means that there is no color.
    */
  case object NoColor extends TextColor

  case object Black       extends TextColor
  case object DarkBlue    extends TextColor
  case object DarkGreen   extends TextColor
  case object DarkAqua    extends TextColor
  case object DarkRed     extends TextColor
  case object DarkPurple  extends TextColor
  case object Gold        extends TextColor
  case object Gray        extends TextColor
  case object DarkGray    extends TextColor
  case object Blue        extends TextColor
  case object Green       extends TextColor
  case object Aqua        extends TextColor
  case object Red         extends TextColor
  case object LightPurple extends TextColor
  case object Yellow      extends TextColor
  case object White       extends TextColor
  case object Reset       extends TextColor

  val AllColors =
    Seq(
      Black,
      DarkBlue,
      DarkGreen,
      DarkAqua,
      DarkRed,
      DarkPurple,
      Gold,
      Gray,
      DarkGray,
      Blue,
      Green,
      Aqua,
      Red,
      LightPurple,
      Yellow,
      White
    )
}
