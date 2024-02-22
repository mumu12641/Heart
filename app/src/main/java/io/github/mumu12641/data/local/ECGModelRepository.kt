package io.github.mumu12641.data.local

import io.github.mumu12641.data.local.dao.ECGDao
import io.github.mumu12641.data.local.model.ECGModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ECGModelRepository {
    val ecgModels: Flow<List<ECGModel>>

    suspend fun addECG(ecgModel: ECGModel)

    suspend fun deleteECG(ecgModel: ECGModel)
}

class DefaultECGModelRepository @Inject constructor(
    private val ecgDao: ECGDao
) : ECGModelRepository {

    override val ecgModels: Flow<List<ECGModel>> =
        ecgDao.getAllECGModels()

    override suspend fun addECG(ecgModel: ECGModel) {
        ecgDao.insertECG(ecgModel)
    }

    override suspend fun deleteECG(ecgModel: ECGModel) {
        ecgDao.deleteECG(ecgModel)
    }
}