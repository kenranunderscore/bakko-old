package kenran.util

import java.util.*

class FixedSizeStack<E>(maxSize: Int) {
    val _maxSize = maxSize
    val _items = LinkedList<E>()

    fun push(item: E) {
        if (_items.size >= _maxSize) {
            _items.removeFirst()
        }
        _items.push(item)
    }
}