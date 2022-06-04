package com.graduation.healthapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.graduation.healthapp.data.*
import com.graduation.healthapp.ui.find.FindViewModel
import com.graduation.healthapp.ui.find.TopicDetailsViewModel
import com.graduation.healthapp.ui.splash.SplashViewModel

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class ViewModelFactory : ViewModelProvider.Factory {
    //需要考虑一下这个数据源的方法是否需要加锁
    val loginDataSource = LoginDataSource()
    val verifyCodeDataSource = VerifyCodeDataSource()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = loginDataSource,
                    dataSourceVerify = verifyCodeDataSource
                )
            ) as T
        } else if (modelClass.isAssignableFrom(VerifyCodeViewModel::class.java)) {
            return VerifyCodeViewModel(
                repository = LoginRepository(
                    dataSource = loginDataSource,
                    dataSourceVerify = verifyCodeDataSource
                )
            ) as T
        } else if (modelClass.isAssignableFrom(PassWordLoginViewModel::class.java)) {
            return PassWordLoginViewModel(
                repository = LoginRepository(
                    dataSource = loginDataSource,
                    dataSourceVerify = verifyCodeDataSource
                )
            ) as T
        }
        else if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(
                repository = SplashRepository(
                    datasource = SplashDataSource(),
                )
            ) as T
        }
        else if (modelClass.isAssignableFrom(FindViewModel::class.java)) {
            return FindViewModel(
                repository = FindRepository(
                    datasource = FindDataSource(),
                )
            ) as T
        }
        else if (modelClass.isAssignableFrom(TopicDetailsViewModel::class.java)) {
            return TopicDetailsViewModel(
                repository = TopicDetailsRepository(
                    dataSource = TopicDetailsDataSource(),
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}