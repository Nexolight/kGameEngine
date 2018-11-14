package io

import flow.ActionHandler
import flow.NotifyThread
import jline.console.ConsoleReader
import models.Notification
import models.NotificationType
import mu.KLogger
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.InputStreamReader

class KbdConsole(ah:ActionHandler):NotifyThread(){

    val ah:ActionHandler = ah
    var kill = false
    lateinit var log:KLogger

    override fun run() {
        log = KotlinLogging.logger(this::class.java.name)
        log.info { "KbdConsole started!" }

        val instream:BufferedReader = BufferedReader(InputStreamReader(System.`in`))
        val reader: ConsoleReader = ConsoleReader()
        reader.handleUserInterrupt = false

        var chr:Char=0.toChar()
        var code:Int=0
        while(!kill){
            code = 0
            code = reader.readCharacter()
            if(code>0){
                chr = code.toChar()
                ah.notify(Notification(this,NotificationType.USERINPUT,chr))
            }
        }
        log.info { "KbdConsole stopped gracefully" }
    }

    override fun onNotify(n: Notification) {
        if(n.type == NotificationType.SIGNAL && n.n == 2){
            log.info { "Killing KbdConsole!" }
            kill = true
        }
    }

}