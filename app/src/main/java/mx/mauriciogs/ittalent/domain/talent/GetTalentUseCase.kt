package mx.mauriciogs.ittalent.domain.talent

import mx.mauriciogs.ittalent.data.talent.TalentRepository

class GetTalentUseCase {

    private val talentRepository = TalentRepository()

    suspend fun getAllTalent() = talentRepository.getAllTalent()
}