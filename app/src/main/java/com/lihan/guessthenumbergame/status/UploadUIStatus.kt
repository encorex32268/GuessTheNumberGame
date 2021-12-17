package com.lihan.guessthenumbergame.status

sealed class UploadUIStatus{
    object Success : UploadUIStatus()
    data class Error(val message : String ) : UploadUIStatus()
    object Loading : UploadUIStatus()
    object Empty : UploadUIStatus()
}