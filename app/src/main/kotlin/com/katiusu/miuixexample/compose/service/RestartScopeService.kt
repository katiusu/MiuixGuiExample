package com.katiusu.miuixexample.compose.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 本模板不执行任何 root 命令，保留接口以维持既有界面流程。 */
internal object RestartScopeService {
    suspend fun restart(commands: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        Result.failure(IllegalStateException("Root shell is disabled in this template"))
    }
}
