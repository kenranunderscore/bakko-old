package kenran.util

class CyclicArray<E>(maxSize: Int) {
    private val _maxSize = maxSize
    private val _items = Array<Any?>(_maxSize) { null }
    private var _position = -1
    private var _hasOverflown = false

    val currentSize: Int
        get() = if (_hasOverflown) _maxSize else _position + 1

    fun push(item: E) {
        ++_position
        if (_position >= _maxSize) {
            _position -= _maxSize
            _hasOverflown = true
        }
        _items[_position] = item
    }

    @Suppress("UNCHECKED_CAST")
    fun get(i: Int): E {
        val offset = if (_hasOverflown) _position + 1 else 0
        val targetIndex = (offset + i) % _maxSize
        return _items[targetIndex] as E
    }
}