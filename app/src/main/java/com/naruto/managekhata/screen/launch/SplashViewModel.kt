package com.naruto.managekhata.screen.launch

import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
): MainViewModel() {
    fun onAppStart(openAndPopUp: (NavigationGraphComponent, NavigationGraphComponent) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(NavigationGraphComponent.NavHomeScreen, NavigationGraphComponent.NavSplashScreen)
        else openAndPopUp(NavigationGraphComponent.NavLoginScreen, NavigationGraphComponent.NavSplashScreen)
    }
}