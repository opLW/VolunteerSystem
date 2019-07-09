package com.oplw.volunteersystem.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieDrawable
import com.oplw.volunteersystem.BaseAnimatorListener
import com.oplw.volunteersystem.LoginViewModel
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    private var lastKeyDown = 0L
    private val enterAnimationDuration = 700L
    private var existAnimationDuration = 300L
    private lateinit var binding: ActivityLoginBinding
    inner class CallBack {
        fun loading(prompt: String) {
            login_top_loading_result_tv.visibility = View.VISIBLE
            login_top_loading_result_tv.text = prompt
        }

        fun loadingFinished(isSuccessful: Boolean) {
            login_top_loading_result_tv.visibility = View.GONE
            if (isSuccessful) {
                finishSuccessfully()
            } else {
                Toast.makeText(this@LoginActivity, "操作失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewmodel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewmodel.callback = CallBack()
        binding.data = viewmodel

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
        val topStartPosition: Float
        val topEndPosition: Float
        val bottomStartPosition: Float
        val bottomEndPosition: Float
        val duration: Long
        if (isOpen) {
            topStartPosition = -login_top_container.height.toFloat()
            topEndPosition = 0f
            bottomStartPosition = login_bottom_container.height.toFloat()
            bottomEndPosition = 0f
            duration = enterAnimationDuration
        } else {
            topStartPosition = 0f
            topEndPosition = -login_top_container.height.toFloat()
            bottomStartPosition = 0f
            bottomEndPosition = login_bottom_container.height.toFloat()
            duration = existAnimationDuration
        }

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
            this.setAnimation("login_successful.json")
            this.visibility = View.VISIBLE
            this.speed = 1.6f
            this.repeatCount = 1
            this.addAnimatorListener(object : BaseAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    finish()
                }
            })
            this.playAnimation()
        }
        showAnimation(false)
    }
}

fun AppCompatActivity.hideKeyBroad() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}
