package mx.mauriciogs.ittalent.ui.main

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mx.mauriciogs.ittalent.core.extensions.no
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.ui.connectivity.ConnectivityObserver
import mx.mauriciogs.ittalent.ui.connectivity.NetworkConnecivityObserver

class MainViewModel(private val application: Application): ViewModel() {

    private lateinit var connectivityObserver: ConnectivityObserver

    private var _isConnected = MutableLiveData<Boolean>()
    val isConnected : LiveData<Boolean>
        get() = _isConnected

    fun monitorStateConnection(){
        connectivityObserver = NetworkConnecivityObserver(application.applicationContext)

        viewModelScope.launch {
            viewModelScope.launch {
                connectivityObserver.observe().collectLatest {
                    when(it) {
                        ConnectivityObserver.Status.Available -> { _isConnected.value = Boolean.yes() }
                        ConnectivityObserver.Status.Losing -> {  }
                        ConnectivityObserver.Status.Lost -> { _isConnected.value = Boolean.no() }
                        ConnectivityObserver.Status.Unavailable -> {  }
                    }
                }
            }
        }
    }

    class MainVMFactory(val app: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw  java.lang.IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}