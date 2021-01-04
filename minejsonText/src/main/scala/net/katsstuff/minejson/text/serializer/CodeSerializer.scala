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
package net.katsstuff.minejson.text.serializer

import scala.util.{Success, Try}

import net.katsstuff.minejson.text._
import net.katsstuff.minejson.text.format.TextObject

abstract class CodeSerializer extends TextSerializer {

  val codesToObjects: Map[Char, TextObject] = Map(
    '0' -> Black,
    '1' -> DarkBlue,
    '2' -> DarkGreen,
    '3' -> DarkAqua,
    '4' -> DarkRed,
    '5' -> DarkPurple,
    '6' -> Gold,
    '7' -> Gray,
    '8' -> DarkGray,
    '9' -> Blue,
    'a' -> Green,
    'b' -> Aqua,
    'c' -> Red,
    'd' -> LightPurple,
    'e' -> Yellow,
    'f' -> White,
    'k' -> Obfuscated,
    'l' -> Bold,
    'm' -> StrikeThrough,
    'n' -> Underlined,
    'o' -> Italic,
    'r' -> Reset
  )

  def objectsToCodes(obj: TextObject): String =
    codesToObjects.map(_.swap).get(obj).fold(defaultObjectToCode(obj))(_.toString)

  def defaultObjectToCode(obj: TextObject): String = ""

  def codeChar: Char

  override def serialize(text: Text): String = {
    val builder = new StringBuilder

    def inner(text: Text): Unit = {
      builder.append(codeChar.toString)
      builder.append(objectsToCodes(Reset))
      for ((style, applied) <- text.format.style.styles if applied) builder.append(s"$codeChar${objectsToCodes(style)}")
      builder.append(s"$codeChar${objectsToCodes(text.format.color)}")

      val rawContent = text match {
        case text: LiteralText                           => text.content
        case text: TranslateText                         => text.key.format(text.args: _*)
        case ScoreText(_, _, Some(value), _, _, _, _, _) => value
        case ScoreText(name, _, None, _, _, _, _, _)     => name
        case text: SelectorText                          => text.selector
        case text: KeybindText                           => text.key
        case text: NBTText                               => text.nbtPath
      }
      builder.append(rawContent)

      text.children.foreach(inner)
    }

    inner(text)
    builder.mkString
  }

  override def deserialize(string: String): Try[Text] = Success(deserializeThrow(string))

  override def deserializeThrow(string: String): Text = {
    val elements = string.split(codeChar).toSeq

    elements.zipWithIndex
      .foldLeft((TextFormat.None, Text.Empty)) {
        case ((format, acc), (element, i)) =>
          if (element.isEmpty) (format, Text(acc, codeChar.toString))
          else if (i == 0) (TextFormat.None, Text(element))
          else if (codesToObjects.contains(element.head)) {
            val newFormat = codesToObjects(element.head) match {
              case Reset            => TextFormat.None.copy(color = Reset)
              case style: TextStyle => format.copy(style = format.style + ((style, true)))
              case color: TextColor =>
                format.copy(color = color, style = CompositeTextStyle.None) //Setting a color resets the style
            }

            if (element.length == 1) newFormat -> acc
            else newFormat                     -> Text(acc, newFormat, element.tail)
          } else format -> Text(acc, format, codeChar.toString, element)
      }
      ._2
  }
}
