package mx.mauriciogs.ittalent.authentication

import com.example.ittalent.R
import com.example.ittalent.databinding.CardUserTypeFragmentBinding
import mx.mauriciogs.ittalent.authentication.WelcomeFragment.Companion.RECRUIT_CARD
import mx.mauriciogs.ittalent.authentication.WelcomeFragment.Companion.TALENT_CARD
import mx.mauriciogs.ittalent.global.BaseFrag

class CardUserType(private val userType: Int) : BaseFrag<CardUserTypeFragmentBinding>(R.layout.card_user_type_fragment) {

    private lateinit var mBinding: CardUserTypeFragmentBinding

    override fun CardUserTypeFragmentBinding.initialize() {
        mBinding = this
        initCard()
    }

    private fun initCard() {
        with(mBinding) {
            when(userType) {
                TALENT_CARD -> {
                    tvDescription.text = getString(R.string.talento_desc)
                    civVector.setImageResource(R.drawable.talent_user_vector)
                }
                RECRUIT_CARD -> {
                    tvDescription.text = getString(R.string.reclutador_desc)
                    civVector.setImageResource(R.drawable.recluta_user_vector)
                }
            }
        }
    }
}