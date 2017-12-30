package kenran.util

import java.util.*

class FixedSizeStack<E>(maxSize: Int) {
    private val _maxSize = maxSize
    private val _items = LinkedList<E>()

    val size
        get() = _items.size

    fun push(item: E) {
        if (_items.size >= _maxSize) {
            _items.removeFirst()
        }
        _items.push(item)
    }

    fun peek(index: Int): E = _items[index]
}