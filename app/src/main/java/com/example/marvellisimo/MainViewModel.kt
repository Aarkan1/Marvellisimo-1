package com.example.marvellisimo

import com.example.marvellisimo.repository.Repository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: Repository
)


