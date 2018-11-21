package io

import abstracted.CompositorType
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser:ArgParser){
    val argCompositor by parser.storing(
            "-c","--compositor",
            help = "Use a specific compositor (ascii/web/qt)"
    ){toString()}
            .default(CompositorType.ascii)
            .addValidator {
                if(!CompositorType.values().any{it.name.equals(value)}){
                    errOptions("compositor must be one of these:",CompositorType.values())
                    invalidParam()
                }
            }

    /**
     * Deals with what happens when the validation failed
     */
    fun invalidParam(){
        System.exit(1)
    }

    /**
     * Prints a message as to why the given option was not accepted
     * and a list which ones would be valid.
     */
    fun <E : Enum<E>> errOptions(msg:String,opts:Array<E>){
        println(msg)
        var lst:String = ""
        for(opt in opts){
            lst+=opt.toString()
            if(opt!=opts[opts.size-1]){
                lst+=","
            }
        }
        println(lst)
    }
}