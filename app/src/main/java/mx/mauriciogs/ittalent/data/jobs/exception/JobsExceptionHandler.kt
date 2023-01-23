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

