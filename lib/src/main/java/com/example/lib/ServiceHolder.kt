package com.example.lib

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.reflect.KClass

class ServiceHolder(
    val services: HashMap<KClass<out Service>, Any>
) {

    private val servicesManager = Class.forName("Init").newInstance()

    companion object {

        private val services = HashMap<KClass<out Service>, Any>()
        @Volatile
        private var serviceHolder: ServiceHolder? = null

        fun init(service: Pair<KClass<out Service>, Any>) {
            services += service
            println(services)
        }

        fun getInstance(): ServiceHolder {
            var localServiceHolder =
                serviceHolder
            if (localServiceHolder == null) {
                synchronized(this::class) {
                    localServiceHolder =
                        serviceHolder
                    if (localServiceHolder == null) {
                        localServiceHolder = ServiceHolder(
                            services
                        )
                            .also { serviceHolder = it }
                    }
                }
            }
            return localServiceHolder!!
        }
    }

    inline fun <reified T : Service> getService(): T? {
        return services[T::class] as? T
    }
}