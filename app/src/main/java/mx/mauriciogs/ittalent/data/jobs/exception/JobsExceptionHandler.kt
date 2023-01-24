package mx.mauriciogs.ittalent.data.jobs.exception

sealed class JobsException(override val message: String?) : Exception() {
    object UnrecognizedError : JobsException("Error al crear el Empleo, Error desconocido")
    object EmptyListOfAciveJobs : JobsException("No se encontraron empleos activos")
    object EmptyListOfPastJobs : JobsException("No se encontraron empleos pasados")
    object EmptyListOfFilteredJobs : JobsException("No se encontraron empleos con el filtro especificado")
}

sealed class ApplyJobException(override val message: String?) : Exception() {
    object UnrecognizedError : ApplyJobException("Ocurrió un erro al enviar los datos, intenta de nuevo más tarde")
    object EmptyListOfApplyJobs : ApplyJobException("No has aplicado a trabajos tovadía")
}

sealed class RecruitmentException(override val message: String?) : Exception() {
    object EmptyListStage1 : RecruitmentException("Ocurrió un error obteniendo los datos")
    object EmptyListStage2 : RecruitmentException("Ocurrió un error obteniendo los datos")
    object EmptyListStage3 : RecruitmentException("Ocurrió un error obteniendo los datos")
    object EmptyListStage4 : RecruitmentException("Ocurrió un error obteniendo los datos")
    object UnrecognizedError2 : RecruitmentException("Ocurrió un error obteniendo los datos")
    object UnrecognizedError : RecruitmentException("Ocurrió un error actualizando el empleo")
    object EmptyListApplicants : RecruitmentException("No hay talento solicitante para esta oferta, intenta de nuevo más tarde")
}

