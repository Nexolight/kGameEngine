
import abstracted.CompositorType
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.Args


fun main(args:Array<String>) = mainBody{
    var compositor: CompositorType = CompositorType.ascii
    ArgParser(args).parseInto(::Args).run{
        when(argCompositor){
            "ascii" -> compositor = CompositorType.ascii
            "web" -> compositor = CompositorType.web
            "qt" -> compositor = CompositorType.qt
        }
    }
    val engine:Engine = Engine()
    engine.start()
}