package mx.camaronpirata.campaigns

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class CampaignAccessibilityService: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Punto de integración conservador: observa exclusivamente WhatsApp Business.
        // No se codifican selectores frágiles ni simulación engañosa. Cada versión de
        // WhatsApp debe validarse en dos grupos de prueba antes de habilitar acciones.
    }
    override fun onInterrupt() {}
}
