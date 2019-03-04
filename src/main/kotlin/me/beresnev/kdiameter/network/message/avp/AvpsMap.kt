package me.beresnev.kdiameter.network.message.avp

import net.jcip.annotations.ThreadSafe

@ThreadSafe
class AvpsMap(avpList: List<Avp>) {
    private val avpByCodeAndVendorId: Map<Long, Map<Long, Avp>>

    init {
        avpByCodeAndVendorId = buildMap(avpList)
    }

    private fun buildMap(avpList: List<Avp>): Map<Long, Map<Long, Avp>> {
        val resultMap: MutableMap<Long, MutableMap<Long, Avp>> = HashMap()
        avpList.forEach { avp ->
            val vendorId = if (avp.vendorId == null) 0 else avp.vendorId
            resultMap.getOrPut(avp.code) { HashMap() }[vendorId] = avp
        }
        return resultMap
    }

    fun get(code: Long): Avp? {
        return get(code, 0L)
    }

    fun get(code: Long, vendorId: Long): Avp? {
        return avpByCodeAndVendorId[code]?.get(vendorId)
    }
}