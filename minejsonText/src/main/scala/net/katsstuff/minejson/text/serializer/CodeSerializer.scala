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

  val codesToObjects = Map(
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

  val objectsToCodes: Map[TextObject, Char] = codesToObjects.map(_.swap)

  def codeChar: Char

  override def serialize(text: Text): String = {
    val builder = new StringBuilder

    def inner(text: Text): Unit = {
      if (text.format.color != NoColor) builder.append(s"$codeChar${objectsToCodes(text.format.color)}")
      for ((style, applied) <- text.format.style.styles if applied) builder.append(s"$codeChar${objectsToCodes(style)}")
      val rawContent = text match {
        case text: LiteralText                           => text.content
        case text: TranslateText                         => text.key.format(text.args: _*)
        case ScoreText(_, _, Some(value), _, _, _, _, _) => value
        case ScoreText(name, _, None, _, _, _, _, _)     => name
        case text: SelectorText                          => text.selector
        case text: KeybindText                           => text.key
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

    val parts = for ((element, i) <- elements.zipWithIndex) yield {
      if (element.isEmpty) Text.Empty
      else if (codesToObjects.contains(element.head)) {
        val obj = codesToObjects(element.head)
        if (element.length == 1) obj //If there is just the code, just return the obj itself
        else t"$obj${element.tail}"
      } else if (i != 0)
        t"$codeChar$element" //The first element may not have a charCode before it, so we treat it for itself
      else t"$element"
    }

    Text(parts: _*)
  }
}
