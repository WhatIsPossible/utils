package lex.utils.net

import android.content.Context
import android.net.*
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

/**
 * @Author dxl
 * @Date 2023/3/30 14:37
 * @Email lex911118@gmail.com
 * @Description Network Utility to detect availability or unavailability of Internet connection
 * 网络实用程序检测可用性或不可用的互联网连接. 并不是wifi的连接状态.
 *
 * 在某些情况下，用户连接到网络，但网络可能无法访问 Internet。例如：
 * 用户启用了移动数据，但他或她没有数据余额。
 * 设备在漫游网络中，数据漫游已被禁用。
 * 一些 wifi 网络要求登录信息。在这种情况下，用户在提供凭据之前无法访问 Internet。
 */
class NetworkStateHelper(context: Context) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val _networkStateFlow by lazy {
        MutableStateFlow(false).apply {
            registerNetworkCallback()
        }
    }

    /**
     * Returns instance of [StateFlow] which can be observed for network changes.
     */
    val networkStateFlow: StateFlow<Boolean>
        get() = _networkStateFlow

    private fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), this)
        }

        // Retrieve current status of connectivity
        val activeNetwork = connectivityManager.activeNetwork

        /**
         * NET_CAPABILITY_INTERNET：它表明网络应该能够访问互联网。
         * NET_CAPABILITY_VALIDATED：表示已成功检测到互联网连接。
         * NET_CAPABILITY_NOT_SUSPENDED：表示网络可以传输数据。
         */
        val isConnected = checkNetwork(activeNetwork)
        log("NetworkUtils: isConnected=$isConnected")
    }

    fun unregisterNetworkCallback() {
        log("unregisterNetworkCallback")
        connectivityManager.unregisterNetworkCallback(this@NetworkStateHelper)
    }

    private fun checkNetwork(network: Network?) =
        connectivityManager.getNetworkCapabilities(network)?.run {
            log(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                ) && hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
            } else {
                hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
            }
        } ?: false

    override fun onAvailable(network: Network) {
/*        val linkProperties = connectivityManager.getLinkProperties(network)
        linkProperties?.linkAddresses?.forEach {
            XLog.i("hostAddress=${it.address.hostAddress}")
            XLog.i("isLinkLocalAddress=${it.address.isLinkLocalAddress}")
            XLog.i("isAnyLocalAddress=${it.address.isAnyLocalAddress}")
            XLog.i("isLoopbackAddress=${it.address.isLoopbackAddress}")
            XLog.i("isMCGlobal=${it.address.isMCGlobal}")
            XLog.i("isMulticastAddress=${it.address.isMulticastAddress}")
            XLog.i("isMCLinkLocal=${it.address.isMCLinkLocal}")
        }*/
        val value = checkNetwork(network)
        log("NetworkUtils: onAvailable=$value")
        _networkStateFlow.tryEmit(value)
    }

    override fun onLost(network: Network) {
        log("NetworkUtils: onLost")
        _networkStateFlow.tryEmit(false)
    }

    fun getAddress(): List<LinkAddress>? {
        return connectivityManager.activeNetwork?.let { network ->
            val linkProperties = connectivityManager.getLinkProperties(network)
            linkProperties?.linkAddresses
        }
    }

    fun log(networkCapabilities: NetworkCapabilities?) {
        val linkDownstreamBandwidthKbps = networkCapabilities?.linkDownstreamBandwidthKbps
        val linkUpstreamBandwidthKbps = networkCapabilities?.linkUpstreamBandwidthKbps

        val vpn = networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN)
        val wifi = networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
        val cellular =
            networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)

        val internet =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val ims =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_IMS)
        val dun =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_DUN)
        val not_metered =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        val not_suspended =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
        val validated =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        log("vpn传输=$vpn")
        log("wifi传输=$wifi")
        log("蜂窝传输=$cellular")
        log("internet=$internet")
        log("运营商IMS服务器的网络=$ims")
        log("运营商的 DUN 或网络共享网关的网络=$dun")
        log("网络未按流量计费=$not_metered")
        log("网络当前未挂起=$not_suspended")
        log("已成功验证此网络上的连接=$validated")
        log("down=$linkDownstreamBandwidthKbps")
        log("up=$linkUpstreamBandwidthKbps")
    }

    private fun log(msg: String) {
        Timber.tag("网络").d(msg)
    }
}
