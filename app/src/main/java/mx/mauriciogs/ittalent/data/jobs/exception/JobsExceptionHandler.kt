package mx.mauriciogs.ittalent.data.jobs.exception

sealed class JobsException(override val message: String?) : Exception() {
    object UnrecognizedError : JobsException("Error al crear el Empleo, Error desconocido")
    object EmptyListOfJobs : JobsException("No se encontraron empleos publicados")
}
