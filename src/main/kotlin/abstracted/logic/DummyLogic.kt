package abstracted.logic

import models.Notification

class DummyLogic : EntityLogic() {
    override fun onNotify(n: Notification) {
        //Don't care
    }
}