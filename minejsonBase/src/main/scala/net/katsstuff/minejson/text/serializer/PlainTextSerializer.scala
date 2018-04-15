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

object PlainTextSerializer extends TextSerializer {

  override def serialize(text: Text): String = {
    val childrenContent = text.children.map(serialize).mkString
    val content = text match {
      case text: LiteralText                           => text.content
      case text: TranslateText                         => text.key.format(text.args: _*)
      case ScoreText(_, _, Some(value), _, _, _, _, _) => value
      case ScoreText(name, _, None, _, _, _, _, _)     => name
      case text: SelectorText                          => text.selector
      case text: KeybindText                           => text.key
    }
    s"$content$childrenContent"
  }
  override def deserialize(string: String):      Try[Text] = Success(Text(string))
  override def deserializeThrow(string: String): Text      = Text(string)
}
