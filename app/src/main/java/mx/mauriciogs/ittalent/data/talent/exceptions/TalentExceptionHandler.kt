package mx.mauriciogs.ittalent.data.talent.exceptions

sealed class TalentExceptionHandler(override val message: String?) : Exception(){
    object TalentEmpyList : TalentExceptionHandler("No se encontraron talentos")
}