import com.strobel.assembler.metadata.JarTypeLoader
import com.strobel.assembler.metadata.MetadataSystem
import com.strobel.decompiler.DecompilationOptions
import com.strobel.decompiler.DecompilerSettings
import com.strobel.decompiler.PlainTextOutput
import imgui.ImGui
import imgui.app.Application
import imgui.app.Configuration
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import tv.wunderbox.nfd.FileDialog
import tv.wunderbox.nfd.FileDialogResult
import tv.wunderbox.nfd.nfd.NfdFileDialog
import java.io.File
import java.nio.charset.Charset
import java.util.jar.JarFile
import kotlin.math.max

private val MOD = ImString(1000)
private val FOLDER = ImString(1000)
private val MAPPINGS_FILE = ImString(1000)

fun main() {
    var decompiling = false
    var decompiledClasses = 0
    var classesToDecompilation = 0
    var mappingsType = Mappings.Tiny

    fun parseTiny(
        lines : Collection<String>
    ) : Map<String, String> {
        val mappings = hashMapOf<String, String>()

        for(line in lines) {
            val split = line.split(Regex("\\s"))

            when(split[0]) {
                "CLASS" -> mappings[split[max(split.lastIndex - 1, 0)].split("/").last()] = split.last().split("/").last()
                "FIELD", "METHOD" -> mappings[split[max(split.lastIndex - 1, 0)]] = split.last()
            }
        }

        return mappings
    }

    fun parseCsv(
        lines : Collection<String>
    ) : Map<String, String> {
        val mappings = hashMapOf<String, String>()

        for(line in lines.toMutableList().also { it.removeFirst() }) {
            val split = line.split(",")

            if(split.size >= 2) {
                mappings[split[0]] = split[1]
            } else {
                println("Skipping mapping entry \"$line\"")
            }
        }

        return mappings
    }

    fun decompile(
        mappingsName : String,
        mappingsType : Mappings
    ) {
        decompiling = true

        val modFile = File(MOD.get())
        val jarFile = JarFile(modFile)

        val loader = JarTypeLoader(jarFile)
        val metadata = MetadataSystem(loader)
        val settings = DecompilerSettings()
        val options = DecompilationOptions()

        val mappingsFile = File(mappingsName)
        val mappings = when(mappingsType) {
            Mappings.Tiny -> parseTiny(mappingsFile.readLines(Charset.defaultCharset()))
            Mappings.Csv -> parseCsv(mappingsFile.readLines(Charset.defaultCharset()))
        }

        val classRegex = Regex("class_[0-9]*")
        val methodRegex = Regex("method_[0-9]*")
        val fieldRegex = Regex("field_[0-9]*")

        val outputPath = "${FOLDER.get().removeSuffix("/")}/${modFile.nameWithoutExtension}/"
        val outputFolder = File(outputPath)

        if(outputFolder.exists()) {
            outputFolder.delete()
        }

        outputFolder.mkdirs()

        decompiledClasses = 0
        classesToDecompilation = 0

        for(entry in jarFile.entries()) {
            if(entry.isDirectory) {
                val directoryFile = File("$outputPath${entry.name}")

                directoryFile.mkdirs()
            } else if(entry.name.endsWith(".class")) {
                classesToDecompilation++
            }
        }

        for(entry in jarFile.entries()) {
            val `is` = jarFile.getInputStream(entry)
            val bytes = `is`.readBytes()

            if(!entry.isDirectory) {
                if(entry.name.endsWith(".class")) {
                    val type = metadata.lookupType(entry.name.removeSuffix(".class")).resolve()
                    val output = PlainTextOutput()

                    settings.language.decompileType(type, output, options)

                    val decompiled = output.toString()
                    val deobfuscated = decompiled
                        .replace(classRegex) { mappings[it.value] ?: it.value }
                        .replace(methodRegex) { mappings[it.value] ?: it.value }
                        .replace(fieldRegex) { mappings[it.value] ?: it.value }

                    val file = File("$outputPath${entry.name.replace(".class", ".java")}")

                    file.createNewFile()
                    file.writeText(deobfuscated, Charset.defaultCharset())

                    decompiledClasses++
                } else {
                    val file = File("$outputPath${entry.name}")

                    file.createNewFile()
                    file.writeBytes(bytes)
                }
            }
        }

        decompiling = false
    }

    val gui = object : Application() {
        private var state1 = true

        override fun configure(
            config : Configuration
        ) {
            colorBg.set(0f, 0f, 0f, 0f)

            config.title = "deobfuscator 4000"
            config.width = 550
            config.height = 300

            GLFWErrorCallback.createPrint(System.err).set()

            if(!GLFW.glfwInit()) {
                throw IllegalStateException("Unable to initialize GLFW")
            }

            GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, 1)
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 0)
        }

        override fun process() {
            ImGui.styleColorsDark()

            val style = ImGui.getStyle()

            style.frameBorderSize = 1f

            val window = style.getColor(ImGuiCol.WindowBg)

            style.setColor(ImGuiCol.WindowBg, window.x, window.y, window.z, 0.6f)

            val frame = style.getColor(ImGuiCol.FrameBg)

            style.setColor(ImGuiCol.FrameBg, frame.x, frame.y, frame.z, frame.w / 2f)

            ImGui.pushStyleColor(ImGuiCol.ChildBg, frame.x, frame.y, frame.z, frame.w / 2f)

            ImGui.begin("deobfuscator 4000", ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoDecoration)

            val w = intArrayOf(1)
            val h = intArrayOf(1)

            GLFW.glfwGetWindowSize(handle, w, h)

            ImGui.setWindowPos(0f, 0f)
            ImGui.setWindowSize(w[0].toFloat(), h[0].toFloat())

            ImGui.text("Decompiles minecraft mods and converts to named/mojang(legacy) namespace")
            ImGui.beginChild("Paths", 0f, 135f, true)

            ImGui.text("Compiled mod")
            ImGui.inputText("## Mod", MOD)
            ImGui.sameLine()

            if(ImGui.button("Select")) {
                val dialog = NfdFileDialog()
                val result = dialog.pickFile(listOf(FileDialog.Filter("JARs", listOf("jar"))))

                if(result is FileDialogResult.Success) {
                    val file = result.value
                    val path = file.path

                    MOD.set(path)
                }
            }

            ImGui.text("Decompiled files")
            ImGui.inputText("## Folder", FOLDER)
            ImGui.sameLine()

            if(ImGui.button("Select## 1")) {
                val dialog = NfdFileDialog()
                val result = dialog.pickDirectory()

                if(result is FileDialogResult.Success) {
                    val file = result.value
                    val path = file.path

                    FOLDER.set(path)
                }
            }

            ImGui.text("Mappings")
            ImGui.inputText("## Mappings File", MAPPINGS_FILE)
            ImGui.sameLine()

            if(ImGui.button("Select## 2")) {
                val dialog = NfdFileDialog()
                val result = dialog.pickFile(emptyList())

                if(result is FileDialogResult.Success) {
                    val file = result.value
                    val path = file.path

                    MAPPINGS_FILE.set(path)
                }
            }

            ImGui.sameLine()

            if(ImGui.button("Type: ${if(state1) "Tiny" else "Csv"}")) {
                state1 = !state1

                mappingsType = if(state1) {
                    Mappings.Tiny
                } else {
                    Mappings.Csv
                }
            }

            ImGui.endChild()

            if(ImGui.button("Decompile") && !decompiling) {
                decompile(MAPPINGS_FILE.get(), mappingsType)
            }

            ImGui.sameLine()
            ImGui.text(if(classesToDecompilation != 0) "Processed $decompiledClasses/$classesToDecompilation" else "Idling")

            ImGui.end()
            ImGui.popStyleColor()
        }
    }

    Application.launch(gui)
}

enum class Mappings {
    Tiny,
    Csv
}