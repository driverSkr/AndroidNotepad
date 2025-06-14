package com.ethan.flowbus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.set

object FlowBus {

    private val busMap = mutableMapOf<String, EventBus<*>>()
    private val busStickMap = mutableMapOf<String, StickEventBus<*>>()
    private val mainScope = MainScope()


    const val EVEN_LOGIN = "EVEN_LOGIN"
    const val BUS_RE_FRESH = "BUS_RE_FRESH"
    const val BUS_WORK_UPDATE = "BUS_WORK_UPDATE"


    @Synchronized
    fun <T> with(key: String): EventBus<T> {
        var eventBus = busMap[key]
        if (eventBus == null) {
            eventBus = EventBus<T>(key)
            busMap[key] = eventBus
        }
        return eventBus as EventBus<T>
    }

    @Synchronized
    fun <T> withStick(key: String): StickEventBus<T> {
        var eventBus = busStickMap[key]
        if (eventBus == null) {
            eventBus = StickEventBus<T>(key)
            busStickMap[key] = eventBus
        }
        return eventBus as StickEventBus<T>
    }

    // 真正实现类
    open class EventBus<T>(private val key: String) : LifecycleObserver {

        // 私有对象用于发送消息
        private val _events: MutableSharedFlow<T> by lazy {
            obtainEvent()
        }

        // 暴露的公有对象用于接收消息
        val events = _events.asSharedFlow()

        open fun obtainEvent(): MutableSharedFlow<T> =
            MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)

        // 主线程接收数据
        fun register(lifecycleOwner: LifecycleOwner, action: (t: T) -> Unit) {
            lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    val subscriptCount = _events.subscriptionCount.value
                    if (subscriptCount <= 0) {
                        busMap.remove(key)
                    }
                }
            })
            lifecycleOwner.lifecycleScope.launch {
                events.collect {
                    try {
                        action(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // 协程中发送数据
        suspend fun postCurrentSp(event: T) {
            _events.emit(event)
        }

        // 主线程发送数据
        fun postMain(event: T) {
            mainScope.launch {
                _events.emit(event)
            }
        }

    }

    class StickEventBus<T>(key: String) : EventBus<T>(key) {
        override fun obtainEvent(): MutableSharedFlow<T> =
            MutableSharedFlow(1, 1, BufferOverflow.DROP_OLDEST)
    }

    class FlowMessage<T> {
        var key: String? = null
        var data: T? = null
    }
}