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
package net.katsstuff.minejson.text

import org.scalatest.{FunSuite, Matchers}

class TextSpec extends FunSuite with Matchers {

  test("A text should be constructed from no args") {
    Text() should equal(Text.Empty)
  }

  test("A text with only text should return a literal text") {
    Text("Foo") should equal(LiteralText("Foo"))
  }

  test("A text with only text and formatting should return a literal text") {
    Text(Red, "Foo") should equal(LiteralText("Foo", format = TextFormat(Red)))
  }

  test("A text should merge in children in a predictable manner") {
    Text(Text("Foo"), Text("Bar")) should equal(LiteralText("FooBar"))
  }

  test("Text with no children should remain unchanged") {
    val text = Text(Red, "Foo")
    text.optimize should equal(text)
  }

  test("Two texts with the same format should be mergeable") {
    val merged = Text(Red, "Foo").merge(Text(Red, "Bar"))
    merged should contain(Text(Red, "FooBar"))
  }

  test("Text with a single child should be optimized") {
    val text = Text(Red, "Foo", Text(Red, "Bar"))
    text.optimize should equal(Text(Red, "FooBar"))
  }

  test("A text with formatting and two literal children should be optimizable") {
    Text(Red, Text("Foo"), Text("Bar")).optimize should equal(LiteralText("FooBar", format = TextFormat(Red)))
  }

}
