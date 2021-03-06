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

import java.io.File
import java.nio.file.{Files, Path}

import scala.collection.JavaConverters._

import net.katsstuff.minejson.advancement.Advancement
import io.circe.syntax._

trait AdvancementGenerator {

  def advancements: Seq[Advancement]

  def advancementFileMap: Map[String, String] =
    advancements
      .map(a => s"assets/${a.fileName.domain}/advancements/${a.fileName.path}.json" -> a.asJson.printWith(ResourcePrinter))
      .toMap

  def advancementCreateFiles(resources: Path): Unit = {
    val map = advancementFileMap

    map.foreach {
      case (advPath, content) =>
        val path = resources.resolve(advPath)
        Files.createDirectories(path.getParent)
        Files.write(path, content.split("\n").toSeq.asJava)
    }
  }

  def advancementCreateFiles(resource: File): Unit = advancementCreateFiles(resource.toPath)
}
