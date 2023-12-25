package com.chad.mylittlebookmanager

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chad.mylittlebookmanager.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLogin()

        binding.email.addTextChangedListener(this.emailWatcher())
        binding.password.addTextChangedListener(this.passwordWatcher())
        binding.submit.setOnClickListener(this.clickListener())
    }

    private fun checkLogin() {
        if (auth.currentUser != null) {
            changeFragment()
        }
    }

    private fun clickListener(): OnClickListener {
        return OnClickListener {
            auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        changeFragment()
                    } else {
                        Toast.makeText(requireContext(), "Bad credentials", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun changeFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val listMoviesFragment = ListItemsFragment()
        fragmentTransaction.replace(R.id.fragment_container_view, listMoviesFragment)
        fragmentTransaction.commit()
    }

    private fun emailWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                binding.submit.isEnabled = emptyFields() && isEmail(s.toString())
            }
        }
    }

    private fun passwordWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                binding.submit.isEnabled = emptyFields() && isPassword(s.toString())
            }
        }
    }

    private fun emptyFields(): Boolean {
        return this.binding.email.text.toString().isNotEmpty() && this.binding.password.text.toString().isNotEmpty()
    }

    private fun isEmail(email: String): Boolean {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex())
    }

    private fun isPassword(password: String): Boolean {
        return password.matches("^.{6,}\$".toRegex())
    }

}