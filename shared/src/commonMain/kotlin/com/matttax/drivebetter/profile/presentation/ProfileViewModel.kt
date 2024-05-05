package com.matttax.drivebetter.profile.presentation

import com.matttax.drivebetter.profile.domain.ProfileRepository
import com.matttax.drivebetter.profile.domain.model.Gender
import com.matttax.drivebetter.profile.domain.state.AuthState
import com.matttax.drivebetter.profile.domain.state.ProfileEvent
import com.matttax.drivebetter.profile.domain.model.ProfileDomainModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.lighthousegames.logging.KmLog

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<AuthState>(AuthState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        fetchState()
    }

    fun obtainEvent(viewEvent: ProfileEvent) {
        when(viewEvent) {
            is ProfileEvent.FetchState -> fetchState()
            is ProfileEvent.LogIn -> _viewState.value = AuthState.LoggingIn
            is ProfileEvent.LogOut -> logOut()
            is ProfileEvent.SignUp -> _viewState.value = AuthState.Registration
            is ProfileEvent.AbortAuthorization -> _viewState.value = AuthState.Unauthorized
            is ProfileEvent.CreateProfile -> createProfile(viewEvent)
            is ProfileEvent.EnterProfile -> enterProfile(viewEvent)
            is ProfileEvent.EditProfile -> editProfile(viewEvent.profile)
        }
    }

    private fun fetchState() {
        viewModelScope.launch {
            val profile = repository.isLoggedIn()
            _viewState.value = if (profile == null) {
                AuthState.Unauthorized
            } else {
                AuthState.LoggedIn(profile)
            }
        }
    }

    private fun createProfile(request: ProfileEvent.CreateProfile) {
        viewModelScope.launch {
            if (repository.signUp(request)) {
                log.d { "logging in..." }
                _viewState.value = AuthState.LoggedIn(request.profile)
            } else {
                log.d { "log in error" }
            }
        }
    }

    private fun enterProfile(request: ProfileEvent.EnterProfile) {
        viewModelScope.launch {
            if (repository.logIn(request)) {
                log.d { "logging in..." }
//                _viewState.value = AuthState.LoggedIn(request.profile)
            } else {
                log.d { "log in error" }
            }
        }
    }

    private fun editProfile(profile: ProfileDomainModel) {

    }

    private fun logOut() {
        repository.logOut()
        _viewState.value = AuthState.Unauthorized
    }

    companion object {
        private val log = KmLog("ProfileViewModel")
    }
}
