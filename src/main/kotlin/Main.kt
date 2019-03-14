
import abstracted.CompositorType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.Args


/**
 * Set output mode to handle virtual terminal sequences for windows
 */
fun windowsConFix(){
    if (System.getProperty("os.name").startsWith("Windows")) {
        val GetStdHandleFunc = com.sun.jna.Function.getFunction("kernel32", "GetStdHandle")
        val STD_OUTPUT_HANDLE = WinDef.DWORD(-11)
        val hOut = GetStdHandleFunc.invoke(WinNT.HANDLE::class.java, arrayOf<Any>(STD_OUTPUT_HANDLE)) as WinNT.HANDLE

        val p_dwMode = WinDef.DWORDByReference(WinDef.DWORD(0))
        val GetConsoleModeFunc = com.sun.jna.Function.getFunction("kernel32", "GetConsoleMode")
        GetConsoleModeFunc.invoke(WinDef.BOOL::class.java, arrayOf<Any>(hOut, p_dwMode))

        val ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4
        val dwMode:WinDef.DWORD = p_dwMode.getValue()
        dwMode.setValue(dwMode.toLong() or ENABLE_VIRTUAL_TERMINAL_PROCESSING.toLong())
        val SetConsoleModeFunc = com.sun.jna.Function.getFunction("kernel32", "SetConsoleMode")
        SetConsoleModeFunc.invoke(WinDef.BOOL::class.java, arrayOf<Any>(hOut, dwMode))
    }
}

fun main(args:Array<String>) = mainBody{
    var compositor: CompositorType = CompositorType.ascii
    ArgParser(args).parseInto(::Args).run{
        when(argCompositor){
            "ascii" -> {
                windowsConFix()
                compositor = CompositorType.ascii
            }
            "web" -> compositor = CompositorType.web
            "qt" -> compositor = CompositorType.qt
            "dummy" -> {
                windowsConFix()
                compositor = CompositorType.dummy
            }
        }
    }
    val engine:Engine = Engine(compositor)
    engine.start()
}