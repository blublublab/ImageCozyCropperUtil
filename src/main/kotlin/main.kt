
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import java.util.function.Consumer
import javax.imageio.ImageIO

//This code is shit as hell. Glad i'm only one person who need it. It works like a charm :)


const val OUTPUT_PATH = "d:\\tiles/input/"
fun main(){
    val file = File("d:\\tiles/")
    val filesPng = mutableListOf<String>()
    fetchFiles(file) { f -> run {
            filesPng.add(f.absolutePath)
        }
    }

    Files.createDirectories(Paths.get(OUTPUT_PATH))


    val folders = filesPng
        .filter { it.contains(".png") }
        .map { it.split("tiles\\")[1] }
        .map {it.split("\\")[0]}
        .distinct()

    folders.forEach { action ->
       val fileNames =  filesPng.filter {  file -> file.contains(action) }
        for(filename in fileNames){
            if(filename.contains("char")) {
                val charIndex = filename
                    .split("char")[1]
                    .take(1)
                    .toInt()
                val filePath = File(filename)
                var pathName = ""

                val  additionalWord = if(filename.contains( "without")) " without" else ""
                pathName = "${OUTPUT_PATH}${action}${additionalWord}_${charIndex - 1}.png"
                filePath.renameTo(File(pathName))
            } else {
                  try {
                      val image = ImageIO.read(File(filename))

                      var hairName = filename.split("\\")[4]
                      val underscores = hairName.count { it == '_' }
                      val hairNames = hairName.split("_")
                      //пофиксить удаление нижних подчеркиваний и готово

                      hairName = ""
                      for(i in 0 until underscores){
                          val underscore = if(i != 0 || i != underscores-1) " " else ""
                          hairName += underscore + hairNames[i]
                      }

                      for (i in 0..image.width/32) {
                          val subImage = image.getSubimage(
                              i * (32 * getWidthTiles(action)), 0, 32 * getWidthTiles(action), image.height
                          )
                          val nameWithoutFile = filename.split("\\").subList(0, 4).joinToString("\\")
                          val path = "${OUTPUT_PATH}$action ${hairName}_$i.png"
                          ImageIO.write(subImage, "png", File(path))
                      }
                  } catch (e : Exception) {
                      e.printStackTrace()
                  }
                val  hairFile = File(filename)
                hairFile.delete()

            }
        }

    }

}
fun getWidthTiles(folderName: String) : Int = when(folderName){
    "walk", "carry" -> 8
    "axe", "jump", "pickaxe", "fish", "hoe" -> 5
    "sword", "water" -> 4
    "hurt", "block" -> 1
    "die" -> 2
    else -> 5
}

fun fetchFiles(dir: File, fileConsumer: Consumer<File>) {
    if (dir.isDirectory) {
        for (file1 in dir.listFiles()) {
            fetchFiles(file1, fileConsumer)
        }
    } else {
        fileConsumer.accept(dir)
    }
}