package abstracted.logic

import models.Notification

class DummyLogic : EntityLogic() {
    override fun run() {
        //Don't care
    }

    override fun onNotify(n: Notification) {
        //Don't care
    }
}