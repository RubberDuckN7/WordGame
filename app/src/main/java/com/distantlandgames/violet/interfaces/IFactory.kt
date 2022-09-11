package com.distantlandgames.violet.interfaces

import android.content.Context

interface IFactory {
    fun create(context: Context): IObject
}