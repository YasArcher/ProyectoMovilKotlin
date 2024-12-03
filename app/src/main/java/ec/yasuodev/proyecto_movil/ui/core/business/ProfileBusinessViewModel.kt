package ec.yasuodev.proyecto_movil.ui.core.business

import androidx.lifecycle.ViewModel
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

open class ProfileBusinessViewModel : ViewModel() {
    open val store = Store(
        id = "1",
        name = "Inventory Hub",
        owner = "Edison Ortiz",
        business_image = ""
    )
}
