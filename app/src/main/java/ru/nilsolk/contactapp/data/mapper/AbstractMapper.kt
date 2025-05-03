package ru.nilsolk.contactapp.data.mapper

abstract class AbstractMapper<T, I> {
    abstract fun map(input: T): I
    abstract fun mapList(input: List<T>): List<I>
}