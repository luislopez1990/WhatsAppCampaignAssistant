package mx.camaronpirata.campaigns

enum class DeliveryState { PENDING, SENT, FAILED, REVIEW }
data class Target(val name: String, val excluded: Boolean = false, val consent: Boolean = false)
data class Delivery(val campaignId: String, val target: String, val state: DeliveryState, val at: Long)

object Store {
    fun key(campaign: String, target: String) = "delivery_${campaign}_${target.hashCode()}"
}
