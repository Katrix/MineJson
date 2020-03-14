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

import io.circe.syntax._
import net.katsstuff.minejson.loottable.LootTable

trait LootTableGenerator {

  def lootTables: Seq[LootTable]

  def lootTablesFileMap: Map[String, String] =
    lootTables
      .map(a => s"assets/${a.fileName.domain}/loot_tables/${a.fileName.path}.json" -> a.asJson.printWith(ResourcePrinter))
      .toMap

  def lootTablesCreateFiles(resources: Path): Unit = {
    val map = lootTablesFileMap

    map.foreach {
      case (advPath, content) =>
        val path = resources.resolve(advPath)
        Files.createDirectories(path.getParent)
        Files.write(path, content.split("\n").toSeq.asJava)
    }
  }

  def lootTablesCreateFiles(resource: File): Unit = lootTablesCreateFiles(resource.toPath)
}
