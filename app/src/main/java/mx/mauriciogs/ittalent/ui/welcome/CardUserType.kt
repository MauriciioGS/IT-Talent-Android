package mx.mauriciogs.ittalent.ui.welcome

import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment.Companion.RECRUIT_CARD
import mx.mauriciogs.ittalent.ui.welcome.WelcomeFragment.Companion.TALENT_CARD
import mx.mauriciogs.ittalent.core.BaseFrag
import mx.mauriciogs.ittalent.databinding.CardUserTypeFragmentBinding

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