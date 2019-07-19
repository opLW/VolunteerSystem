package com.oplw.volunteersystem.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieDrawable
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.base.BaseAnimatorListener
import com.oplw.volunteersystem.base.hideKeyBroad
import com.oplw.volunteersystem.base.isNetConnected
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.databinding.ActivityLoginBinding
import com.oplw.volunteersystem.net.bean.User
import com.oplw.volunteersystem.viewmodel.LoginViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    private var lastKeyDown = 0L
    private val enterAnimationDuration = 700L
    private var existAnimationDuration = 300L
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    inner class CallBack {
        fun showMsg(prompt: String) {
            login_top_loading_result_tv.visibility = View.VISIBLE
            login_top_loading_result_tv.text = prompt
        }

        fun loadingFinished(isSuccessful: Boolean, prompt: String? = "登录成功", user: User? = null) {
            login_top_loading_result_tv.visibility = View.GONE
            if (isSuccessful) {
                MyManager.getInstance().user = user!!
                finishSuccessfully()
            }
            showToastInBottom(prompt)
        }

        fun addNewConnector(disposable: Disposable) {
            MyManager.getInstance().addDisposable(disposable)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.callback = CallBack()
        binding.data = viewModel

        login_top_lottie.also {
            it.setAnimation("welcome.json")
            it.repeatCount = LottieDrawable.INFINITE
            it.speed = 0.2f
        }

        login_root_view.postDelayed(100) {
            showAnimation(true, object : BaseAnimatorListener() {
                override fun onAnimationStart(animation: Animator?) {
                    login_top_container.visibility = View.VISIBLE
                    login_bottom_container.visibility = View.VISIBLE
                }
                override fun onAnimationEnd(animation: Animator?) {}
            })
        }
    }

    private fun showAnimation(isOpen: Boolean, listener: Animator.AnimatorListener? = null) {
        val topStartPosition = if (isOpen) -login_top_container.height.toFloat() else 0f
        val topEndPosition = if (isOpen) 0f else -login_top_container.height.toFloat()
        val bottomStartPosition = if (isOpen) login_bottom_container.height.toFloat() else 0f
        val bottomEndPosition = if (isOpen) 0f else login_bottom_container.height.toFloat()
        val duration: Long = if (isOpen) enterAnimationDuration else existAnimationDuration

        val topValueHolder = PropertyValuesHolder
            .ofFloat("translationY", topStartPosition, topEndPosition)
        val bottomValueHolder = PropertyValuesHolder
            .ofFloat("translationY", bottomStartPosition, bottomEndPosition)
        show(topValueHolder, bottomValueHolder, listener, duration)
    }

    private fun show(topValueHolder: PropertyValuesHolder, bottomValuesHolder: PropertyValuesHolder,
                     listener: Animator.AnimatorListener?, duration: Long) {
        val topAnimator = ObjectAnimator.ofPropertyValuesHolder(login_top_container, topValueHolder)
        val bottomAnimator = ObjectAnimator.ofPropertyValuesHolder(login_bottom_container, bottomValuesHolder)
        with(AnimatorSet()) {
            this.playTogether(topAnimator, bottomAnimator)
            this.duration = duration
            if (listener != null) {
                this.addListener(listener)
            }
            this.start()
        }
    }

    fun doAction(view: View) {
        if (isNetConnected()) {
            viewModel.doAction()
        }
    }

    fun onRootViewTouched(view: View) {
        hideKeyBroad()
    }

    override fun onResume() {
        super.onResume()
        login_top_lottie.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        login_top_lottie.cancelAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyManager.getInstance().shutdownAllConnector()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentKeyDown = System.currentTimeMillis()
        return if (keyCode == KeyEvent.KEYCODE_BACK && currentKeyDown > lastKeyDown + 1000) {
            finish()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun finishSuccessfully() {
        with(login_successful_lottie) {
            setAnimation("login_successful.json")
            visibility = View.VISIBLE
            speed = 1.6f
            repeatCount = 1
            addAnimatorListener(object : BaseAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    setResult(MainActivity.RESULT_CODE)
                    finish()
                }
            })
            playAnimation()
        }
        showAnimation(false)
    }
}
